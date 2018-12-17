--Purpose    : Used to validate copy job code upload data
--Author     : raghuram.d
--Created On : 08-Jun-09

CREATE OR REPLACE PROCEDURE upload_copy_jc_ff_validation 
AS

    CURSOR all_rec
    IS
        SELECT *
        FROM stg_copy_job_code_ff
        WHERE NVL(error_status,'N') = 'N' AND
            upload_status IS NULL
            ORDER BY id ASC;

    --ALL GLOBAL VARIABLE DECLARED FOR THIS PROCEDURE
    v_error_code                VARCHAR2(4000) := NULL;
    v_error_flag                BOOLEAN := NULL;
    v_error                     VARCHAR2(4000) := NULL;
    v_count                     NUMBER := 0;
    v_file_upload_mgt_id        NUMBER := 0;
    v_success_count             NUMBER := 0;
    v_error_count               NUMBER := 0;
    v_loop_count                NUMBER := 0;
    v_valid_bu                  BOOLEAN := FALSE;
    v_valid_product             BOOLEAN := FALSE;
    v_uploaded_by               VARCHAR(255) := NULL;
    v_bu_name                   VARCHAR(255) := NULL;

BEGIN

    BEGIN
        SELECT u.login, f.business_unit_info INTO v_uploaded_by, v_bu_name
        FROM org_user u,file_upload_mgt f
        WHERE u.id = f.uploaded_by AND f.id = 
            (SELECT file_upload_mgt_id FROM stg_copy_job_code_ff WHERE rownum = 1);
    EXCEPTION 
        WHEN OTHERS THEN
            NULL;
    END;

    FOR each_rec IN all_rec LOOP
    BEGIN

        v_error_code := NULL;

        --Business Unit is mandatory and the uploaded user belongs to the BU
        v_valid_bu := FALSE;
        IF each_rec.business_unit_name IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ001');
        --Business Unit Name is valid
        ELSIF UPPER(each_rec.business_unit_name) != UPPER(v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ002');
        ELSIF NOT common_validation_utils.isUserBelongsToBU(each_rec.business_unit_name, v_uploaded_by) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ002');
        ELSE
            v_valid_bu := TRUE;
        END IF;

        --From Product Code is mandatory and valid
        v_valid_product := FALSE;
        IF each_rec.from_product_code IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ003');
        ELSIF v_valid_bu AND NOT common_validation_utils.isValidProductCode(each_rec.from_product_code, v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ004');
        ELSIF v_valid_bu THEN
            v_valid_product := TRUE;
        END IF;

        --From Model is mandatory and is valid
        IF each_rec.from_model_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ005');
        ELSIF v_valid_product AND NOT common_validation_utils.isValidModelCodeForProduct(
                each_rec.from_model_number, each_rec.from_product_code, v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ006');
        END IF;

        --To Product Code is mandatory and valid
        v_valid_product := FALSE;
        IF each_rec.to_product_code IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ007');
        ELSIF v_valid_bu AND NOT common_validation_utils.isValidProductCode(each_rec.to_product_code, v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ008');
        ELSIF v_valid_bu THEN
            v_valid_product := TRUE;
        END IF;
        
        --To Model is mandatory and is valid
        IF each_rec.to_model_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ009');
        ELSIF v_valid_product AND NOT common_validation_utils.isValidModelCodeForProduct(
                each_rec.to_model_number, each_rec.to_product_code, v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ010');
        END IF;

        --Copy has a valid value
        IF each_rec.copy IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ011');
        ELSIF UPPER(each_rec.copy) NOT IN ('FF', 'JC') THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'CJ012');
        END IF;

        --UPDATE RECORDS RESPECTIVELY FOR EACH LOOP
        IF v_error_code IS NULL THEN
            UPDATE stg_copy_job_code_ff SET
                error_status = 'Y',
                error_code = NULL
            WHERE id = each_rec.id;
        ELSE
            UPDATE stg_copy_job_code_ff SET
                error_status = 'N',
                error_code = v_error_code
            WHERE id = each_rec.id;
        END IF;

        v_loop_count := v_loop_count + 1;

        IF v_loop_count = 10 THEN
            COMMIT; --Do a commit for 10 records
            v_loop_count := 0; -- Initialize the count size
        END IF;

    END;
    END LOOP;

    IF v_loop_count > 0 THEN
        COMMIT;
    END IF;

    BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id
        FROM stg_copy_job_code_ff WHERE ROWNUM = 1;
        
        -- Success Count
        SELECT count(*) INTO v_success_count
        FROM stg_copy_job_code_ff 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';

        -- Error Count    
        SELECT count(*) INTO v_error_count
        FROM stg_copy_job_code_ff 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        
        -- Total Count
        SELECT count(*) INTO v_count
        FROM stg_copy_job_code_ff 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id ;

        UPDATE file_upload_mgt 
        SET 
            success_records = v_success_count, 
            error_records = v_error_count,
            total_records = v_count
        WHERE id = v_file_upload_mgt_id;   
    EXCEPTION
        WHEN OTHERS THEN
            -- Capture the error code into the table
            v_error := SUBSTR(SQLERRM, 1, 4000);
            UPDATE file_upload_mgt 
            SET error_message = v_error
            WHERE id = v_file_upload_mgt_id;
    END;

    COMMIT; -- Final Commit for the procedure

END upload_copy_jc_ff_validation;
/
COMMIT
/