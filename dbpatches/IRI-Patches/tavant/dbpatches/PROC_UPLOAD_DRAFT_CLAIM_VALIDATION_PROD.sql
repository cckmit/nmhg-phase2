--Purpose    : Used to validate draft claim upload data
--Author     : Jhulfikar Ali. A
--Created On : 06-Mar-08

CREATE OR REPLACE PROCEDURE UPLOAD_DRAFT_CLAIM_VALIDATION AS
CURSOR ALL_REC IS
	SELECT * FROM STG_DRAFT_CLAIM
	WHERE NVL(ERROR_STATUS,'N') = 'N' AND
		 UPLOAD_STATUS IS NULL
		 ORDER BY ID ASC;
		 
	v_loop_count            NUMBER         := 0;
  v_success_count         NUMBER         := 0;
  v_error_count           NUMBER         := 0;
  v_count                 NUMBER         := 0;
  v_file_upload_mgt_id    NUMBER         := 0;
  v_number_temp           NUMBER         := 0;
  isFaultFoundValid       BOOLEAN        := FALSE;
  v_error                 VARCHAR2(4000) := NULL;
  v_error_code            VARCHAR2(4000) := NULL;
  v_model                 VARCHAR2(4000) := NULL;
	
BEGIN
  
  FOR EACH_REC IN ALL_REC
  LOOP
	
  v_error_code := '';
	-- ERROR CODE: DC0001_BU
	-- VALIDATE THAT BUSINESS UNIT NAME IS NOT NULL AND BUSINESS UNIT NAME IS AN ALLOWED ONE IN TWMS
	-- REASON FOR ERROR: DATE OF FAILURE IS NULL
	BEGIN
		 IF EACH_REC.BUSINESS_UNIT_NAME IS NULL OR 
     NOT (COMMON_VALIDATION_UTILS.isValidBusinessUnitName(EACH_REC.BUSINESS_UNIT_NAME))
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Business Unit Name');
		 END IF;
	END;

	-- ERROR CODE: DC0002_UI
	-- VALIDATE THAT UNIQUE IDENTIFIER IS NULL
	-- REASON FOR ERROR: UNIQUE IDENTIFIER IS NULL
	BEGIN
		 IF EACH_REC.UNIQUE_IDENTIFIER IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Unique Identifier');
		 END IF;
	END;

	-- ERROR CODE: DC0003_CT
	-- VALIDATE THAT CLAIM TYPE IS NOT NULL AND AN ACCEPTED VALUE
	-- REASON FOR ERROR: CLAIM TYPE IS NULL OR NOT AN ACCEPTED VALUE
	BEGIN
		 IF EACH_REC.CLAIM_TYPE IS NULL OR 
     UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('MACHINE SERIALIZED', 'MACHINE NON SERIALIZED', 'PARTS WITH HOST', 
     'PARTS WITHOUT HOST', 'FIELDMODIFICATION')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Claim Type');
		 END IF;
	END;

	-- ERROR CODE: DC0004_SN/ DC0005_SN
	-- VALIDATE THAT SERIAL NUMBER IS NOT NULL/ VALIDATE THAT SERIAL NUMBER IS NOT NULL AND A VALID INVENTORY
	-- REASON FOR ERROR: CLAIM TYPE IS NULL/ CLAIM TYPE IS NOT NULL AND NOT A VALID INVENTORY
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE SERIALIZED', 'PARTS WITH HOST', 'FIELDMODIFICATION')
		 THEN
       IF EACH_REC.SERIAL_NUMBER IS NULL
       THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Empty-Serial Number');
       ELSIF NOT (COMMON_VALIDATION_UTILS.isValidInventory(EACH_REC.SERIAL_NUMBER, EACH_REC.BUSINESS_UNIT_NAME))
       THEN
         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Serial Number');
       END IF;
		 END IF;
	END;
	
	-- ERROR CODE: DC0006_MN
	-- VALIDATE THAT MODEL NUMBER IS NOT NULL FOR MACHINE NON-SERIALIZED
	-- REASON FOR ERROR: SERIAL NUMBER IS NULL 
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED') 
		 THEN
       IF EACH_REC.MODEL_NUMBER IS NULL
       THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Empty-Model');
       ELSIF NOT (COMMON_VALIDATION_UTILS.isValidModel(EACH_REC.MODEL_NUMBER, EACH_REC.BUSINESS_UNIT_NAME))
       THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Model');
       END IF;
		 END IF;
	END;
  
	-- ERROR CODE: DC0007_PN
	-- VALIDATE THAT PART ITEM NUMBER IS NOT NULL AND PART ITEM NUMBER IS A VALID ITEM
	-- REASON FOR ERROR: PART ITEM NUMBER IS NULL OR PART ITEM NUMBER IS NOT A VALID ITEM
	BEGIN
    IF UPPER(EACH_REC.CLAIM_TYPE) IN ('PARTS WITH HOST', 'PARTS WITHOUT HOST') 
    THEN
       IF EACH_REC.PART_ITEM_NUMBER IS NULL
       THEN
         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Empty-Part Item');
       END IF;
       IF EACH_REC.PART_ITEM_NUMBER IS NOT NULL AND 
       NOT (COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.PART_ITEM_NUMBER, EACH_REC.BUSINESS_UNIT_NAME))
       THEN
         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Part Item');
       END IF;
    END IF;
	END;

	-- ERROR CODE: DC0008_HS
	-- VALIDATE THAT HOURS IN SERVICE IS NOT NULL
	-- REASON FOR ERROR: HOURS IN SERVICE IS NULL OR NOT IN RANGE OF 0-999999
  IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE SERIALIZED', 'PARTS WITH HOST', 'FIELDMODIFICATION')
  THEN
    IF EACH_REC.HOURS_IN_SERVICE IS NULL
    THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Empty-Hours In Service');
    END IF;
    
    IF EACH_REC.HOURS_IN_SERVICE IS NOT NULL AND (EACH_REC.HOURS_IN_SERVICE < 0 OR EACH_REC.HOURS_IN_SERVICE > 999999)
    THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Hours In Service');
    END IF;
  END IF;
     
	-- ERROR CODE: DC0009_RD
	-- VALIDATE THAT REPAIR DATE IS NOT NULL AND VALID DATE
	-- REASON FOR ERROR: REPAIR DATE IS NULL OR NOT A VALID DATE
  IF EACH_REC.REPAIR_DATE IS NULL OR NOT (COMMON_VALIDATION_UTILS.isValidDate(EACH_REC.REPAIR_DATE, 'YYYYMMDD'))
  THEN
    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Repair Date');
  END IF;
  
	-- ERROR CODE: DC0010_FD
	-- VALIDATE THAT FAILURE DATE IS NOT NULL AND VALID DATE
	-- REASON FOR ERROR: FAILURE DATE IS NULL OR NOT A VALID DATE
  IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('FIELDMODIFICATION') AND 
  ( EACH_REC.FAILURE_DATE IS NULL OR NOT (COMMON_VALIDATION_UTILS.isValidDate(EACH_REC.FAILURE_DATE, 'YYYYMMDD')) )
  THEN
    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Failure Date');
  END IF;
  
	-- ERROR CODE: DC0011_FD
	-- VALIDATE THAT INSTALLATION DATE IS NOT NULL AND VALID DATE
	-- REASON FOR ERROR: INSTALLATION DATE IS NULL OR NOT A VALID DATE
  IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED', 'PARTS WITH HOST') AND 
  ( EACH_REC.INSTALLATION_DATE IS NULL OR NOT (COMMON_VALIDATION_UTILS.isValidDate(EACH_REC.INSTALLATION_DATE, 'YYYYMMDD')) )
  THEN
    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Installation Date');
  END IF;
  
	-- ERROR CODE: DC0012_WN
	-- VALIDATE THAT WORK ORDER NUMBER IS NOT NULL
	-- REASON FOR ERROR: WORK ORDER NUMBER IS NULL
	BEGIN
		 IF EACH_REC.WORK_ORDER_NUMBER IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Work Order Number');
		 END IF;
	END;

	-- ERROR CODE: DC0013_CF
	-- VALIDATE THAT CONDITION FOUND IS NOT NULL
	-- REASON FOR ERROR: CONDITION FOUND IS NULL
	BEGIN
		 IF EACH_REC.CONDITIONS_FOUND IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Conditions Found');
		 END IF;
	END;

	-- ERROR CODE: DC0014_WP
	-- VALIDATE THAT WORK PERFORMED IS NOT NULL
	-- REASON FOR ERROR: WORK PERFORMED IS NULL
	BEGIN
		 IF EACH_REC.WORK_PERFORMED IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Work Performed');
		 END IF;
	END;

	-- ERROR CODE: DC0015_PC
	-- VALIDATE THAT PROBABLE CAUSE IS NOT NULL
	-- REASON FOR ERROR: PROBABLE CAUSE IS NULL
	BEGIN
		 IF EACH_REC.PROBABLE_CAUSE IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Probable Cause');
		 END IF;
	END;

	-- ERROR CODE: DC0016_CP
	-- VALIDATE THAT CAUSAL PART IS NOT NULL
	-- REASON FOR ERROR: CAUSAL PART IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
     (EACH_REC.CAUSAL_PART IS NULL OR 
     NOT (COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.CAUSAL_PART, EACH_REC.BUSINESS_UNIT_NAME)))
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Causal Part');
		 END IF;
	END;

	-- ERROR CODE: DC0017_CC
	-- VALIDATE THAT CAMPAIGN CODE IS NOT NULL
	-- REASON FOR ERROR: CAMPAIGN CODE IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) IN ('FIELDMODIFICATION') AND EACH_REC.CAMPAIGN_CODE IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Campaign Code');
		 END IF;
	END;

	-- ERROR CODE: DC0018_RQ
	-- VALIDATE THAT REPLACED IR PARTS QUANTITY IS NOT NULL
	-- REASON FOR ERROR: REPLACED IR PARTS QUANTITY IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
      EACH_REC.REPLACED_IR_PARTS IS NOT NULL AND EACH_REC.REPLACED_IR_PARTS_QUANTITY IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced IR Parts Quantity');
		 END IF;
	END;

	-- ERROR CODE: DC0019_NQ
	-- VALIDATE THAT REPLACED NON IR PARTS QUANTITY IS NOT NULL
	-- REASON FOR ERROR: REPLACED NON IR PARTS QUANTITY IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
      EACH_REC.REPLACED_NON_IR_PARTS IS NOT NULL AND EACH_REC.REPLACED_NON_IR_PARTS_QUANTITY IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced Non IR Parts quantity');
		 END IF;
	END;

	-- ERROR CODE: DC0020_NP
	-- VALIDATE THAT REPLACED NON IR PARTS PRICE IS NOT NULL
	-- REASON FOR ERROR: REPLACED NON IR PARTS PRICE IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
      EACH_REC.REPLACED_NON_IR_PARTS IS NOT NULL AND EACH_REC.REPLACED_NON_IR_PARTS_PRICE IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced Non IR Parts Price');
		 END IF;
	END;
  
	-- ERROR CODE: DC0020_ND
	-- VALIDATE THAT REPLACED NON IR PARTS DESCRIPTION IS NOT NULL
	-- REASON FOR ERROR: REPLACED NON IR PARTS DESCRIPTION IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
      EACH_REC.REPLACED_NON_IR_PARTS IS NOT NULL AND EACH_REC.REPLACED_NON_IR_PARTS_DESC IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced Non IR parts description');
		 END IF;
	END;
  
	-- ERROR CODE: DC0021_SR
	-- VALIDATE THAT SMR REQUEST IS NOT NULL AND AN ALLOWED VALUE OF 'Y'/'N'
	-- REASON FOR ERROR: SMR REQUEST IS NULL OR NOT AN ALLOWED VALUE OF 'Y'/'N'
	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND EACH_REC.SMR_CLAIM NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'SMR');
		 END IF;
	END;
  
  
	-- ERROR CODE: DC0022_IN
	-- VALIDATE THAT INVOICE NUMBER IS NOT NULL
	-- REASON FOR ERROR: INVOICE NUMBER IS NULL
	BEGIN
		 IF COMMON_VALIDATION_UTILS.isConfigParamSet('invoiceNumberApplicable', EACH_REC.BUSINESS_UNIT_NAME) AND 
     EACH_REC.INVOICE_NUMBER IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Invoice number');
		 END IF;
	END;
  
	-- ERROR CODE: DC0023_HP
	-- VALIDATE THAT HOURS ON PARTS IS NOT A NUMBER
	-- REASON FOR ERROR: HOURS ON PARTS IS NULL
	BEGIN
		 IF EACH_REC.HOURS_ON_PARTS IS NOT NULL  AND 
		 NOT (Common_Utils.isPositiveInteger(EACH_REC.HOURS_ON_PARTS) )
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Hours on parts');
		 END IF;
	END;
  
	-- ERROR CODE: DC0024_RE
	-- VALIDATE THAT REASON FOR EXTRA LABOR HOURS IS NOT NULL
	-- REASON FOR ERROR: REASON FOR EXTRA LABOR HOURS IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
		 EACH_REC.LABOUR_HOURS IS NOT NULL AND EACH_REC.REASON_FOR_EXTRA_LABOR_HOURS IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Reason for Extra Labor Hours');
		 END IF;
	END;

	-- ERROR CODE: DC0024_RE
	-- VALIDATE THAT REASON FOR SMR CLAIM IS NOT NULL
	-- REASON FOR ERROR: REASON FOR SMR CLAIM IS NULL
	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND EACH_REC.REASON_FOR_SMR_CLAIM IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Reason for SMR Claim');
		 END IF;
	END;

	-- ERROR CODE: DC0025_RE
	-- VALIDATE THAT REPAIR DATE IS NOT LESS THAN FAILURE DATE
	-- REASON FOR ERROR: REPAIR DATE IS LESS THAN FAILURE DATE
	BEGIN
		 IF EACH_REC.REPAIR_DATE IS NOT NULL AND EACH_REC.FAILURE_DATE IS NOT NULL AND 
     TO_CHAR( TO_DATE (EACH_REC.REPAIR_DATE, 'YYYYMMDD') ,'YYYY/MM/DD') < 
        TO_CHAR(TO_DATE (EACH_REC.FAILURE_DATE, 'YYYYMMDD') ,'YYYY/MM/DD')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Repair Date<Failure Date');
		 END IF;
	END;

  IF UPPER(EACH_REC.CLAIM_TYPE) IN ('PARTS WITH HOST', 'PARTS WITHOUT HOST')
  THEN 
    BEGIN
      SELECT model.name
      INTO v_model
      FROM ITEM_GROUP model, ITEM i
      WHERE i.model = model.id AND 
      (lower(i.item_number) = lower(trim(EACH_REC.PART_ITEM_NUMBER)) OR 
      lower(i.alternate_item_number) = lower(trim(EACH_REC.PART_ITEM_NUMBER))) AND 
      lower(i.business_unit_info) = lower(trim(EACH_REC.BUSINESS_UNIT_NAME)) AND i.d_active = 1 AND ROWNUM = 1;
    EXCEPTION 
    WHEN OTHERS THEN
      v_model := '';
    END;
  ELSIF EACH_REC.SERIAL_NUMBER IS NOT NULL AND 
  UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE SERIALIZED', 'FIELDMODIFICATION')
  THEN 
    BEGIN
      SELECT model.name
      INTO v_model
      FROM ITEM_GROUP model, INVENTORY_ITEM ii, ITEM i
      WHERE ii.of_type = i.id AND I.model = MODEL.ID AND 
      lower(ii.serial_number) = lower(trim(EACH_REC.SERIAL_NUMBER)) AND 
      lower(ii.business_unit_info) = lower(trim(EACH_REC.BUSINESS_UNIT_NAME)) AND ROWNUM = 1;
    EXCEPTION 
    WHEN OTHERS THEN
      v_model := '';
    END;
  ELSIF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED') 
  THEN
    BEGIN
      SELECT model.name
      INTO v_model
      FROM ITEM_GROUP model
      WHERE 
      lower(model.name) = lower(trim(EACH_REC.MODEL_NUMBER)) AND 
      lower(model.business_unit_info) = lower(trim(EACH_REC.BUSINESS_UNIT_NAME)) AND 
      model.d_active = 1 AND ROWNUM = 1;
    EXCEPTION 
    WHEN OTHERS THEN
      v_model := '';
    END;
  END IF;
  dbms_output.put_line('Model: ' || v_model);
  
	-- ERROR CODE: DC0026_FC
	-- VALIDATE THAT FAULT CODE IS VALID
	-- REASON FOR ERROR: FAULT CODE IS NOT VALID
	BEGIN
		 IF v_model <> ''  AND EACH_REC.FAULT_CODE IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidFaultCode(v_model, EACH_REC.FAULT_CODE, EACH_REC.BUSINESS_UNIT_NAME)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Fault Code');
		 END IF;
	END;

	-- ERROR CODE: DC0027_JC
	-- VALIDATE THAT JOB CODE IS VALID
	-- REASON FOR ERROR: JOB CODE IS NOT VALID
	BEGIN
		 IF v_model <> ''  AND EACH_REC.JOB_CODE IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidJobCode(v_model, EACH_REC.JOB_CODE, EACH_REC.BUSINESS_UNIT_NAME)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Job Code');
		 END IF;
	END;
  
	-- ERROR CODE: DC0028_FF
	-- VALIDATE THAT FAULT FOUND IS VALID
	-- REASON FOR ERROR: FAULT FOUND IS NOT VALID
	BEGIN
		 IF v_model <> ''  AND EACH_REC.FAULT_FOUND IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidFaultFound(v_model, EACH_REC.FAULT_FOUND, EACH_REC.BUSINESS_UNIT_NAME)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Fault Found');
		 END IF;
	END;

	-- ERROR CODE: DC0029_FF
	-- VALIDATE THAT CAUSED BY IS VALID
	-- REASON FOR ERROR: CAUSED BY IS NOT VALID
	BEGIN
		 IF v_model <> ''  AND EACH_REC.CAUSED_BY IS NOT NULL AND EACH_REC.FAULT_FOUND IS NOT NULL AND
     NOT COMMON_VALIDATION_UTILS.isValidCausedBy(v_model, EACH_REC.FAULT_FOUND, EACH_REC.CAUSED_BY, EACH_REC.BUSINESS_UNIT_NAME)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Caused By');
		 END IF;
	END;

	-- ERROR CODE: DC0030_FF
	-- VALIDATE THAT ROOT CAUSE IS VALID
	-- REASON FOR ERROR: ROOT CAUSE IS NOT VALID
	BEGIN
		 IF v_model <> ''  AND EACH_REC.ROOT_CAUSE IS NOT NULL AND EACH_REC.FAULT_FOUND IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidRootCause(v_model, EACH_REC.FAULT_FOUND, EACH_REC.ROOT_CAUSE, EACH_REC.BUSINESS_UNIT_NAME)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Root Cause');
		 END IF;
	END;

	--UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
	IF v_error_code IS NULL
	THEN
	   --RECORD IS CLEAN AND IS SUCCESSFULLY VALIDATED
	   UPDATE STG_DRAFT_CLAIM
	   SET
		  ERROR_STATUS = 'Y',
			ERROR_CODE = NULL
		WHERE
		  ID = EACH_REC.ID;
	ELSE
	   --RECORD HAS ERRORS
		UPDATE STG_DRAFT_CLAIM
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
	
  END LOOP;
    
  BEGIN
    -- Update the status of validation
    
    -- In a given time there will be only one file for a given upload
    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_DRAFT_CLAIM 
    WHERE ROWNUM < 2;
    
    -- Success Count
    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_DRAFT_CLAIM 
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
      FROM STG_DRAFT_CLAIM 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N'
      group by file_upload_mgt_id;
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*)
    INTO v_count
    FROM STG_DRAFT_CLAIM 
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
  
END UPLOAD_DRAFT_CLAIM_VALIDATION;
/
COMMIT
/