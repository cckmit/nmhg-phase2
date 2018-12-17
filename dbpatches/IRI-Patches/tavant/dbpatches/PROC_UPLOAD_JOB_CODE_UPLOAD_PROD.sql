--Purpose    : Procedure to populate the data with Job Code for the model
--Author     : Jhulfikar Ali. A
--Created On : 10-Feb-09

CREATE OR REPLACE
PROCEDURE UPLOAD_JOB_CODE_UPLOAD AS
      CURSOR all_rec IS
      SELECT *
      FROM STG_JOB_CODE
      WHERE error_status = 'Y' AND NVL (upload_status, 'N') = 'N';

   --ALL GLOBAL VARIABLES DECLARED FOR A PROCEDURE
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
   v_flag                    NUMBER          := 0;
   v_current_assembly        NUMBER;
   v_fault_code_def_id       NUMBER;
   v_fault_code_id           NUMBER;
   v_var                     NUMBER;
   v_job_code_flag           NUMBER;
   v_count                   NUMBER;
   v_loop_count              NUMBER         := 0;
BEGIN
   FOR each_rec IN all_rec LOOP
      BEGIN
         --RESET THE VALUE TO ZERO
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
         v_item_group_id := NULL;
         v_parent_assembly := NULL;
         v_current_assembly := NULL;
         v_top_assembly := NULL;
         v_flag := 0;
         v_action_node := NULL;
         v_job_code_flag := 0;
         v_count := 0;
		 
         --CHECK IF THIS FAULT CODE EXIST ALREADY THEN SKIP ALL THE FAULT CODE FANCY STEPS AND DIVE INTO JOBCODE PART
         BEGIN
            SELECT /*+INDEX(FAULT_CODE_CODE_IDX)*/
                   1
            INTO   v_flag
            FROM fault_code_definition
            WHERE code = each_rec.job_code
          AND UPPER (business_unit_info) = UPPER (each_rec.business_unit_name);
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               NULL;            --THIS DOES NOT EXISTS AS YET SO DO NOTHING!!
         END;

		  dbms_output.put_line('v_flag-'||v_flag);
		  
         --GET THE ACTION CODE
         BEGIN
            SELECT ID
            INTO   v_action_code_id
            FROM action_definition
            WHERE code = each_rec.action;
         END;

         --GET THE ID OF SYSTEM CODE
         BEGIN
            SELECT ID
            INTO   v_system_code_id
            FROM assembly_definition
            WHERE code = each_rec.system_code AND assembly_level = 1;
         END;

         --GET THE ID OF SUB SYSTEM CODE
         BEGIN
            IF each_rec.sub_system_code <> '000' THEN
               SELECT ID
               INTO   v_sub_system_code_id
               FROM assembly_definition
               WHERE code = each_rec.sub_system_code AND assembly_level = 2;
            ELSE
               v_sub_system_code_id := NULL;
            END IF;
         END;

         --GET THE COMPONENT CODE
         BEGIN
            IF each_rec.component_code <> '0000' THEN
               SELECT ID
               INTO   v_component_code_id
               FROM assembly_definition
               WHERE code = each_rec.component_code AND assembly_level = 3;
            ELSE
               v_component_code_id := NULL;
            --SINCE THERE IS NO LOWER LEVEL AFTER SUB SYSTEM
            END IF;
         EXCEPTION
         WHEN OTHERS THEN
          v_component_code_id := NULL;
         END;

         --GET THE SUB COMPONENT CODE
         BEGIN
            IF each_rec.sub_component_code <> '0000' THEN
               SELECT ID
               INTO   v_sub_component_code_id
               FROM assembly_definition
               WHERE code = each_rec.sub_component_code AND assembly_level = 4;
            ELSE
               v_sub_component_code_id := NULL;
            --SINCE THERE IS NO LOWER LEVEL AFTER COMPONENT
            END IF;
         END;

         --GET THE ASSOCIATED ITEM GROUP FOR THIS JOB CODE
         BEGIN
          SELECT /*+ INDEX(ITEM_GROUP_CODE_IDX A) INDEX(ITEM_GROUP_PK B)*/
                   a.ID
          INTO v_item_group_id
          FROM item_group a, item_group b
          WHERE UPPER (a.group_code) = UPPER (each_rec.field_model)
          AND a.item_group_type = 'MODEL'
          AND a.is_part_of = b.ID
          AND UPPER (b.group_code) = UPPER (each_rec.immediate_parent_code)
          AND UPPER (a.business_unit_info) = UPPER (each_rec.business_unit_name)
          AND ROWNUM = 1;
         EXCEPTION
         WHEN OTHERS THEN
          v_item_group_id := NULL;
         END;

         IF v_flag = 0 THEN
            --GET THE CAMPAIGN DETAILS
            IF each_rec.field_modification_only = 'Y' THEN
               v_campaign_only := 1;
            END IF;

            SELECT each_rec.job_code
            INTO   v_complete_fault_code
            FROM DUAL;

            --INSERT THE RECORD INTO FAULT CODE DEFINITION
            BEGIN
               SELECT /*+INDEX(FAULT_CODE_CODE_IDX)*/
                      ID
               INTO   v_fault_code_def_id
               FROM fault_code_definition
               WHERE code = v_complete_fault_code
             AND UPPER (business_unit_info) = UPPER (each_rec.business_unit_name);
            EXCEPTION
               WHEN NO_DATA_FOUND THEN
                  --GET THE SEQUENCE FOR FAULT CODE DEFINITION
                  SELECT fault_code_definition_seq.NEXTVAL
                  INTO   v_fault_code_def_id
                  FROM DUAL;

                  INSERT INTO fault_code_definition
                              (ID, code, VERSION,
                               business_unit_info,d_internal_comments
                              )
                       VALUES (v_fault_code_def_id, v_complete_fault_code, 0,
                               each_rec.business_unit_name,'IRI-Migration-'||each_rec.id
                              );
            END;

			BEGIN
               SELECT ID
               INTO   v_fault_code_id
               FROM fault_code
               WHERE definition = v_fault_code_def_id;
			EXCEPTION
               WHEN NO_DATA_FOUND THEN
                  --GET THE FAULT CODE ID
                  SELECT fault_code_seq.NEXTVAL
                  INTO   v_fault_code_id
                  FROM DUAL;

                  --INSERT THE RECORD INTO FAULT CODE
                  INSERT INTO fault_code
                              (ID, last_updated_date, VERSION, 
                               definition,d_internal_comments
                              )
                       VALUES (v_fault_code_id, SYSDATE, 1, 
                               v_fault_code_def_id,'IRI-Migration-'||each_rec.id
                              );
            END;
			
            FOR i IN 0 .. 3 LOOP
               BEGIN
                  SELECT DECODE (i,
                                 0, v_system_code_id,
                                 1, v_sub_system_code_id,
                                 2, v_component_code_id,
                                 3, v_sub_component_code_id
                                )
                  INTO   v_curr_ass_def_id
                  FROM DUAL;

                  IF v_curr_ass_def_id IS NULL THEN
                     EXIT;
