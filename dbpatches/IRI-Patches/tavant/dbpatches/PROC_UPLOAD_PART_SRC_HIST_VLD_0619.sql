-- Purpose    : Procedure to upload the data of Part Source History
-- Author     : Jhulfikar Ali. A
-- Created On : 11-Feb-09

CREATE OR REPLACE PROCEDURE upload_part_src_hist_vld 
AS
CURSOR ALL_REC IS
    SELECT *
    FROM stg_part_src_hist_upld
    WHERE NVL(error_status,'N') = 'N' 
        AND upload_status IS NULL
    ORDER BY id;

  --ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
  v_error_code 		              VARCHAR2(4000)	:=  NULL;
  v_error                       VARCHAR2(4000)	:=  NULL;
  v_count	                      NUMBER	        :=	0;
  v_file_upload_mgt_id          NUMBER          :=  0;
  v_success_count               NUMBER          :=  0;
  v_error_count                 NUMBER          :=  0;
  v_loop_count	                NUMBER	        :=	0;
  v_bu_name                     VARCHAR2(255);
  v_valid_bu                    BOOLEAN;
  v_valid_from_dt               BOOLEAN;
  v_product_type_id             NUMBER;
  v_supplier_id                 NUMBER;

--MAIN LOOP OF THE PROCEDURE WHICH WILL LOOP AND VALIDATE RECORDS
BEGIN

    SELECT business_unit_info INTO v_bu_name
    FROM file_upload_mgt 
    WHERE id = (SELECT file_upload_mgt_id FROM stg_part_src_hist_upld WHERE rownum = 1);

    SELECT p.id INTO v_product_type_id
    FROM item_group p, item_scheme_purposes isp, purpose pur
    WHERE p.scheme=isp.item_scheme AND isp.purposes=pur.id
      AND pur.name='PRODUCT STRUCTURE'
      AND p.item_group_type='PRODUCT TYPE'
      AND UPPER(p.name) = 'PARTS'
      AND p.business_unit_info = v_bu_name;

    FOR EACH_REC IN ALL_REC LOOP
    BEGIN

        v_error_code := NULL;
        v_valid_bu := FALSE;
        v_valid_from_dt := FALSE;
          
        IF each_rec.business_unit_name IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS001');
        ELSIF UPPER(each_rec.business_unit_name) != UPPER(v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS002');
        ELSE 
            v_valid_bu := TRUE;
        END IF;

        IF each_rec.item_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS003');
        ELSIF v_valid_bu THEN
            SELECT COUNT(*) INTO v_count
            FROM item i,items_in_group ig,item_group m
            WHERE i.id = ig.item AND ig.item_group = m.id
                AND m.is_part_of = v_product_type_id
                AND (UPPER(i.item_number)=UPPER(each_rec.item_number) 
                    OR UPPER(i.alternate_item_number)=UPPER(each_rec.item_number));
            IF v_count = 0 THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'PS004');
            END IF;
        END IF;

        IF each_rec.supplier_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS005');
        ELSIF v_valid_bu AND NOT 
                common_validation_utils.isValidSupplier(each_rec.supplier_number,v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS006');
        END IF;

        IF each_rec.from_date IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS007');
        ELSIF NOT common_utils.isValidDate(each_rec.from_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS008');
        ELSE
            v_valid_from_dt := TRUE;
        END IF;

        IF each_rec.to_date IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS009');
        ELSIF NOT common_utils.isValidDate(each_rec.to_date) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS010');
        ELSIF v_valid_from_dt AND TO_DATE(each_rec.from_date,'YYYYMMDD') > TO_DATE(each_rec.to_date,'YYYYMMDD') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS017');
        END IF;
            
        --IF each_rec.catalog_name IS NULL THEN
        --    v_error_code := common_utils.addErrorMessage(v_error_code, 'PS011');
        --ELSIF UPPER(each_rec.catalog_name) != 'IRI' THEN
        --    v_error_code := common_utils.addErrorMessage(v_error_code, 'PS012');
        --END IF;

        IF each_rec.action IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS013');
        ELSIF each_rec.action NOT IN ('0','1') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'PS014');
        END IF;

        IF v_error_code IS NULL THEN
            SELECT s.id INTO v_supplier_id
            FROM supplier s,bu_org_mapping m
            WHERE UPPER(s.supplier_number) = UPPER(each_rec.supplier_number)
                AND s.id = m.org AND m.bu = v_bu_name AND ROWNUM = 1;

            IF each_rec.action = '1' THEN
            BEGIN
                SELECT 1 INTO v_count
                FROM item i,item_mapping m
                WHERE i.owned_by = v_supplier_id AND i.id = m.to_item
                    AND i.business_unit_info = v_bu_name
                    AND (UPPER(i.item_number) = UPPER(each_rec.item_number)
                        OR UPPER(i.alternate_item_number) = UPPER(each_rec.item_number))
                    AND ((TO_DATE(each_rec.from_date,'YYYYMMDD') >= m.from_date AND TO_DATE(each_rec.from_date,'YYYYMMDD') < m.to_date)
                        OR (TO_DATE(each_rec.to_date,'YYYYMMDD') >= m.from_date AND TO_DATE(each_rec.to_date,'YYYYMMDD') < m.to_date)
                        OR (TO_DATE(each_rec.to_date,'YYYYMMDD') >= m.to_date AND TO_DATE(each_rec.from_date,'YYYYMMDD') <= m.from_date));
                v_error_code := common_utils.addErrorMessage(v_error_code, 'PS015');
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;
            ELSE

                SELECT COUNT(*) INTO v_count
                FROM item i,item_mapping m
                WHERE i.owned_by = v_supplier_id AND i.id = m.to_item
                    AND i.business_unit_info = v_bu_name
                    AND (UPPER(i.item_number) = UPPER(each_rec.item_number)
                        OR UPPER(i.alternate_item_number) = UPPER(each_rec.item_number))
                    AND TO_DATE(each_rec.from_date,'YYYYMMDD') <= m.from_date 
                    AND TO_DATE(each_rec.to_date,'YYYYMMDD') >= m.to_date;
                IF v_count = 0 THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'PS016');
                END IF;

            END IF;
        END IF;

        --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
        IF v_error_code IS NULL THEN
            UPDATE stg_part_src_hist_upld SET
                error_status = 'Y',
                error_code = NULL
            WHERE id = each_rec.id;
        ELSE
            UPDATE stg_part_src_hist_upld SET
                error_status = 'N',
                error_code = v_error_code
            WHERE id = each_rec.id;
        END IF;
          
        v_loop_count := v_loop_count + 1;

        IF v_loop_count = 10 THEN
            COMMIT;
            v_loop_count := 0; 
        END IF;

    END;
    END LOOP;

    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;


    -- Update the status of validation
    BEGIN

        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id
        FROM stg_part_src_hist_upld WHERE ROWNUM = 1;

        -- Success Count
        SELECT count(*) INTO v_success_count
        FROM stg_part_src_hist_upld 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id AND error_status = 'Y';

        -- Error Count
        SELECT count(*) INTO v_error_count
        FROM stg_part_src_hist_upld 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id AND error_status = 'N';

        -- Total Count
        SELECT count(*) INTO v_count
        FROM stg_part_src_hist_upld 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id;

        UPDATE file_upload_mgt SET 
            success_records = v_success_count, 
            error_records = v_error_count,
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

END;
/
COMMIT
/