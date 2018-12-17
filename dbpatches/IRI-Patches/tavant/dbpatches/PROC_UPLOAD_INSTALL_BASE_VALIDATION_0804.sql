-- Purpose    : Dealer site number is optional for Hussmann
-- Author     : raghuram.d
-- Created On : 04-Jul-09

CREATE OR REPLACE PROCEDURE upload_install_base_validation 
AS
    CURSOR ALL_REC
    IS
        SELECT *
        FROM STG_INSTALL_BASE
        WHERE NVL(ERROR_STATUS,'N') = 'N'
            AND UPLOAD_STATUS IS NULL
        ORDER BY ID ASC;

    --ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
    v_error_code                VARCHAR2(4000) := NULL;
    v_error                     VARCHAR2(4000) := NULL;
    v_count                     NUMBER := NULL;
    v_error_flag                BOOLEAN := NULL;
    v_file_upload_mgt_id        NUMBER := 0;
    v_success_count             NUMBER := 0;
    v_error_count               NUMBER := 0;
    v_loop_count                NUMBER := 0;
    v_uploaded_by               VARCHAR2(255);
    v_bu_name                   VARCHAR2(255);
    v_dealer_id                 NUMBER;

    v_valid_bu                  BOOLEAN;
    v_valid_type                BOOLEAN;
    v_is_retail                 BOOLEAN;
    v_is_stock                  BOOLEAN;

    v_valid_shipment_dt         BOOLEAN;
    v_valid_deilvery_dt         BOOLEAN;
    v_valid_installation_dt     BOOLEAN;

    v_valid_state               BOOLEAN;
    v_valid_city                BOOLEAN;