--SINCE THIS OR ANY OF THE LOWER LEVELS DO NOT EXIST JUST QUIT IT OUT BABY! :)
                  END IF;

                  SELECT COUNT (1)
                  INTO   v_count
                  FROM fault_code_def_comps
                  WHERE fault_code_definition = v_fault_code_def_id
                AND components = v_curr_ass_def_id;

                  IF (v_count <= 0) THEN
                     INSERT INTO fault_code_def_comps
                                 (fault_code_definition, components,
                                  list_index
                                 )
                          VALUES (v_fault_code_def_id, v_curr_ass_def_id,
                                  i
                                 );

                     v_count := 0;
                  END IF;
               END;
            END LOOP;
         END IF;

         --SEARCH THE FIRST NODE IN ASSEMBLY TABLE
         BEGIN
            SELECT /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX a) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX a)*/
                   a.ID
            INTO   v_current_assembly
            FROM assembly a,
               failure_structure_assemblies fsa,
               failure_structure fs
            WHERE a.assembly_definition = v_system_code_id
          AND a.is_part_of_assembly IS NULL
          AND fsa.assemblies = a.ID
          AND fsa.failure_structure = fs.ID
          AND fs.for_item_group = v_item_group_id;

            --UPDATE THE ACTIVE COLUMN OF ASSEMBLY TABLE IN CASE IT WAS NOT ACTIVE BUT WAS PRESENT ALREADY
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
                            is_part_of_assembly, fault_code, active,d_internal_comments
                           )
                    VALUES (v_current_assembly, 0,
--CHECK IF THERE IS SOME SPECIFIC LOGIC BEHIND THIS, AS IF NOW FOR CLUB CAR 0 IS JUST FINE!  :)
                            0, v_system_code_id,
                            NULL,
                                 --SINCE THIS IS THE TOP MOST LEVEL, JUST LEAVE IT BLANK!
                            NULL,
