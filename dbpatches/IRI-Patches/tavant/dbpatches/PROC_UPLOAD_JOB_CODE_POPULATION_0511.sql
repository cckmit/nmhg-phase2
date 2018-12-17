--Purpose    : Fixed the job codes upload - TWMS4.1-2705
--Author     : Raghu
--Created On : 11-May-09

CREATE OR REPLACE PROCEDURE upload_job_code_population AS

    CURSOR ALL_REC
    IS
        SELECT *
	    FROM STG_JOB_CODE
	    WHERE NVL(ERROR_STATUS,'N') = 'Y' AND
		    UPLOAD_STATUS IS NULL
		    ORDER BY ID ASC;

    v_immediate_parent		    NUMBER := 0;
    v_error_count		        NUMBER := 0;
    v_file_upload_mgt_id        NUMBER := 0;
    v_system_code			    VARCHAR2(4000) := NULL;
    v_sub_system_code			VARCHAR2(4000) := NULL;
    v_component_code			VARCHAR2(4000) := NULL;
    v_sub_component_code		VARCHAR2(4000) := NULL;
    v_index                     NUMBER := 0;
    v_prev_index                NUMBER := 0;
    v_ass_def_id                NUMBER := NULL;
    v_error_code 		  		VARCHAR2(4000) := NULL;
    v_code			            VARCHAR2(4000) := NULL;

BEGIN

    FOR EACH_REC IN ALL_REC LOOP
    BEGIN
   
        v_index := instr(EACH_REC.JOB_CODE, '-', 1, 1);
        IF v_index > 0 THEN 
            select substr(EACH_REC.JOB_CODE, 1, v_index - 1) 
            into v_system_code from dual;
        ELSIF v_index = 0 THEN
            select substr(EACH_REC.JOB_CODE, 1) 
            into v_system_code from dual;
        END IF;

        FOR i IN 2 .. 4 LOOP
        BEGIN
            IF v_index > 0 THEN
                v_prev_index := v_index;
                v_index := instr(EACH_REC.JOB_CODE, '-', 1, i);
                IF v_index > 0 THEN 
                    select substr(EACH_REC.JOB_CODE, v_prev_index + 1, v_index - v_prev_index - 1)
                    into v_code from dual;
                ELSE
                    select substr(EACH_REC.JOB_CODE, v_prev_index + 1)
                    into v_code from dual;
                END IF;
                IF i = 2 THEN
                    v_sub_system_code := v_code;
                ELSIF i = 3 THEN
                    v_component_code := v_code;
                ELSIF i = 3 THEN
                    v_sub_component_code := v_code;
                END IF;
            END IF;
        END;
        END LOOP;

        IF v_system_code IS NULL OR NOT COMMON_VALIDATION_UTILS.isValidAssemblyDefinitionCode(v_system_code,1) THEN
            v_error_code := Common_Utils.addError(v_error_code, 'IB0030');
        END IF;

        IF v_sub_system_code IS NOT NULL THEN
            IF v_sub_system_code = '0000' OR NOT COMMON_VALIDATION_UTILS.isValidAssemblyDefinitionCode(v_sub_system_code, 2) THEN
                v_error_code := Common_Utils.addError(v_error_code, 'IB0031');
            END IF;
        END IF;

        IF v_component_code IS NOT NULL THEN
            IF v_sub_system_code IS NULL OR v_sub_system_code = '0000' THEN
                v_error_code := Common_Utils.addError(v_error_code, 'IB0032');
            END IF;
            IF v_component_code = '0000' OR NOT COMMON_VALIDATION_UTILS.isValidAssemblyDefinitionCode(v_component_code, 3) THEN
                v_error_code := Common_Utils.addError(v_error_code, 'IB0033');
            END IF;
        END IF;

        IF v_sub_component_code IS NOT NULL THEN
            IF v_sub_system_code IS NULL OR v_sub_system_code = '0000' 
               OR v_component_code IS NULL OR v_component_code = '0000'
            THEN
                v_error_code := Common_Utils.addError(v_error_code, 'IB0034');
            END IF;
            IF v_sub_component_code = '0000' OR NOT COMMON_VALIDATION_UTILS.isValidAssemblyDefinitionCode(v_sub_component_code, 4) THEN
                v_error_code := Common_Utils.addError(v_error_code, 'IB0035');
            END IF;
        END IF;


        IF v_error_code IS NOT NULL THEN
            --RECORD HAS ERRORS
            UPDATE STG_JOB_CODE
            SET ERROR_STATUS = 'N',
                ERROR_CODE = v_error_code
            WHERE ID = EACH_REC.ID;

            v_error_count := v_error_count + 1;
        ELSE
        
            select id 
            into v_immediate_parent
            from item_group 
            where name = EACH_REC.PRODUCT_CODE and business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;

            UPDATE STG_JOB_CODE
            SET system_code = v_system_code,
                sub_system_code = v_sub_system_code,
                component_code = v_component_code,
                sub_component_code = v_sub_component_code,
                immediate_parent_code = v_immediate_parent
            WHERE ID = EACH_REC.ID;

        END IF;

        COMMIT;

    END;
    END LOOP;

    SELECT DISTINCT file_upload_mgt_id 
    INTO v_file_upload_mgt_id
    FROM STG_JOB_CODE 
    WHERE ROWNUM < 2;

    UPDATE FILE_UPLOAD_MGT 
    SET SUCCESS_RECORDS = SUCCESS_RECORDS - v_error_count, 
      ERROR_RECORDS = ERROR_RECORDS + v_error_count
    WHERE ID = v_file_upload_mgt_id;

    COMMIT; -- Final Commit for the procedure

END UPLOAD_JOB_CODE_POPULATION;
/
COMMIT
/