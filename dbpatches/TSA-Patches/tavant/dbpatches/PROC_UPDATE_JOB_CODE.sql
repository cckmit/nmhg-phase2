--Purpose : TSESA-478 - Update Job Codes
--Author  : raghuram.d
--Date    : 03/May/2011

create or replace PROCEDURE UPLOAD_JOB_CODE_UPDATE AS

    CURSOR all_rec IS
        SELECT *
        FROM STG_UPDATE_JOB_CODE
        WHERE error_status = 'Y' AND NVL (upload_status, 'N') = 'N';
		
	CURSOR global_level(p_jc VARCHAR2, p_bu varchar2) IS
	SELECT sp.id sp_id
	FROM action_node an,service_procedure sp, service_procedure_definition spd
	WHERE an.id=sp.defined_for AND sp.definition=spd.id 
		AND spd.code = p_jc AND sp.d_active=1 AND an.defined_for in 
		(SELECT a.id FROM assembly a WHERE a.fault_code IS NOT NULL
			CONNECT BY PRIOR a.id = a.is_part_of_assembly START WITH a.id IN
			(SELECT fsa.assemblies FROM failure_structure_assemblies fsa,
				failure_structure fs, item_group m
			WHERE fsa.failure_structure=fs.id AND fs.for_item_group = m.id
				AND m.d_active = 1 AND UPPER(m.business_unit_info) = UPPER(p_bu))
		);
	
	CURSOR product_level(p_jc VARCHAR2, p_item_group NUMBER) IS
	SELECT sp.id sp_id
	FROM action_node an,service_procedure sp, service_procedure_definition spd
	WHERE an.id=sp.defined_for AND sp.definition=spd.id 
		AND spd.code = p_jc AND sp.d_active=1 AND an.defined_for in 
		(SELECT a.id FROM assembly a WHERE a.fault_code IS NOT NULL
			CONNECT BY PRIOR a.id = a.is_part_of_assembly START WITH a.id IN
			(SELECT fsa.assemblies FROM failure_structure_assemblies fsa,
				failure_structure fs, item_group m
			WHERE fsa.failure_structure=fs.id AND fs.for_item_group = m.id
				AND m.is_part_of = p_item_group AND m.d_active = 1)
		);
	
	CURSOR model_level(p_jc VARCHAR2, p_item_group NUMBER) IS
	SELECT sp.id sp_id
	FROM action_node an,service_procedure sp, service_procedure_definition spd
	WHERE an.id=sp.defined_for AND sp.definition=spd.id 
		AND spd.code = p_jc AND sp.d_active=1 AND an.defined_for in 
		(SELECT a.id FROM assembly a WHERE a.fault_code IS NOT NULL
			CONNECT BY PRIOR a.id = a.is_part_of_assembly START WITH a.id IN
			(SELECT fsa.assemblies FROM failure_structure_assemblies fsa, failure_structure fs
			WHERE fsa.failure_structure=fs.id AND fs.for_item_group = p_item_group)
		);

	v_update_std_labor        BOOLEAN;
	v_update_field_mod_only   BOOLEAN;
    v_upload_error            VARCHAR2 (4000) := NULL;
	v_error                   VARCHAR2(4000) := NULL;
    v_count                   NUMBER;
	v_file_upload_mgt_id      NUMBER := 0;
    
