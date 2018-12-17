CREATE OR REPLACE
PROCEDURE UPLOAD_JOB_CODE_UPLOAD
AS
  CURSOR all_rec
  IS
    SELECT *
    FROM STG_JOB_CODE
    WHERE error_status            = 'Y'
    AND NVL (upload_status, 'N')  = 'N';
  v_upload_error          VARCHAR2 (4000) := NULL;
  v_service_proc_def_id   VARCHAR2 (255)  := NULL;
  v_service_proc_id       VARCHAR2 (255)  := NULL;
  v_system_code_id        VARCHAR2 (255)  := NULL;
  v_sub_system_code_id    VARCHAR2 (255)  := NULL;
  v_component_code_id     VARCHAR2 (255)  := NULL;
  v_sub_component_code_id VARCHAR2 (255)  := NULL;
  v_action_code_id        VARCHAR2 (255)  := NULL;
  v_campaign_only         VARCHAR2 (255)  := NULL;
  v_assembly_id           VARCHAR2 (255)  := NULL;
  v_temp_assembly_id      VARCHAR2 (255)  := NULL;
  v_action_node           VARCHAR2 (255)  := NULL;
  v_curr_ass_def_id       VARCHAR2 (255)  := NULL;
  v_fail_struct_id        VARCHAR2 (255)  := NULL;
  v_item_group_id         VARCHAR2 (255)  := NULL;
  v_parent_assembly       VARCHAR2 (255)  := NULL;
  v_top_assembly          VARCHAR2 (255)  := NULL;
  v_complete_job_code     VARCHAR2 (4000) := NULL;
  v_complete_fault_code   VARCHAR2 (4000) := NULL;
  v_current_assembly      NUMBER;
  v_fault_code_def_id     NUMBER;
  v_fault_code_id         NUMBER;
  v_code_id               NUMBER;
  v_var                   NUMBER;
  v_job_code_flag         NUMBER;
  v_count                 NUMBER;
  v_loop_count            NUMBER       := 0;
  v_action_code           VARCHAR(255) := NULL;
