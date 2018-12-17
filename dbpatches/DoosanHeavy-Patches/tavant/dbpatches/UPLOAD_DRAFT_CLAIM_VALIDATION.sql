create or replace
PROCEDURE                 UPLOAD_DRAFT_CLAIM_VALIDATION AS
CURSOR ALL_REC IS
	SELECT * FROM STG_DRAFT_CLAIM
	WHERE NVL(ERROR_STATUS,'N') = 'N' -- AND

		 ORDER BY ID ASC;

CURSOR DUP_REC IS
SELECT serial_number,campaign_code,COUNT(*)
          FROM STG_DRAFT_CLAIM
          WHERE claim_type='FIELDMODIFICATION'
          GROUP BY serial_number,campaign_code
          HAVING COUNT(*)>1;  

    v_loop_count            NUMBER         := 0;
    v_success_count         NUMBER         := 0;
    v_error_count           NUMBER         := 0;
    v_count                 NUMBER         := 0;
    v_count2                NUMBER         := 0;
    v_file_upload_mgt_id    NUMBER         := 0;
    v_number_temp           NUMBER         := 0;
    isFaultFoundValid       BOOLEAN        := FALSE;
    v_error                 VARCHAR2(4000) := NULL;
    v_error_code            VARCHAR2(4000) := NULL;
    v_model                 NUMBER := NULL;
	v_causal_part           VARCHAR2(255) := NULL;
	v_replaced_parts        VARCHAR2(255) := NULL;
	v_installed_parts       VARCHAR2(255) := NULL;
    v_temp_part_number      VARCHAR2(255) := NULL;
    v_product               NUMBER := NULL;
    v_machine_serial_number VARCHAR2(4000) := NULL;
    v_replaced_part      NUMBER := NULL;
    v_flag                  BOOLEAN := FALSE;
    v_valid_bu              BOOLEAN;
    v_valid_fault_found     BOOLEAN := FALSE;
    v_valid_campaign_code   BOOLEAN := FALSE;
    v_user_locale           VARCHAR2(255) := NULL;
    v_dealer                VARCHAR2(255) := NULL;
    v_dealer_id             NUMBER := NULL;
    v_bu_name               VARCHAR2(255) := NULL;
    v_smr_reason_id         VARCHAR2(255) := NULL;
    v_service_provider      NUMBER := NULL;
    v_service_provider_number  VARCHAR2(255) := NULL;
    v_delimiter             VARCHAR2(10) := '#$#';
    v_fault_code            VARCHAR2(255);
    v_job_codes_delimited   VARCHAR2(255);
    v_job_code              VARCHAR2(255);
    v_fault_found           VARCHAR2(255);    
    v_item_number           VARCHAR2(255) := NULL;   
    v_competitor_model_id   NUMBER := NULL;
    v_ac_input      		    NUMBER         := 0;
    v_id                    NUMBER := NULL;   
    v_repairdate        DATE := NULL;
    v_installeddate      VARCHAR2(255) := NULL;
    v_failuredate       VARCHAR2(255) := NULL;
    v_serial_number       VARCHAR2(20) := NULL;
	v_serialized BOOLEAN;
	v_part_installed BOOLEAN;
	v_part_installed_on_tktsa BOOLEAN;
	v_bu_config_display_item BOOLEAN := FALSE;
	v_err VARCHAR2(20);
	v_inv NUMBER;
	v_item NUMBER;
	v_part_serial NUMBER;
	v_part NUMBER;
	v_model_id NUMBER;

