--Purpose    : Used to populate job code upload data
--Author     : Jhulfikar Ali. A
--Created On : 10-Feb-09

create or replace
PROCEDURE UPLOAD_JOB_CODE_POPULATION AS

  CURSOR ALL_REC
  IS
  SELECT *
	FROM STG_JOB_CODE
	WHERE
		 NVL(ERROR_STATUS,'N') = 'Y' AND
		 UPLOAD_STATUS IS NULL
		 ORDER BY ID ASC;

  --ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
  v_immediate_parent		    NUMBER := 0;
  v_error_count		          NUMBER := 0;
  v_file_upload_mgt_id      NUMBER := 0;
  v_instr_flag              NUMBER := 0;
  v_system_code			        VARCHAR2(4000) := NULL;
  v_sub_system_code			    VARCHAR2(4000) := NULL;
  v_component_code			    VARCHAR2(4000) := NULL;
  v_sub_component_code			VARCHAR2(4000) := NULL;

BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN

       -- Populating System code given by user in Fault Code field
       v_instr_flag := instr(EACH_REC.JOB_CODE, '-', 1, 1);
       IF v_instr_flag > 0 THEN 
         select substr(EACH_REC.JOB_CODE, 1, instr(EACH_REC.JOB_CODE, '-', 1, 1)-1) 
         into v_system_code from dual;
       ELSIF v_instr_flag = 0 THEN
         select substr(EACH_REC.JOB_CODE, 1) 
         into v_system_code from dual;
       END IF;
      
       -- Populating Sub System code given by user in Fault Code field
       v_instr_flag := instr(EACH_REC.JOB_CODE, '-', 1, 2);
       IF v_instr_flag > 0 THEN 
         select replace(substr(EACH_REC.JOB_CODE, 1, v_instr_flag-1), v_system_code || '-', '')
         into v_sub_system_code from dual;
       ELSIF (length(EACH_REC.JOB_CODE)>instr(EACH_REC.JOB_CODE, '-', 1, 1)) AND (instr(EACH_REC.JOB_CODE, '-', 1, 1) <> 0) THEN
         select replace(substr(EACH_REC.JOB_CODE, 1), v_system_code || '-', '')
         into v_sub_system_code from dual;
       END IF;
      
       -- Populating Component code given by user in Fault Code field
       v_instr_flag := instr(EACH_REC.JOB_CODE, '-', 1, 3);
       IF v_instr_flag > 0 THEN 
         select replace(substr(EACH_REC.JOB_CODE, 1, v_instr_flag-1), v_system_code || '-' || v_sub_system_code || '-', '')
         into v_component_code from dual;
       ELSIF (length(EACH_REC.JOB_CODE)>instr(EACH_REC.JOB_CODE, '-', 1, 2)) AND (instr(EACH_REC.JOB_CODE, '-', 1, 2) <> 0)
       THEN
         select replace(substr(EACH_REC.JOB_CODE, 1, length(EACH_REC.JOB_CODE)), v_system_code || '-' || v_sub_system_code || '-', '')
         into v_component_code from dual;
       END IF;
      
       -- Populating Sub Component code given by user in Fault Code field
       v_instr_flag := instr(EACH_REC.JOB_CODE, '-', 1, 3);
       IF v_instr_flag > 0 THEN
         select substr(EACH_REC.JOB_CODE, v_instr_flag+1)
         into v_sub_component_code from dual;
       END IF;
      
       -- Populating Immediate Parent id using Product and Business unit
       select id 
       into v_immediate_parent
       from item_group 
       where name = EACH_REC.PRODUCT_CODE and business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;
      
        UPDATE STG_JOB_CODE
			  SET
				  system_code = v_system_code,
				  sub_system_code = v_sub_system_code,
				  component_code = v_component_code,
				  sub_component_code = v_sub_component_code,
				  immediate_parent_code = v_immediate_parent
				WHERE
				  ID = EACH_REC.ID;

		   --DO A COMMIT FOR EACH RECORD
		   COMMIT;

    END;
  END LOOP;

    -- In a given time there will be only one file for a given upload
    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_JOB_CODE 
    WHERE ROWNUM < 2;
    
    UPDATE FILE_UPLOAD_MGT 
    SET 
      SUCCESS_RECORDS = SUCCESS_RECORDS - v_error_count, 
      ERROR_RECORDS = ERROR_RECORDS + v_error_count
    WHERE ID = v_file_upload_mgt_id;
        
    COMMIT; -- Final Commit for the procedure

END UPLOAD_JOB_CODE_POPULATION;
/
COMMIT
/