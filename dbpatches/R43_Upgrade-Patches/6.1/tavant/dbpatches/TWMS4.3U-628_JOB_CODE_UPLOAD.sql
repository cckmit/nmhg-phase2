--Purpose : TWMS4.3U-628
--Author : raghuram.d
--Date : 26/May/2011

ALTER TABLE stg_job_code ADD model_id NUMBER(19,0)
/
create or replace
PROCEDURE UPLOAD_JOB_CODE_VALIDATION
AS
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_JOB_CODE
    WHERE NVL(ERROR_STATUS,'N') = 'N'
    AND UPLOAD_STATUS          IS NULL
    ORDER BY ID ASC;
  v_error_code         VARCHAR2(4000) := NULL;
  v_error              VARCHAR2(4000) := NULL;
  v_flag               BOOLEAN        := NULL;
  v_count              NUMBER;
  v_item_group_id      NUMBER;
  v_assem_id           NUMBER;
  v_file_upload_mgt_id NUMBER        := 0;
  v_success_count      NUMBER        := 0;
  v_error_count        NUMBER        := 0;
  v_loop_count         NUMBER        := 0;
  v_uploaded_by        VARCHAR2(255) := NULL;
  v_system_code        VARCHAR2(255) := NULL;
  v_sub_system_code    VARCHAR2(255) := NULL;
  v_component_code     VARCHAR2(255) := NULL;
  v_sub_component_code VARCHAR2(255) := NULL;
  v_job_code           VARCHAR2(255) := NULL;
  v_index              NUMBER;
  v_complete_job_code  VARCHAR2(255) := NULL;
  v_action_code        VARCHAR2(255) := NULL;
  v_bu_name            VARCHAR2(255) := NULL;
  v_valid_bu           BOOLEAN       := FALSE;
  v_valid_product      BOOLEAN       := FALSE;
  v_valid_jc           BOOLEAN       := FALSE;
