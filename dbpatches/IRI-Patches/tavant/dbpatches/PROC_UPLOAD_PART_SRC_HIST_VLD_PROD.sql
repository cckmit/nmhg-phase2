-- Purpose    : Procedure to upload the data of Part Source History
-- Author     : Jhulfikar Ali. A
-- Created On : 11-Feb-09

CREATE OR REPLACE
PROCEDURE UPLOAD_PART_SRC_HIST_VLD AS
   CURSOR ALL_REC IS
      SELECT *
      FROM STG_PART_SRC_HIST_UPLD
	WHERE
		 NVL(ERROR_STATUS,'N') = 'N' AND
		 UPLOAD_STATUS IS NULL
     ORDER BY FILE_UPLOAD_MGT_ID;

  --ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
  v_error_code 		              VARCHAR2(4000)	:=  NULL;
  v_error                       VARCHAR2(4000)	:=  NULL;
  v_error_flag 		              BOOLEAN	  	    :=  NULL;
  v_count	                      NUMBER	        :=	0;
  v_file_upload_mgt_id          NUMBER          :=  0;
  v_success_count               NUMBER          :=  0;
  v_error_count                 NUMBER          :=  0;
  v_loop_count	                NUMBER	        :=	0;

--MAIN LOOP OF THE PROCEDURE WHICH WILL LOOP AND VALIDATE RECORDS
BEGIN
   FOR EACH_REC IN ALL_REC LOOP
      --MAIN BEGIN LOOP
      BEGIN
	  
  			-- ERROR CODE: PS0001
				-- VALIDATE THAT BUSINESS UNIT INFO/NAME IS NOT NULL
				-- REASON FOR ERROR: BUSINESS UNIT INFO/NAME IS NULL
				BEGIN
					 IF EACH_REC.BUSINESS_UNIT_NAME IS NULL 
					 THEN
					 	 v_error_code	 := COMMON_UTILS.addError(v_error_code, 'PS0001');
					 END IF;
        END;
        
				-- ERROR CODE: PS0002
				-- VALIDATE THAT BUSINESS UNIT NAME IS VALID
				-- REASON FOR ERROR: BUSINESS UNIT NAME IS NOT VALID
        v_error_flag := COMMON_VALIDATION_UTILS.isValidBusinessUnitName(EACH_REC.BUSINESS_UNIT_NAME);
        IF NOT (v_error_flag)
        THEN
        v_error_code := COMMON_UTILS.addError(v_error_code, 'PS0002');
        END IF;

  			-- ERROR CODE: PS0003
				-- VALIDATE THAT Item Number IS NOT NULL
				-- REASON FOR ERROR: Item Number IS NULL
				BEGIN
					 IF EACH_REC.Item_Number IS NULL 
					 THEN
					 	 v_error_code	 := COMMON_UTILS.addError(v_error_code, 'PS0003');
					 END IF;
        END;

  			-- ERROR CODE: PS0004
				-- VALIDATE THAT Supplier Name IS NOT NULL
				-- REASON FOR ERROR: Supplier Name IS NULL
				BEGIN
					 IF EACH_REC.Supplier_Name IS NULL 
					 THEN
					 	 v_error_code	 := COMMON_UTILS.addError(v_error_code, 'PS0004');
					 END IF;
        END;

  			-- ERROR CODE: PS0005
				-- VALIDATE THAT Item Number IS NOT VALID
				-- REASON FOR ERROR: Item Number IS NOT VALID
        v_error_flag := COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.Item_Number, EACH_REC.BUSINESS_UNIT_NAME);
        IF (v_error_flag)
        THEN
        v_error_code := COMMON_UTILS.addError(v_error_code, 'PS0005');
        END IF;

  			-- ERROR CODE: PS0006
				-- VALIDATE THAT Supplier Name IS NOT VALID
				-- REASON FOR ERROR: Supplier Name IS NOT VALID
        v_error_flag := COMMON_VALIDATION_UTILS.isValidSupplier(EACH_REC.Supplier_Name, EACH_REC.BUSINESS_UNIT_NAME);
        IF (v_error_flag)
        THEN
        v_error_code := COMMON_UTILS.addError(v_error_code, 'PS0006');
        END IF;

  			-- ERROR CODE: PS0007
				-- VALIDATE THAT FROM DATE IS NOT NULL
				-- REASON FOR ERROR: FROM DATE IS NULL
				BEGIN
					 IF EACH_REC.FROM_DATE IS NULL 
					 THEN
					 	 v_error_code	 := COMMON_UTILS.addError(v_error_code, 'PS0007');
					 END IF;
        END;

  			-- ERROR CODE: PS0008
				-- VALIDATE THAT TO DATE IS NOT NULL
				-- REASON FOR ERROR: TO DATE IS NULL
				BEGIN
					 IF EACH_REC.TO_DATE IS NULL 
					 THEN
					 	 v_error_code	 := COMMON_UTILS.addError(v_error_code, 'PS0008');
					 END IF;
        END;
        
		   --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
		   IF v_error_code IS NULL
		   THEN
		   	 --RECORD IS CLEAN AND IS SUCCESSFULLY VALIDATED
		   	 UPDATE STG_PART_SRC_HIST_UPLD
			   SET
			   	  ERROR_STATUS = 'Y',
            ERROR_CODE = NULL
				WHERE
				  ID = EACH_REC.ID;
		   ELSE
		   	 --RECORD HAS ERRORS
         UPDATE STG_PART_SRC_HIST_UPLD
			   SET
			   	  ERROR_STATUS = 'N',
			   	  ERROR_CODE = v_error_code
				WHERE
				  ID = EACH_REC.ID;
		   END IF;
	  
      v_loop_count := v_loop_count + 1;
      
      IF v_loop_count = 10 THEN
         --DO A COMMIT FOR 10 RECORDS
         COMMIT;
         v_loop_count := 0; -- Initialize the count size
      END IF;

      END;

   END LOOP;

  BEGIN
    -- Update the status of validation
    
    -- In a given time there will be only one file for a given upload
    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_PART_SRC_HIST_UPLD 
    WHERE ROWNUM < 2;
    
    -- Success Count
    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_PART_SRC_HIST_UPLD 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y'
      group by file_upload_mgt_id;
    EXCEPTION
    WHEN OTHERS THEN
      v_success_count := 0;
    END;
    
    -- Error Count
    BEGIN
      SELECT count(*)
      INTO v_error_count
      FROM STG_PART_SRC_HIST_UPLD 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N'
      group by file_upload_mgt_id;
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*)
    INTO v_count
    FROM STG_PART_SRC_HIST_UPLD 
    where file_upload_mgt_id = v_file_upload_mgt_id 
    group by file_upload_mgt_id;
    
    UPDATE FILE_UPLOAD_MGT 
    SET 
      SUCCESS_RECORDS= v_success_count, 
      ERROR_RECORDS= v_error_count,
      TOTAL_RECORDS = v_count
    WHERE ID = v_file_upload_mgt_id;
        
  EXCEPTION
  WHEN OTHERS THEN
    -- Capture the error code into the table
    v_error := SUBSTR(SQLERRM, 1, 4000);
    UPDATE FILE_UPLOAD_MGT 
    SET 
      ERROR_MESSAGE = v_error
    WHERE ID = v_file_upload_mgt_id;
    
  END;

  COMMIT; -- Final Commit for the procedure

END;
/
COMMIT
/