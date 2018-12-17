--Purpose    : Fixed the draft claims upload
--Author     : Raghu
--Created On : 27-May-09

CREATE OR REPLACE PROCEDURE UPLOAD_DRAFT_CLAIM_VALIDATION AS
CURSOR ALL_REC IS
	SELECT * FROM STG_DRAFT_CLAIM
	WHERE NVL(ERROR_STATUS,'N') = 'N' --AND
		 -- UPLOAD_STATUS IS NULL
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
    v_model                 NUMBER := NULL;
    v_flag                  BOOLEAN := FALSE;
    v_valid_bu              BOOLEAN;
    v_valid_repair_date     BOOLEAN := FALSE;
    v_valid_failure_date    BOOLEAN := FALSE;
    v_valid_fault_found     BOOLEAN := FALSE;
    v_valid_campaign_code   BOOLEAN := FALSE;
    v_user_locale           VARCHAR2(255) := NULL;
    v_dealer                VARCHAR2(255) := NULL;
    v_bu_name               VARCHAR2(255) := NULL;
    v_smr_reason_id         VARCHAR2(255) := NULL;
    v_service_provider      NUMBER := NULL;
    v_delimiter             VARCHAR2(10) := '#$#';
    v_fault_code            VARCHAR2(255);
    v_job_codes_delimited   VARCHAR2(255);
    v_job_code              VARCHAR2(255);
    v_fault_found           VARCHAR2(255);