BEGIN
  BEGIN
    SELECT u.login,
      f.business_unit_info
    INTO v_uploaded_by,
      v_bu_name
    FROM org_user u,
      file_upload_mgt f
    WHERE u.id = f.uploaded_by
    AND f.id   =
      (SELECT file_upload_mgt_id FROM stg_job_code WHERE rownum = 1
      );
  EXCEPTION
  WHEN OTHERS THEN
    NULL;
  END;
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      v_error_code         := NULL;
      v_system_code        := NULL;
      v_sub_system_code    := NULL;
      v_component_code     := NULL;
      v_sub_component_code := NULL;
      v_job_code           := NULL;
      v_complete_job_code  := NULL;
      v_action_code        := NULL;
	  v_item_group_id      := NULL;

      v_valid_bu                     := FALSE;
      IF each_rec.business_unit_name IS NULL THEN
        v_error_code                 := common_utils.addErrorMessage(v_error_code, 'JC001');
      ELSIF UPPER(v_bu_name)         != UPPER(each_rec.business_unit_name) THEN
        v_error_code                 := common_utils.addErrorMessage(v_error_code, 'JC002');
      ELSIF NOT common_validation_utils.isUserBelongsToBU(each_rec.business_unit_name, v_uploaded_by) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC002');
      ELSE
        v_valid_bu := TRUE;
      END IF;
      v_valid_product          := FALSE;
      IF each_rec.product_code IS NULL THEN
        v_error_code           := common_utils.addErrorMessage(v_error_code, 'JC003');
      ELSIF NOT common_validation_utils.isValidProductCode(each_rec.product_code, v_bu_name) THEN
        v_error_code := Common_Utils.addErrorMessage(v_error_code, 'JC004');
      ELSE
        v_valid_product := TRUE;
      END IF;
      IF each_rec.field_model IS NULL THEN
        v_error_code          := common_utils.addErrorMessage(v_error_code, 'JC005');
      ELSE
		v_item_group_id := common_validation_utils.getModelForModelCodeAndProduct( each_rec.field_model, each_rec.product_code, v_bu_name);
        IF v_item_group_id IS NULL THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'JC006');
		END IF;
      END IF;
      v_valid_jc           := FALSE;
      IF each_rec.job_code IS NULL THEN
        v_error_code       := common_utils.addErrorMessage(v_error_code, 'JC007');
      ELSIF NOT common_validation_utils.isValidDelimitedValue(each_rec.job_code, '-') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'JC013');
      ELSE
        v_valid_jc     := TRUE;
        v_count        := common_utils.count_delimited_values(each_rec.job_code, '-');
        IF v_count      > 4 THEN
          v_error_code := common_utils.addErrorMessage(v_error_code, 'JC013');
          v_valid_jc   := FALSE;
        ELSE
          v_job_code   := each_rec.job_code;
          v_index      := 1;
          WHILE v_index > 0
          LOOP
            v_index                                  := INSTR(v_job_code, '-0000',   -1, 1);
            IF (v_index                                                              + 5)=LENGTH(v_job_code) THEN
              v_job_code                             := SUBSTR(v_job_code, 1, v_index-1);
            ELSE
              v_index := 0;
            END IF;
          END LOOP;
          v_count                := common_utils.count_delimited_values(v_job_code, '-');
          IF v_count              > 3 THEN
            v_sub_component_code := common_utils.get_delimited_value(v_job_code, '-', 4);
            IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_sub_component_code,4) THEN
              v_error_code := common_utils.addErrorMessage(v_error_code, 'JC014');
              v_valid_jc   := FALSE;
            END IF;
          END IF;
          IF v_count          > 2 THEN
            v_component_code := common_utils.get_delimited_value(v_job_code, '-', 3);
            IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_component_code,3) THEN
              v_error_code := common_utils.addErrorMessage(v_error_code, 'JC015');
              v_valid_jc   := FALSE;
            END IF;
          END IF;
          IF v_count           > 1 THEN
            v_sub_system_code := common_utils.get_delimited_value(v_job_code, '-', 2);
            IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_sub_system_code,2) THEN
              v_error_code := common_utils.addErrorMessage(v_error_code, 'JC016');
              v_valid_jc   := FALSE;
            END IF;
          END IF;
          IF v_count       > 0 THEN
            v_system_code := common_utils.get_delimited_value(v_job_code, '-', 1);
            IF NOT common_validation_utils.isValidAssemblyDefinitionCode(v_system_code,1) THEN
              v_error_code := common_utils.addErrorMessage(v_error_code, 'JC017');
              v_valid_jc   := FALSE;
            END IF;
          END IF;
        END IF;
      END IF;
      IF each_rec.action IS NULL THEN
       v_error_code     := common_utils.addErrorMessage(v_error_code, 'JC009');
      v_valid_jc       := FALSE;
      ELSIF NOT common_validation_utils.isValidActionCode(each_rec.action) THEN
       v_error_code := common_utils.addErrorMessage(v_error_code, 'JC018');
      v_valid_jc   := FALSE;
      END IF;
      IF v_valid_jc AND v_item_group_id IS NOT NULL THEN
        BEGIN
          v_complete_job_code := v_job_code || '-' || each_rec.action;
          SELECT 1
          INTO v_assem_id
          FROM dual
          WHERE EXISTS
            (SELECT an.defined_for
            FROM service_procedure_definition spd,
              service_procedure sp,
              action_node an
            WHERE spd.code                    = v_complete_job_code
            AND UPPER(spd.business_unit_info) = UPPER(v_bu_name)
            AND sp.definition                 = spd.id
            AND sp.defined_for                = an.id
            INTERSECT
            SELECT id
            FROM assembly
              CONNECT BY PRIOR id = is_part_of_assembly
              START WITH id      IN
              (SELECT assemblies
              FROM failure_structure_assemblies a,
                failure_structure b
              WHERE a.failure_structure = b.id
              AND b.for_item_group      = v_item_group_id
              )
            );
          v_error_code := common_utils.addErrorMessage(v_error_code, 'JC008');
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          NULL; --DO NOTHING
        END;
      END IF;
      IF NVL(each_rec.labor_standard_hours,0) < 0 AND NVL(each_rec.labor_standard_minutes,0) < 0 THEN
        v_error_code                         := common_utils.addErrorMessage(v_error_code, 'JC010');
      ELSE
        IF NVL(each_rec.labor_standard_hours,0) < 0 AND NVL(each_rec.labor_standard_hours,0) > 99 THEN
          v_error_code                         := common_utils.addErrorMessage(v_error_code, 'JC011');
        END IF;
        IF NOT common_utils.isPositiveInteger(each_rec.labor_standard_minutes) THEN
          v_error_code := common_utils.addErrorMessage(v_error_code, 'JC020');
        END IF;
      END IF;
      IF each_rec.field_modification_only               IS NULL THEN
        v_error_code                                    := common_utils.addErrorMessage(v_error_code, 'JC012');
      ELSIF UPPER(each_rec.field_modification_only) NOT IN ('Y','N') THEN
        v_error_code                                    := common_utils.addErrorMessage(v_error_code, 'JC019');
      END IF;
      IF v_error_code IS NULL THEN
        UPDATE stg_job_code
        SET error_status = 'Y',
          error_code     = NULL,
		  model_id = v_item_group_id,
		  job_code = v_job_code
        WHERE id         = each_rec.id;
      ELSE
        UPDATE stg_job_code
        SET error_status = 'N',
          error_code     = v_error_code
        WHERE id         = each_rec.id;
      END IF;
      v_loop_count   := v_loop_count + 1;
      IF v_loop_count = 10 THEN
        COMMIT;
        v_loop_count := 0;
      END IF;
    EXCEPTION
    WHEN OTHERS THEN
      v_error := SUBSTR(SQLERRM, 1, 4000);
      UPDATE stg_job_code
      SET ERROR_CODE = V_ERROR,
      error_status = 'N'
      WHERE id         = each_rec.id;
    END;
  END LOOP;
      IF v_loop_count > 0 THEN
        COMMIT;
      END IF;
  BEGIN
    SELECT DISTINCT file_upload_mgt_id
    INTO v_file_upload_mgt_id
    FROM stg_job_code
    WHERE ROWNUM < 2;
    BEGIN
      SELECT COUNT(*)
      INTO v_success_count
      FROM stg_job_code
      WHERE file_upload_mgt_id = v_file_upload_mgt_id
      AND error_status         = 'Y';
    EXCEPTION
    WHEN OTHERS THEN
      v_success_count := 0;
    END;
    BEGIN
      SELECT COUNT(*)
      INTO v_error_count
      FROM stg_job_code
      WHERE file_upload_mgt_id = v_file_upload_mgt_id
      AND error_status         = 'N';
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;
    SELECT COUNT(*)
    INTO v_count
    FROM stg_job_code
    WHERE file_upload_mgt_id = v_file_upload_mgt_id;
    UPDATE file_upload_mgt
    SET success_records = v_success_count,
      error_records     = v_error_count,
      total_records     = v_count
    WHERE ID            = V_FILE_UPLOAD_MGT_ID;
  EXCEPTION
  WHEN OTHERS THEN
    v_error := SUBSTR(SQLERRM, 1, 4000);
    UPDATE file_upload_mgt
    SET ERROR_MESSAGE = V_ERROR
    WHERE ID          = V_FILE_UPLOAD_MGT_ID;
  END;
  COMMIT; -- Final Commit for the procedure
