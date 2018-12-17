-- Purpose    : Used to validate install base data
-- Author     : Jhulfikar Ali. A
-- Created On : 14-Jan-08

CREATE OR REPLACE
PROCEDURE UPLOAD_INSTALL_BASE_VALIDATION AS
  CURSOR ALL_REC
  IS
  	SELECT *
	FROM STG_INSTALL_BASE
	WHERE
		 NVL(ERROR_STATUS,'N') = 'N' AND
		 UPLOAD_STATUS IS NULL
		 ORDER BY ID ASC;

	--ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
	v_error_code 		  		        VARCHAR2(4000)	  		 	:=  		 	NULL;
  v_error                       VARCHAR2(4000)	  		 	:=  		 	NULL;
	v_count	          	   		    VARCHAR2(1)	   			 	  :=		  	NULL;
  v_error_flag                  BOOLEAN                 :=        NULL;
  v_file_upload_mgt_id          NUMBER := 0;
  v_success_count               NUMBER := 0;
  v_error_count                 NUMBER := 0;
  v_loop_count                  NUMBER := 0;

  BEGIN
  
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      --RESET THE VARIABLE
	   			v_error_code := NULL;
          
        -- ERROR CODE: IB0001
				-- VALIDATE THAT SERIAL NUMBER IS NOT NULL
				-- REASON FOR ERROR: SERIAL NUMBER IS NULL
				BEGIN
					 IF EACH_REC.SERIAL_NUMBER IS NULL
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0001');
					 END IF;
		   		END;

				-- ERROR CODE: IB0002
				-- VALIDATE THAT ITEM NUMBER IS NOT NULL
				-- REASON FOR ERROR: ITEM NUMBER IS NULL
				BEGIN
					 IF EACH_REC.ITEM_NUMBER IS NULL
					 THEN
					 	 v_error_code	 := Common_Utils.addError(v_error_code, 'IB0002');
					 END IF;
		   		END;

				-- ERROR CODE: IB0016
				-- VALIDATE THAT BUSINESS UNIT INFO/NAME IS NOT NULL
				-- REASON FOR ERROR: BUSINESS UNIT INFO/NAME IS NULL
				BEGIN
					 IF EACH_REC.BUSINESS_UNIT_NAME IS NULL 
					 THEN
					 	 v_error_code	 := Common_Utils.addError(v_error_code, 'IB0016');
					 END IF;
        END;
          
        -- ERROR CODE: IB0003
				-- VALIDATE THAT SERIAL NUMBER IS NOT PRESENT ALREADY IN BASE TABLE
				-- REASON FOR ERROR: SERIAL NUMBER SHOULD NOT BE PRESENT ALREADY IN BASE TABLE
				BEGIN
					SELECT 1
					INTO v_count
					FROM INVENTORY_ITEM
					WHERE
						SERIAL_NUMBER = EACH_REC.SERIAL_NUMBER AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_NAME;
					
					v_error_code := Common_Utils.addError(v_error_code, 'IB0003');
				EXCEPTION
				WHEN NO_DATA_FOUND
				THEN
					NULL; --DO NOTHING SINCE SERIAL NUMBER IS NOT EXIST YET
				END;

				-- ERROR CODE: IB0004
				-- VALIDATE THAT TYPE IS NOT NULL OR NOT A ALLOWED TYPE ('STOCK' OR 'RETAIL')
				-- REASON FOR ERROR: TYPE IS NULL OR NOT A ALLOWED TYPE ('STOCK' OR 'RETAIL')
				BEGIN
					IF (EACH_REC.STOCK_OR_RETAIL IS NULL) OR (EACH_REC.STOCK_OR_RETAIL NOT IN ('STOCK','RETAIL'))
					THEN
						v_error_code := Common_Utils.addError(v_error_code, 'IB0004');
					END IF;
		   		END;


				-- ERROR CODE: IB0005
				-- VALIDATE THAT CONDITION IS NOT NULL OR NOT A ALLOWED CONDITIONS ('NEW' OR 'REFURBISHED')
				-- REASON FOR ERROR: CONDITION IS NULL OR NOT A ALLOWED CONDITIONS ('NEW' OR 'REFURBISHED')
				BEGIN
					 IF (EACH_REC.INVENTORY_ITEM_TYPE IS NULL) OR (EACH_REC.INVENTORY_ITEM_TYPE NOT IN ('NEW','REFURBISHED'))
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0005');
					 END IF;
		   		END;

				-- ERROR CODE: IB0006
				-- VALIDATE THAT HOURS IN SERVICE IS NOT NULL AND VALID POSITIVE NUMBER
				-- REASON FOR ERROR: HOURS ON SERVICE IS NULL AND VALID POSITIVE NUMBER
				BEGIN
					IF EACH_REC.HOURS_IN_SERVICE IS NULL OR NOT (Common_Utils.isPositiveInteger(EACH_REC.HOURS_IN_SERVICE))
					THEN
						v_error_code := Common_Utils.addError(v_error_code, 'IB0006');
					END IF;
		   		END;

				--ERROR CODE: IB0007
				--VALIDATE THAT BUILD DATE IS NOT NULL
				-- REASON FOR ERROR: BUILD DATE IS NULL
				BEGIN
					 IF EACH_REC.MACHINE_BUILD_DATE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0007');
					 END IF;
		   		END;

				-- ERROR CODE: IB0008
				-- VALIDATE THAT OWNER DEALER NUMBER IS NOT NULL
				-- REASON FOR ERROR: OWNER DEALER NAME IS NULL
				BEGIN
					 IF EACH_REC.DEALER_NUMBER IS NULL
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0008');
					 END IF;
		   		END;

				-- ERROR CODE: IB0009
				-- VALIDATE THAT OWNER END CUSTOMER IS NOT NULL IF THE TYPE IS 'RETAIL'
				-- REASON FOR ERROR: OWNER END CUSTOMER IS NULL WHEN THE TYPE IS 'RETAIL'
				BEGIN
					 IF EACH_REC.STOCK_OR_RETAIL = 'RETAIL'
					 THEN
						 IF EACH_REC.END_CUSTOMER_NAME IS NULL
						 THEN
							 v_error_code := Common_Utils.addError(v_error_code, 'IB0009');
						 END IF;
					 END IF;
		   		END;

				-- ERROR CODE: IB0010
				-- VALIDATE THAT SHIPMENT DATE IS NOT NULL
				-- REASON FOR ERROR: SHIPMENT DATE IS NULL
				BEGIN
					 IF EACH_REC.SHIPMENT_DATE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0010');
					 END IF;
		   		END;
				
				-- ERROR CODE: IB0011
				-- VALIDATE THAT BUILD DATE IS VALID
				-- REASON FOR ERROR: BUILD DATE SHOULD BE IN VALID FORMAT
				BEGIN
					 IF NOT Common_Utils.isValidDate(EACH_REC.MACHINE_BUILD_DATE)
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0011');
					 END IF;
		   		END;

				-- ERROR CODE: IB0012
				-- VALIDATE THAT ITEM NUMBER IS VALID
				-- REASON FOR ERROR: ITEM NUMBER SHOULD BE VALID
				BEGIN
					 --CHECK IN BASE TABLE IF THIS ITEM NUMBER ALREADY EXIST
           v_error_flag := COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.ITEM_NUMBER, EACH_REC.BUSINESS_UNIT_NAME);
           IF (NOT v_error_flag)
           THEN
              v_error_code := Common_Utils.addError(v_error_code, 'IB0012');
           END IF;
        END;

				-- ERROR CODE: IB0013
				-- VALIDATE THAT DEALER NUMBER IS VALID
				-- REASON FOR ERROR: DEALER NUMBER SHOULD BE VALID
				BEGIN
					SELECT 1
					INTO v_count
					FROM DEALERSHIP
					WHERE
						DEALER_NUMBER = EACH_REC.DEALER_NUMBER;
				EXCEPTION
				WHEN NO_DATA_FOUND
				THEN				
					v_error_code := Common_Utils.addError(v_error_code, 'IB0013');
				END;

				-- ERROR CODE: IB0014
				-- VALIDATE THAT SHIPMENT DATE IS VALID
				-- REASON FOR ERROR: SHIPMENT DATE SHOULD BE IN VALID FORMAT
				BEGIN
					 IF NOT Common_Utils.isValidDate(EACH_REC.SHIPMENT_DATE)
					 THEN
					 	 v_error_code := Common_Utils.addError(v_error_code, 'IB0014');
					 END IF;
		   		END;
				
				-- ERROR CODE: IB0015
				-- VALIDATE THAT OWNER END CUSTOMER SHOULD BE A VALID END CUSTOMER
				-- REASON FOR ERROR: OWNER END CUSTOMER SHOULD BE A VALID END CUSTOMER
				BEGIN
					IF EACH_REC.STOCK_OR_RETAIL = 'RETAIL'
					THEN
            BEGIN
              SELECT 1
              INTO v_count
              FROM CUSTOMER
              WHERE
                COMPANY_NAME = EACH_REC.END_CUSTOMER_NAME;
            EXCEPTION
              WHEN NO_DATA_FOUND
              THEN				
                v_error_code := Common_Utils.addError(v_error_code, 'IB0015');
            END;
					END IF;
				END;
        
				-- ERROR CODE: IB0017
				-- VALIDATE THAT BUSINESS UNIT NAME IS VALID
				-- REASON FOR ERROR: BUSINESS UNIT NAME IS NOT VALID
        v_error_flag := COMMON_VALIDATION_UTILS.isvalidbusinessunitname(EACH_REC.BUSINESS_UNIT_NAME);
        IF (NOT v_error_flag)
        THEN
        v_error_code := Common_Utils.addError(v_error_code, 'IB0017');
        END IF;

				-- ERROR CODE: IB0018
				-- VALIDATE THAT COUNTRY IS VALID
				-- REASON FOR ERROR: COUNTRY IS NOT VALID
        BEGIN
          SELECT 1
          INTO v_count
          FROM COUNTRY
          WHERE
            lower(code) = lower(EACH_REC.COUNTRY);
        EXCEPTION
          WHEN NO_DATA_FOUND
          THEN				
            v_error_code := Common_Utils.addError(v_error_code, 'IB0018');
        END;
				
				-- ERROR CODE: IB0019
				-- VALIDATE THAT ITEM IS VALID FOR GIVEN BUSINESS UNIT
				-- REASON FOR ERROR: ITEM IS NOT VALID FOR GIVEN BUSINESS UNIT
        v_error_flag := COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.Item_Number, EACH_REC.BUSINESS_UNIT_NAME);
        IF NOT (v_error_flag)
        THEN
        v_error_code := COMMON_UTILS.addError(v_error_code, 'IB0019');
        END IF;

				-- ERROR CODE: IB0020
				-- VALIDATE THAT ITEM IS VALID SERIAL NUMBER
				-- REASON FOR ERROR: ITEM IS NOT VALID SERIAL NUMBER
        -- BEGIN
        --    IF EACH_REC.SERIAL_NUMBER IS NOT NULL 
        --    THEN
        --      v_error_code := Common_Utils.addError(v_error_code, 'IB0020');
        --    END IF;
        -- END;
				
		   --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
		   IF v_error_code IS NULL
		   THEN
		   	   --RECORD IS CLEAN AND IS SUCCESSFULLY VALIDATED
		   	   UPDATE STG_INSTALL_BASE
			   SET
			   	  ERROR_STATUS = 'Y',
				    ERROR_CODE = NULL
				WHERE
				  ID = EACH_REC.ID;
		   ELSE
		   	   --RECORD HAS ERRORS
		   	   	UPDATE STG_INSTALL_BASE
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
    FROM STG_INSTALL_BASE 
    WHERE ROWNUM < 2;
    
    -- Success Count
    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_INSTALL_BASE 
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
      FROM STG_INSTALL_BASE 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N'
      group by file_upload_mgt_id;
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*)
    INTO v_count
    FROM STG_INSTALL_BASE 
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

  END UPLOAD_INSTALL_BASE_VALIDATION;
/
COMMIT
/