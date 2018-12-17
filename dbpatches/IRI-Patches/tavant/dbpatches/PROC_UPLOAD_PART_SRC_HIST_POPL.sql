-- Purpose    : Procedure to upload the data of Part Source History
-- Author     : Jhulfikar Ali. A
-- Created On : 11-Feb-09

CREATE OR REPLACE
PROCEDURE UPLOAD_PART_SRC_HIST_POPL AS
   CURSOR ALL_REC IS
      SELECT *
      FROM STG_PART_SRC_HIST_UPLD
      WHERE NVL (error_status, 'N') = 'Y' AND NVL (upload_status, 'N') = 'N';

   --ALL THE GLOBAL VARIABLE FOR THE PROCEDURE ARE DEFINED HERE
   v_error_code           VARCHAR2 (1100) := NULL;
   v_item_number          VARCHAR2 (255)  := NULL;
   v_item_number_hash     VARCHAR2 (255)  := NULL;
   v_loop_count           NUMBER          := 0;
   v_item_number_index    NUMBER;
   v_supp_item_id         NUMBER;
   v_supp_id	            NUMBER;
   v_error_count          NUMBER;
   v_file_upload_mgt_id   NUMBER;

--MAIN LOOP OF THE PROCEDURE WHICH WILL LOOP AND INSERT RECORDS
BEGIN
   FOR EACH_REC IN ALL_REC LOOP
      --Initialize the values
	v_error_count   := 0;

      --MAIN BEGIN LOOP
     BEGIN
	  
       --GET THE ITEM ID FOR THE SUPPLIER ITEM FROM THE ITEM TABLE
       SELECT i.ID
       INTO   v_supp_item_id
       FROM item i, supplier s, party p, bu_org_mapping bom
       WHERE (i.item_number = EACH_REC.ITEM_NUMBER
              OR i.alternate_item_number = EACH_REC.ITEM_NUMBER)
       AND i.owned_by = s.ID
       AND p.name = EACH_REC.SUPPLIER_NAME
       AND bom.org = s.id
       AND bom.bu = EACH_REC.BUSINESS_UNIT_NAME
       AND s.id = p.id 
       AND ROWNUM = 1;
  
       SELECT p.id
       INTO   v_supp_id
       FROM supplier s, party p, bu_org_mapping bom
       WHERE p.name = EACH_REC.SUPPLIER_NAME 
       AND s.id = p.id 
       AND bom.org = s.id
       AND bom.bu = EACH_REC.BUSINESS_UNIT_NAME
       AND ROWNUM = 1;
  
       -- UPDATE THE RECORD IN STG_PART_SRC_HIST_UPLD TABLE
       UPDATE STG_PART_SRC_HIST_UPLD
       SET supplier_item_number = to_char(v_supp_item_id), 
            supplier_number = to_char(v_supp_id)
       WHERE id = EACH_REC.ID;

      EXCEPTION
         WHEN OTHERS THEN
            --ROLLBACK IMMEDIATLY
            ROLLBACK;
            v_error_code := SUBSTR (SQLERRM, 0, 1000);

            UPDATE STG_PART_SRC_HIST_UPLD
               SET upload_error = v_error_code,
                   upload_status = 'N'
             WHERE ID = EACH_REC.id;
          v_error_count := v_error_count + 1;
      END;

      v_loop_count := v_loop_count + 1;
      
      IF v_loop_count = 10 THEN
         --DO A COMMIT FOR 10 RECORDS
         COMMIT;
         v_loop_count := 0; -- Initialize the count size
      END IF;

   END LOOP;

    -- In a given time there will be only one file for a given upload
    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_PART_SRC_HIST_UPLD 
    WHERE ROWNUM < 2;
    
    UPDATE FILE_UPLOAD_MGT 
    SET 
      SUCCESS_RECORDS = SUCCESS_RECORDS - v_error_count, 
      ERROR_RECORDS = ERROR_RECORDS + v_error_count
    WHERE ID = v_file_upload_mgt_id;
        
    COMMIT; -- Final Commit for the procedure

END;
/
COMMIT
/