-- WE ARE NOT SURE IF THIS IS THE LEVEL WE NEED TO ASSOCIATE FAULT CODE TO; SO JUST LETS WAIT BEFORE WE UPDATE
                            1,
							'IRI-Migration-'||each_rec.id
                           );
         END;

         --SET THE PARENT ID AS THE PREVIOUS ONE
         v_parent_assembly := v_current_assembly;
         v_top_assembly := v_current_assembly;

         -- THIS IS REQUIERED TO STORE FAILURE STRUCTURE AND ASSEMBLIES MAPPING!
         IF v_sub_system_code_id IS NOT NULL THEN
            BEGIN
               SELECT /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX ) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX )*/
                      ID
               INTO   v_current_assembly
               FROM assembly
               WHERE assembly_definition = v_sub_system_code_id
             AND is_part_of_assembly = v_parent_assembly;

               --UPDATE THE ACTIVE COLUMN OF ASSEMBLY TABLE IN CASE IT WAS NOT ACTIVE BUT WAS PRESENT ALREADY
               UPDATE assembly
                  SET active = 1
                WHERE ID = v_current_assembly;
            EXCEPTION
               WHEN NO_DATA_FOUND THEN
                  --INSERT INTO ASSEMBLY TABLE
                  SELECT assembly_seq.NEXTVAL
                  INTO   v_current_assembly
                  FROM DUAL;

                  INSERT INTO assembly
                              (ID, tread_able, VERSION,
                               assembly_definition, is_part_of_assembly,
                               fault_code, active,d_internal_comments
                              )
                       VALUES (v_current_assembly, 0,
                               0,
                               v_sub_system_code_id, v_parent_assembly,
                               NULL,
                               1,
							   'IRI-Migration-'||each_rec.id
                              );
            END;
         END IF;

         --SET THE PARENT ID AS THE PREVIOUS ONE
         v_parent_assembly := v_current_assembly;

         IF v_component_code_id IS NOT NULL THEN
            BEGIN
               SELECT /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX ) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX )*/
                      ID
               INTO   v_current_assembly
               FROM assembly
               WHERE assembly_definition = v_component_code_id
             AND is_part_of_assembly = v_parent_assembly;

               --UPDATE THE ACTIVE COLUMN OF ASSEMBLY TABLE IN CASE IT WAS NOT ACTIVE BUT WAS PRESENT ALREADY
               UPDATE assembly
                  SET active = 1
                WHERE ID = v_current_assembly;
            EXCEPTION
               WHEN NO_DATA_FOUND THEN
                  --INSERT INTO ASSEMBLY TABLE
                  SELECT assembly_seq.NEXTVAL
                  INTO   v_current_assembly
                  FROM DUAL;

                  INSERT INTO assembly
                              (ID, tread_able, VERSION, assembly_definition,
                               is_part_of_assembly, fault_code, active, d_internal_comments
                              )
                       VALUES (v_current_assembly, 0,
--CHECK IF THERE IS SOME SPECIFIC LOGIC BEHIND THIS, AS IF NOW FOR CLUB CAR 0 IS JUST FINE!  :)
                               0, v_component_code_id,
                               v_parent_assembly, NULL,
-- WE ARE NOT SURE IF THIS IS THE LEVEL WE NEED TO ASSOCIATE FAULT CODE TO; SO JUST LETS WAIT BEFORE WE UPDATE
                               1,'IRI-Migration-'||each_rec.id
                              );
            END;
         END IF;

         --SET THE PARENT ID AS THE PREVIOUS ONE
         v_parent_assembly := v_current_assembly;

         IF v_sub_component_code_id IS NOT NULL THEN
            BEGIN
               SELECT /*+INDEX(ASSEMBLY_ASSEMBLYDEFINITION_IX ) INDEX(ASSEMBLY_ISPARTOFASSEMBLY_IX )*/
                      ID
               INTO   v_current_assembly
               FROM assembly
               WHERE assembly_definition = v_sub_component_code_id
             AND is_part_of_assembly = v_parent_assembly;

               --UPDATE THE ACTIVE COLUMN OF ASSEMBLY TABLE IN CASE IT WAS NOT ACTIVE BUT WAS PRESENT ALREADY
               UPDATE assembly
                  SET active = 1
                WHERE ID = v_current_assembly;
            EXCEPTION
               WHEN NO_DATA_FOUND THEN
                  --INSERT INTO ASSEMBLY TABLE
                  SELECT assembly_seq.NEXTVAL
                  INTO   v_current_assembly
                  FROM DUAL;

                  INSERT INTO assembly
                              (ID, tread_able, VERSION,
                               assembly_definition, is_part_of_assembly,
                               fault_code, active,d_internal_comments
                              )
                       VALUES (v_current_assembly, 0,
--CHECK IF THERE IS SOME SPECIFIC LOGIC BEHIND THIS, AS IF NOW FOR CLUB CAR 0 IS JUST FINE!  :)
                               0,
                               v_sub_component_code_id, v_parent_assembly,
                               NULL,
