--Purpose : TWMS4.3U-628
--Author : raghuram.d
--Date : 26/May/2011

create or replace
PROCEDURE                COPY_JOB_CODE_TO_MODEL (
        p_model_id NUMBER,
        p_job_code VARCHAR2,
        p_bu_name VARCHAR2,
        p_labor_hours FLOAT,
        p_campaign_only NUMBER)
AS

    v_system_code_id          NUMBER := NULL;
    v_sub_system_code_id      NUMBER := NULL;
    v_component_code_id       NUMBER := NULL;
    v_sub_component_code_id   NUMBER := NULL;
    v_action_code_id          NUMBER := NULL;
    v_fault_code              VARCHAR2 (4000) := NULL;
    v_fault_code_def_id       NUMBER := NULL;
    v_fault_code_id           NUMBER := NULL;
    v_current_assembly        NUMBER := NULL;
    v_code_id                 NUMBER := NULL;
    v_top_assembly            NUMBER := NULL;
    v_parent_assembly         NUMBER := NULL;
    v_job_code                VARCHAR2 (4000) := NULL;
    v_action_node             NUMBER := NULL;
    v_service_proc_def_id     NUMBER := NULL;
    v_service_proc_id         NUMBER := NULL;
    v_fail_struct_id          NUMBER := NULL;
    v_var                     NUMBER := NULL;
    v_count                   NUMBER := NULL;

    v_system_code             VARCHAR2 (255) := NULL;
    v_sub_system_code         VARCHAR2 (255) := NULL;
    v_component_code          VARCHAR2 (255) := NULL;
    v_sub_component_code      VARCHAR2 (255) := NULL;
    v_action_code             VARCHAR2 (255) := NULL;

