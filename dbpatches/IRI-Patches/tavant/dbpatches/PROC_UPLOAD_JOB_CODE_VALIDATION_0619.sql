--Purpose    : Procedure to validates job codes for upload
--Author     : raghuram.d
--Created On : 08-Jun-09

CREATE OR REPLACE PROCEDURE upload_job_code_validation 
AS

    CURSOR ALL_REC
    IS
        SELECT *
        FROM STG_JOB_CODE
        WHERE
            NVL(ERROR_STATUS,'N') = 'N' AND
            UPLOAD_STATUS IS NULL
        ORDER BY ID ASC;

    --ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
    v_error_code                VARCHAR2(4000) := NULL;
    v_error                     VARCHAR2(4000) := NULL;
    v_flag                      BOOLEAN := NULL;
    v_count                     NUMBER;
    v_item_group_id             NUMBER;
    v_assem_id                  NUMBER;	
    v_file_upload_mgt_id        NUMBER := 0;
    v_success_count             NUMBER := 0;
    v_error_count               NUMBER := 0;
    v_loop_count                NUMBER := 0;
    v_uploaded_by               VARCHAR2(255) := NULL;
    v_system_code			    VARCHAR2(255) := NULL;
    v_sub_system_code			VARCHAR2(255) := NULL;
    v_component_code			VARCHAR2(255) := NULL;
    v_sub_component_code		VARCHAR2(255) := NULL;
    v_job_code                  VARCHAR2(255) := NULL;
    v_index                     NUMBER;
    v_complete_job_code         VARCHAR2(255) := NULL;
    v_action_code               VARCHAR2(255) := NULL;
    v_bu_name                   VARCHAR2(255) := NULL;

    v_valid_bu                  BOOLEAN := FALSE;
    v_valid_product             BOOLEAN := FALSE;
    v_valid_model               BOOLEAN := FALSE;
    v_valid_jc                  BOOLEAN := FALSE;