BEGIN

    FOR each_rec IN all_rec LOOP
    BEGIN

	v_update_std_labor := FALSE;
	v_update_field_mod_only := FALSE;
	v_count := 0;
	
	IF each_rec.labor_standard_hours IS NOT NULL OR each_rec.labor_standard_minutes IS NOT NULL THEN
		v_update_std_labor := TRUE;
	END IF;
	
	IF each_rec.field_modification_only IS NOT NULL THEN
		v_update_field_mod_only := TRUE;
	END IF;
	
	IF each_rec.update_level = 'GLOBAL' THEN
		FOR each_jc IN global_level(each_rec.complete_job_code, each_rec.business_unit_name) LOOP
			IF v_update_std_labor THEN
				UPDATE service_procedure SET suggested_labour_hours =
						(SELECT NVL(NVL (each_rec.labor_standard_hours, 0) + 
					    (ROUND(NVL (each_rec.labor_standard_minutes, 0)/60,2)), 0) FROM DUAL)
				WHERE id = each_jc.sp_id;
			END IF;
			IF v_update_field_mod_only THEN
				UPDATE service_procedure SET for_campaigns = 
						DECODE(UPPER(each_rec.field_modification_only),'Y',1,0)
				WHERE id = each_jc.sp_id;
			END IF;
			UPDATE service_procedure SET d_updated_on = sysdate, d_updated_time = systimestamp,
				d_internal_comments = d_internal_comments||':Upload - updateJobCodes'
			WHERE id = each_jc.sp_id;
			v_count := v_count + 1;
		END LOOP;
	ELSIF each_rec.update_level = 'PRODUCT' THEN
		FOR each_jc IN product_level(each_rec.complete_job_code, each_rec.item_group_id) LOOP
			IF v_update_std_labor THEN
				UPDATE service_procedure SET suggested_labour_hours =
						(SELECT NVL(NVL (each_rec.labor_standard_hours, 0) + 
					    (ROUND(NVL (each_rec.labor_standard_minutes, 0)/60,2)), 0) FROM DUAL)
				WHERE id = each_jc.sp_id;
			END IF;
			IF v_update_field_mod_only THEN
				UPDATE service_procedure SET for_campaigns = 
						DECODE(UPPER(each_rec.field_modification_only),'Y',1,0)
				WHERE id = each_jc.sp_id;
			END IF;
			UPDATE service_procedure SET d_updated_on = sysdate, d_updated_time = systimestamp,
				d_internal_comments = d_internal_comments||':Upload - updateJobCodes'
			WHERE id = each_jc.sp_id;
			v_count := v_count + 1;
		END LOOP;
	ELSE
		FOR each_jc IN model_level(each_rec.complete_job_code, each_rec.item_group_id) LOOP
			IF v_update_std_labor THEN
				UPDATE service_procedure SET suggested_labour_hours =
						(SELECT NVL(NVL (each_rec.labor_standard_hours, 0) + 
					    (ROUND(NVL (each_rec.labor_standard_minutes, 0)/60,2)), 0) FROM DUAL)
				WHERE id = each_jc.sp_id;
			END IF;
			IF v_update_field_mod_only THEN
				UPDATE service_procedure SET for_campaigns = 
						DECODE(UPPER(each_rec.field_modification_only),'Y',1,0)
				WHERE id = each_jc.sp_id;
			END IF;
			UPDATE service_procedure SET d_updated_on = sysdate, d_updated_time = systimestamp,
				d_internal_comments = d_internal_comments||':updateJobCodes'
			WHERE id = each_jc.sp_id;
			v_count := v_count + 1;
		END LOOP;
	END IF;

        UPDATE STG_UPDATE_JOB_CODE
        SET upload_status = 'Y',
            upload_error = NULL,
			sp_count = v_count
        WHERE id = each_rec.id;
        COMMIT;
	
    EXCEPTION
        WHEN OTHERS THEN    
            ROLLBACK;
            v_upload_error := SUBSTR (SQLERRM, 0, 3500);

            UPDATE STG_UPDATE_JOB_CODE
            SET upload_status = 'N',
                upload_error = v_upload_error
            WHERE id = each_rec.id;
            COMMIT;
    END;

    END LOOP; -- End of for Loop	
	
	BEGIN
        SELECT DISTINCT file_upload_mgt_id  INTO v_file_upload_mgt_id
        FROM stg_update_job_code WHERE ROWNUM < 2;

        BEGIN
            SELECT sum(sp_count) INTO v_count
            FROM stg_update_job_code 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and upload_status='Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_count := 0;
        END;
		
		UPDATE file_upload_mgt SET 
            success_records = v_count
        WHERE id = v_file_upload_mgt_id;
		
	EXCEPTION
        WHEN OTHERS THEN
            v_error := SUBSTR(SQLERRM, 1, 4000);
            UPDATE file_upload_mgt SET 
                error_message = v_error
            WHERE id = v_file_upload_mgt_id;
    END;
		
END UPLOAD_JOB_CODE_UPDATE;
/