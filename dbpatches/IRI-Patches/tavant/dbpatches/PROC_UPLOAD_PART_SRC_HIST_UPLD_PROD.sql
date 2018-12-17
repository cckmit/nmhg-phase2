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

--MAIN LOOP OF THE PROCEDURE WHICH WILL LOOP AND INSERT RECORDS
BEGIN
   FOR EACH_REC IN ALL_REC LOOP
      --MAIN BEGIN LOOP
      BEGIN
	  
       --GET THE ITEM NUMBER FOR THE OEM ITEM FROM ITEM TABLE
       SELECT i.ID
       INTO   v_oem_item_id
       FROM item i, party p
       WHERE i.item_number = EACH_REC.ITEM_NUMBER
       AND i.owned_by = p.ID
       AND p.NAME = COMMON_UTILS.CONSTANT_OEM_NAME;

       --GET THE ID FROM THE SEQUENCE
       SELECT item_mapping_seq.NEXTVAL
       INTO   v_item_mapping_id
       FROM DUAL;

       --INSERT THE RECORD IN ITEM MAPPING TABLE
       INSERT INTO ITEM_MAPPING
         (ID, VERSION, to_item, from_item,
         from_date, TO_DATE, d_internal_comments, 
         d_created_on, d_updated_on, d_active)
       VALUES (v_item_mapping_id, 1, to_number(EACH_REC.SUPPLIER_ITEM_NUMBER), 
               v_oem_item_id, TO_DATE (EACH_REC.FROM_DATE, 'YYYYMMDD'),
               TO_DATE (EACH_REC.TO_DATE, 'YYYYMMDD'), 'System: Through User upload', 
               SYSDATE, SYSDATE, 1);

       --UPDATE TO BE A SUCCESS
       UPDATE STG_PART_SRC_HIST_UPLD
          SET upload_error = NULL,
              upload_status = 'Y',
              upload_date = SYSDATE,
              error_status = NULL
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