BEGIN

    v_count := common_utils.count_delimited_values(p_job_code,'-');
    v_system_code := common_utils.get_delimited_value(p_job_code,'-',1);
    IF v_count > 2 THEN
        v_sub_system_code := common_utils.get_delimited_value(p_job_code,'-',2);
    END IF;
    IF v_count > 3 THEN
        v_component_code := common_utils.get_delimited_value(p_job_code,'-',3);
    END IF;
    IF v_count > 4 THEN
        v_sub_component_code := common_utils.get_delimited_value(p_job_code,'-',4);
    END IF;
    v_action_code := common_utils.get_delimited_value(p_job_code,'-',v_count);

    SELECT id INTO v_action_code_id FROM action_definition
    WHERE LOWER(code) = LOWER(v_action_code) and d_active=1;

    SELECT id INTO v_system_code_id FROM assembly_definition 
    WHERE lower(code)=lower(v_system_code) AND assembly_level=1 and d_active=1;
    v_fault_code := UPPER(v_system_code);

    IF v_sub_system_code IS NOT NULL THEN
        SELECT id INTO v_sub_system_code_id FROM assembly_definition 
        WHERE lower(code)=lower(v_sub_system_code) AND assembly_level=2 and d_active=1;
        v_fault_code := v_fault_code || '-' || UPPER(v_sub_system_code);
    END IF;

    IF v_component_code IS NOT NULL THEN
        SELECT id INTO v_component_code_id FROM assembly_definition 
        WHERE lower(code)=lower(v_component_code) AND assembly_level=3 and d_active=1;
        v_fault_code := v_fault_code || '-' || UPPER(v_component_code);
    END IF;

    IF v_sub_component_code IS NOT NULL THEN
        SELECT id INTO v_sub_component_code_id FROM assembly_definition 
        WHERE lower(code)=lower(v_sub_component_code) AND assembly_level=4 and d_active=1;
        v_fault_code := v_fault_code || '-' || UPPER(v_sub_component_code);
    END IF;

    SELECT id INTO v_fault_code_def_id 
    FROM fault_code_definition
    WHERE UPPER(code) = v_fault_code
        AND UPPER(business_unit_info) = UPPER(p_bu_name);

    BEGIN
        SELECT a.ID  INTO v_current_assembly
        FROM assembly a,
            failure_structure_assemblies fsa,
            failure_structure fs
        WHERE a.assembly_definition = v_system_code_id
            AND a.is_part_of_assembly IS NULL
            AND fsa.assemblies = a.ID
            AND fsa.failure_structure = fs.ID
            AND fs.for_item_group = p_model_id;

        UPDATE assembly SET active = 1 WHERE ID = v_current_assembly;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT assembly_seq.NEXTVAL INTO v_current_assembly FROM DUAL;

            INSERT INTO assembly
                       (ID, tread_able, VERSION, assembly_definition,
                        is_part_of_assembly, fault_code, active,d_internal_comments)
            VALUES (v_current_assembly, 0, 0, v_system_code_id,
                    NULL, NULL, 1, 'Copy Job Codes Upload');

    END;

    v_top_assembly := v_current_assembly;

    FOR i IN 1 .. 3 LOOP
    BEGIN

        SELECT DECODE(i,
                    1,v_sub_system_code_id,
                    2,v_component_code_id,
                    3,v_sub_component_code_id)
        INTO v_code_id FROM DUAL;

        IF v_code_id IS NOT NULL THEN
        BEGIN
            v_parent_assembly := v_current_assembly;

            SELECT ID      
            INTO   v_current_assembly
            FROM assembly
            WHERE assembly_definition = v_code_id
                AND is_part_of_assembly = v_parent_assembly;

            UPDATE assembly
            SET active = 1
            WHERE ID = v_current_assembly;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN      
                SELECT assembly_seq.NEXTVAL INTO v_current_assembly FROM DUAL;

                INSERT INTO assembly
                        (ID, tread_able, VERSION,
                         assembly_definition, is_part_of_assembly,
                         fault_code, active,d_internal_comments)
                VALUES (v_current_assembly, 0, 0,
                        v_code_id, v_parent_assembly,
                        NULL, 1, 'Copy Job Codes Upload');

        END;
        END IF;

    END;
    END LOOP;

    BEGIN
        SELECT fault_code INTO v_fault_code_id
        FROM assembly
        WHERE id = v_current_assembly;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_fault_code_id := NULL;
    END;

    IF v_fault_code_id IS NULL THEN
            SELECT fault_code_seq.NEXTVAL INTO v_fault_code_id FROM DUAL;

            INSERT INTO fault_code
                    (ID, last_updated_date, VERSION, 
                     definition,d_internal_comments)
            VALUES (v_fault_code_id, SYSDATE, 1, 
                    v_fault_code_def_id,'Copy Job Codes Upload');

            UPDATE assembly
            SET fault_code = v_fault_code_id
            WHERE ID = v_current_assembly;
    END IF;



    BEGIN
        SELECT id 
        INTO   v_action_node
        FROM action_node
        WHERE defined_for = v_current_assembly
        AND definition = v_action_code_id;

        UPDATE action_node SET active = 1
        WHERE ID = v_action_node;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN   
            SELECT action_node_seq.NEXTVAL
            INTO   v_action_node
            FROM DUAL;

            INSERT INTO action_node
                    (ID, VERSION, defined_for,
                    definition, active, d_internal_comments)
            VALUES (v_action_node, 1, v_current_assembly,
                    v_action_code_id, 1, 'Copy Job Codes Upload');

    END;

    v_job_code := UPPER(p_job_code);

    SELECT ID
    INTO   v_service_proc_def_id
    FROM service_procedure_definition
    WHERE code = v_job_code
        AND UPPER (business_unit_info) = UPPER (p_bu_name);

    BEGIN
        SELECT sp.id 
        INTO    v_service_proc_id
        FROM service_procedure sp
        WHERE sp.definition = v_service_proc_def_id
            AND sp.defined_for = v_action_node;

        UPDATE /*INDEX(SERVICE_PROCEDURE_PK)*/ service_procedure
        SET suggested_labour_hours = p_labor_hours
        WHERE ID = v_service_proc_id;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN

            SELECT service_proc_seq.NEXTVAL
            INTO   v_service_proc_id
            FROM DUAL;

            INSERT INTO service_procedure
                    (ID, for_campaigns,
                    suggested_labour_hours,
                    VERSION, definition, defined_for, d_internal_comments)
            VALUES (v_service_proc_id, p_campaign_only,
                    p_labor_hours,
                    1, v_service_proc_def_id, v_action_node, 'Copy Job Codes Upload');
    END;

    BEGIN
        SELECT ID
        INTO   v_fail_struct_id
        FROM failure_structure
        WHERE for_item_group = p_model_id and d_active=1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            SELECT failure_structure_seq.NEXTVAL
            INTO   v_fail_struct_id
            FROM DUAL;

            INSERT INTO failure_structure
                    (ID, NAME, VERSION, for_item_group, d_internal_comments)
            VALUES (v_fail_struct_id, NULL,1, p_model_id, 'Copy Job Codes Upload');           
    END;

    BEGIN
        SELECT 1
        INTO   v_var
        FROM failure_structure_assemblies
        WHERE failure_structure = v_fail_struct_id
            AND assemblies = v_top_assembly;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN

           INSERT INTO failure_structure_assemblies
                    (failure_structure, assemblies)
            VALUES (v_fail_struct_id, v_top_assembly);

    END;

    COMMIT;

END copy_job_code_to_model;
/
create or replace
PROCEDURE                UPLOAD_COPY_JC_FF_UPLOAD 
AS   
   CURSOR all_rec IS
      SELECT *
      FROM STG_COPY_JOB_CODE_FF
      WHERE error_status = 'Y' AND NVL (upload_status, 'N') = 'N';

    CURSOR all_failure_types(v_from_model_id INTEGER, v_to_model_id INTEGER)
    IS
        SELECT *
        FROM failure_type fm
        WHERE fm.for_item_group_id = v_from_model_id
            AND fm.definition_id NOT IN (
                SELECT tm.definition_id FROM failure_type tm 
                WHERE tm.for_item_group_id = v_to_model_id);

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
      
		v_from_item_group_id := common_validation_utils.getModelForModelCodeAndProduct(
							each_rec.from_model_number,each_rec.from_product_code,v_bu_name);
							
		v_to_item_group_id := common_validation_utils.getModelForModelCodeAndProduct(
							each_rec.to_model_number,each_rec.to_product_code,v_bu_name);

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

END UPLOAD_COPY_JC_FF_UPLOAD;
/