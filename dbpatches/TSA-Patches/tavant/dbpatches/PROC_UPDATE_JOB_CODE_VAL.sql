--Purpose : Merged TSESA-478 from R4 to R6
--Author : raghuram.d
--Date : 03/May/2011

create or replace PROCEDURE UPLOAD_JOB_CODE_UPDATE_VAL 
AS
    CURSOR ALL_REC
    IS
        SELECT *
        FROM STG_UPDATE_JOB_CODE
        WHERE
            NVL(ERROR_STATUS,'N') = 'N' AND
            UPLOAD_STATUS IS NULL
        ORDER BY ID ASC;

	v_uploaded_by               VARCHAR2(255) := NULL;
	v_bu_name                   VARCHAR2(255) := NULL;
	v_count                     NUMBER;
	v_index                     NUMBER;
	v_action_code               VARCHAR2(255) := NULL;
	v_job_code                  VARCHAR2(255) := NULL;
	v_item_group_id             NUMBER;
	v_level                     VARCHAR2(255) := NULL;
	
    v_error_code                VARCHAR2(4000) := NULL;
    v_error                     VARCHAR2(4000) := NULL;
	v_loop_count                NUMBER := 0;
	
    v_file_upload_mgt_id        NUMBER := 0;
    v_success_count             NUMBER := 0;
    v_error_count               NUMBER := 0;
    
    v_valid_bu                  BOOLEAN := FALSE;
    v_valid_product             BOOLEAN := FALSE;
    v_valid_model               BOOLEAN := FALSE;
    v_valid_jc                  BOOLEAN := FALSE;
	v_valid_action				BOOLEAN := FALSE;
	
