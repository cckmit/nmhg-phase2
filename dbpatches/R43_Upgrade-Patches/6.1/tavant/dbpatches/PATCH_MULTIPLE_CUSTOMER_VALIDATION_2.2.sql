--Purpose    : Adding validation of duplicate dealer site for the same customers
--Author     : Kuldeep Patil
--Created On : 02-Aug-2011
--Impact     : None

create or replace
PROCEDURE MULTIPLE_CUSTOMER_VALIDATION
AS
  CURSOR all_customer
  IS
    SELECT * FROM customer_staging WHERE NVL(error_status,'N') = 'N';
  v_error_code         VARCHAR2(4000);
  v_error              VARCHAR2(4000);
  v_var                NUMBER;
  v_bu_name            VARCHAR(255);
  v_valid_state        BOOLEAN;
  v_valid_city         BOOLEAN;
  v_success_count      NUMBER;
  v_error_count        NUMBER;
  v_count              NUMBER;
  v_file_upload_mgt_id NUMBER;
  --changes done for bug fixes
  V_TEMP_CUST_NUM    VARCHAR2(4000) := '';
  V_TEMP_DEALER_SITE VARCHAR2(4000) := '';
  v_dlr_site_err_code varchar2(4000) := '';
BEGIN
    SELECT F.BUSINESS_UNIT_INFO
    INTO V_BU_NAME
    FROM FILE_UPLOAD_MGT F
    WHERE F.ID =
      (SELECT FILE_UPLOAD_MGT_ID FROM CUSTOMER_STAGING WHERE ROWNUM = 1
      );

    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU003', ';CU003'),
      ERROR_STATUS         = 'N'
    WHERE customer_number IS NULL;

    UPDATE CUSTOMER_STAGING TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU026', ';CU026'),
      ERROR_STATUS = 'N'
    WHERE NOT EXISTS
      (SELECT S.SERVICE_PROVIDER_NUMBER
      FROM SERVICE_PROVIDER S,
        BU_ORG_MAPPING BOM
      WHERE S.ID                    = BOM.ORG
      AND UPPER(S.SERVICE_PROVIDER_NUMBER) = UPPER(TAV.CUSTOMER_NUMBER)
      AND BOM.BU                    = V_BU_NAME
      )
    AND TAV.CUSTOMER_NUMBER IS NOT NULL;
    
    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU0020', ';CU0020'),
      ERROR_STATUS         = 'N'
    WHERE DEALER_SITE IS NULL;

    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU006', ';CU006'),
      ERROR_STATUS         = 'N'
    WHERE addressline1 IS NULL;

    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU007', ';CU007'),
      ERROR_STATUS         = 'N'
    WHERE city IS NULL;

    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU008', ';CU008'),
      ERROR_STATUS         = 'N'
    WHERE country IS NULL;

    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU019', ';CU019'),
      ERROR_STATUS         = 'N'
    WHERE status IS NULL;
    
    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU020', ';CU020'),
      ERROR_STATUS         = 'N'
    WHERE status IS not NULL and upper(status)  NOT IN ('ACTIVE','INACTIVE');

    UPDATE customer_staging
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'CU021', ';CU021'),
      ERROR_STATUS         = 'N'
    WHERE UPPER(NVL(updates,'N')) NOT IN ('Y','N');

  FOR each_rec IN all_customer
  LOOP
    v_error_code                := NULL;

    IF length(each_rec.country) > 0 and NOT common_validation_utils.isValidCountry(each_rec.country) THEN
      v_error_code               := common_utils.addErrorMessage(v_error_code, 'CU009');
    ELSIF UPPER(each_rec.country) = 'US' THEN
      IF each_rec.state          IS NULL THEN
        v_error_code             := common_utils.addErrorMessage(v_error_code, 'CU010');
      ELSIF NOT common_validation_utils.isValidState(each_rec.state, each_rec.country) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CU011');
      ELSE
        v_valid_state := TRUE;
      END IF;
      IF each_rec.city IS NULL THEN
        v_valid_city   := FALSE;
      ELSIF v_valid_state AND NOT common_validation_utils.isValidCity(each_rec.city, each_rec.state, each_rec.country) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CU012');
      ELSIF v_valid_state THEN
        v_valid_city := TRUE;
      END IF;
      IF each_rec.zip_code IS NULL THEN
        v_error_code          := common_utils.addErrorMessage(v_error_code, 'CU013');
      ELSIF v_valid_city AND NOT common_validation_utils.isValidZipcode(each_rec.zip_code, each_rec.city, each_rec.state, each_rec.country) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'CU014');
      END IF;
    END IF;

    --validation of duplicate dealer site for the same customer
    IF (length(EACH_REC.CUSTOMER_NUMBER) > 0 AND instr(NVL(V_TEMP_CUST_NUM, '--'), EACH_REC.CUSTOMER_NUMBER) <= 0 ) THEN
      BEGIN
        V_TEMP_DEALER_SITE   := '';
        v_dlr_site_err_code := '';
        FOR EACH_DEALER_SITE IN
        (SELECT ID,
          DEALER_SITE
        FROM CUSTOMER_STAGING
        WHERE NVL(ERROR_STATUS,'N') = 'N'
        AND UPPER(CUSTOMER_NUMBER)         = UPPER(EACH_REC.CUSTOMER_NUMBER)
        AND DEALER_SITE            IS NOT NULL
        ORDER BY DEALER_SITE,
          ID
        )
        LOOP
          BEGIN
            IF V_TEMP_DEALER_SITE = EACH_DEALER_SITE.DEALER_SITE THEN
                IF INSTR(NVL(v_dlr_site_err_code, 'X'), 'CU025') <= 0 THEN
                  v_dlr_site_err_code       := common_utils.addErrorMessage(v_dlr_site_err_code, 'CU025');
                end if;
            END IF;
            IF INSTR(NVL(v_dlr_site_err_code, 'X'), 'CU025') > 0 THEN
              UPDATE customer_staging
              SET ERROR_CODE = ERROR_CODE || DECODE(ERROR_CODE, NULL, v_dlr_site_err_code, ';'||v_dlr_site_err_code),
                error_status = 'N'
              WHERE id       = EACH_DEALER_SITE.id;
            END IF;
            V_TEMP_DEALER_SITE := EACH_DEALER_SITE.DEALER_SITE;
          END;
        END LOOP;
      END;
    END IF;
    V_TEMP_CUST_NUM := EACH_REC.CUSTOMER_NUMBER;

    IF V_ERROR_CODE IS NULL AND EACH_REC.ERROR_CODE IS NULL THEN
      UPDATE customer_staging
      SET ERROR_STATUS = 'Y',
        ERROR_CODE     = NULL
      WHERE ID         = EACH_REC.ID and error_code is null;
    ELsif length(V_ERROR_CODE) > 0 then
       UPDATE customer_staging
      SET ERROR_STATUS = 'N',
        ERROR_CODE     = ERROR_CODE
        || DECODE (ERROR_CODE,NULL, V_ERROR_CODE,';' || V_ERROR_CODE)
      WHERE ID = EACH_REC.ID;
    END IF;

    COMMIT;
  END LOOP;

