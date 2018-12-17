create or replace
PROCEDURE "UPLOAD_JOB_CODE_POPULATION"
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
      V_IMMEDIATE_PARENT       := NULL;
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
      SELECT ISCH.ID
      INTO V_SCHEME
      FROM ITEM_SCHEME ISCH
      WHERE ISCH.NAME = 'Prod Struct Scheme';
      SELECT is_part_of
      INTO v_immediate_parent
      FROM ITEM_GROUP
      WHERE name                    = each_rec.field_model
      AND LOWER(BUSINESS_UNIT_INFO) = LOWER(EACH_REC.BUSINESS_UNIT_NAME)
      AND d_active                  = 1
      AND scheme                    = V_SCHEME;
      UPDATE stg_job_code
      SET system_code         = v_system_code,
        sub_system_code       = v_sub_system_code,
        component_code        = v_component_code,
        sub_component_code    = v_sub_component_code,
        immediate_parent_code = v_immediate_parent,
        business_unit_name    = v_bu_name
      WHERE id                = each_rec.id;
      COMMIT;
    END;
  END LOOP;
END UPLOAD_JOB_CODE_POPULATION;
/
COMMIT
/
