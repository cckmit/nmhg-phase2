--Purpose    : Used to validate copy job code upload data
--Author     : Jhulfikar Ali. A
--Created On : 15-Jan-08

CREATE OR REPLACE
PROCEDURE UPLOAD_COPY_JC_FF_VALIDATION AS

  CURSOR ALL_REC
  IS
  SELECT *
	FROM STG_COPY_JOB_CODE_FF
	WHERE
		 NVL(ERROR_STATUS,'N') = 'N' AND
		 UPLOAD_STATUS IS NULL
		 ORDER BY ID ASC;

	--ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
	v_error_code 		  		        VARCHAR2(4000)	  		 	:=  		 	NULL;
	v_error_flag 		  		        BOOLEAN	  		 	        :=  		 	NULL;
  v_error                       VARCHAR2(4000)	  		 	:=  		 	NULL;
	v_count	          	   		    NUMBER :=	0;
  v_file_upload_mgt_id          NUMBER := 0;
  v_success_count               NUMBER := 0;
  v_error_count                 NUMBER := 0;
  v_loop_count                  NUMBER := 0;

BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
    
  			-- ERROR CODE: CJ0001
				-- VALIDATE THAT BUSINESS UNIT INFO/NAME IS NOT NULL
				-- REASON FOR ERROR: BUSINESS UNIT INFO/NAME IS NULL
				BEGIN
					 IF EACH_REC.BUSINESS_UNIT_NAME IS NULL 
					 THEN
					 	 v_error_code	 := Common_Utils.addError(v_error_code, 'CJ0001');
					 END IF;
        END;
        
				-- ERROR CODE: CJ0002
				-- VALIDATE THAT BUSINESS UNIT NAME IS VALID
				-- REASON FOR ERROR: BUSINESS UNIT NAME IS NOT VALID
        v_error_flag := COMMON_VALIDATION_UTILS.isvalidbusinessunitname(EACH_REC.BUSINESS_UNIT_NAME);
        IF (NOT v_error_flag)
        THEN
        v_error_code := Common_Utils.addError(v_error_code, 'CJ0002');
        END IF;

				-- ERROR CODE: CJ0003
				-- VALIDATE THAT FROM MODEL IS NOT NULL
				-- REASON FOR ERROR: FROM MODEL IS NULL
				BEGIN
					 IF EACH_REC.FROM_MODEL_NUMBER IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0003');
					 END IF;
		   	END;

				-- ERROR CODE: CJ0004
				-- VALIDATE THAT TO MODEL IS NOT NULL
				-- REASON FOR ERROR: TO MODEL IS NULL
				BEGIN
					 IF EACH_REC.TO_MODEL_NUMBER IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'CJ0004');
					 END IF;
		   	END;

				-- ERROR CODE: CJ0005
				-- VALIDATE THAT FROM PRODUCT CODE IS NOT NULL
				-- REASON FOR ERROR: FROM PRODUCT CODE IS NULL
				BEGIN
					 IF EACH_REC.FROM_PRODUCT_CODE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'CJ0005');
					 END IF;
		   	END;
	
				-- ERROR CODE: CJ0006
				-- VALIDATE THAT TO PRODUCT CODE IS NOT NULL
				-- REASON FOR ERROR: TO PRODUCT CODE IS NULL
				BEGIN
					 IF EACH_REC.TO_PRODUCT_CODE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'CJ0006');
					 END IF;
		   	END;
		   	
				-- ERROR CODE: CJ0007
				-- VALIDATE THAT FROM MODEL IS VALID
				-- REASON FOR ERROR: FROM MODEL SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS FROM MODEL NUMBER NOT EXIST
           v_error_flag := COMMON_VALIDATION_UTILS.isValidModel(EACH_REC.FROM_MODEL_NUMBER, EACH_REC.BUSINESS_UNIT_NAME);
           IF NOT (v_error_flag)
           THEN
            v_error_code := Common_Utils.addError(v_error_code, 'CJ0007');
           END IF;
        END;
				
				-- ERROR CODE: CJ0008
				-- VALIDATE THAT TO MODEL IS VALID
				-- REASON FOR ERROR: TO MODEL SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS FROM MODEL NUMBER NOT EXIST
           v_error_flag := COMMON_VALIDATION_UTILS.isValidModel(EACH_REC.TO_MODEL_NUMBER, EACH_REC.BUSINESS_UNIT_NAME);
           IF NOT (v_error_flag)
           THEN
            v_error_code := Common_Utils.addError(v_error_code, 'CJ0008');
           END IF;
        END;
        
				-- ERROR CODE: CJ0009
				-- VALIDATE THAT FROM PRODUCT CODE IS VALID
				-- REASON FOR ERROR: FROM PRODUCT CODE SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS FROM PRODUCT CODE NOT EXIST
           v_error_flag := COMMON_VALIDATION_UTILS.isValidProductCode(EACH_REC.FROM_PRODUCT_CODE, EACH_REC.BUSINESS_UNIT_NAME);
           IF (v_error_flag)
           THEN
            v_error_code := Common_Utils.addError(v_error_code, 'CJ0009');
           END IF;
        END;

				-- ERROR CODE: CJ0010
				-- VALIDATE THAT TO PRODUCT CODE IS VALID
				-- REASON FOR ERROR: TO PRODUCT CODE SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS TO PRODUCT CODE NOT EXIST
           v_error_flag := COMMON_VALIDATION_UTILS.isValidProductCode(EACH_REC.TO_PRODUCT_CODE, EACH_REC.BUSINESS_UNIT_NAME);
           IF (v_error_flag)
           THEN
            v_error_code := Common_Utils.addError(v_error_code, 'CJ0010');
           END IF;
        END;
        
  			-- ERROR CODE: CJ0011
				-- VALIDATE THAT COPY OF JOB CODE/FAULT FOUND IS NOT NULL
				-- REASON FOR ERROR: BUSINESS UNIT INFO/NAME IS NULL
				BEGIN
					 IF EACH_REC.COPY IS NULL 
					 THEN
					 	 v_error_code	 := Common_Utils.addError(v_error_code, 'CJ0011');
					 END IF;
        END;
        
  			-- ERROR CODE: CJ0012
				-- VALIDATE THAT COPY OF JOB CODE/FAULT FOUND IS AN ALLOWED ACTION
				-- REASON FOR ERROR: BUSINESS UNIT INFO/NAME IS NOT AN ALLOWED ACTION
				BEGIN
					 IF UPPER(EACH_REC.COPY) NOT IN ('FF', 'JC')
					 THEN
					 	 v_error_code	 := Common_Utils.addError(v_error_code, 'CJ0012');
					 END IF;
        END;
        
		   --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
		   IF v_error_code IS NULL
		   THEN
		   	   --RECORD IS CLEAN AND IS SUCCESSFULLY VALIDATED
		   	   UPDATE STG_COPY_JOB_CODE_FF
			   SET
			   	  ERROR_STATUS = 'Y',
				  ERROR_CODE = NULL
				WHERE
				  ID = EACH_REC.ID;
		   ELSE
		   	   --RECORD HAS ERRORS
		   	   	UPDATE STG_COPY_JOB_CODE_FF
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
    FROM STG_COPY_JOB_CODE_FF 
    WHERE ROWNUM < 2;
    
    -- Success Count
    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_COPY_JOB_CODE_FF 
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
      FROM STG_COPY_JOB_CODE_FF 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N'
      group by file_upload_mgt_id;
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*)
    INTO v_count
    FROM STG_COPY_JOB_CODE_FF 
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

END UPLOAD_COPY_JC_FF_VALIDATION;
/
COMMIT
/