BEGIN
  FOR each_rec IN all_rec
  LOOP
    BEGIN
      v_fault_code_def_id     := NULL;
      v_fault_code_id         := NULL;
      v_service_proc_def_id   := NULL;
      v_service_proc_id       := NULL;
      v_system_code_id        := NULL;
      v_sub_system_code_id    := NULL;
      v_component_code_id     := NULL;
      v_sub_component_code_id := NULL;
      v_action_code_id        := NULL;
      v_campaign_only         := 0;
      v_assembly_id           := NULL;
      v_temp_assembly_id      := NULL;
      v_complete_fault_code   := NULL;
      v_curr_ass_def_id       := NULL;
      v_complete_job_code     := NULL;
      v_fail_struct_id        := NULL;
      v_item_group_id         := each_rec.model_id;
      v_parent_assembly       := NULL;
      v_current_assembly      := NULL;
      v_top_assembly          := NULL;
      v_action_node           := NULL;
      v_job_code_flag         := 0;
      v_count                 := 0;
      v_action_code           := NULL;
      SELECT id,
        code
      INTO v_action_code_id,
        v_action_code
      FROM action_definition
      WHERE LOWER(CODE) = LOWER(each_rec.action)
      AND d_active      =1;
      SELECT id
      INTO v_system_code_id
      FROM assembly_definition
      WHERE lower(code)            =lower(each_rec.system_code)
      AND assembly_level           =1
      AND d_active                 =1;
      IF each_rec.sub_system_code IS NOT NULL AND each_rec.sub_system_code!='000' THEN
        SELECT id
        INTO v_sub_system_code_id
        FROM assembly_definition
        WHERE lower(code) =lower(each_rec.sub_system_code)
        AND assembly_level=2
        AND d_active      =1
        AND NAME         !='NO LOWER LEVEL';
      ELSE
        v_sub_system_code_id:=NULL;
      END IF;
      IF each_rec.component_code IS NOT NULL AND each_rec.component_code!='0000' THEN
        SELECT id
        INTO v_component_code_id
        FROM assembly_definition
        WHERE lower(code) =lower(each_rec.component_code)
        AND assembly_level=3
        AND d_active      =1;
      END IF;
      IF each_rec.sub_component_code IS NOT NULL AND each_rec.sub_component_code!='0000' THEN
        SELECT id
        INTO v_sub_component_code_id
        FROM assembly_definition
        WHERE lower(code) =lower(each_rec.sub_component_code)
        AND assembly_level=4
        AND d_active      =1;
      END IF;
      v_complete_fault_code := UPPER(each_rec.job_code);
      BEGIN
        SELECT ID
          /*+INDEX(FAULT_CODE_CODE_IDX)*/
        INTO v_fault_code_def_id
        FROM fault_code_definition
        WHERE UPPER(code)              = v_complete_fault_code
        AND UPPER (business_unit_info) = UPPER (each_rec.business_unit_name);
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        SELECT fault_code_definition_seq.NEXTVAL INTO v_fault_code_def_id FROM DUAL;
        INSERT
        INTO fault_code_definition
          (
            ID,
            code,
            VERSION,
            business_unit_info,
            d_internal_comments
          )
          VALUES
          (
            V_FAULT_CODE_DEF_ID,
            V_COMPLETE_FAULT_CODE,
            0,
            each_rec.business_unit_name,
            'Uploaded-'
            ||each_rec.id
          );
      END;
      FOR i IN 0 .. 3
      LOOP
        BEGIN
          SELECT DECODE (i, 0, v_system_code_id, 1, v_sub_system_code_id, 2, v_component_code_id, 3, v_sub_component_code_id )
          INTO v_curr_ass_def_id
          FROM DUAL;
          IF v_curr_ass_def_id IS NULL THEN
            EXIT;
          END IF;
          SELECT COUNT (1)
          INTO v_count
          FROM fault_code_def_comps
          WHERE fault_code_definition = v_fault_code_def_id
          AND COMPONENTS              = V_CURR_ASS_DEF_ID;
          IF (v_count                <= 0) THEN
            INSERT
            INTO fault_code_def_comps
              (
                fault_code_definition,
                components,
                list_index
              )
              VALUES
              (
                v_fault_code_def_id,
                v_curr_ass_def_id,
                i
              );
            v_count := 0;
          END IF;
        END;
      END LOOP;
      BEGIN
        SELECT a.ID
          /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX a) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX a)*/
        INTO v_current_assembly
        FROM assembly a,
          failure_structure_assemblies fsa,
          failure_structure fs
        WHERE a.assembly_definition = v_system_code_id
        AND a.is_part_of_assembly  IS NULL
        AND fsa.assemblies          = a.ID
        AND fsa.failure_structure   = fs.ID
        AND FS.FOR_ITEM_GROUP       = V_ITEM_GROUP_ID;
        UPDATE assembly SET active = 1 WHERE ID = v_current_assembly;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        SELECT assembly_seq.NEXTVAL INTO v_current_assembly FROM DUAL;
        INSERT
        INTO assembly
          (
            ID,
            tread_able,
            VERSION,
            assembly_definition,
            is_part_of_assembly,
            fault_code,
            active,
            d_internal_comments
          )
          VALUES
          (
            v_current_assembly,
            0,
            0,
            v_system_code_id,
            NULL,
            NULL,
            1,
            'Uploaded-'
            ||each_rec.id
          );
      END;
      v_top_assembly := v_current_assembly;
      FOR i          IN 1 .. 3
      LOOP
        BEGIN
          SELECT DECODE(i, 1,v_sub_system_code_id, 2,v_component_code_id, 3,v_sub_component_code_id)
          INTO v_code_id
          FROM DUAL;
          IF v_code_id IS NOT NULL THEN
            BEGIN
              V_PARENT_ASSEMBLY := V_CURRENT_ASSEMBLY;
              SELECT ID
                /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX ) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX )*/
              INTO v_current_assembly
              FROM assembly
              WHERE assembly_definition = v_code_id
              AND IS_PART_OF_ASSEMBLY   = V_PARENT_ASSEMBLY;
              UPDATE assembly SET active = 1 WHERE ID = v_current_assembly;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              SELECT assembly_seq.NEXTVAL INTO v_current_assembly FROM DUAL;
              INSERT
              INTO assembly
                (
                  ID,
                  tread_able,
                  VERSION,
                  assembly_definition,
                  is_part_of_assembly,
                  fault_code,
                  active,
                  d_internal_comments
                )
                VALUES
                (
                  v_current_assembly,
                  0,
                  0,
                  v_code_id,
                  v_parent_assembly,
                  NULL,
                  1,
                  'Uploaded-'
                  ||each_rec.id
                );
            END;
          END IF;
        END;
      END LOOP;
      BEGIN
        SELECT fault_code
        INTO v_fault_code_id
        FROM assembly
        WHERE ID = V_CURRENT_ASSEMBLY;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        v_fault_code_id := NULL;
      END;
      IF v_fault_code_id IS NULL THEN
        SELECT FAULT_CODE_SEQ.NEXTVAL INTO V_FAULT_CODE_ID FROM DUAL;
        INSERT
        INTO fault_code
          (
            ID,
            last_updated_date,
            VERSION,
            definition,
            d_internal_comments
          )
          VALUES
          (
            v_fault_code_id,
            SYSDATE,
            1,
            v_fault_code_def_id,
            'Copy Job Codes Upload'
          );
        UPDATE assembly
        SET fault_code = v_fault_code_id
        WHERE ID       = v_current_assembly;
      END IF;
      BEGIN
        SELECT id
          /*+INDEX(ACTIONNODE_DEFINEDFOR_IX) INDEX(ACTIONNODE_DEFINITION_IX)*/
        INTO v_action_node
        FROM action_node
        WHERE defined_for = v_current_assembly
        AND DEFINITION    = V_ACTION_CODE_ID;
        UPDATE action_node SET active = 1 WHERE ID = v_action_node;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        SELECT action_node_seq.NEXTVAL INTO v_action_node FROM DUAL;
        INSERT
        INTO action_node
          (
            ID,
            VERSION,
            defined_for,
            definition,
            active,
            d_internal_comments
          )
          VALUES
          (
            v_action_node,
            1,
            v_current_assembly,
            v_action_code_id,
            1,
            'Uploaded-'
            ||each_rec.id
          );
      END;
      BEGIN
        SELECT UPPER(each_rec.job_code
          ||'-'
          ||v_action_code)
        INTO v_complete_job_code
        FROM DUAL;
        SELECT ID
        INTO v_service_proc_def_id
        FROM service_procedure_definition
        WHERE code                     = v_complete_job_code
        AND UPPER (BUSINESS_UNIT_INFO) = UPPER (EACH_REC.BUSINESS_UNIT_NAME);
        v_job_code_flag               := 1;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        SELECT service_proc_definition_seq.NEXTVAL
        INTO v_service_proc_def_id
        FROM DUAL;
        INSERT
        INTO service_procedure_definition
          (
            ID,
            code,
            VERSION,
            action_definition,
            business_unit_info,
            d_internal_comments
          )
          VALUES
          (
            v_service_proc_def_id,
            v_complete_job_code,
            1,
            v_action_code_id,
            each_rec.business_unit_name,
            'Uploaded-'
            ||each_rec.id
          );
      END;
      BEGIN
        SELECT ID
          /*INDEX(SERVICEPROCEDURE_DEFINEDFOR_IX) INDEX(SERVICEPROCEDURE_DEFINITION_IX)*/
        INTO v_service_proc_id
        FROM service_procedure
        WHERE definition = v_service_proc_def_id
        AND DEFINED_FOR  = V_ACTION_NODE;
        UPDATE
          /*INDEX(SERVICE_PROCEDURE_PK)*/
          service_procedure
        SET suggested_labour_hours =
          (SELECT CAST( NVL(NVL (each_rec.labor_standard_hours, 0) + (NVL (each_rec.labor_standard_minutes, 0)/60), 0)AS DECIMAL(5,2))
          FROM DUAL
          )
        WHERE ID = v_service_proc_id;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        IF each_rec.field_modification_only = 'Y' THEN
          v_campaign_only                  := 1;
        END IF;
        SELECT service_proc_seq.NEXTVAL INTO v_service_proc_id FROM DUAL;
        INSERT
        INTO service_procedure
          (
            ID,
            for_campaigns,
            suggested_labour_hours,
            VERSION,
            definition,
            defined_for,
            d_internal_comments
          )
          VALUES
          (
            v_service_proc_id,
            v_campaign_only,
            (SELECT CAST(NVL(NVL (each_rec.labor_standard_hours, 0) + (NVL (each_rec.labor_standard_minutes, 0)/60), 0)AS DECIMAL(5,2))
            FROM DUAL
            ),
            1,
            v_service_proc_def_id,
            v_action_node,
            'Uploaded-'
            ||each_rec.id);
      END;
      IF v_job_code_flag = 0 THEN
        FOR i           IN 0 .. 3
        LOOP
          BEGIN
            SELECT DECODE (i, 0, v_system_code_id, 1, v_sub_system_code_id, 2, v_component_code_id, 3, v_sub_component_code_id)
            INTO v_curr_ass_def_id
            FROM DUAL;
            IF v_curr_ass_def_id IS NULL THEN
              EXIT;
            END IF;
            SELECT COUNT (1)
            INTO v_count
            FROM service_proc_def_comps
            WHERE service_procedure_definition = v_service_proc_def_id
            AND components                     = v_curr_ass_def_id;
            IF (v_count                       <= 0) THEN
              INSERT
              INTO service_proc_def_comps
                (
                  service_procedure_definition,
                  components,
                  list_index
                )
                VALUES
                (
                  v_service_proc_def_id,
                  v_curr_ass_def_id,
                  i
                );
            END IF;
            v_count := 0;
          END;
        END LOOP;
      END IF;
      BEGIN
        SELECT ID
        INTO v_fail_struct_id
        FROM failure_structure
        WHERE FOR_ITEM_GROUP = V_ITEM_GROUP_ID
        AND D_ACTIVE         = 1;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        SELECT failure_structure_seq.NEXTVAL INTO v_fail_struct_id FROM DUAL;
        INSERT
        INTO failure_structure
          (
            ID,
            NAME,
            VERSION,
            for_item_group,
            d_internal_comments
          )
          VALUES
          (
            v_fail_struct_id,
            NULL,
            1,
            v_item_group_id,
            'Uploaded-'
            ||each_rec.id
          );
      END;
      BEGIN
        SELECT 1
        INTO v_var
        FROM failure_structure_assemblies
        WHERE failure_structure = v_fail_struct_id
        AND ASSEMBLIES          = V_TOP_ASSEMBLY;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        INSERT
        INTO failure_structure_assemblies
          (
            failure_structure,
            assemblies
          )
          VALUES
          (
            v_fail_struct_id,
            v_top_assembly
          );
      END;
      UPDATE STG_JOB_CODE
      SET upload_status = 'Y',
        upload_date     = SYSDATE,
        upload_error    = NULL
      WHERE id          = each_rec.id;
      COMMIT;
    EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      v_upload_error := SUBSTR (SQLERRM, 0, 3500);
      UPDATE STG_JOB_CODE
      SET upload_status = 'N',
        upload_date     = SYSDATE,
        upload_error    = v_upload_error
      WHERE id          = each_rec.id;
      COMMIT;
    END;
  END LOOP; -- End of for Loop
END UPLOAD_JOB_CODE_UPLOAD;
/