END upload_job_code_validation;
/
create or replace
PROCEDURE UPLOAD_JOB_CODE_POPULATION
AS
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_JOB_CODE
    WHERE NVL(error_status, 'N') = 'Y'
    AND UPLOAD_STATUS           IS NULL
    ORDER BY ID ASC;
  v_immediate_parent   NUMBER        := 0;
  v_error_count        NUMBER        := 0;
  v_file_upload_mgt_id NUMBER        := 0;
  v_system_code        VARCHAR2(255) := NULL;
  v_sub_system_code    VARCHAR2(255) := NULL;
  v_component_code     VARCHAR2(255) := NULL;
  v_sub_component_code VARCHAR2(255) := NULL;
  V_JOB_CODE           VARCHAR2(255) := NULL;
  v_count              NUMBER;
  V_BU_NAME            VARCHAR(255) := NULL;
  v_scheme             NUMBER;
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      v_system_code            := NULL;
      v_sub_system_code        := NULL;
      v_component_code         := NULL;
      v_sub_component_code     := NULL;
      v_job_code               := NULL;
      v_scheme                 := 0;
      v_bu_name                := common_validation_utils.getValidBusinessUnitName(each_rec.business_unit_name);
      v_count                  := common_utils.count_delimited_values(each_rec.job_code, '-');
      v_job_code               := each_rec.job_code;
      IF v_count                > 3 THEN
        v_sub_component_code   := common_utils.get_delimited_value(v_job_code, '-', 4);
        IF v_sub_component_code = '0000' THEN
          v_sub_component_code := NULL;
        END IF;
      END IF;
      IF v_count            > 2 THEN
        v_component_code   := common_utils.get_delimited_value(v_job_code, '-', 3);
        IF v_component_code = '0000' THEN
          v_component_code := NULL;
        END IF;
      END IF;
      IF v_count             > 1 THEN
        v_sub_system_code   := common_utils.get_delimited_value(v_job_code, '-', 2);
        IF v_sub_system_code = '0000' THEN
          v_sub_system_code := NULL;
        END IF;
      END IF;
      IF v_count       > 0 THEN
        v_system_code := common_utils.get_delimited_value(v_job_code, '-', 1);
      END IF;
     
      UPDATE stg_job_code
      SET system_code         = v_system_code,
        sub_system_code       = v_sub_system_code,
        component_code        = v_component_code,
        sub_component_code    = v_sub_component_code,
        business_unit_name    = v_bu_name
      WHERE id                = each_rec.id;
      COMMIT;
    END;
  END LOOP;