BEGIN

    BEGIN
     FOR EACH_DUP_REC IN DUP_REC
      LOOP
        UPDATE STG_DRAFT_CLAIM
        SET ERROR_STATUS    = 'N',
          ERROR_CODE        = 'Field modification code is duplicate for same Serial Number'
        WHERE serial_number = EACH_DUP_REC.serial_number
        AND campaign_code   = EACH_DUP_REC.campaign_code;
      END LOOP;
    END;

    BEGIN
    SELECT u.locale, u.login, f.business_unit_info, o.id INTO v_user_locale, v_dealer, v_bu_name, v_dealer_id
    FROM org_user u,file_upload_mgt f,org_user_belongs_to_orgs orgu,organization o
    WHERE u.id = f.uploaded_by 
        and orgu.org_user=u.id and orgu.belongs_to_organizations=o.id AND f.id=(SELECT file_upload_mgt_id FROM stg_draft_claim WHERE rownum = 1);
       /* SELECT u.locale, u.login, f.business_unit_info, u.belongs_to_organization  INTO v_user_locale, v_dealer, v_bu_name, v_dealer_id
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_draft_claim WHERE rownum = 1);*/
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            v_user_locale := 'en_US';
    END;


    BEGIN
        SELECT id, service_provider_number 
		INTO v_service_provider,v_service_provider_number 
        FROM service_provider WHERE id=v_dealer_id;
    EXCEPTION 
        WHEN NO_DATA_FOUND THEN
            NULL;
    END;

	IF common_validation_utils.isConfigParamSet('isItemNumberDisplayRequired', v_bu_name) THEN
		v_bu_config_display_item := TRUE;
	END IF;

  FOR EACH_REC IN ALL_REC
  LOOP

    v_error_code := '';
    v_model := NULL;
	v_causal_part := NULL;
	v_replaced_parts := NULL;
	v_installed_parts := NULL;
    v_product := NULL;
    v_valid_bu := FALSE;
    v_valid_fault_found := FALSE;
    v_job_codes_delimited := NULL;
    v_valid_campaign_code := FALSE;
    v_smr_reason_id := NULL;
    v_fault_code := NULL;
    v_job_code := NULL;
    v_fault_found := NULL;
	v_serial_number := ltrim(rtrim(each_rec.serial_number));
	v_err := NULL;
	v_inv := NULL;
	v_item := NULL;
	v_part_serial := NULL;
	v_part := NULL;
	v_model_id := NULL;
	v_machine_serial_number := NULL;
	v_item_number := NULL;
	v_competitor_model_id := NULL;
	v_serialized := TRUE;
	v_part_installed := FALSE;
	v_part_installed_on_tktsa := FALSE;
	v_repairdate := NULL;
	v_failuredate := NULL;
	v_installeddate := NULL;

	IF EACH_REC.REPAIR_DATE IS NOT NULL AND 
		COMMON_VALIDATION_UTILS.isValidDate(each_rec.REPAIR_DATE, 'YYYY-MM-DD') THEN
		v_repairdate := TO_DATE(each_rec.repair_date,'YYYY-MM-DD');
	ELSIF EACH_REC.REPAIR_DATE IS NOT NULL AND 
		COMMON_VALIDATION_UTILS.isValidDate(each_rec.REPAIR_DATE, 'YYYYMMDD') THEN
		v_repairdate := TO_DATE(each_rec.repair_date,'YYYYMMDD');
	ELSE
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC018');
	END IF;

	IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('FIELDMODIFICATION') THEN
		IF EACH_REC.FAILURE_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.FAILURE_DATE, 'YYYY-MM-DD') THEN
			v_failuredate := TO_DATE(each_rec.FAILURE_DATE,'YYYY-MM-DD');
		ELSIF EACH_REC.FAILURE_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.FAILURE_DATE, 'YYYYMMDD') THEN
			v_failuredate := TO_DATE(each_rec.FAILURE_DATE,'YYYYMMDD');
		ELSE
			v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC019');
		END IF;
	END IF;

	IF UPPER(EACH_REC.CLAIM_TYPE) IN ('MACHINE NON SERIALIZED', 'PARTS WITH HOST') THEN
		IF EACH_REC.INSTALLATION_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.INSTALLATION_DATE, 'YYYY-MM-DD') THEN
			v_installeddate := TO_DATE(each_rec.INSTALLATION_DATE,'YYYY-MM-DD');
		ELSIF EACH_REC.INSTALLATION_DATE IS NOT NULL AND 
			COMMON_VALIDATION_UTILS.isValidDate(each_rec.INSTALLATION_DATE, 'YYYYMMDD') THEN
			v_installeddate := TO_DATE(each_rec.INSTALLATION_DATE,'YYYYMMDD');
		ELSE
			v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC020');
		END IF;
	END IF;


    BEGIN
        IF v_bu_name IS NULL OR lower(v_bu_name) != lower(each_rec.business_unit_name) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC001');
        ELSIF NOT (COMMON_VALIDATION_UTILS.isUserBelongsToBU(v_bu_name,v_dealer)) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC044');
        ELSE
            v_valid_bu := TRUE;
        END IF;
	END;

	IF EACH_REC.UNIQUE_IDENTIFIER IS NULL THEN
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC002');
	END IF;

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


    IF UPPER(each_rec.claim_type) IN ('FIELDMODIFICATION') THEN
        IF each_rec.campaign_code IS NULL THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC026');
        ELSIF NOT common_validation_utils.isValidCampaignCode(each_rec.campaign_code, v_service_provider, v_bu_name,v_repairdate,v_service_provider_number,v_serial_number) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC065');
        ELSE
            v_valid_campaign_code := TRUE;
        END IF;
    END IF;

	IF UPPER(each_rec.is_serialized) = 'N' THEN
		v_serialized := FALSE;
	END IF;
	IF UPPER(each_rec.is_part_installed) = 'Y' THEN
		v_part_installed := TRUE;
	END IF;
	IF UPPER(each_rec.is_part_installed_on_oem) = 'Y' THEN
		v_part_installed_on_tktsa := TRUE;
	END IF;

	IF UPPER(each_rec.claim_type) = 'PARTS WITHOUT HOST' THEN
		v_serialized := NULL;
		v_part_installed_on_tktsa := NULL;
	ELSIF UPPER(each_rec.claim_type) in ('FIELDMODIFICATION',
		'MACHINE SERIALIZED','MACHINE NON SERIALIZED') THEN
		v_part_installed := NULL;
		v_part_installed_on_tktsa := NULL;
	ELSIF UPPER(each_rec.claim_type) = 'PARTS WITH HOST' AND v_serialized THEN
		v_part_installed_on_tktsa := NULL;
	END IF;

	IF NOT v_serialized AND UPPER(each_rec.claim_type) IN 
			('MACHINE SERIALIZED','FIELDMODIFICATION') THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC101');
	ELSIF v_serialized AND UPPER(each_rec.claim_type) IN 
			('MACHINE NON SERIALIZED') THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC107');
	END IF;
	IF UPPER(each_rec.claim_type) = 'PARTS WITH HOST' 
			AND NOT v_part_installed THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC102');
	END IF;
	IF UPPER(each_rec.claim_type) = 'PARTS WITHOUT HOST' 
			AND v_part_installed THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'DC103');
	END IF;

	IF UPPER(each_rec.claim_type) IN ('PARTS WITHOUT HOST','PARTS WITH HOST') THEN
		IF each_rec.part_serial_number IS NOT NULL THEN
			v_part_serial := common_validation_utils.getValidPartSerialNumber(
								each_rec.part_serial_number, v_bu_name);
			IF v_part_serial IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC073_PS');
			ELSE
			SELECT of_type INTO v_part 
			FROM inventory_item WHERE id=v_part_serial;
			BEGIN
				select id,serial_number into v_inv,v_machine_serial_number 
				from inventory_item where id in ( 
					select part_of from inventory_item_composition  
					where part = v_part_serial);
			EXCEPTION 
				WHEN NO_DATA_FOUND THEN
					NULL;
			END;
			END IF;
		ELSIF each_rec.part_item_number IS NOT NULL THEN
			v_part := common_validation_utils.isValidItemForPartsClaim(
								each_rec.PART_ITEM_NUMBER, v_bu_name,v_err);
			IF v_err IS NOT NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_'||v_err);
				v_err := NULL;
			END IF;
		ELSE
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC014');
		END IF;
	END IF;

	IF v_machine_serial_number IS NOT NULL THEN
		IF UPPER(each_rec.claim_type)='PARTS WITHOUT HOST' THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC104');
		ELSIF NOT v_serialized THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC105');
		ELSIF v_serial_number IS NULL THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC004');
		ELSIF UPPER(v_serial_number) != UPPER(v_machine_serial_number) THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_90');
		END IF;
	ELSIF v_serialized THEN
		IF v_serial_number IS NOT NULL THEN
			IF UPPER(each_rec.claim_type)='FIELDMODIFICATION' THEN
				v_inv := common_validation_utils.isValidInventoryForFieldMod(
							v_serial_number,each_rec.campaign_code,
							v_service_provider,v_bu_name,v_repairdate,v_err);
			ELSE
				v_inv := common_validation_utils.isValidMachineSerialNumber(
								v_serial_number, v_bu_name, each_rec.model_number,
								v_service_provider, v_err);
			END IF;
		ELSIF each_rec.container_number IS NOT NULL THEN
			IF UPPER(each_rec.claim_type)='FIELDMODIFICATION' THEN
				v_inv := common_validation_utils.isValidInventoryForFieldModWCN(
							each_rec.container_number,each_rec.campaign_code,
							v_service_provider,v_bu_name,v_repairdate,v_err);
			ELSE 
				v_inv := common_validation_utils.isValidInventoryWithConNum(
								each_rec.container_number, v_bu_name,v_err);
			END IF;
		ELSE
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC004');
		END IF;
		IF v_err IS NOT NULL THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_'||v_err);
			v_err := NULL;
			v_inv := NULL;
		END IF;
	ELSIF NOT v_serialized THEN
		IF v_part_installed AND NOT v_part_installed_on_tktsa THEN
			IF each_rec.competitor_model IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC106');
			ELSE
				v_competitor_model_id := common_validation_utils.getValidCompetitorModelId(
					each_rec.competitor_model, v_user_locale, v_bu_name);
				IF v_competitor_model_id IS NULL THEN
					v_error_code := common_utils.addErrorMessage(v_error_code,'DC071_CM');
				END IF;
			END IF;
		ELSIF (v_part_installed AND v_part_installed_on_tktsa)
				OR v_part_installed IS NULL THEN
			IF v_bu_config_display_item THEN
				IF each_rec.item_number IS NULL THEN
					v_error_code := common_utils.addErrorMessage(v_error_code,'DC011');
				ELSE 
					v_item := common_validation_utils.isValidItemForNonSerialized(
									each_rec.item_number,v_bu_name,v_err);
				END IF;
			ELSIF each_rec.model_number IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code,'DC009');
			ELSE
				v_model_id := common_validation_utils.isValidModelForNonSerialized(
									each_rec.model_number, v_bu_name, v_err);
			END IF;
			IF v_err IS NOT NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'DC_'||v_err);
				v_err := NULL;
			END IF;
		END IF;
	END IF;

	IF v_model_id IS NOT NULL THEN
		v_model := v_model_id;
	ELSIF v_item IS NOT NULL THEN
		SELECT m.id INTO v_model
		FROM item i,item_group m
		WHERE i.id=v_item AND i.model=m.id;
	ELSIF v_inv IS NOT NULL THEN
		SELECT m.id,CASE WHEN p1.item_group_type='PRODUCT' THEN p1.id
			ELSE p2.id END INTO v_model,v_product
		FROM inventory_item ii,item i,item_group m,item_group p1,item_group p2
		WHERE ii.id=v_inv AND ii.of_type=i.id and i.model=m.id
			AND m.is_part_of=p1.id AND p1.is_part_of=p2.id;
	END IF;


     BEGIN
       IF EACH_REC.ALARM_CODES IS NOT NULL THEN          
            v_ac_input := Common_Utils.count_delimited_values(each_rec.ALARM_CODES, ',');
            FOR i IN 1 .. v_ac_input LOOP

                IF  v_product IS NOT NULL THEN       
                      IF NOT common_validation_utils.isValidAlarmCode(
                              common_utils.get_delimited_value(each_rec.ALARM_CODES, ',', i),v_product, v_bu_name)
                      THEN
                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC067_IAC');
                          EXIT;
                      END IF;
                ELSE 
                     select alarm_code.id into v_id from alarm_code where  lower(trim(code)) = lower(trim(common_utils.get_delimited_value(each_rec.ALARM_CODES, ',', i)));

                END IF;
            END LOOP;
        END IF; 
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC067_IAC');
     END;




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

	BEGIN
		 IF EACH_REC.WORK_ORDER_NUMBER IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC021');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.CONDITIONS_FOUND IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC022');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.WORK_PERFORMED IS NULL
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC023');
		 END IF;
	END;




	BEGIN
    IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST', 'FIELDMODIFICATION') THEN
        IF (EACH_REC.CAUSAL_PART IS NOT NULL) THEN
            v_causal_part := COMMON_VALIDATION_UTILS.getValidCausalPart(EACH_REC.CAUSAL_PART, v_bu_name);
        END IF;
        IF EACH_REC.CAUSAL_PART IS NULL OR (EACH_REC.CAUSAL_PART IS NOT NULL AND v_causal_part IS NULL) THEN
		    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC025');
        ELSIF v_causal_part != each_rec.causal_part THEN
            UPDATE stg_draft_claim SET causal_part=v_causal_part WHERE id=each_rec.id;
		END IF;
    END IF;
	END;







    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.replaced_oem_parts IS NOT NULL THEN

         IF  each_rec.INSTALLED_OEM_PARTS IS NULL THEN            
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045_ADD_INP');         
         END IF;    

        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_oem_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045');
            v_flag := FALSE;
        ELSE
            v_count := Common_Utils.count_delimited_values(each_rec.replaced_oem_parts, v_delimiter);
             IF  each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL THEN            
                IF NOT common_validation_utils.isValidDelimitedValue(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM, v_delimiter) THEN
                  v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045_SE');
                  v_flag := FALSE;
                ELSE
                  v_count2 := Common_Utils.count_delimited_values(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM, v_delimiter); 
                END IF; 
             END IF;
            IF v_flag = TRUE THEN


            IF UPPER(each_rec.claim_type) IN ('PARTS WITH HOST') AND common_validation_utils.hasDuplicateSerializedPart(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM,each_rec.replaced_oem_parts, v_delimiter) THEN
                  v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_98');          
            END IF;

            FOR i IN 1 .. v_count LOOP
                IF i <= v_count2 THEN
                IF UPPER(each_rec.claim_type) IN ('PARTS WITHOUT HOST') THEN

                            IF UPPER(each_rec.PART_SERIAL_NUMBER) IS NOT NULL THEN
                               IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NULL THEN   
                                        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Removed part should be serialized replaced part');                                         
                               ELSE
                                                     IF each_rec.PART_SERIAL_NUMBER != each_rec.REPLACED_OEM_PARTS_SERIAL_NUM THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced part serial number should be same as part serial number');

                                                     ELSIF common_utils.get_delimited_value(each_rec.replaced_oem_parts_quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Serialized replaced part quatity should be one');
                                                     END IF;

                               END IF;  
                            ELSIF UPPER(each_rec.Part_item_Number) IS NOT NULL THEN
                                 IF each_rec.Replaced_OEM_Parts IS NOT NULL THEN
                                                  IF common_utils.get_delimited_value(each_rec.Replaced_OEM_Parts, v_delimiter, i) != each_rec.Part_item_Number THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Please add correct replaced part');
                                                   END IF;
                                  END IF;                         
                                  IF UPPER(each_rec.REPLACED_OEM_PARTS_QUANTITY) IS NOT NULL THEN
                                                  IF common_utils.get_delimited_value(each_rec.replaced_oem_parts_quantity, v_delimiter, i) !=1 THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Replaced part quantity should be one only');   
                                                   END IF;
                                  END IF;                 
                             END IF;
                END IF; 
                        IF UPPER(each_rec.claim_type) IN ('PARTS WITH HOST') THEN

                            IF UPPER(each_rec.PART_SERIAL_NUMBER) IS NOT NULL THEN
                                   IF common_utils.get_delimited_value(each_rec.replaced_oem_parts_quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_79');
                                   END IF;

                                   IF each_rec.Is_Serialized in ('Y') THEN

                                          IF each_rec.Is_Part_Installed_on_OEM in ('Y') THEN                                         

                                              IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                    select count(*) into v_replaced_part from  Inventory_Item_Composition where part_of in (select id from inventory_item where serial_number = v_machine_serial_number ) and part in (select id from inventory_item where serial_number=common_utils.get_delimited_value(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM, v_delimiter, i) );   

                                                     IF v_replaced_part != 1 THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_78');                                                     
                                                     END IF;
                                               END IF;


                                               IF each_rec.Installed_OEM_Parts_Serial_Num IS NOT NULL THEN
                                                    IF common_utils.get_delimited_value(each_rec.Installed_OEM_Parts_Quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_80');
                                                     END IF;
                                               END IF;

                                           ELSE 

                                              IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL AND common_utils.get_delimited_value(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM, v_delimiter, i) != each_rec.Part_Serial_Number THEN
                                                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_81');
                                              END IF;   

                                              IF each_rec.Installed_OEM_Parts_Serial_Num IS NOT NULL AND common_utils.get_delimited_value(each_rec.Installed_OEM_Parts_Quantity, v_delimiter, i) !=1 THEN

                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_80');

                                               END IF;
                                           END IF;

                                    ELSE
                                             IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL AND common_utils.get_delimited_value(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM, v_delimiter, i) != each_rec.Part_Serial_Number THEN
                                                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_81');
                                              END IF;    

                                              IF each_rec.Installed_OEM_Parts_Serial_Num IS NOT NULL AND common_utils.get_delimited_value(each_rec.Installed_OEM_Parts_Quantity, v_delimiter, i) !=1 THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_80');
                                               END IF;  
                                    END IF;


                            ELSIF UPPER(each_rec.Part_item_Number) IS NOT NULL THEN

                                      IF each_rec.Is_Serialized in ('Y') THEN    

                                            IF each_rec.Is_Part_Installed_on_OEM in ('Y') THEN
                                                  IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_82');
                                                   END IF;        

                                            ELSE
                                                  IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_82'); 
                                                  END IF;
                                                  IF each_rec.Replaced_OEM_Parts IS NOT NULL AND UPPER(each_rec.Part_item_Number) != common_utils.get_delimited_value(each_rec.Replaced_OEM_Parts, v_delimiter, 1) THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_85');
                                                   END IF;
                                                   IF Common_Utils.count_delimited_values(each_rec.Replaced_OEM_Parts, v_delimiter)  !=1 THEN
                                                      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Should not add more than one part');
                                                   END IF;

                                                   IF each_rec.Installed_OEM_Parts_Serial_Num is NOT NULL THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_87');
                                                   END IF;  
                                             END IF;

                                       ELSE

                                              IF each_rec.REPLACED_OEM_PARTS_SERIAL_NUM IS NOT NULL THEN
                                                            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_84');
                                               END IF;

                                              IF each_rec.Replaced_OEM_Parts IS NOT NULL AND UPPER(each_rec.Part_item_Number) != common_utils.get_delimited_value(each_rec.Replaced_OEM_Parts, v_delimiter, i) THEN
                                                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_85');
                                              END IF;

                                              IF Common_Utils.count_delimited_values(each_rec.Replaced_OEM_Parts, v_delimiter)  !=1 THEN
                                                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Should not add more than one part');
                                              END IF;

                                              IF each_rec.Installed_OEM_Parts_Serial_Num is NOT NULL THEN
                                                         v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_87');
                                              END IF;  
                                       END IF;
                            END IF;   
                         END IF; 
                        IF NOT common_validation_utils.isValidReplacedIRPart(common_utils.get_delimited_value(each_rec.REPLACED_OEM_PARTS_SERIAL_NUM, v_delimiter, i),
                              common_utils.get_delimited_value(each_rec.replaced_OEM_parts, v_delimiter, i), v_bu_name)
                        THEN
                          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046');
                          EXIT;
                        END IF;

                ELSE          

                              IF UPPER(each_rec.Part_item_Number) IS NOT NULL THEN
                                   IF each_rec.Is_Part_Installed_on_OEM in ('N') OR each_rec.Is_Serialized in ('N') THEN                                         

                                        IF each_rec.Replaced_OEM_Parts IS NOT NULL AND UPPER(each_rec.Part_item_Number) != common_utils.get_delimited_value(each_rec.Replaced_OEM_Parts, v_delimiter, i) THEN
                                                                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_88');
                                        END IF;

                                        IF Common_Utils.count_delimited_values(each_rec.Replaced_OEM_Parts, v_delimiter)  !=1 THEN
                                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'Should not add more than one part');
                                        END IF;

                                        IF each_rec.Installed_OEM_Parts_Serial_Num is NOT NULL THEN
                                                v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC_87');
                                        END IF;  
                                    END IF;
                              END IF;

                      v_temp_part_number := common_validation_utils.getReplacedIRPartNumber(
		                        common_utils.get_delimited_value(each_rec.replaced_OEM_parts, v_delimiter, i), v_bu_name);
		                IF v_temp_part_number IS NULL
		                THEN
		                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046');
		                    v_replaced_parts := NULL;
		                    EXIT;
		                END IF;
		                IF i > 1 THEN
		                    v_replaced_parts := v_replaced_parts || v_delimiter;
		                END IF;			  
						v_replaced_parts := v_replaced_parts || v_temp_part_number;
                END IF;     

            END LOOP;
			IF v_replaced_parts IS NOT NULL AND v_replaced_parts != each_rec.replaced_OEM_parts THEN
                UPDATE stg_draft_claim SET replaced_OEM_parts=v_replaced_parts WHERE id=each_rec.id;
            ENd IF;
            END IF;   
        END IF;

        IF each_rec.replaced_oem_parts_quantity IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC027');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_oem_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC047');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.replaced_oem_parts_quantity, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC048');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.replaced_oem_parts_quantity, v_delimiter, i)) 
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC054');
                    EXIT;
                END IF;
            END LOOP;
        END IF;
    END IF; 




    IF UPPER(each_rec.claim_type) NOT IN ('PARTS WITHOUT HOST') AND 
            each_rec.INSTALLED_OEM_PARTS IS NOT NULL THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.INSTALLED_OEM_PARTS, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC045_INP');
            v_flag := FALSE;
        ELSE            
              IF v_count != Common_Utils.count_delimited_values(each_rec.INSTALLED_OEM_PARTS, v_delimiter) THEN
                 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC075');
              End If;
            v_count := Common_Utils.count_delimited_values(each_rec.INSTALLED_OEM_PARTS, v_delimiter);
            For I In 1 .. V_Count Loop               
                v_temp_part_number := common_validation_utils.getInstalledIRPart(
                        common_utils.get_delimited_value(each_rec.INSTALLED_OEM_PARTS, v_delimiter, i), v_bu_name);
                IF v_temp_part_number IS NULL
                THEN
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC046_INP');
                    v_installed_parts := NULL;
                    EXIT;
                END IF;
                IF i > 1 THEN
                    v_installed_parts := v_installed_parts || v_delimiter;
                END IF;
                v_installed_parts := v_installed_parts || v_temp_part_number;
            END LOOP;
			IF v_installed_parts IS NOT NULL AND v_installed_parts != each_rec.INSTALLED_OEM_PARTS THEN
                UPDATE stg_draft_claim SET INSTALLED_OEM_PARTS=v_installed_parts WHERE id=each_rec.id;
            ENd IF;
        END IF;

        IF each_rec.INSTALLED_OEM_PARTS_QUANTITY IS NULL THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC027_INP');
        ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.INSTALLED_OEM_PARTS_QUANTITY, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC047_INP');
        ELSIF v_flag = TRUE AND v_count != Common_Utils.count_delimited_values(each_rec.INSTALLED_OEM_PARTS_QUANTITY, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC048_INP');
        ELSIF v_flag = TRUE THEN
            FOR i IN 1 .. v_count LOOP
                IF NOT common_utils.isPositiveInteger(
                        common_utils.get_delimited_value(each_rec.INSTALLED_OEM_PARTS_QUANTITY, v_delimiter, i)) 
                THEN
                      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC054_INP');
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
            each_rec.replaced_non_oem_parts IS NOT NULL 
    THEN
        v_flag := TRUE;
        IF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_oem_parts, v_delimiter) THEN
            v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC056');
            v_flag := FALSE;
        ELSE
            v_count := common_utils.count_delimited_values(each_rec.replaced_non_oem_parts, v_delimiter);
            IF each_rec.replaced_non_oem_parts_qty IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC028');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_oem_parts_qty, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC057');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_oem_parts_qty, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC058');
            ELSE
                FOR i IN 1 .. v_count LOOP
                    IF NOT common_utils.isPositiveInteger(
                            common_utils.get_delimited_value(each_rec.replaced_non_oem_parts_qty, v_delimiter, i)) 
                    THEN
                        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC059');
                        EXIT;
                    END IF;
                END LOOP;
            END IF;

            IF each_rec.replaced_non_oem_parts_price IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC029');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_oem_parts_price, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC060');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_oem_parts_price, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC061');
            END IF;

            IF each_rec.replaced_non_oem_parts_desc IS NULL THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC030');
            ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.replaced_non_oem_parts_desc, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC062');
            ELSIF v_count != common_utils.count_delimited_values(each_rec.replaced_non_oem_parts_desc, v_delimiter) THEN
                v_error_code := common_utils.addErrorMessage(v_error_code, 'DC063');
            END IF;        
        END IF;
    END IF;




	BEGIN
		 IF EACH_REC.SMR_CLAIM IS NOT NULL AND EACH_REC.SMR_CLAIM NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC031');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.COMMERCIAL_POLICY IS NOT NULL AND EACH_REC.COMMERCIAL_POLICY NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC068_CP');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.IS_PART_INSTALLED IS NOT NULL AND EACH_REC.IS_PART_INSTALLED NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC069_IPI');
		 END IF;
	END;




	BEGIN
		 IF EACH_REC.IS_PART_INSTALLED_ON_OEM IS NOT NULL AND EACH_REC.IS_PART_INSTALLED_ON_OEM NOT IN ('Y', 'N')
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC070_PITKTSA');
		 END IF;
	END;



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

	IF (UPPER(each_rec.claim_type) = 'MACHINE NON SERIALIZED' OR
			(UPPER(each_rec.claim_type) = 'PARTS WITH HOST' 
				AND NOT v_serialized AND v_part_installed_on_tktsa))
			AND COMMON_VALIDATION_UTILS.isConfigParamSet('invoiceNumberApplicable', v_bu_name) 
			AND EACH_REC.INVOICE_NUMBER IS NULL THEN
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC034');
	END IF;

	BEGIN
		 IF TO_NUMBER(EACH_REC.HOURS_ON_PARTS) IS NOT NULL  AND (TO_NUMBER(EACH_REC.HOURS_ON_PARTS)<0) 

		 THEN

			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC035');
		 END IF;
	END;





	BEGIN

		 IF UPPER(EACH_REC.CLAIM_TYPE) NOT IN ('PARTS WITHOUT HOST') AND 
		 EACH_REC.LABOUR_HOURS IS NOT NULL  
		 THEN
        IF EACH_REC.REASON_FOR_EXTRA_LABOR_HOURS IS NULL THEN 
        	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036');
        ELSE 
         select count(1)  into v_id  from add_lbr_egl_service_providers alesp,additional_labor_eligibility ale where 
          alesp.additional_labor_eligibility=ale.id and ale.business_unit_info=v_bu_name and alesp.d_active = 1;
           IF v_id =0 then
              null;               
            ELSE             
                select count(1) into v_id from add_lbr_egl_service_providers alesp,additional_labor_eligibility ale where 
                alesp.SERVICE_PROVIDERS = v_dealer_id and alesp.additional_labor_eligibility=ale.id and ale.business_unit_info=v_bu_name
                and alesp.d_active = 1; 
                IF v_id = 0 then
                    v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_ALH');
                END IF; 
            END IF; 
        END IF;  
		 END IF;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
      v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_ALH');

	END;