BEGIN

    BEGIN
        SELECT u.login, f.business_unit_info INTO v_uploaded_by, v_bu_name
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_job_code WHERE rownum = 1);
    EXCEPTION 
        WHEN OTHERS THEN
            NULL;
    END;

    FOR EACH_REC IN ALL_REC LOOP
    BEGIN
    
    v_error_code := NULL;
    v_system_code			    := NULL;
    v_sub_system_code			:= NULL;
    v_component_code			:= NULL;
    v_sub_component_code		:= NULL;
    v_job_code                  := NULL;
    v_complete_job_code         := NULL;
    v_action_code               := NULL;

  	-- Business unit info/name is mandatory
    v_valid_bu := FALSE;
    IF each_rec.business_unit_name IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC001');
    --Business Unit Name is valid
    ELSIF UPPER(v_bu_name) != UPPER(each_rec.business_unit_name) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC002');
    ELSIF NOT common_validation_utils.isUserBelongsToBU(each_rec.business_unit_name, v_uploaded_by) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC002');
    ELSE
        v_valid_bu := TRUE;
    END IF;

    --Product Code is mandatory
    v_valid_product := FALSE;
    IF each_rec.product_code IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC003');
    --Product Code is valid
    ELSIF NOT common_validation_utils.isValidProductCode(each_rec.product_code, v_bu_name) THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'JC004');
    ELSE
        v_valid_product := TRUE;
    END IF;

    --Field Model is mandatory
    v_valid_model := FALSE;
    IF each_rec.field_model IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC005');
    --Is a valid Model of the Product
    ELSIF NOT common_validation_utils.isValidModelCodeForProduct(
            each_rec.field_model, each_rec.product_code, v_bu_name) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC006');
    ELSE
        v_valid_model := TRUE;
    END IF;

    --Job Code is mandatory
    v_valid_jc := FALSE;
    IF each_rec.job_code IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC007');
    ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.job_code, '-') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC013');
    ELSE
        v_valid_jc := TRUE;
        v_count := common_utils.count_delimited_values(each_rec.job_code, '-');
        IF v_count > 4 THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'JC013');
            v_valid_jc := FALSE;
        ELSE
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
            v_count := common_utils.count_delimited_values(v_job_code, '-');
            IF v_count > 3 THEN
                v_sub_component_code := common_utils.get_delimited_value(v_job_code, '-', 4);
                IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_sub_component_code,4) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'JC014');
                    v_valid_jc := FALSE;
                END IF;
            END IF;
            IF v_count > 2 THEN
                v_component_code := common_utils.get_delimited_value(v_job_code, '-', 3);
                IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_component_code,3) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'JC015');
                    v_valid_jc := FALSE;
                END IF;
            END IF;
            IF v_count > 1 THEN
                v_sub_system_code := common_utils.get_delimited_value(v_job_code, '-', 2);
                IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_sub_system_code,2) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'JC016');
                    v_valid_jc := FALSE;
                END IF;
            END IF;
            IF v_count > 0 THEN
                v_system_code := common_utils.get_delimited_value(v_job_code, '-', 1);
                IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_system_code,1) THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'JC017');
                    v_valid_jc := FALSE;
                END IF;
            END IF;
        END IF;
    END IF;

    --Action is mandatory
    IF each_rec.action IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC009');
        v_valid_jc := FALSE;
    ELSIF NOT common_validation_utils.isValidActionName(each_rec.action) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC018');
        v_valid_jc := FALSE;
    END IF;

    --Job Code already exists
    IF v_valid_jc AND v_valid_model THEN
    BEGIN
        SELECT m.id INTO v_item_group_id
        FROM item_group m,item_group p
        WHERE m.item_group_type = 'MODEL' AND
            UPPER(m.group_code) = UPPER(each_rec.field_model) AND 
            UPPER(m.business_unit_info) = UPPER(v_bu_name) AND
            m.is_part_of = p.id AND 
            UPPER(p.name) = UPPER(each_rec.product_code);

        SELECT code INTO v_action_code FROM action_definition WHERE UPPER(name)=UPPER(each_rec.action);
        v_complete_job_code := v_job_code || '-' || v_action_code;

        SELECT 1 INTO v_assem_id FROM dual 
        WHERE EXISTS( SELECT an.defined_for
            FROM service_procedure_definition spd,
                service_procedure sp, action_node an
            WHERE spd.code = v_complete_job_code AND
                UPPER(spd.business_unit_info) = UPPER(v_bu_name) AND
                sp.definition = spd.id AND
                sp.defined_for = an.id
            INTERSECT
            SELECT id FROM assembly CONNECT BY PRIOR id = is_part_of_assembly START WITH id IN (
                SELECT assemblies FROM failure_structure_assemblies a, failure_structure b 
                WHERE a.failure_structure = b.id AND b.for_item_group =  v_item_group_id ));
                    
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC008');	  
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            NULL; --DO NOTHING
    END;
    END IF;
             
    --Standard Labor time in hours and mins is > '0'
    IF NVL(each_rec.labor_standard_hours,0) = 0 AND 
            NVL(each_rec.labor_standard_minutes,0) = 0 THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC010');
    --Standard Labor time in hours is in the range 0 to 99
    ELSE
        IF NVL(each_rec.labor_standard_hours,0) < 0 AND
                NVL(each_rec.labor_standard_hours,0) > 99 THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'JC011');
        END IF;
        IF NOT common_utils.isPositiveInteger(each_rec.labor_standard_minutes) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'JC020');
        END IF;
    END IF;
                    
    --Field Modification Only is mandatory
    IF each_rec.field_modification_only IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC012');
    ELSIF UPPER(each_rec.field_modification_only) NOT IN ('Y','N') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC019');
    END IF;

    --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
    IF v_error_code IS NULL THEN
        --Record is clean and is successfully validated
        UPDATE stg_job_code SET
            error_status = 'Y',
            error_code = NULL
        WHERE id = each_rec.id;
    ELSE
        --Record has errors
        UPDATE stg_job_code SET
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
    
    -- Update the status of validation
    BEGIN
        SELECT DISTINCT file_upload_mgt_id  INTO v_file_upload_mgt_id
        FROM stg_job_code WHERE ROWNUM < 2;
        
        -- Success Count
        BEGIN
            SELECT count(*) INTO v_success_count
            FROM stg_job_code 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;

        -- Error Count
        BEGIN
            SELECT count(*) INTO v_error_count
            FROM stg_job_code 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;

        -- Total Count
        SELECT count(*) INTO v_count
        FROM stg_job_code 
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

    COMMIT; -- Final Commit for the procedure
  
END upload_job_code_validation;
/
COMMIT
/