END UPLOAD_JOB_CODE_POPULATION;
/
create or replace
PROCEDURE                 UPLOAD_JOB_CODE_UPLOAD AS

    CURSOR all_rec IS
        SELECT *
        FROM STG_JOB_CODE
        WHERE error_status = 'Y' AND NVL (upload_status, 'N') = 'N';

    v_upload_error            VARCHAR2 (4000) := NULL;
    v_service_proc_def_id     VARCHAR2 (255)  := NULL;
    v_service_proc_id         VARCHAR2 (255)  := NULL;
    v_system_code_id          VARCHAR2 (255)  := NULL;
    v_sub_system_code_id      VARCHAR2 (255)  := NULL;
    v_component_code_id       VARCHAR2 (255)  := NULL;
    v_sub_component_code_id   VARCHAR2 (255)  := NULL;
    v_action_code_id          VARCHAR2 (255)  := NULL;
    v_campaign_only           VARCHAR2 (255)  := NULL;
    v_assembly_id             VARCHAR2 (255)  := NULL;
    v_temp_assembly_id        VARCHAR2 (255)  := NULL;
    v_action_node             VARCHAR2 (255)  := NULL;
    v_curr_ass_def_id         VARCHAR2 (255)  := NULL;
    v_fail_struct_id          VARCHAR2 (255)  := NULL;
    v_item_group_id           VARCHAR2 (255)  := NULL;
    v_parent_assembly         VARCHAR2 (255)  := NULL;
    v_top_assembly            VARCHAR2 (255)  := NULL;
    v_complete_job_code       VARCHAR2 (4000) := NULL;
    v_complete_fault_code     VARCHAR2 (4000) := NULL;
    v_current_assembly        NUMBER;
    v_fault_code_def_id       NUMBER;
    v_fault_code_id           NUMBER;
    v_code_id                 NUMBER;
    v_var                     NUMBER;
    v_job_code_flag           NUMBER;
    v_count                   NUMBER;
    v_loop_count              NUMBER := 0;
    v_action_code             VARCHAR(255) := NULL;