IF v_product IS NOT NULL THEN     




BEGIN
       IF UPPER(EACH_REC.LABOUR_HOURS)IS NOT NULL THEN 
               IF NOT common_validation_utils.isAllowedCostCategory('LABOR',v_product,v_bu_name) THEN
                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC072');                                
               END IF;
        END IF;

        EXCEPTION 
          WHEN OTHERS THEN
          v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC072');
END;




BEGIN
       IF UPPER(EACH_REC.REPLACED_OEM_PARTS)IS NOT NULL THEN 
               IF NOT common_validation_utils.isAllowedCostCategory('OEM_PARTS',v_product,v_bu_name) THEN
                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_RP');                                
              END IF;
        END IF;

    EXCEPTION 
        WHEN OTHERS THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, '123');
END;





BEGIN
       IF UPPER(EACH_REC.MISCELLANEOUS_PARTS)IS NOT NULL THEN 
            IF NOT common_validation_utils.isAllowedCostCategory('MISC_PARTS',v_product,'v_bu_name') THEN
                               v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC036_MP');                                
              END IF;
       END IF;
END;

END IF;

	IF v_repairdate IS NOT NULL AND v_failuredate IS NOT NULL 
			AND v_repairdate < v_failuredate THEN
		v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC037');
	END IF;

	BEGIN
        v_fault_code := common_utils.getValidFaultCode(EACH_REC.fault_location);
		 IF v_model IS NOT NULL  AND EACH_REC.fault_location IS NOT NULL AND 
     NOT COMMON_VALIDATION_UTILS.isValidFaultCodeForModelId(v_model, v_fault_code, v_bu_name)
		 THEN
			 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'DC038');
		 END IF;
	END;


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


 IF v_error_code IS NULL
	THEN

	   UPDATE STG_DRAFT_CLAIM
	   SET
		  ERROR_STATUS = 'Y',
			ERROR_CODE = NULL,
            business_unit_name = v_bu_name,
            reason_for_smr_claim = v_smr_reason_id,
            competitor_model_id = v_competitor_model_id,
            fault_location = v_fault_code,
            job_code = v_job_codes_delimited,
            fault_found = v_fault_found,
            part_number=  v_item_number,
            repair_date= CAST (each_rec.REPAIR_DATE AS number(8,0)),                      
            failure_date= CAST (each_rec.FAILURE_DATE AS number(8,0)) ,                    
            installation_date=CAST (each_rec.INSTALLATION_DATE AS number(8,0)),
			serial_number_id = v_inv,
			item_number_id = v_item,
			model_id = v_model_id,
			part_id = v_part,
			part_serial_id = v_part_serial

		WHERE
		  ID = EACH_REC.ID;
	ELSE

		UPDATE STG_DRAFT_CLAIM
	   SET
		  ERROR_STATUS = 'N',
		  ERROR_CODE = v_error_code
		WHERE
		  ID = EACH_REC.ID;
	END IF;

    v_loop_count := v_loop_count + 1;

    IF v_loop_count = 10 THEN

      COMMIT;
      v_loop_count := 0; -- Initialize the count size
    END IF;

  END LOOP;

    BEGIN
     FOR EACH_DUP_REC IN DUP_REC
      LOOP
        UPDATE STG_DRAFT_CLAIM
        SET ERROR_STATUS    = 'N',
          ERROR_CODE        = 'Field modification code is duplicate for same Serial Number'
        WHERE serial_number = EACH_DUP_REC.serial_number
        AND campaign_code   = EACH_DUP_REC.campaign_code;
      END LOOP;
    END;

    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;

  BEGIN



    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_DRAFT_CLAIM 
    WHERE ROWNUM < 2;


    BEGIN
      SELECT count(*)
      INTO v_success_count
      FROM STG_DRAFT_CLAIM 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
    EXCEPTION
    WHEN OTHERS THEN
      v_success_count := 0;
    END;


    BEGIN
      SELECT count(*)
      INTO v_error_count
      FROM STG_DRAFT_CLAIM 
      where file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;


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

    v_error := SUBSTR(SQLERRM, 1, 4000);
    UPDATE FILE_UPLOAD_MGT 
    SET 
      ERROR_MESSAGE = v_error
    WHERE ID = v_file_upload_mgt_id;

  END;
  COMMIT; -- Final Commit for the procedure

END UPLOAD_DRAFT_CLAIM_VALIDATION;
/