BEGIN
  
    BEGIN
        SELECT u.login, f.business_unit_info INTO v_uploaded_by, v_bu_name
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_install_base WHERE rownum = 1);
    EXCEPTION 
        WHEN OTHERS THEN
            NULL;
    END;

    FOR EACH_REC IN all_rec LOOP
    BEGIN

        v_error_code := NULL;
        v_valid_bu := FALSE;
        v_valid_type := FALSE;
        v_is_retail := FALSE;
        v_is_stock := FALSE;
        v_valid_shipment_dt := FALSE;
        v_valid_deilvery_dt := FALSE;
        v_valid_installation_dt := FALSE;
        v_valid_state := FALSE;
        v_valid_city := FALSE;


        --Business Unit is mandatory and the uploaded user belongs to the BU
        IF each_rec.business_unit_name IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB001');
        ELSIF UPPER(each_rec.business_unit_name) != UPPER(v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB002');
        ELSIF NOT common_validation_utils.isUserBelongsToBU(each_rec.business_unit_name, v_uploaded_by) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB002');
        ELSE
            v_valid_bu := TRUE;
        END IF;

        IF each_rec.serial_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB003');
        ELSIF v_valid_bu THEN
            BEGIN
                SELECT 1 INTO v_count
                FROM inventory_item
                WHERE UPPER(serial_number) = UPPER(each_rec.serial_number) 
                    AND business_unit_info = v_bu_name;
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB004');
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;
        END IF;

        IF each_rec.item_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB005');
        ELSIF v_valid_bu AND NOT COMMON_VALIDATION_UTILS.isValidItemNumber(each_rec.item_number, v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB006');
        END IF;

        IF each_rec.lifecycle_status IS NOT NULL AND each_rec.lifecycle_status != '1' THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB007');
        END IF;
        
        IF each_rec.stock_or_retail IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB008');
        ELSIF UPPER(each_rec.stock_or_retail) NOT IN ('STOCK','RETAIL') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB009');
        ELSE
            v_valid_type := TRUE;
            IF UPPER(each_rec.stock_or_retail) = 'STOCK' THEN
                v_is_stock := TRUE;
            ELSE
                v_is_retail := TRUE;
            END IF;
        END IF;

        IF each_rec.acr_date IS NOT NULL AND NOT common_utils.isValidDate(each_rec.acr_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB010');
        END IF;

        IF each_rec.machine_build_date IS NOT NULL AND NOT common_utils.isValidDate(each_rec.machine_build_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB011');
        END IF;

        IF each_rec.hours_in_service IS NOT NULL AND NOT common_utils.isPositiveInteger(each_rec.hours_in_service) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB012');
        END IF;

        IF each_rec.shipment_date IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB013');
        ELSIF NOT common_utils.isValidDate(each_rec.shipment_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB014');
        ELSE
            v_valid_shipment_dt := TRUE;
        END IF;
        
        IF v_is_retail THEN
            
            IF each_rec.delivery_date IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB015');
            ELSIF NOT common_utils.isValidDate(each_rec.delivery_date) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB016');
            ELSE
                v_valid_deilvery_dt := TRUE;
            END IF;

            IF each_rec.installation_date IS NOT NULL AND NOT common_utils.isValidDate(each_rec.installation_date) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB017');
            ELSE
                v_valid_installation_dt := TRUE;
            END IF;

            IF v_valid_shipment_dt AND v_valid_deilvery_dt 
                    AND TO_DATE(each_rec.delivery_date,'YYYYMMDD') < TO_DATE(each_rec.shipment_date,'YYYYMMDD') THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB018');
            END IF;

            IF v_valid_deilvery_dt AND v_valid_installation_dt 
                    AND TO_DATE(each_rec.installation_date, 'YYYYMMDD') < TO_DATE(each_rec.delivery_date, 'YYYYMMDD') THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB019');
            END IF;

            IF v_valid_shipment_dt AND v_valid_installation_dt 
                    AND TO_DATE(each_rec.installation_date, 'YYYYMMDD') < TO_DATE(each_rec.shipment_date, 'YYYYMMDD') THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB020');
            END IF;
        END IF;

        IF each_rec.warranty_start_date IS NOT NULL AND NOT common_utils.isValidDate(each_rec.warranty_start_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB021');
        ELSIF v_valid_deilvery_dt 
                AND TO_DATE(each_rec.warranty_start_date, 'YYYYMMDD') < TO_DATE(each_rec.delivery_date, 'YYYYMMDD') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB022');
        END IF;

        IF each_rec.invoice_number IS NULL AND each_rec.sales_order_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB023');
        END IF;
	
        IF each_rec.invoice_date IS NOT NULL AND NOT common_utils.isValidDate(each_rec.invoice_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB024');
        END IF;

        IF each_rec.dealer_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB025');
        ELSIF v_valid_bu THEN
            BEGIN
                SELECT d.id INTO v_dealer_id
                FROM dealership d, bu_org_mapping m
                WHERE d.id = m.org AND m.bu = v_bu_name
                    AND UPPER(dealer_number) = UPPER(each_rec.dealer_number) AND ROWNUM=1;
                
                IF each_rec.dealer_site_number IS NULL THEN
                    IF v_bu_name != 'Hussmann' THEN
                        v_error_code := common_utils.addErrorMessage(v_error_code, 'IB045');
                    END IF;
                ELSE
                    SELECT COUNT(*) INTO v_count 
                    FROM organization_address oa, organization_org_addresses ooa
                    WHERE ooa.organization = v_dealer_id
                        AND ooa.org_addresses = oa.id
                        AND UPPER(oa.site_number) = UPPER(each_rec.dealer_site_number);
                    IF v_count = 0 THEN
                        v_error_code := common_utils.addErrorMessage(v_error_code, 'IB046');
                    END IF;
                END IF;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN				
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB026');
            END;
        END IF;

        IF v_is_retail THEN

            IF each_rec.end_customer_name IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB027');
            END IF;

            IF each_rec.e_mail IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB028');
            END IF;

            IF each_rec.address_line1 IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB029');
            END IF;

            IF each_rec.country IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB030');
            ELSIF NOT common_validation_utils.isValidCountry(each_rec.country) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB031');
            ELSIF UPPER(each_rec.country) = 'US' THEN
                IF each_rec.state IS NULL THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB032');
                ELSIF NOT common_validation_utils.isValidState(each_rec.state, each_rec.country) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB033');
                ELSE 
                    v_valid_state := TRUE;
                END IF;                
                
                IF each_rec.city IS NULL THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB034');
                ELSIF v_valid_state AND NOT common_validation_utils.isValidCity(each_rec.city, each_rec.state, each_rec.country) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB035');
                ELSIF v_valid_state THEN
                    v_valid_city := TRUE;
                END IF;                

                IF each_rec.zipcode IS NULL THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB036');
                ELSIF v_valid_city AND NOT common_validation_utils.isValidZipcode(each_rec.zipcode, each_rec.city, each_rec.state, each_rec.country) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB037');
                END IF;
            ELSE
                IF each_rec.city IS NULL THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB034');
                END IF;
            END IF;

        END IF;

        IF v_is_retail AND each_rec.prefered_language IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB038');
        ELSIF each_rec.prefered_language IS NOT NULL 
                AND each_rec.prefered_language NOT IN ('en_US', 'nl_NL', 'en_GB', 'fr_FR', 'it_IT', 'es_ES') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB039');
        END IF;

        IF each_rec.apply_coverage IS NOT NULL AND UPPER(each_rec.apply_coverage) NOT IN ('Y','N') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB040');
        END IF;
	
        IF each_rec.inventory_item_type IS NOT NULL 
                AND UPPER(each_rec.inventory_item_type) NOT IN ('NEW', 'REFURBISHED') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IB041');
        END IF;

        IF v_valid_bu AND v_bu_name != 'Hussmann' THEN
            IF each_rec.ship_from_warehouse IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB043');
            ELSE
                BEGIN
                    SELECT 1 INTO v_count
                    FROM source_warehouse
                    WHERE business_unit_info = v_bu_name
                        AND UPPER(code) = UPPER(each_rec.ship_from_warehouse);
                EXCEPTION
                    WHEN NO_DATA_FOUND THEN				
                        v_error_code := common_utils.addErrorMessage(v_error_code, 'IB044');
                END;            
            END IF;
        END IF;

        IF v_valid_bu AND v_bu_name = 'Hussmann' THEN
            IF each_rec.factory_order_number IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB047');
            END IF;

            IF each_rec.manufacturing_site IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IB048');
            ELSE
                SELECT COUNT(*) INTO v_count
                FROM list_of_values 
                WHERE business_unit_info = v_bu_name AND type='MANUFACTURINGSITEINVENTORY'
                    AND UPPER(code) = UPPER(each_rec.manufacturing_site);
                IF v_count = 0 THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IB049');
                END IF;
            END IF;

        END IF;

        --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
        IF v_error_code IS NULL THEN
            UPDATE stg_install_base SET
                error_status = 'Y',
                error_code = NULL
            WHERE id = each_rec.id;
        ELSE
            UPDATE stg_install_base SET
                error_status = 'N',
                error_code = v_error_code
            WHERE id = each_rec.id;
        END IF;
    
        v_loop_count := v_loop_count + 1;
        IF v_loop_count = 10 THEN
            COMMIT;
            v_loop_count := 0; -- Initialize the count size
        END IF;

    END;
    END LOOP;

    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;

    BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
        FROM stg_install_base WHERE ROWNUM = 1;
    
        -- Success Count
        BEGIN
            SELECT count(*) INTO v_success_count
            FROM stg_install_base 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;
            
        -- Error Count
        BEGIN
            SELECT count(*) INTO v_error_count
            FROM stg_install_base 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;

        -- Total Count
        SELECT count(*) INTO v_count
        FROM stg_install_base 
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

    COMMIT; -- Final Commit for the procedure

END upload_install_base_validation;
/
COMMIT
/