-- WE ARE NOT SURE IF THIS IS THE LEVEL WE NEED TO ASSOCIATE FAULT CODE TO; SO JUST LETS WAIT BEFORE WE UPDATE
                               1,'IRI-Migration-'||each_rec.id
                              );
            END;
         END IF;

         --SET THE PARENT ID AS THE PREVIOUS ONE
         v_parent_assembly := v_current_assembly;

         --GET THE ID OF THE FAULT CODE IF THE ID IS NULL
         BEGIN
            SELECT each_rec.job_code
            INTO   v_complete_fault_code
            FROM DUAL;
			
            SELECT /*+INDEX(FAULT_CODE_CODE_IDX)*/
                   b.ID
            INTO   v_fault_code_id
            FROM fault_code_definition a, fault_code b
            WHERE code = v_complete_fault_code
          AND b.definition = a.ID
          AND a.business_unit_info = each_rec.business_unit_name;
		  
         --AND ROWNUM = 1;
         END;

         --UPDATE THE FAULT CODE RECORD IN THE LAST RECORD IN THE CHAIN OF ALL THE LEVELS
         UPDATE assembly
            SET fault_code = v_fault_code_id
          WHERE ID = v_parent_assembly;

         --THIS WOULD THE LAST ENDING LEVEL IN CHAIN.
         BEGIN
            SELECT /*+INDEX(ACTIONNODE_DEFINEDFOR_IX) INDEX(ACTIONNODE_DEFINITION_IX)*/
                   ID
            INTO   v_action_node
            FROM action_node
            WHERE defined_for = v_parent_assembly
          AND definition = v_action_code_id;

            UPDATE action_node
               SET active = 1
             WHERE ID = v_action_node;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               --GET THE SEQUENCE IN THE VARIABLE
               SELECT action_node_seq.NEXTVAL
               INTO   v_action_node
               FROM DUAL;

               --INSERT THE RECORD IN ACTION NODE TABLE
               INSERT INTO action_node
                           (ID, VERSION, defined_for,
                            definition, active, d_internal_comments
                           )
                    VALUES (v_action_node, 1, v_parent_assembly,
                            v_action_code_id, 1, 'IRI-Migration-'||each_rec.id
                           );
         END;

         BEGIN
            --FORM THE COMPLETE JOB CODE FOR THE ENTRY IN TABLE
            SELECT each_rec.job_code
            INTO   v_complete_job_code
            FROM DUAL;

            --CHECK IF THIS JOB CODE ALREADY EXISTS
            SELECT ID
            INTO   v_service_proc_def_id
            FROM service_procedure_definition
            WHERE code = v_complete_job_code
          AND UPPER (business_unit_info) = UPPER (each_rec.business_unit_name);

            v_job_code_flag := 1;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               --GET THE SEQUENCE FOR SERVICE PROCEDURE DEFINITION
               SELECT service_proc_definition_seq.NEXTVAL
               INTO   v_service_proc_def_id
               FROM DUAL;

               --INSERT THE RECORD INTO  SERVICE_PROCEDURE_DEFINITION
               INSERT INTO service_procedure_definition
                           (ID, code, VERSION,
                            action_definition, business_unit_info, d_internal_comments
                           )
                    VALUES (v_service_proc_def_id, v_complete_job_code, 1,
                            v_action_code_id, each_rec.business_unit_name, 'IRI-Migration-'||each_rec.id
                           );
         END;

         --ENTER IN SERVICCE PROCEDURE TABLE ONLY IF RECORD DOESN'T EXIST ALREADY
         BEGIN
            SELECT /*INDEX(SERVICEPROCEDURE_DEFINEDFOR_IX) INDEX(SERVICEPROCEDURE_DEFINITION_IX)*/ ID
            INTO    v_service_proc_id
            FROM service_procedure
            WHERE definition = v_service_proc_def_id
          AND defined_for = v_action_node;

            --JUST UPDATE THE HOURS
            UPDATE /*INDEX(SERVICE_PROCEDURE_PK)*/ service_procedure
               SET suggested_labour_hours =
                                (SELECT NVL(NVL (each_rec.labor_standard_hours, 0) + 
					(ROUND(NVL (each_rec.labor_standard_minutes, 0)/60,2)), 0) FROM DUAL)
                                 FROM DUAL)
             WHERE ID = v_service_proc_id;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               --GET THE SEQUENCE FOR SERVICE PROCEDURE
               SELECT service_proc_seq.NEXTVAL
               INTO   v_service_proc_id
               FROM DUAL;

               --INSERT THE RECORD INTO  SERVICE_PROCEDURE
               INSERT INTO service_procedure
                           (ID, for_campaigns,
                            suggested_labour_hours,
                            VERSION, definition, defined_for, d_internal_comments
                           )
                    VALUES (v_service_proc_id, v_campaign_only,
                            (SELECT NVL(NVL (each_rec.labor_standard_hours, 0) + 
					(ROUND (NVL (each_rec.labor_standard_minutes, 0)/60, 2)), 0) FROM DUAL),
                            1, v_service_proc_def_id, v_action_node, 'IRI-Migration-'||each_rec.id
                           );
         END;

         --ADD COMPONENTS ONLY IF JOB CODES DIDN'T EXIST ALREADY!
         IF v_job_code_flag = 0 THEN
            --INSERT THE VALUES IN SERVICE PROC DEF COMPS
            FOR i IN 0 .. 3 LOOP
               BEGIN
                  SELECT DECODE (i,
                                 0, v_system_code_id,
                                 1, v_sub_system_code_id,
                                 2, v_component_code_id,
                                 3, v_sub_component_code_id
                                )
                  INTO   v_curr_ass_def_id
                  FROM DUAL;

                  IF v_curr_ass_def_id IS NULL THEN
                     EXIT;