UPDATE customer_staging cust_stage
SET ERROR_CODE = ERROR_CODE
  || DECODE(ERROR_CODE, NULL, 'CU025', ';CU025'),
  ERROR_STATUS = 'N'
WHERE EXISTS
  (SELECT 1
  FROM SERVICE_PROVIDER T1 ,
    ORGANIZATION_ORG_ADDRESSES T2,
    ORGANIZATION_ADDRESS T3 ,
    PARTY T4
  WHERE T1.ID                 = T2.ORGANIZATION
  AND T3.ID                   = T2.ORG_ADDRESSES
  AND T4.ID                   = T1.ID
  AND UPPER(SERVICE_PROVIDER_NUMBER) = UPPER(CUST_STAGE.CUSTOMER_NUMBER)
  AND T3.site_number          = CUST_STAGE.DEALER_SITE
  )
AND ERROR_CODE                          IS NULL
AND upper(NVL(cust_stage.updates, 'N')) <> 'Y';
  
  BEGIN
    SELECT file_upload_mgt_id
    INTO v_file_upload_mgt_id
    FROM customer_staging
    WHERE ROWNUM = 1;
    SELECT COUNT(*)
    INTO v_success_count
    FROM customer_staging
    WHERE file_upload_mgt_id = v_file_upload_mgt_id
    AND error_status         = 'Y';
    SELECT COUNT(*)
    INTO v_error_count
    FROM customer_staging
    WHERE file_upload_mgt_id = v_file_upload_mgt_id
    AND error_status         = 'N';
    SELECT COUNT(*)
    INTO v_count
    FROM customer_staging
    WHERE file_upload_mgt_id = v_file_upload_mgt_id;
    UPDATE file_upload_mgt
    SET success_records= v_success_count,
      error_records    = v_error_count,
      total_records    = v_count
    WHERE id           = v_file_upload_mgt_id;
  EXCEPTION
  WHEN OTHERS THEN
    v_error := SUBSTR(SQLERRM, 1, 4000);
    UPDATE file_upload_mgt
    SET error_message = v_error
    WHERE id          = v_file_upload_mgt_id;
  END;
END;
/
commit
/