--Purpose    : Used for Customer validation
--Author     : Priyank Gupta
--Created On : 14-Mar-09

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--THIS IS THE PROCEDURE TO VALIDATE THE UPLOADED CUSTOMER DATA AND TO UPLOAD IT IN BASE TABLES. IT VALIDATES AND UPLOAD FOLLOWING
--CUSTOMER TYPES.
--1. DEALER 	2. SUPPLIER	3. THIRD PARTY		4. NATIONAL ACCOUNT	5. DIRECT CUSTOMER		6. OEM
--
--DATE		: 15 FEB 2009
--AUTHOR		: PRIYANK GUPTA
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE customer_validation_procedure
AS

    CURSOR all_customer 
    IS
        SELECT *
        FROM customer_staging
        WHERE NVL(error_status,'N') = 'N';

    v_error_code        VARCHAR2(4000);
    v_error             VARCHAR2(4000);
    v_var               NUMBER;
    v_bu_name           VARCHAR(255);
    v_valid_bu          BOOLEAN;
    v_valid_state       BOOLEAN;
    v_valid_city        BOOLEAN;
    v_success_count     NUMBER;
    v_error_count       NUMBER;
    v_count             NUMBER;
    v_file_upload_mgt_id    NUMBER;

BEGIN

    SELECT f.business_unit_info INTO v_bu_name 
    FROM file_upload_mgt f
    WHERE f.id = (SELECT file_upload_mgt_id FROM customer_staging WHERE rownum = 1);

    FOR each_rec IN all_customer 
    LOOP

        v_error_code := NULL;
        v_valid_bu := FALSE;

        IF each_rec.business_unit IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU001');
        ELSIF UPPER(each_rec.business_unit) != UPPER(v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU002');
        ELSE
            v_valid_bu := TRUE;
        END IF;

        IF each_rec.customer_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU003');
        END IF;

        IF each_rec.customer_name IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU004');
        END IF;

        IF each_rec.email IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU005');
        END IF;

        IF each_rec.address IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU006');
        END IF;

        IF each_rec.city IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU007');
        END IF;

        IF each_rec.country IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU008');
        ELSIF NOT common_validation_utils.isValidCountry(each_rec.country) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU009');
        ELSIF UPPER(each_rec.country) = 'US' THEN
            IF each_rec.state IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CU010');
            ELSIF NOT common_validation_utils.isValidState(each_rec.state, each_rec.country) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CU011');
            ELSE 
                v_valid_state := TRUE;
            END IF;                
            
            IF each_rec.city IS NULL THEN
                v_valid_city := FALSE;
            ELSIF v_valid_state AND NOT common_validation_utils.isValidCity(each_rec.city, each_rec.state, each_rec.country) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CU012');
            ELSIF v_valid_state THEN
                v_valid_city := TRUE;
            END IF;                

            IF each_rec.postal_code IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CU013');
            ELSIF v_valid_city AND NOT common_validation_utils.isValidZipcode(each_rec.postal_code, each_rec.city, each_rec.state, each_rec.country) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CU014');
            END IF;
        END IF;

        IF each_rec.customer_type IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU015');
        ELSIF UPPER(each_rec.customer_type) NOT IN ('DEALER','SUPPLIER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER')
        THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU016');
        END IF;

        IF each_rec.currency IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU017');
        ELSIF NOT common_validation_utils.isValidCurrency(each_rec.currency) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU018');
        END IF;

        IF each_rec.status IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU019');
        ELSIF UPPER(each_rec.status) NOT IN ('ACTIVE','INACTIVE') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU020');
        END IF;

        IF UPPER(NVL(each_rec.updates,'N')) NOT IN ('Y','N') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU021');
        END IF;

        --CHECK IF THE RECORD IS UNIQUE WITH THE PROVIDED ID IN IT'S OWN TYPES
        BEGIN
            SELECT 1 INTO v_var
            FROM customer_staging
            WHERE customer_number = each_rec.customer_number 
                AND UPPER(customer_type) = UPPER(each_rec.customer_type)
                AND id <> each_rec.id 
                AND ROWNUM = 1;
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU022');
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                NULL;
        END;			
                    
        IF v_valid_bu THEN
        BEGIN
            IF UPPER(each_rec.customer_type) IN ('DEALER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER') THEN   
                SELECT 1 INTO v_var
                FROM service_provider sp, bu_org_mapping bom
                WHERE UPPER(service_provider_number) = UPPER(each_rec.customer_number)
                    AND bom.org = sp.id 
                    AND bom.bu = v_bu_name
                    AND ROWNUM = 1;                
            ELSIF each_rec.customer_type IN ('SUPPLIER') THEN
                SELECT 1 INTO v_var
                FROM supplier s, bu_org_mapping bom
                WHERE UPPER(s.supplier_number) = UPPER(each_rec.customer_number)
                    AND s.id = bom.org 
                    AND bom.bu = v_bu_name
                    AND ROWNUM = 1 ;
            END IF;
            IF UPPER(NVL(each_rec.updates,'N')) = 'N' THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'CU023');
            END IF;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                IF UPPER(NVL(each_rec.updates,'N')) = 'Y' THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'CU024');
                END IF;
        END;
        END IF;
        
        BEGIN
            IF v_error_code IS NULL THEN
                UPDATE customer_staging
                SET error_code = NULL,
                    error_status = 'Y'
                WHERE id = each_rec.id;	
            ELSE
                UPDATE customer_staging
                SET error_code = v_error_code,
                    error_status = 'N'
                WHERE id = each_rec.id;
            END IF;				
        END;

        COMMIT;

    END LOOP;


    BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
        FROM customer_staging WHERE ROWNUM = 1;
    
        -- Success Count
        SELECT count(*) INTO v_success_count
        FROM customer_staging 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
            
        -- Error Count
        SELECT count(*) INTO v_error_count
        FROM customer_staging 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';

        -- Total Count
        SELECT count(*) INTO v_count
        FROM customer_staging 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id;
    
        UPDATE file_upload_mgt SET 
            success_records= v_success_count, 
            error_records= v_error_count,
            total_records = v_count
        WHERE id = v_file_upload_mgt_id;
        
    EXCEPTION
        WHEN OTHERS THEN
            v_error := SUBSTR(SQLERRM, 1, 4000);
            UPDATE file_upload_mgt SET 
                error_message = v_error
            WHERE id = v_file_upload_mgt_id;
    END;

END;
/
COMMIT
/