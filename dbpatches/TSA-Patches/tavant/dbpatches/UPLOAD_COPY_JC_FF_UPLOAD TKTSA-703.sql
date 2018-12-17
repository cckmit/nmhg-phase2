
  CREATE OR REPLACE PROCEDURE "TWMS_TKOWNER"."UPLOAD_COPY_JC_FF_UPLOAD" 
AS

   
   CURSOR all_rec IS
      SELECT *
      FROM STG_COPY_JOB_CODE_FF
      WHERE error_status = 'Y' AND NVL (upload_status, 'N') = 'N';

    CURSOR all_failure_types(v_from_model INTEGER, v_to_model INTEGER)
    IS
        SELECT *
        FROM failure_type fm
        WHERE fm.for_item_group_id = v_from_model
            AND fm.definition_id NOT IN (
                SELECT tm.definition_id FROM failure_type tm 
                WHERE tm.for_item_group_id = v_to_model);

    CURSOR job_codes_to_copy(v_from_model NUMBER, v_to_model NUMBER)
    IS
        SELECT spd.code, sp.for_campaigns, sp.suggested_labour_hours
        FROM action_node an,service_procedure sp,service_procedure_definition spd,
            ( SELECT id FROM assembly CONNECT BY PRIOR id = is_part_of_assembly START WITH id IN 
                ( SELECT a.id 
                FROM failure_structure fs,failure_structure_assemblies fsa, assembly a
                WHERE a.active = 1 AND fs.for_item_group = v_from_model
                    AND fs.id = fsa.failure_structure AND fsa.assemblies = a.id 
            )) adata
        WHERE adata.id = an.defined_for AND an.id = sp.defined_for
            AND an.active = 1 AND sp.definition = spd.id AND spd.d_active = 1 
            AND spd.code not in ( SELECT spd.code
                FROM action_node an,service_procedure sp,service_procedure_definition spd,
                    ( SELECT id FROM assembly CONNECT BY PRIOR id = is_part_of_assembly START WITH id IN 
                        ( SELECT a.id 
                        FROM failure_structure fs,failure_structure_assemblies fsa, assembly a
                        WHERE a.active = 1 AND fs.for_item_group = v_to_model
                            AND fs.id = fsa.failure_structure AND fsa.assemblies = a.id 
                    )) adata
                WHERE adata.id = an.defined_for AND an.id = sp.defined_for 
                    AND an.active = 1 AND sp.definition = spd.id AND spd.d_active = 1);


   
   v_error_code              VARCHAR2 (4000) := NULL;
   v_from_item_group_id      NUMBER          := 0;
   v_to_item_group_id        NUMBER          := 0;
   v_failure_type_id         NUMBER          := 0;
   v_loop_count              NUMBER          := 0;
   v_bu_name                 VARCHAR2(255)   := NULL;

BEGIN
    FOR each_rec IN all_rec LOOP
    BEGIN

        v_bu_name := common_validation_utils.getValidBusinessUnitName(each_rec.business_unit_name);
      
      
           
	 SELECT M.ID 
	  INTO v_from_item_group_id
	  From Item_Group M, Item_Group PF,item_group P
    Where Lower(M.group_code) = LOWER(each_rec.from_model_number)
    And Lower(P.Name) = LOWER(each_rec.from_product_code)    
     and lower(m.business_unit_info) = lower(v_bu_name)
     and lower(PF.business_unit_info) = lower(v_bu_name)
   
     AND m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;
               

	 SELECT M.ID 
	  INTO v_to_item_group_id
	  From Item_Group M, Item_Group PF,item_group P
    Where Lower(M.group_code) = LOWER(each_rec.to_model_number)
    And Lower(P.Name) = LOWER(each_rec.to_product_code)   
     and lower(m.business_unit_info) = lower(v_bu_name)
     and lower(PF.business_unit_info) = lower(v_bu_name)
   
     AND m.item_group_type = 'MODEL' and m.d_active = 1 AND ROWNUM = 1;

        BEGIN

            IF UPPER(each_rec.copy) = 'FF' THEN
            BEGIN
                FOR each_fault_found IN all_failure_types(v_from_item_group_id, v_to_item_group_id)
                LOOP
                    
                    SELECT failure_type_seq.nextval
                    INTO v_failure_type_id
                    FROM DUAL;

                    INSERT INTO failure_type (id, version, definition_id, for_item_group_id,
                            d_created_on, d_internal_comments, d_active)
                    VALUES (v_failure_type_id, 1, each_fault_found.definition_id, v_to_item_group_id, 
                            sysdate, 'Upload Process', 1);
                END LOOP;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;
            ELSIF UPPER(each_rec.copy) = 'JC' THEN 
            
            BEGIN
                FOR each_service_proc IN job_codes_to_copy(v_from_item_group_id, v_to_item_group_id) LOOP
                BEGIN 
                    copy_job_code_to_model(v_to_item_group_id, each_service_proc.code, v_bu_name,
                        each_service_proc.suggested_labour_hours, each_service_proc.for_campaigns);
                EXCEPTION 
                    WHEN OTHERS THEN
                        NULL;
                END;
                END LOOP;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    NULL;
            END;
            END IF;

            
            UPDATE stg_copy_job_code_ff SET 
                upload_error = NULL,
                upload_status = 'Y',
                upload_date = SYSDATE
            WHERE id = each_rec.id;

        EXCEPTION
            WHEN OTHERS THEN
                ROLLBACK;
                v_error_code := SUBSTR (SQLERRM, 0, 1000);
                UPDATE stg_copy_job_code_ff SET 
                    upload_error = v_error_code,
                    upload_status = 'N'
                WHERE id = each_rec.id;
        END;

        IF v_loop_count = 10 THEN
            COMMIT; -- Do a commit for 10 records
            v_loop_count := 0; -- Initialize the count size
        END IF;

    END;
    END LOOP;

    COMMIT;

END;


 
 
 
 
 
 
 
/
 