BEGIN

    --Fetch the LoginId, Locale & BU of the user who had uploaded the claims
    BEGIN
        SELECT u.locale, u.login, f.business_unit_info INTO v_user_locale, v_dealer, v_bu_name 
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_draft_claim WHERE rownum = 1);
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            v_user_locale := 'en_US';
    END;

    --Fetch the Service Provider of the user who had uploaded the claims
    BEGIN
        SELECT p.id INTO v_service_provider
        FROM party p, service_provider sp, org_user_belongs_to_orgs orgs, org_user u
        WHERE p.id=sp.id AND 
            sp.id= orgs.belongs_to_organizations AND
            orgs.org_user = u.id AND u.login = v_dealer AND
            ROWNUM = 1;
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;

  FOR EACH_REC IN ALL_REC
  LOOP
	
    v_error_code := '';
    v_model := NULL;
    v_valid_bu := FALSE;
    v_valid_repair_date := FALSE;
    v_valid_failure_date := FALSE;
    v_valid_fault_found := FALSE;
    v_valid_campaign_code := FALSE;
    v_smr_reason_id := NULL;
    v_fault_code := NULL;
    v_job_code := NULL;
    v_job_codes_delimited := NULL;
    v_fault_found := NULL;
    
	-- ERROR CODE: DC0001_BU
	-- VALIDATE THAT BUSINESS UNIT NAME IS NOT NULL AND BUSINESS UNIT NAME IS AN ALLOWED ONE IN TWMS
    BEGIN
        IF v_bu_name IS NULL OR lower(v_bu_name) != lower(each_rec.business_unit_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC001');
        ELSIF NOT (COMMON_VALIDATION_UTILS.isUserBelongsToBU(v_bu_name,v_dealer)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC044');
        ELSE
            v_valid_bu := TRUE;
        END IF;
	END;

	-- ERROR CODE: DC0002_UI
	-- VALIDATE THAT UNIQUE IDENTIFIER IS NULL
	-- REASON FOR ERROR: UNIQUE IDENTIFIER IS NULL
	BEGIN
		 IF EACH_REC.UNIQUE_IDENTIFIER IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC002');
		 END IF;
	END;

	-- VALIDATE THAT CLAIM TYPE IS NOT NULL AND AN ACCEPTED VALUE
	BEGIN
        IF each_rec.claim_type IS NULL OR UPPER(each_rec.claim_type) NOT IN 
            ('MACHINE SERIALIZED', 'MACHINE NON SERIALIZED', 'PARTS WITH HOST', 
            'PARTS WITHOUT HOST', 'FIELDMODIFICATION')
        THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC003');
        ELSIF v_valid_bu = TRUE AND NOT common_validation_utils.isClaimTypeAllowed(each_rec.claim_type, v_bu_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC066');
        END IF;
	END;

    -- VALIDATE CAMPAIGN CODE
    IF UPPER(each_rec.claim_type) IN ('FIELDMODIFICATION') THEN
        IF each_rec.campaign_code IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC026');
        ELSIF NOT common_validation_utils.isValidCampaignCode(each_rec.campaign_code, v_service_provider, v_bu_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC065');
        ELSE
            v_valid_campaign_code := TRUE;
        END IF;
    END IF;
    
    -- ERROR CODE: DC0004_SN/ DC0005_SN
    -- VALIDATE THAT SERIAL NUMBER IS NOT NULL/ VALIDATE THAT SERIAL NUMBER IS NOT NULL AND A VALID INVENTORY
    -- REASON FOR ERROR: CLAIM TYPE IS NULL/ CLAIM TYPE IS NOT NULL AND NOT A VALID INVENTORY
    BEGIN
    IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE SERIALIZED', 'PARTS WITH HOST', 'FIELDMODIFICATION') THEN
        IF EACH_REC.SERIAL_NUMBER IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC004');        
        ELSIF NOT (COMMON_VALIDATION_UTILS.isValidInventory(EACH_REC.SERIAL_NUMBER, v_bu_name)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC005');
        ELSIF UPPER(each_rec.claim_type) = 'FIELDMODIFICATION' AND v_valid_campaign_code = TRUE AND NOT
                common_validation_utils.isValidInventoryForFieldMod(each_rec.serial_number,each_rec.campaign_code,v_service_provider,v_bu_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC064');
        ELSE
            SELECT COUNT(1) INTO v_count FROM inventory_item 
            WHERE business_unit_info = v_bu_name
            AND lower(serial_number) = lower(each_rec.serial_number);

            IF v_count = 1 THEN
                SELECT model.id INTO v_model
                FROM ITEM_GROUP model, INVENTORY_ITEM ii, ITEM i
                WHERE ii.of_type = i.id AND I.model = MODEL.ID AND 
                lower(ii.serial_number) = lower(trim(EACH_REC.SERIAL_NUMBER)) AND 
                lower(ii.business_unit_info) = lower(trim(v_bu_name)) AND ROWNUM = 1; 
            ELSIF v_count > 1 THEN
                IF EACH_REC.MODEL_NUMBER IS NULL THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC006');
                ELSIF NOT (COMMON_VALIDATION_UTILS.isValidModel(EACH_REC.MODEL_NUMBER, v_bu_name)) THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC007');
                ELSE
                    SELECT m.id INTO v_model
                    FROM inventory_item inv, item i, item_group m
                    WHERE LOWER(inv.serial_number) = LOWER(each_rec.serial_number)
                    AND inv.of_type = i.id
                    AND inv.business_unit_info = v_bu_name
                    AND i.model = m.id AND m.item_group_type = 'MODEL'
                    AND LOWER(m.name) = LOWER(each_rec.model_number)
                    AND inv.d_active=1 AND i.d_active = 1 AND m.d_active = 1;
                END IF;
            END IF;
        END IF;
    END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC008');
    END;
	
    -- ERROR CODE: DC0006_MN
    -- VALIDATE THAT MODEL NUMBER IS NOT NULL FOR MACHINE NON-SERIALIZED
    -- REASON FOR ERROR: SERIAL NUMBER IS NULL 
    BEGIN
    IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED') THEN
        v_flag := FALSE;
        IF EACH_REC.MODEL_NUMBER IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC009');
        ELSIF NOT (COMMON_VALIDATION_UTILS.isValidModel(EACH_REC.MODEL_NUMBER, v_bu_name)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC010');
        ELSE
            v_flag := TRUE;
        END IF;

        IF each_rec.item_number IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC011');
        ELSIF NOT (common_validation_utils.isValidItemNumber(each_rec.item_number, v_bu_name)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC012');
        ELSIF v_flag THEN
            SELECT m.id INTO v_model
            FROM item i, party p, item_group m
            WHERE (lower(i.alternate_item_number) = lower(ltrim(rtrim(each_rec.item_number))) OR 
                    lower(i.item_number) = lower(ltrim(rtrim(each_rec.item_number))) )
                AND i.business_unit_info = v_bu_name
                AND i.owned_by = p.id AND p.name = common_utils.constant_oem_name 
                AND i.model = m.id AND m.item_group_type = 'MODEL'
                AND lower(m.name) = lower(each_rec.model_number)
                AND i.d_active = 1 AND m.d_active = 1;
        END IF;
    END IF;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC013');
    END;
  
    -- ERROR CODE: DC0007_PN
    -- VALIDATE THAT PART ITEM NUMBER IS NOT NULL AND PART ITEM NUMBER IS A VALID ITEM
    -- REASON FOR ERROR: PART ITEM NUMBER IS NULL OR PART ITEM NUMBER IS NOT A VALID ITEM
    BEGIN
    IF UPPER(EACH_REC.CLAIM_TYPE) IN ('PARTS WITH HOST', 'PARTS WITHOUT HOST')  THEN
        IF EACH_REC.PART_ITEM_NUMBER IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC014');
        ELSIF NOT (COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.PART_ITEM_NUMBER, v_bu_name)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC015');
        ELSE
            SELECT model.id 
            INTO v_model
            FROM ITEM_GROUP model, ITEM i
            WHERE i.model = model.id AND 
            (lower(i.item_number) = lower(trim(EACH_REC.PART_ITEM_NUMBER)) OR 
            lower(i.alternate_item_number) = lower(trim(EACH_REC.PART_ITEM_NUMBER))) AND 
            lower(i.business_unit_info) = lower(trim(v_bu_name)) AND i.d_active = 1 AND ROWNUM = 1;
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
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC016');
    END IF;
    
    IF EACH_REC.HOURS_IN_SERVICE IS NOT NULL AND (EACH_REC.HOURS_IN_SERVICE < 0 OR EACH_REC.HOURS_IN_SERVICE > 999999)
    THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC017');
    END IF;
  END IF;
     
	-- ERROR CODE: DC0009_RD
	-- VALIDATE THAT REPAIR DATE IS NOT NULL AND VALID DATE
	-- REASON FOR ERROR: REPAIR DATE IS NULL OR NOT A VALID DATE
  IF EACH_REC.REPAIR_DATE IS NULL OR NOT (COMMON_VALIDATION_UTILS.isValidDate(EACH_REC.REPAIR_DATE, 'YYYYMMDD'))
  THEN
    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC018');
  ELSE
    v_valid_repair_date := TRUE;
  END IF;
  
	-- ERROR CODE: DC0010_FD
	-- VALIDATE THAT FAILURE DATE IS NOT NULL AND VALID DATE
	-- REASON FOR ERROR: FAILURE DATE IS NULL OR NOT A VALID DATE
  IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('FIELDMODIFICATION') AND 
  ( EACH_REC.FAILURE_DATE IS NULL OR NOT (COMMON_VALIDATION_UTILS.isValidDate(EACH_REC.FAILURE_DATE, 'YYYYMMDD')) )
  THEN
    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC019');
  ELSE 
    v_valid_failure_date := TRUE;
  END IF;
  
	-- ERROR CODE: DC0011_FD
	-- VALIDATE THAT INSTALLATION DATE IS NOT NULL AND VALID DATE
	-- REASON FOR ERROR: INSTALLATION DATE IS NULL OR NOT A VALID DATE
  IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED', 'PARTS WITH HOST') AND 
  ( EACH_REC.INSTALLATION_DATE IS NULL OR NOT (COMMON_VALIDATION_UTILS.isValidDate(EACH_REC.INSTALLATION_DATE, 'YYYYMMDD')) )
  THEN
    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC020');
  END IF;
  
	-- ERROR CODE: DC0012_WN
	-- VALIDATE THAT WORK ORDER NUMBER IS NOT NULL
	-- REASON FOR ERROR: WORK ORDER NUMBER IS NULL
	BEGIN
		 IF EACH_REC.WORK_ORDER_NUMBER IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC021');
		 END IF;
	END;

	-- ERROR CODE: DC0013_CF
	-- VALIDATE THAT CONDITION FOUND IS NOT NULL
	-- REASON FOR ERROR: CONDITION FOUND IS NULL
	BEGIN
		 IF EACH_REC.CONDITIONS_FOUND IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC022');
		 END IF;
	END;

	-- ERROR CODE: DC0014_WP
	-- VALIDATE THAT WORK PERFORMED IS NOT NULL
	-- REASON FOR ERROR: WORK PERFORMED IS NULL
	BEGIN
		 IF EACH_REC.WORK_PERFORMED IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC023');
		 END IF;
	END;

	-- ERROR CODE: DC0016_CP
	-- VALIDATE THAT CAUSAL PART IS NOT NULL
	-- REASON FOR ERROR: CAUSAL PART IS NULL
	BEGIN
		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
     (EACH_REC.CAUSAL_PART IS NULL OR 
     NOT (COMMON_VALIDATION_UTILS.isValidItemNumber(EACH_REC.CAUSAL_PART, v_bu_name)))
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC025');
		 END IF;
	END;

	

    
	-- ERROR CODE: DC0018_RQ
	-- VALIDATE THAT REPLACED IR PARTS QUANTITY IS NOT NULL
	-- REASON FOR ERROR: REPLACED IR PARTS QUANTITY IS NULL
    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.replaced_ir_parts IS NOT NULL THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_ir_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045');
            v_flag := FALSE;
        ELSE
            v_count := Common_Utils.count_delimited_values(each_rec.replaced_ir_parts, v_delimiter);
            FOR i IN 1 .. v_count LOOP
                IF NOT common_validation_utils.isValidReplacedIRPart(
                        common_utils.get_delimited_value(each_rec.replaced_ir_parts, v_delimiter, i), v_bu_name)
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
        
        IF each_rec.replaced_ir_parts_quantity IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC027');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_ir_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC047');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.replaced_ir_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC048');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.replaced_ir_parts_quantity, v_delimiter, i)) 
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC054');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
    END IF;

    IF each_rec.miscellaneous_parts IS NOT NULL THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.miscellaneous_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC049');
            v_flag := FALSE;
        ELSE
            v_count := Common_Utils.count_delimited_values(each_rec.miscellaneous_parts, v_delimiter);
            FOR i IN 1 .. v_count LOOP
                IF NOT common_validation_utils.isValidMiscPart(
                        common_utils.get_delimited_value(each_rec.miscellaneous_parts, v_delimiter, i), v_service_provider, v_bu_name)
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC050');
                    EXIT;
                END IF;
            END LOOP;
        END IF;

        IF each_rec.misc_parts_quantity IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC051');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.misc_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC052');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.misc_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC053');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.misc_parts_quantity, v_delimiter, i)) 
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC055');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
    END IF;

    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.replaced_non_ir_parts IS NOT NULL 
    THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC056');
            v_flag := FALSE;
        ELSE
            v_count := common_utils.count_delimited_values(each_rec.replaced_non_ir_parts, v_delimiter);
            IF each_rec.replaced_non_ir_parts_quantity IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC028');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts_quantity, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC057');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_ir_parts_quantity, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC058');
            ELSE
                FOR i IN 1 .. v_count LOOP
                    IF NOT common_utils.isPositiveInteger(
                            common_utils.get_delimited_value(each_rec.replaced_non_ir_parts_quantity, v_delimiter, i)) 
                    THEN
                        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC059');
                        EXIT;
                    END IF;
                END LOOP;
            END IF;

            IF each_rec.replaced_non_ir_parts_price IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC029');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts_price, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC060');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_ir_parts_price, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC061');
            END IF;

            IF each_rec.replaced_non_ir_parts_desc IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC030');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_ir_parts_desc, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC062');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_ir_parts_desc, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC063');
            END IF;        
        END IF;
    END IF;
  
	-- ERROR CODE: DC0021_SR
	-- VALIDATE THAT SMR REQUEST IS NOT NULL AND AN ALLOWED VALUE OF 'Y'/'N'
	-- REASON FOR ERROR: SMR REQUEST IS NULL OR NOT AN ALLOWED VALUE OF 'Y'/'N'
	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND EACH_REC.SMR_CLAIM NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC031');
		 END IF;
	END;
  

    -- ERROR CODE: DC0024_RE
	-- VALIDATE THAT REASON FOR SMR CLAIM IS NOT NULL
	-- REASON FOR ERROR: REASON FOR SMR CLAIM IS NULL
	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND each_rec.smr_claim = 'Y' THEN
            IF EACH_REC.REASON_FOR_SMR_CLAIM IS NULL THEN
			    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC032');
            ELSE
                v_smr_reason_id := common_validation_utils.getValidSMRReasonId(each_rec.reason_for_smr_claim, v_user_locale, v_bu_name);
                IF v_smr_reason_id IS NULL THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC033');
                END IF;
            END IF;
		 END IF;
	END;

  
	-- ERROR CODE: DC0022_IN
	-- VALIDATE THAT INVOICE NUMBER IS NOT NULL
	-- REASON FOR ERROR: INVOICE NUMBER IS NULL
	BEGIN
		 IF COMMON_VALIDATION_UTILS.isConfigParamSet('invoiceNumberApplicable', v_bu_name) AND 
     EACH_REC.INVOICE_NUMBER IS NULL 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC034');
		 END IF;
	END;
  
	-- ERROR CODE: DC0023_HP
	-- VALIDATE THAT HOURS ON PARTS IS NOT A NUMBER
	-- REASON FOR ERROR: HOURS ON PARTS IS NULL
	BEGIN
		 IF EACH_REC.HOURS_ON_PARTS IS NOT NULL  AND 
		 NOT (Common_Utils.isPositiveInteger(EACH_REC.HOURS_ON_PARTS) )
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC035');
		 END IF;
	END;

	-- ERROR CODE: DC0025_RE
	-- VALIDATE THAT REPAIR DATE IS NOT LESS THAN FAILURE DATE
	-- REASON FOR ERROR: REPAIR DATE IS LESS THAN FAILURE DATE
	BEGIN
		 IF v_valid_repair_date AND v_valid_failure_date AND 
            TO_DATE (EACH_REC.REPAIR_DATE, 'YYYYMMDD') < 
                TO_DATE (EACH_REC.FAILURE_DATE, 'YYYYMMDD') 
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC037');
		 END IF;
	END;

  
	-- ERROR CODE: DC0026_FC
	-- VALIDATE THAT FAULT CODE IS VALID
	-- REASON FOR ERROR: FAULT CODE IS NOT VALID
	BEGIN
        v_fault_code := common_utils.getValidFaultCode(EACH_REC.fault_location);
		 IF v_model IS NOT NULL  AND EACH_REC.fault_location IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidFaultCodeForModelId(v_model, v_fault_code, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC038');
		 END IF;
	END;

	-- ERROR CODE: DC0027_JC
	-- VALIDATE THAT JOB CODE IS VALID
	-- REASON FOR ERROR: JOB CODE IS NOT VALID
	IF each_rec.job_code IS NOT NULL THEN
        v_count := Common_Utils.count_delimited_values_new(each_rec.job_code, ',');
        FOR i IN 1 .. v_count LOOP
            v_job_code := common_utils.getValidJobCode(common_utils.get_delimited_value(each_rec.job_code, ',', i));
            IF v_job_code IS NULL THEN
                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC039');
                EXIT;
            ELSIF v_model IS NOT NULL AND
                NOT common_validation_utils.isValidJobCodeForModelId(v_model, v_job_code, v_bu_name)
		    THEN
			    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC039');
                EXIT;
            ELSE
                IF v_job_codes_delimited IS NULL THEN
                    v_job_codes_delimited := '';
                ELSE
                    v_job_codes_delimited := v_job_codes_delimited || ',';
                END IF;
                v_job_codes_delimited := v_job_codes_delimited || v_job_code;
            END IF;

        END LOOP;
        IF each_rec.labour_hours IS NOT NULL AND Common_Utils.count_delimited_values(each_rec.labour_hours, ',') > 0 THEN
            IF Common_Utils.count_delimited_values_new(each_rec.labour_hours, ',') < v_count THEN
                v_count := Common_Utils.count_delimited_values_new(each_rec.labour_hours, ',');
            END IF;
            FOR i IN 1 .. v_count LOOP
                IF common_utils.get_delimited_value(each_rec.labour_hours, ',', i) IS NOT NULL AND 
                        common_utils.get_delimited_value(each_rec.labour_hours, ',', i) != '0' AND 
                        common_utils.get_delimited_value(each_rec.reason_for_extra_labor_hours, v_delimiter, i) IS NULL THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
	END IF;

	-- ERROR CODE: DC0028_FF
	-- VALIDATE THAT FAULT FOUND IS VALID
	-- REASON FOR ERROR: FAULT FOUND IS NOT VALID
	BEGIN
		 IF v_model IS NOT NULL  AND EACH_REC.FAULT_FOUND IS NOT NULL THEN
            IF NOT COMMON_VALIDATION_UTILS.isValidFaultFoundForModelId(v_model, EACH_REC.FAULT_FOUND, v_bu_name)
		    THEN
			    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC040');
            ELSE
                v_valid_fault_found := TRUE;
                SELECT ftd.name INTO v_fault_found
                FROM failure_type ft, 
                    i18nfailure_type_definition i18n_ftd,
                    failure_type_definition ftd
                where 
                    ft.definition_id = i18n_ftd.failure_type_definition
                    AND lower(i18n_ftd.name) = lower(ltrim(rtrim(each_rec.fault_found))) 
                    AND ft.for_item_group_id = v_model 
                    AND ft.d_active = 1
                    AND ftd.id = i18n_ftd.failure_type_definition
                    AND ROWNUM = 1;
            END IF;
		 END IF;
	END;


	-- ERROR CODE: DC0030_FF
	-- VALIDATE THAT ROOT CAUSE IS VALID
	-- REASON FOR ERROR: ROOT CAUSE IS NOT VALID
	BEGIN
		 IF v_valid_fault_found  AND EACH_REC.failure_detail IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidRootCauseForModelId(v_model, EACH_REC.FAULT_FOUND, EACH_REC.failure_detail, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC042');
		 END IF;
	END;

    BEGIN
		 IF each_rec.technician_id IS NOT NULL AND 
            NOT COMMON_VALIDATION_UTILS.isValidTechnician(each_rec.technician_id, v_dealer, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC043');
		 END IF;
	END;

	--UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
	IF v_error_code IS NULL
	THEN
	   --RECORD IS CLEAN AND IS SUCCESSFULLY VALIDATED
	   UPDATE STG_DRAFT_CLAIM
	   SET
		  ERROR_STATUS = 'Y',
			ERROR_CODE = NULL,
            business_unit_name = v_bu_name,
            reason_for_smr_claim = v_smr_reason_id,
            fault_location = v_fault_code,
            job_code = v_job_codes_delimited,
            fault_found = v_fault_found
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
  
    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;

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
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
    EXCEPTION
    WHEN OTHERS THEN
      v_success_count := 0;
    END;
    
    -- Error Count
    BEGIN
      SELECT count(*)
      INTO v_error_count
      FROM STG_DRAFT_CLAIM 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;

    -- Total Count
    SELECT count(*)
    INTO v_count
    FROM STG_DRAFT_CLAIM 
    where file_upload_mgt_id = v_file_upload_mgt_id ;
    
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