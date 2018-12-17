create or replace
PROCEDURE MULTIPLE_CUSTOMER_VALIDATION AS

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

     
        IF each_rec.customer_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU003');
        END IF;

       IF each_rec.DEALER_SITE IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU0020');
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
       
        IF each_rec.status IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU019');
        ELSIF UPPER(each_rec.status) NOT IN ('ACTIVE','INACTIVE') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU020');
        END IF;

        IF UPPER(NVL(each_rec.updates,'N')) NOT IN ('Y','N') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CU021');
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

        
        SELECT count(*) INTO v_success_count
        FROM customer_staging 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';

        
        SELECT count(*) INTO v_error_count
        FROM customer_staging 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';

        
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