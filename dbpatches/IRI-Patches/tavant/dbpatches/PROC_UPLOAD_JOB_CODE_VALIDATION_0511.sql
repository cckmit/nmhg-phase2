--Purpose    : Fixed the job codes upload - TWMS4.1-2705
--Author     : Raghu
--Created On : 11-May-09

create or replace PROCEDURE UPLOAD_JOB_CODE_VALIDATION AS

  CURSOR ALL_REC
  IS
  SELECT *
	FROM STG_JOB_CODE
	WHERE
		 NVL(ERROR_STATUS,'N') = 'N' AND
		 UPLOAD_STATUS IS NULL
		 ORDER BY ID ASC;

	--ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
	v_error_code 		  		        VARCHAR2(4000)	  		 	:=  		 	NULL;
  v_error                       VARCHAR2(4000)	  		 	:=  		 	NULL;
	v_error_flag 		  		        BOOLEAN	  		 	        :=  		 	NULL;
	v_count	          	   		    NUMBER;
	v_item_group_id				        NUMBER;
	v_assem_id					   		    NUMBER;	
  v_file_upload_mgt_id          NUMBER := 0;
  v_success_count               NUMBER := 0;
  v_error_count                 NUMBER := 0;
  v_loop_count                  NUMBER := 0;

BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
    
  			-- ERROR CODE: JC0001
				-- VALIDATE THAT BUSINESS UNIT INFO/NAME IS NOT NULL
				-- REASON FOR ERROR: BUSINESS UNIT INFO/NAME IS NULL
				BEGIN
					 IF EACH_REC.BUSINESS_UNIT_NAME IS NULL 
					 THEN
					 	 v_error_code	 := Common_Utils.addError(v_error_code, 'JC0001');
					 END IF;
        END;
        
				-- ERROR CODE: JC0002
				-- VALIDATE THAT BUSINESS UNIT NAME IS VALID
				-- REASON FOR ERROR: BUSINESS UNIT NAME IS NOT VALID
        IF NOT (COMMON_VALIDATION_UTILS.isvalidbusinessunitname(EACH_REC.BUSINESS_UNIT_NAME)) THEN
        v_error_code := Common_Utils.addError(v_error_code, 'JC0002');
        END IF;

				-- ERROR CODE: JC0003
				-- VALIDATE THAT MODEL IS NOT NULL
				-- REASON FOR ERROR: MODEL IS NULL
				BEGIN
					 IF EACH_REC.FIELD_MODEL IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0003');
					 END IF;
		   	END;

				-- ERROR CODE: JC0004
				-- VALIDATE THAT JOB_CODE IS NOT NULL
				-- REASON FOR ERROR: JOB_CODE IS NULL
				BEGIN
					 IF EACH_REC.JOB_CODE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0004');
					 END IF;
		   	END;

				-- ERROR CODE: JC0005
				-- VALIDATE THAT SERVICE_CAMPAIGNS_ONLY  IS NOT NULL
				-- REASON FOR ERROR: SERVICE_CAMPAIGNS_ONLY IS NULL
				BEGIN
					 IF EACH_REC.FIELD_MODIFICATION_ONLY IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0005');
					 END IF;
		   	END;
	
				-- ERROR CODE: JC0006
				-- VALIDATE THAT MODEL IS VALID
				-- REASON FOR ERROR: MODEL SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS MODEL NUMBER ALREADY EXIST           
           IF NOT (COMMON_VALIDATION_UTILS.isValidModel(EACH_REC.FIELD_MODEL, EACH_REC.BUSINESS_UNIT_NAME)) THEN
            v_error_code := Common_Utils.addError(v_error_code, 'JC0006');
           END IF;
        END;
				
				-- ERROR CODE: JC0007
				-- VALIDATE THAT JOB CODE EXIST ALREADY OR NOT
				-- REASON FOR ERROR: JOB CODE EXIST ALREADY
				BEGIN
					 
					 SELECT ID
					 INTO v_item_group_id
					 FROM ITEM_GROUP
					 WHERE
						   ITEM_GROUP_TYPE = 'MODEL' AND
					 	   UPPER(GROUP_CODE) = UPPER(EACH_REC.FIELD_MODEL) AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_NAME;
               
				--CHECK IF JOB CODE EXIST ALREADY
					SELECT 1 
					INTO v_assem_id
					FROM dual WHERE EXISTS(
					SELECT AN.DEFINED_FOR
										 FROM 
										 	  SERVICE_PROCEDURE_DEFINITION SPD,
											  SERVICE_PROCEDURE SP,
											  ACTION_NODE AN
											  WHERE
										 	  SPD.CODE = EACH_REC.JOB_CODE 
											  AND SPD.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_NAME
											  AND SP.DEFINITION = SPD.ID AND
											  SP.DEFINED_FOR = AN.ID
					INTERSECT
					SELECT ID FROM ASSEMBLY CONNECT BY PRIOR ID = is_part_of_assembly START WITH ID IN (
					SELECT assemblies FROM FAILURE_STRUCTURE_ASSEMBLIES A, FAILURE_STRUCTURE B 
          WHERE a.FAILURE_STRUCTURE = b.ID AND b.for_item_group =  v_item_group_id ));
				
						  v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0007');	  
				EXCEPTION
				WHEN NO_DATA_FOUND
				THEN
					NULL; --DO NOTHING
				END;
				
				-- ERROR CODE: JC0008
				-- VALIDATE THAT STANDARD LABOR TIME IN HOURS AND MINS IS NOT '0'
				-- REASON FOR ERROR: STANDARD LABOR TIME SHOULD BE VALID
				BEGIN
					 IF NVL(EACH_REC.LABOR_STANDARD_HOURS,0) = 0 AND 
						NVL(EACH_REC.LABOR_STANDARD_MINUTES,0) = 0
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0008');
					 END IF;
		   		END;

				--ERROR CODE: JC0009
				--VALIDATE THAT STANDARD LABOR TIME IN HOURS BETWEEN 0 AND 98
				--REASON : STANDARD LABOR TIME SHOULD BE VALID
				BEGIN
					 IF NVL(EACH_REC.LABOR_STANDARD_HOURS,0) < 0 AND
						NVL(EACH_REC.LABOR_STANDARD_HOURS,0) > 99
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0009');
					 END IF;
		   		END;
				
				-- ERROR CODE: JC0010
				-- VALIDATE THAT JOB IS VALID
				-- REASON FOR ERROR: JOB SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS CODE ALREADY EXIST
           IF COMMON_VALIDATION_UTILS.isValidJobCode(EACH_REC.JOB_CODE, EACH_REC.FIELD_MODEL, EACH_REC.BUSINESS_UNIT_NAME) THEN
            v_error_code := Common_Utils.addError(v_error_code, 'JC0010');
           END IF;
        END;
				
				-- ERROR CODE: JC0011
				-- VALIDATE THAT PRODUCT CODE IS NOT NULL
				-- REASON FOR ERROR: PRODUCT CODE IS NULL
				BEGIN
					 IF EACH_REC.PRODUCT_CODE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0011');
					 END IF;
		   	END;

				-- ERROR CODE: JC0012
				-- VALIDATE THAT ACTION IS NOT NULL
				-- REASON FOR ERROR: ACTION IS NULL
				BEGIN
					 IF EACH_REC.ACTION IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0012');
					 END IF;
		   	END;

				-- ERROR CODE: JC0013
				-- VALIDATE THAT REPEATABLE IS NOT NULL
				-- REASON FOR ERROR: REPEATABLE IS NULL
				BEGIN
					 IF EACH_REC.REPEATABLE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'JC0013');
					 END IF;
		   	END;

		   --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
		   IF v_error_code IS NULL
		   THEN
		   	   --RECORD IS CLEAN AND IS SUCCESSFULLY VALIDATED
		   	   UPDATE STG_JOB_CODE
			   SET
			   	  ERROR_STATUS = 'Y',
				  ERROR_CODE = NULL
				WHERE
				  ID = EACH_REC.ID;
		   ELSE
		   	   --RECORD HAS ERRORS
		   	   	UPDATE STG_JOB_CODE
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
    FROM STG_JOB_CODE 
    WHERE ROWNUM < 2;
    
    -- Success Count
    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_JOB_CODE 
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
      FROM STG_JOB_CODE 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N'
      group by file_upload_mgt_id;
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*)
    INTO v_count
    FROM STG_JOB_CODE 
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
  
END UPLOAD_JOB_CODE_VALIDATION;
/
COMMIT
/