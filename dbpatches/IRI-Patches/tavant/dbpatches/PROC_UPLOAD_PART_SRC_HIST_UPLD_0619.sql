-- Purpose    : Procedure to upload the data of Part Source History
-- Author     : Jhulfikar Ali. A
-- Created On : 11-Feb-09

CREATE OR REPLACE
PROCEDURE UPLOAD_PART_SRC_HIST_UPLD AS
   CURSOR ALL_REC IS
      SELECT *
      FROM STG_PART_SRC_HIST_UPLD
      WHERE NVL (error_status, 'N') = 'Y' AND NVL (upload_status, 'N') = 'N';

   --ALL THE GLOBAL VARIABLE FOR THE PROCEDURE ARE DEFINED HERE
   v_error_code         VARCHAR2 (1100) := NULL;
   v_loop_count         NUMBER := 0;
   v_item_mapping_id    NUMBER;
   v_oem_item_id        NUMBER;
   v_model_id           NUMBER;
   v_bu_name            VARCHAR(255);
   v_supplier_id        NUMBER;
   v_supplier_name      VARCHAR(255);
   v_supplier_item_id   NUMBER;
   v_count              NUMBER;

--MAIN LOOP OF THE PROCEDURE WHICH WILL LOOP AND INSERT RECORDS
BEGIN
   FOR EACH_REC IN ALL_REC LOOP
      --MAIN BEGIN LOOP
      BEGIN
	  
      v_bu_name := common_validation_utils.getValidBusinessUnitName(each_rec.business_unit_name);

       --GET THE ITEM NUMBER FOR THE OEM ITEM FROM ITEM TABLE
       SELECT i.ID, i.model
       INTO   v_oem_item_id, v_model_id
       FROM item i, party p
       WHERE ( UPPER(i.item_number) = UPPER(EACH_REC.ITEM_NUMBER) OR
                UPPER(i.alternate_item_number) = UPPER(EACH_REC.ITEM_NUMBER) )
       AND i.owned_by = p.ID
       AND p.NAME = COMMON_UTILS.CONSTANT_OEM_NAME
       AND i.business_unit_info=v_bu_name;

        SELECT s.id, p.name INTO v_supplier_id, v_supplier_name
        FROM supplier s,bu_org_mapping m,party p
        WHERE UPPER(s.supplier_number) = UPPER(each_rec.supplier_number)
            AND s.id = m.org AND m.bu = v_bu_name AND s.id=p.id AND ROWNUM = 1;

        IF each_rec.action = 1 THEN
            BEGIN
                SELECT i.id INTO v_supplier_item_id
                FROM item i
                WHERE i.owned_by = v_supplier_id
                    AND i.business_unit_info = v_bu_name
                    AND (UPPER(i.item_number) = UPPER(each_rec.item_number)
                        OR UPPER(i.alternate_item_number) = UPPER(each_rec.item_number));
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    SELECT item_seq.nextval INTO v_supplier_item_id FROM DUAL;

                    INSERT INTO item (id, description, make, name, item_number, serialized, usage_meter, version, 
                        owned_by, model, product, item_type, status, business_unit_info, alternate_item_number,
                        duplicate_alternate_number, d_active, d_internal_comments, d_created_on)
                    SELECT v_supplier_item_id, description, v_supplier_name, name, item_number, serialized, usage_meter, '0', 
                        v_supplier_id, model, product, item_type, status, business_unit_info, alternate_item_number,
                        duplicate_alternate_number, d_active, 'Part Source History Upload', SYSDATE
                    FROM item WHERE id = v_oem_item_id;

                    INSERT INTO items_in_group (item_group, item)
                    VALUES(v_model_id, v_supplier_item_id);

                    INSERT INTO i18nitem_text (id, locale, description, item)
                    SELECT i18n_item_text_seq.nextval, locale, description, v_supplier_item_id
                    FROM i18nitem_text WHERE item=v_oem_item_id;
            END;

            SELECT item_mapping_seq.nextval INTO v_item_mapping_id FROM DUAL;

            INSERT INTO item_mapping (id, version, to_item, from_item,
                from_date, to_date,
                d_internal_comments, d_created_on, d_updated_on, d_active)
            VALUES (v_item_mapping_id, 1, v_supplier_item_id, v_oem_item_id,
                TO_DATE(each_rec.from_date, 'YYYYMMDD'),TO_DATE(each_rec.to_date, 'YYYYMMDD'),
                'System: part src history upload', SYSDATE, SYSDATE, 1);
        END IF;

        IF each_rec.action = 0 THEN
            SELECT i.id INTO v_supplier_item_id
            FROM item i
            WHERE i.owned_by = v_supplier_id
                AND i.business_unit_info = v_bu_name
                AND (UPPER(i.item_number) = UPPER(each_rec.item_number)
                    OR UPPER(i.alternate_item_number) = UPPER(each_rec.item_number));
            
            DELETE FROM item_mapping 
            WHERE from_item = v_oem_item_id
                AND to_item = v_supplier_item_id
                AND TO_DATE(each_rec.from_date,'YYYYMMDD') <= from_date 
                AND TO_DATE(each_rec.to_date,'YYYYMMDD') >= to_date;


            SELECT COUNT(*) INTO v_count 
            FROM item_mapping WHERE from_item=v_oem_item_id AND to_item=v_supplier_item_id;

            IF v_count = 0 THEN
                DELETE FROM i18nitem_text WHERE item=v_supplier_item_id;
                DELETE FROM items_in_group WHERE item=v_supplier_item_id;
                DELETE FROM contract_items_covered WHERE items_covered = v_supplier_item_id;
                DELETE FROM item WHERE id=v_supplier_item_id;
            END IF;

        END IF;

       --UPDATE TO BE A SUCCESS
       UPDATE STG_PART_SRC_HIST_UPLD
          SET upload_error = NULL,
              upload_status = 'Y',
              upload_date = SYSDATE
        WHERE id = each_rec.id;
      EXCEPTION
         WHEN OTHERS THEN
            --ROLLBACK IMMEDIATLY
            ROLLBACK;
            v_error_code := SUBSTR (SQLERRM, 0, 1000);

            UPDATE STG_PART_SRC_HIST_UPLD
               SET upload_error = v_error_code,
                   upload_status = 'N'
             WHERE id = each_rec.id;
      END;

      v_loop_count := v_loop_count + 1;
      
      IF v_loop_count = 10 THEN
         --DO A COMMIT FOR 10 RECORDS
         COMMIT;
         v_loop_count := 0; -- Initialize the count size
      END IF;

   END LOOP;
   --DO A FINAL COMMIT
   COMMIT;
END;
/
COMMIT
/