--Purpose    : Procedure to upload the data with Job Code/Fault Found for the model
--Author     : Jhulfikar Ali. A
--Created On : 11-Feb-09

CREATE OR REPLACE
PROCEDURE UPLOAD_COPY_JC_FF_UPLOAD AS
   --GET ALL THE RECORDS WHICH HAVE BEEN MARKED AS VALID BUT HAVE NOT BEEN UPLOADED
   CURSOR all_rec IS
      SELECT *
      FROM STG_COPY_JOB_CODE_FF
      WHERE error_status = 'Y' AND NVL (upload_status, 'N') = 'N';

   CURSOR all_failure_types(v_model INTEGER)
   IS
      SELECT *
      FROM failure_type 
      WHERE for_item_group_id = v_model;

   CURSOR all_failure_assemblies(v_failure_structure INTEGER)
   IS
      SELECT *
      FROM failure_structure_assemblies 
      WHERE failure_structure = v_failure_structure;
      
   --ALL GLOBAL VARIABLES DECLARED FOR A PROCEDURE
   v_upload_error            VARCHAR2 (4000) := NULL;
   v_error_code              VARCHAR2 (4000) := NULL;
   v_from_item_group_id      NUMBER          := 0;
   v_to_item_group_id        NUMBER          := 0;
   v_from_flr_struct_id      NUMBER          := 0;
   v_failure_type_id         NUMBER          := 0;
   v_failure_structure_id    NUMBER          := 0;
   v_flag                    NUMBER          := 0;
   v_var                     NUMBER;
   v_count                   NUMBER;
   v_loop_count              NUMBER          := 0;
BEGIN
   FOR each_rec IN all_rec LOOP
      BEGIN
         --RESET THE VALUE TO ZERO
         v_flag := 0;
         v_count := 0;
         v_from_item_group_id := 0;
         v_from_flr_struct_id := 0;
		 
         -- Get the Item Group id of From Model
         SELECT ID 
         INTO v_from_item_group_id
         FROM ITEM_GROUP
         WHERE lower(NAME) = lower(each_rec.from_model_number)
         AND lower(item_group_type) = 'model' 
         AND is_part_of in (SELECT ID FROM ITEM_GROUP WHERE NAME=each_rec.from_product_code AND 
              business_unit_info = each_rec.business_unit_name AND lower(item_group_type) = 'product')
         AND business_unit_info = each_rec.business_unit_name;
         
         -- Get the Item Group id of From Model
         SELECT ID 
         INTO v_to_item_group_id
         FROM ITEM_GROUP
         WHERE lower(NAME) = lower(each_rec.to_model_number)
         and lower(item_group_type) = 'model' 
         and is_part_of in (SELECT ID FROM ITEM_GROUP WHERE NAME=each_rec.to_product_code AND 
              business_unit_info = each_rec.business_unit_name AND lower(item_group_type) = 'product')
         and business_unit_info = each_rec.business_unit_name;
   
       SELECT ID 
       INTO v_from_flr_struct_id
       FROM failure_structure 
       WHERE FOR_ITEM_GROUP = v_from_item_group_id;
      
      BEGIN
          IF EACH_REC.COPY = 'FF'
          THEN

             BEGIN

                FOR each_fault_found IN all_failure_types(v_from_item_group_id)
                LOOP
                 -- Get the new number for Primary Key
                 SELECT failure_type_seq.nextval
                 INTO v_failure_type_id
                 FROM DUAL;
                
                  INSERT INTO FAILURE_TYPE (ID, VERSION, DEFINITION_ID, FOR_ITEM_GROUP_ID, D_CREATED_ON, 
                  D_INTERNAL_COMMENTS, D_ACTIVE)
                  VALUES (v_failure_type_id, 1, each_fault_found.definition_id, v_to_item_group_id, 
                  sysdate, 'Upload Process', 1);
                END LOOP;
                
             EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  v_error_code := SUBSTR (SQLERRM, 0, 1000);
             END;
             
               
          ELSIF EACH_REC.COPY = 'JC'
          THEN 

             -- INSERT/UPDATE FAILURE STRUCTURE TO MAINTAIN THE MODEL (ITEM GROUP) FOR JOB CODE
             BEGIN
               SELECT ID 
               INTO v_failure_structure_id
               FROM failure_structure 
               WHERE FOR_ITEM_GROUP = v_to_item_group_id;
               
             EXCEPTION
                WHEN NO_DATA_FOUND THEN
                 -- Get the new number for Primary Key
                 SELECT failure_structure_seq.nextval
                 INTO v_failure_structure_id
                 FROM DUAL;
                 
                 INSERT INTO FAILURE_STRUCTURE (ID, NAME, VERSION, FOR_ITEM_GROUP, D_CREATED_ON, D_INTERNAL_COMMENTS, D_ACTIVE)
                 VALUES (v_failure_structure_id, '', 0, v_to_item_group_id, sysdate, 'Upload-To Copy the Job code', 1);
             END;
             
             FOR EACH_ASSEMBLY IN all_failure_assemblies (v_from_flr_struct_id)
             LOOP
              BEGIN
                INSERT INTO FAILURE_STRUCTURE_ASSEMBLIES (FAILURE_STRUCTURE, ASSEMBLIES)
                VALUES (v_failure_structure_id, EACH_ASSEMBLY.ASSEMBLIES);
              EXCEPTION
              WHEN OTHERS THEN
                -- Already data exists.
                v_error_code := SUBSTR (SQLERRM, 0, 1000);
              END;
             END LOOP;
             
          END IF;
          
         --UPDATE TO BE A SUCCESS
         UPDATE STG_COPY_JOB_CODE_FF
            SET upload_error = NULL,
                upload_status = 'Y',
                upload_date = SYSDATE,
                error_status = NULL
          WHERE id = each_rec.id;
          
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_error_code := SUBSTR (SQLERRM, 0, 1000);

            UPDATE STG_COPY_JOB_CODE_FF
               SET upload_error = v_error_code,
                   upload_status = 'N'
             WHERE id = each_rec.id;
      END;
      
      IF v_loop_count = 10 THEN
         --DO A COMMIT FOR 10 RECORDS
         COMMIT;
         v_loop_count := 0; -- Initialize the count size
      END IF;

      END;
   END LOOP;
  --FINAL COMMIT
  COMMIT;
END;
/
COMMIT
/