BEGIN
    FOR each_rec IN all_rec LOOP
    BEGIN

         v_fault_code_def_id := NULL;
         v_fault_code_id := NULL;
         v_service_proc_def_id := NULL;
         v_service_proc_id := NULL;
         v_system_code_id := NULL;
         v_sub_system_code_id := NULL;
         v_component_code_id := NULL;
         v_sub_component_code_id := NULL;
         v_action_code_id := NULL;
         v_campaign_only := 0;
         v_assembly_id := NULL;
         v_temp_assembly_id := NULL;
         v_complete_fault_code := NULL;
         v_curr_ass_def_id := NULL;
         v_complete_job_code := NULL;
         v_fail_struct_id := NULL;
         v_item_group_id := each_rec.model_id;
         v_parent_assembly := NULL;
         v_current_assembly := NULL;
         v_top_assembly := NULL;
         v_action_node := NULL;
         v_job_code_flag := 0;
         v_count := 0;
         v_action_code := NULL;

        SELECT id, code INTO v_action_code_id, v_action_code FROM action_definition
        WHERE LOWER(CODE) = LOWER(each_rec.action) and d_active=1;

        SELECT id INTO v_system_code_id FROM assembly_definition 
        WHERE lower(code)=lower(each_rec.system_code) AND assembly_level=1 and d_active=1;

        IF each_rec.sub_system_code IS NOT NULL AND each_rec.sub_system_code!='000'  THEN
            SELECT id INTO v_sub_system_code_id FROM assembly_definition 
            WHERE lower(code)=lower(each_rec.sub_system_code) AND assembly_level=2 
				 and d_active=1 AND NAME !='NO LOWER LEVEL';
            ELSE v_sub_system_code_id:=NULL;
        END IF;

        IF each_rec.component_code IS NOT NULL AND each_rec.component_code!='0000' THEN
            SELECT id INTO v_component_code_id FROM assembly_definition 
            WHERE lower(code)=lower(each_rec.component_code) AND assembly_level=3
				 and d_active=1;
        END IF;

        IF each_rec.sub_component_code IS NOT NULL  AND each_rec.sub_component_code!='0000' THEN
            SELECT id INTO v_sub_component_code_id FROM assembly_definition 
            WHERE lower(code)=lower(each_rec.sub_component_code) AND assembly_level=4
				 and d_active=1;
        END IF;

        v_complete_fault_code := UPPER(each_rec.job_code);

        BEGIN
           SELECT ID    /*+INDEX(FAULT_CODE_CODE_IDX)*/
           INTO   v_fault_code_def_id
           FROM fault_code_definition
           WHERE UPPER(code) = v_complete_fault_code
                AND UPPER (business_unit_info) = UPPER (each_rec.business_unit_name);
        EXCEPTION
            WHEN NO_DATA_FOUND THEN      

                SELECT fault_code_definition_seq.NEXTVAL
                INTO   v_fault_code_def_id
                FROM DUAL;

                INSERT INTO fault_code_definition 
                        (ID, code, VERSION,
                         business_unit_info,d_internal_comments)
                VALUES (V_FAULT_CODE_DEF_ID, V_COMPLETE_FAULT_CODE, 0,
                        each_rec.business_unit_name,'Uploaded-'||each_rec.id);
        END;

        FOR i IN 0 .. 3 LOOP
        BEGIN
            SELECT DECODE (i,
                         0, v_system_code_id,
                         1, v_sub_system_code_id,
                         2, v_component_code_id,
                         3, v_sub_component_code_id
                        )
            INTO v_curr_ass_def_id
            FROM DUAL;

            IF v_curr_ass_def_id IS NULL THEN
                EXIT;
            END IF;

            SELECT COUNT (1)
            INTO   v_count
            FROM fault_code_def_comps
            WHERE fault_code_definition = v_fault_code_def_id
                AND components = v_curr_ass_def_id;

            IF (v_count <= 0) THEN

                INSERT INTO fault_code_def_comps
                         (fault_code_definition, components,list_index)
                VALUES (v_fault_code_def_id, v_curr_ass_def_id,i);

                v_count := 0;
            END IF;

        END;
        END LOOP;


        BEGIN
            SELECT a.ID        /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX a) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX a)*/
            INTO   v_current_assembly
            FROM assembly a,
                failure_structure_assemblies fsa,
                failure_structure fs
            WHERE a.assembly_definition = v_system_code_id
                AND a.is_part_of_assembly IS NULL
                AND fsa.assemblies = a.ID
                AND fsa.failure_structure = fs.ID
                AND fs.for_item_group = v_item_group_id;

            UPDATE assembly
            SET active = 1
            WHERE ID = v_current_assembly;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                SELECT assembly_seq.NEXTVAL
                INTO   v_current_assembly
                FROM DUAL;

                INSERT INTO assembly
                           (ID, tread_able, VERSION, assembly_definition,
                            is_part_of_assembly, fault_code, active,d_internal_comments)
                VALUES (v_current_assembly, 0, 0, v_system_code_id,
                        NULL, NULL, 1, 'Uploaded-'||each_rec.id);
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

                SELECT ID       /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX ) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX )*/
                INTO   v_current_assembly
                FROM assembly
                WHERE assembly_definition = v_code_id
                    AND is_part_of_assembly = v_parent_assembly;

                UPDATE assembly
                SET active = 1
                WHERE ID = v_current_assembly;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN      
                    SELECT assembly_seq.NEXTVAL
                    INTO   v_current_assembly
                    FROM DUAL;

                    INSERT INTO assembly
                            (ID, tread_able, VERSION,
                             assembly_definition, is_part_of_assembly,
                             fault_code, active,d_internal_comments)
                    VALUES (v_current_assembly, 0, 0,
                            v_code_id, v_parent_assembly,
                            NULL, 1, 'Uploaded-'||each_rec.id);
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
            SELECT id       /*+INDEX(ACTIONNODE_DEFINEDFOR_IX) INDEX(ACTIONNODE_DEFINITION_IX)*/
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
                        v_action_code_id, 1, 'Uploaded-'||each_rec.id);
        END;



        BEGIN    
            SELECT UPPER(each_rec.job_code||'-'||v_action_code)
            INTO   v_complete_job_code
            FROM DUAL;

            SELECT ID
            INTO   v_service_proc_def_id
            FROM service_procedure_definition
            WHERE code = v_complete_job_code
                AND UPPER (business_unit_info) = UPPER (each_rec.business_unit_name);

            v_job_code_flag := 1;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN    
               SELECT service_proc_definition_seq.NEXTVAL
               INTO   v_service_proc_def_id
               FROM DUAL;

               INSERT INTO service_procedure_definition
                        (ID, code, VERSION,
                        action_definition, business_unit_info, d_internal_comments)
                VALUES (v_service_proc_def_id, v_complete_job_code, 1,
                        v_action_code_id, each_rec.business_unit_name, 'Uploaded-'||each_rec.id);
        END;


        BEGIN
            SELECT ID       /*INDEX(SERVICEPROCEDURE_DEFINEDFOR_IX) INDEX(SERVICEPROCEDURE_DEFINITION_IX)*/ 
            INTO    v_service_proc_id
            FROM service_procedure
            WHERE definition = v_service_proc_def_id
                AND defined_for = v_action_node;

            UPDATE /*INDEX(SERVICE_PROCEDURE_PK)*/ service_procedure
            SET suggested_labour_hours =
                    (SELECT cast( NVL(NVL (each_rec.labor_standard_hours, 0) + 
					    (NVL (each_rec.labor_standard_minutes, 0)/60), 0)as decimal(5,2)) FROM DUAL)
            WHERE ID = v_service_proc_id;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                IF each_rec.field_modification_only = 'Y' THEN
                    v_campaign_only := 1;
                END IF;

                SELECT service_proc_seq.NEXTVAL
                INTO   v_service_proc_id
                FROM DUAL;

                INSERT INTO service_procedure
                        (ID, for_campaigns,
                        suggested_labour_hours,
                        VERSION, definition, defined_for, d_internal_comments)
                VALUES (v_service_proc_id, v_campaign_only,
                        (SELECT CAST(NVL(NVL (each_rec.labor_standard_hours, 0) + 
					    (NVL (each_rec.labor_standard_minutes, 0)/60), 0)as decimal(5,2)) FROM DUAL),
                        1, v_service_proc_def_id, v_action_node, 'Uploaded-'||each_rec.id);
        END;



        IF v_job_code_flag = 0 THEN

            FOR i IN 0 .. 3 LOOP
            BEGIN
                SELECT DECODE (i,
                                0, v_system_code_id,
                                1, v_sub_system_code_id,
                                2, v_component_code_id,
                                3, v_sub_component_code_id)
                INTO   v_curr_ass_def_id
                FROM DUAL;

                IF v_curr_ass_def_id IS NULL THEN
                    EXIT;
                END IF;

                SELECT COUNT (1)
                INTO   v_count
                FROM service_proc_def_comps
                WHERE service_procedure_definition = v_service_proc_def_id
                    AND components = v_curr_ass_def_id;

                IF (v_count <= 0) THEN
                    INSERT INTO service_proc_def_comps
                            (service_procedure_definition, components,list_index)
                    VALUES (v_service_proc_def_id, v_curr_ass_def_id,i);
                END IF;
                v_count := 0;
            END;
            END LOOP;
        END IF;


        BEGIN
            SELECT ID
            INTO   v_fail_struct_id
            FROM failure_structure
            WHERE for_item_group = v_item_group_id;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                SELECT failure_structure_seq.NEXTVAL
                INTO   v_fail_struct_id
                FROM DUAL;

                INSERT INTO failure_structure
                        (ID, NAME, VERSION, for_item_group, d_internal_comments)
                VALUES (v_fail_struct_id, NULL,1, v_item_group_id, 'Uploaded-'||each_rec.id);
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


        UPDATE STG_JOB_CODE
        SET upload_status = 'Y',
            upload_date = SYSDATE,
            upload_error = NULL
        WHERE id = each_rec.id;
        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN    
            ROLLBACK;
            v_upload_error := SUBSTR (SQLERRM, 0, 3500);

            UPDATE STG_JOB_CODE
            SET upload_status = 'N',
                upload_date = SYSDATE,
                upload_error = v_upload_error
            WHERE id = each_rec.id;
            COMMIT;
    END;

    END LOOP; -- End of for Loop

END UPLOAD_JOB_CODE_UPLOAD;
/