BEGIN

    BEGIN
        SELECT u.login, f.business_unit_info INTO v_uploaded_by, v_bu_name
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_update_job_code WHERE rownum = 1);
    EXCEPTION 
        WHEN OTHERS THEN
            NULL;
    END;

    FOR EACH_REC IN ALL_REC LOOP
    BEGIN

    v_error_code := NULL;
    v_job_code                  := NULL;
    v_action_code               := NULL;
	v_level                     := 'MODEL';
	v_item_group_id				:= NULL;

    v_valid_bu := FALSE;
    IF each_rec.business_unit_name IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU01');
    ELSIF UPPER(v_bu_name) != UPPER(each_rec.business_unit_name) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU02');
    ELSIF NOT common_validation_utils.isUserBelongsToBU(each_rec.business_unit_name, v_uploaded_by) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU02');
    ELSE
        v_valid_bu := TRUE;
    END IF;

    v_valid_product := FALSE;
    IF each_rec.product_code IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU03');
	ELSIF each_rec.product_code='ALL_PRODUCTS' THEN
		v_valid_product := TRUE;
		v_LEVEL := 'GLOBAL';
    ELSIF NOT common_validation_utils.isValidProductCode(each_rec.product_code, v_bu_name) THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'JCU04');
    ELSE
        v_valid_product := TRUE;
    END IF;

	v_valid_model := FALSE;
    IF each_rec.field_model IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU05');
	ELSIF NOT v_valid_product THEN
		v_valid_model := FALSE;
	ELSIF each_rec.product_code='ALL_PRODUCTS' and each_rec.field_model != 'ALL_MODELS' THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU06');
	ELSIF each_rec.field_model = 'ALL_MODELS' THEN
		v_valid_model := TRUE;
		IF each_rec.product_code != 'ALL_PRODUCTS' THEN
			v_level := 'PRODUCT';
		END IF;
    ELSIF NOT common_validation_utils.isValidModelCodeForProduct(
            each_rec.field_model, each_rec.product_code, v_bu_name) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU07');
    ELSE
        v_valid_model := TRUE;
    END IF;

	v_valid_action := FALSE;
	IF each_rec.action IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU08');
    ELSIF NOT common_validation_utils.isValidActionName(each_rec.action) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU09');
	ELSE
		SELECT code INTO v_action_code
		FROM action_definition WHERE UPPER(name) = UPPER(each_rec.action) AND rownum=1;
		v_valid_action := TRUE;
    END IF;
	
	v_valid_jc := FALSE;
    IF each_rec.job_code IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU10');
    ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.job_code, '-') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU11');
    ELSE
        v_count := common_utils.count_delimited_values(each_rec.job_code, '-');
        IF v_count > 4 THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU11');
        ELSIF v_valid_action AND v_valid_bu THEN
            v_job_code := each_rec.job_code;
            v_index := 1;
            WHILE v_index > 0 LOOP
                v_index := INSTR(v_job_code, '-0000', -1, 1);
                IF (v_index + 5)=LENGTH(v_job_code) THEN
                    v_job_code := SUBSTR(v_job_code, 1, v_index-1);
                ELSE
                    v_index := 0;
                END IF;
            END LOOP;
			v_job_code := v_job_code || '-' || v_action_code;
			
			SELECT COUNT(*) INTO v_count FROM service_procedure_definition 
			WHERE UPPER(v_job_code)=UPPER(code) AND d_active=1
				AND business_unit_info = v_bu_name;
			IF v_count = 0 THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU11');
			ELSE
				v_valid_jc := TRUE;
			END IF;
        END IF;
    END IF;
    
    IF v_valid_jc AND v_valid_model THEN
		IF v_level = 'MODEL' THEN
	        SELECT m.id INTO v_item_group_id
	        FROM item_group m,item_group p1,item_group p2
	        WHERE m.item_group_type = 'MODEL' AND
				((lower(p1.name) = lower(each_rec.product_code) and p1.item_group_type='PRODUCT')
					or (lower(p2.name) = lower(each_rec.product_code) and p2.item_group_type='PRODUCT')) AND
	            UPPER(m.group_code) = UPPER(each_rec.field_model) AND 
	            UPPER(m.business_unit_info) = UPPER(v_bu_name) AND
	            m.is_part_of = p1.id AND p1.is_part_of=p2.id AND m.d_active=1;
		
			SELECT COUNT(*) INTO v_count
			FROM action_node an,service_procedure sp, service_procedure_definition spd
			WHERE an.id=sp.defined_for AND sp.definition=spd.id 
				AND spd.code = v_job_code AND sp.d_active=1 AND an.defined_for in 
				(SELECT a.id FROM assembly a WHERE a.fault_code IS NOT NULL
					CONNECT BY PRIOR a.id = a.is_part_of_assembly START WITH a.id IN
					(SELECT fsa.assemblies FROM failure_structure_assemblies fsa, failure_structure fs
					WHERE fsa.failure_structure=fs.id AND fs.for_item_group = v_item_group_id)
				);
		
			IF v_count = 0 THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU12');
			END IF;
		ELSIF v_level = 'PRODUCT' THEN
			SELECT p.id INTO v_item_group_id
	        FROM item_group p
	        WHERE p.item_group_type = 'PRODUCT'
	            AND p.business_unit_info = v_bu_name AND p.d_active=1
	            AND UPPER(p.name) = UPPER(each_rec.product_code);
			
			SELECT COUNT(*) INTO v_count
			FROM action_node an,service_procedure sp, service_procedure_definition spd
			WHERE an.id=sp.defined_for AND sp.definition=spd.id 
				AND spd.code = v_job_code AND sp.d_active=1 AND an.defined_for in 
				(SELECT a.id FROM assembly a WHERE a.fault_code IS NOT NULL
					CONNECT BY PRIOR a.id = a.is_part_of_assembly START WITH a.id IN
					(SELECT fsa.assemblies FROM failure_structure_assemblies fsa,
						failure_structure fs, item_group m
					WHERE fsa.failure_structure=fs.id AND fs.for_item_group = m.id
						AND m.is_part_of = v_item_group_id AND m.d_active = 1
						AND m.business_unit_info = v_bu_name)
				);
		
			IF v_count = 0 THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU13');
			END IF;
		ELSE
			SELECT COUNT(*) INTO v_count
			FROM action_node an,service_procedure sp, service_procedure_definition spd
			WHERE an.id=sp.defined_for AND sp.definition=spd.id 
				AND spd.code = v_job_code AND sp.d_active=1 AND an.defined_for in 
				(SELECT a.id FROM assembly a WHERE a.fault_code IS NOT NULL
					CONNECT BY PRIOR a.id = a.is_part_of_assembly START WITH a.id IN
					(SELECT fsa.assemblies FROM failure_structure_assemblies fsa,
						failure_structure fs, item_group m
					WHERE fsa.failure_structure=fs.id AND fs.for_item_group = m.id
						AND m.d_active = 1 AND m.business_unit_info = v_bu_name)
				);
		
			IF v_count = 0 THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU14');
			END IF;
		END IF;
    END IF;

    IF each_rec.labor_standard_hours IS NULL
	        AND each_rec.labor_standard_minutes IS NULL 
			AND each_rec.field_modification_only IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU15');
    ELSE
        IF each_rec.labor_standard_hours IS NOT NULL 
				AND NOT common_utils.isPositiveInteger(each_rec.labor_standard_hours) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU16');
        END IF;
        IF each_rec.labor_standard_minutes IS NOT NULL 
				AND NOT common_utils.isPositiveInteger(each_rec.labor_standard_minutes) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU17');
        END IF;
		IF each_rec.field_modification_only IS NOT NULL 
				AND UPPER(each_rec.field_modification_only) NOT IN ('Y','N') THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'JCU18');
		END IF;
    END IF;
    
    IF v_error_code IS NULL THEN
	
        UPDATE stg_update_job_code SET
            error_status = 'Y',
            error_code = NULL,
			complete_job_code = v_job_code,
			update_level = v_level,
			item_group_id = v_item_group_id
        WHERE id = each_rec.id;
    ELSE
        
        UPDATE stg_update_job_code SET
            error_status = 'N',
            error_code = v_error_code
        WHERE id = each_rec.id;
    END IF;

    v_loop_count := v_loop_count + 1;

    IF v_loop_count = 10 THEN
        COMMIT;
        v_loop_count := 0;
    END IF;

    END;
    END LOOP;

    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;

    BEGIN
        SELECT DISTINCT file_upload_mgt_id  INTO v_file_upload_mgt_id
        FROM stg_update_job_code WHERE ROWNUM < 2;

        BEGIN
            SELECT count(*) INTO v_success_count
            FROM stg_update_job_code 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;

        BEGIN
            SELECT count(*) INTO v_error_count
            FROM stg_update_job_code 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;

        SELECT count(*) INTO v_count
        FROM stg_update_job_code 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id;

        UPDATE file_upload_mgt SET 
            success_records = v_success_count, 
            error_records = v_error_count,
            total_records = v_count
        WHERE id = v_file_upload_mgt_id;

    EXCEPTION
        WHEN OTHERS THEN
            v_error := SUBSTR(SQLERRM, 1, 4000);
            UPDATE file_upload_mgt SET 
                error_message = v_error
            WHERE id = v_file_upload_mgt_id;
    END;

    COMMIT;

END UPLOAD_JOB_CODE_UPDATE_VAL;
/