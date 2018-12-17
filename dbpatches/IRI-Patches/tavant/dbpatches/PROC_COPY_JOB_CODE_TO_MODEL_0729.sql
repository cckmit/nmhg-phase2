--Purpose    : Fixed the issue with fault code creation
--Author     : raghuram.d
--Created On : 29-Jul-09

CREATE OR REPLACE PROCEDURE copy_job_code_to_model (
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
    WHERE LOWER(code) = LOWER(v_action_code);

    SELECT id INTO v_system_code_id FROM assembly_definition 
    WHERE lower(code)=lower(v_system_code) AND assembly_level=1;
    v_fault_code := UPPER(v_system_code);

    IF v_sub_system_code IS NOT NULL THEN
        SELECT id INTO v_sub_system_code_id FROM assembly_definition 
        WHERE lower(code)=lower(v_sub_system_code) AND assembly_level=2;
        v_fault_code := v_fault_code || '-' || UPPER(v_sub_system_code);
    END IF;

    IF v_component_code IS NOT NULL THEN
        SELECT id INTO v_component_code_id FROM assembly_definition 
        WHERE lower(code)=lower(v_component_code) AND assembly_level=3;
        v_fault_code := v_fault_code || '-' || UPPER(v_component_code);
    END IF;

    IF v_sub_component_code IS NOT NULL THEN
        SELECT id INTO v_sub_component_code_id FROM assembly_definition 
        WHERE lower(code)=lower(v_sub_component_code) AND assembly_level=4;
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
--dbms_output.put_line('created top assembly - ' || v_current_assembly);
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
--dbms_output.put_line('created assembly - ' || v_current_assembly);
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

--dbms_output.put_line('Setting fault code - '||v_fault_code_id || ','||v_current_assembly);
    
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
--dbms_output.put_line('created action node - ' || v_action_node);
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
        WHERE for_item_group = p_model_id;
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
--dbms_output.put_line('created failure struct assembly - ' || v_fail_struct_id || ',' || v_top_assembly );
    END;

    COMMIT;

END copy_job_code_to_model;
/
COMMIT
/