--SINCE THIS OR ANY OF THE LOWER LEVELS DO NOT EXIST JUST QUIT IT OUT BABY! :)
                  END IF;

                  SELECT COUNT (1)
                  INTO   v_count
                  FROM service_proc_def_comps
                  WHERE service_procedure_definition = v_service_proc_def_id
                AND components = v_curr_ass_def_id;

                  IF (v_count <= 0) THEN
                     INSERT INTO service_proc_def_comps
                                 (service_procedure_definition, components,
                                  list_index
                                 )
                          VALUES (v_service_proc_def_id, v_curr_ass_def_id,
                                  i
                                 );
                  END IF;

                  v_count := 0;
               END;
            END LOOP;
         END IF;

         --SEE IF A FAILURE STRUCTURE ALREADY EXIST WITH THE PROVIDED MODEL
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

               --SINCE IT DOES NOT EXISTS CREATE THIS RECORD
               INSERT INTO failure_structure
                           (ID, NAME, VERSION, for_item_group, d_internal_comments
                           )
                    VALUES (v_fail_struct_id, NULL,
--SEE IF THIS NAME HAS ANY SPECIAL LOGIC BEHIND IT? RIGHT NOW SUITABLE VALUE SEEMS TO BE NULL
                            1, v_item_group_id, 'IRI-Migration-'||each_rec.id
                           );
         END;

         --INSERT INTO FAILURE_STRUCTURE_ASSEMBLIES MAPPING TABLE IF IT DOESNT EXIST ALREADY!
         BEGIN
            SELECT 1
            INTO   v_var
            FROM failure_structure_assemblies
            WHERE failure_structure = v_fail_struct_id
          AND assemblies = v_top_assembly;
         EXCEPTION
            WHEN NO_DATA_FOUND THEN
               INSERT INTO failure_structure_assemblies
                           (failure_structure, assemblies
                           )
                    VALUES (v_fail_struct_id, v_top_assembly
                           );
         END;

         --UPDATE RECORD WITH ERROR MESSAGE
         UPDATE STG_JOB_CODE
            SET upload_status = 'Y',
                upload_date = SYSDATE,
                upload_error = NULL
          WHERE id = each_rec.id;
         COMMIT;
      EXCEPTION
         WHEN OTHERS THEN
            --FIRST ROLLBACK
            ROLLBACK;
            --GET THE ERROR MESSAGE
            v_upload_error := SUBSTR (SQLERRM, 0, 3500);

            --UPDATE RECORD WITH ERROR MESSAGE
            UPDATE STG_JOB_CODE
            SET upload_status = 'N',
                   upload_date = SYSDATE,
                   upload_error = v_upload_error
            WHERE id = each_rec.id;

            --COMMIT UPDATE STATEMENT
            COMMIT;
      END;
      
      v_loop_count := v_loop_count + 1;
      
      IF v_loop_count = 10 THEN
         --DO A COMMIT FOR 10 RECORDS
         COMMIT;
         v_loop_count := 0; -- Initialize the count size
      END IF;

   END LOOP; -- End of for Loop
  
END UPLOAD_JOB_CODE_UPLOAD;
/
COMMIT
/