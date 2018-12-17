create or replace
PROCEDURE                "ITEM_VALIDATION" 
AS

  CURSOR ALL_REC 
  IS
  SELECT *
	FROM ITEM_STAGING
	WHERE NVL(ERROR_STATUS,'N') = 'N';

	v_error_code		            VARCHAR2(4000):= NULL;
	v_var				                NUMBER;
	v_scheme_id 	  	          NUMBER;
	v_bu_name                   VARCHAR2(255);
  v_valid_bu                  BOOLEAN;
  v_file_upload_mgt_id        NUMBER := 0;
  v_success_count             NUMBER := 0;
  v_error_count               NUMBER := 0;
  v_count                     NUMBER := 0;
  v_error                     VARCHAR2(4000) := NULL;

BEGIN
SELECT f.business_unit_info INTO v_bu_name 
FROM file_upload_mgt f
WHERE f.id = (SELECT file_upload_mgt_id FROM item_staging WHERE rownum = 1);

SELECT id INTO v_scheme_id FROM item_scheme WHERE NAME = 'Prod Struct Scheme' 
AND BUSINESS_UNIT_INFO = v_bu_name;

FOR EACH_REC IN ALL_REC LOOP
BEGIN

    v_error_code := NULL;
    v_valid_bu := FALSE;

    IF each_rec.business_unit IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT001');
    ELSIF UPPER(each_rec.business_unit) != UPPER(v_bu_name) THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT002');
    ELSE
        v_valid_bu := TRUE;
    END IF;

    IF EACH_REC.ITEM_NUMBER IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT003');        
    END IF;				

    IF EACH_REC.ITEM_DESC IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT004');
    END IF;				

    IF EACH_REC.ITEM_GROUP_CODE IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT005');
    END IF;				

    IF EACH_REC.UNIT_OF_MEASURE IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT019');
    END IF;				

    IF EACH_REC.IS_SERIALIZED IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT006');
    ELSIF Upper(EACH_REC.IS_SERIALIZED) NOT IN ('Y','N') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT007');
    END IF;				

    IF v_valid_bu AND v_bu_name='Hussmann' AND EACH_REC.PART_MANUFACTURING_CODE IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT008');
    END IF;				

    IF EACH_REC.ITEM_STATUS IS NULL  THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT009');
    ELSIF UPPER(EACH_REC.ITEM_STATUS) NOT IN ('ACTIVE','INACTIVE') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT010');
    END IF;				

    IF EACH_REC.HAS_HOUR_METER IS NULL THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT011');
    ELSIF UPPER(EACH_REC.HAS_HOUR_METER) NOT IN ('N','Y') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT012');
    END IF;	

    IF (EACH_REC.OWNER) IS NOT NULL THEN
    BEGIN
        SELECT 1 INTO v_var
        FROM SUPPLIER S, PARTY P
        WHERE UPPER(S.SUPPLIER_NUMBER) = UPPER(EACH_REC.OWNER) 
            AND S.ID = P.ID
            AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IT013');
    END;
    END IF;


    IF UPPER(EACH_REC.ITEM_GROUP_CODE) IS NOT NULL  THEN
    BEGIN
        SELECT 1 INTO v_var
        FROM ITEM_GROUP IG
        WHERE IG.BUSINESS_UNIT_INFO = v_bu_name
            AND UPPER(IG.GROUP_CODE) = UPPER(EACH_REC.ITEM_GROUP_CODE)
            AND IG.SCHEME = v_scheme_id
            AND ROWNUM = 1;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'IT014');
    END;					 	  
    END IF;				

    IF UPPER(NVL(EACH_REC.UPDATES,'N'))  NOT IN ('Y', 'N') THEN
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT015');
    END IF;    
    BEGIN
        SELECT 1 INTO v_var
        FROM ITEM_STAGING
        WHERE ITEM_NUMBER = EACH_REC.ITEM_NUMBER AND
            PART_MANUFACTURING_CODE = EACH_REC.PART_MANUFACTURING_CODE AND
            NVL(OWNER,'X') = NVL(EACH_REC.OWNER,'X') AND
            ID = EACH_REC.ID;
        v_error_code := common_utils.addErrorMessage(v_error_code, 'IT016');
         DBMS_OUTPUT.PUT_LINE(v_error_code);
    EXCEPTION
        WHEN NO_DATA_FOUND THEN               
        DBMS_OUTPUT.PUT_LINE(EACH_REC.ID);
            NULL;
    END;

    BEGIN
     IF v_bu_name='Hussmann' THEN
        IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NOT NULL
                AND UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N' THEN
            BEGIN
                SELECT 1 INTO v_var
                FROM ITEM I,SUPPLIER S
                WHERE EACH_REC.OWNER = S.SUPPLIER_NUMBER AND
                    S.ID = I.OWNED_BY 
                    AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
                    AND I.BUSINESS_UNIT_INFO = v_bu_name 
                    AND ROWNUM = 1 ;
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IT017');
            EXCEPTION WHEN
                NO_DATA_FOUND THEN
                    NULL;
            END;
        ELSIF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NULL
                AND UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N' THEN
            BEGIN
                SELECT 1 INTO v_var
                FROM ITEM I,PARTY P
                WHERE I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
                    AND I.BUSINESS_UNIT_INFO = v_bu_name
                    AND I.OWNED_BY = P.ID 
                    AND UPPER(P.NAME) = 'OEM'
                    AND	ROWNUM = 1;		
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IT017');
            EXCEPTION WHEN
                NO_DATA_FOUND THEN
                    NULL;
            END;
        END IF;
      ELSE -- other business units(TK TSA)
      IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NOT NULL
                AND UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N' THEN
            BEGIN
                SELECT 1 INTO v_var
                FROM ITEM I,SUPPLIER S
                WHERE EACH_REC.OWNER = S.SUPPLIER_NUMBER AND
                    S.ID = I.OWNED_BY 
                    AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER
                    AND I.BUSINESS_UNIT_INFO = v_bu_name 
                    AND ROWNUM = 1 ;
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IT017');
            EXCEPTION WHEN
                NO_DATA_FOUND THEN
                    NULL;
            END;
        ELSIF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NULL
                AND UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N' THEN
            BEGIN
                SELECT 1 INTO v_var
                FROM ITEM I,PARTY P
                WHERE I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER
                    AND I.BUSINESS_UNIT_INFO = v_bu_name
                    AND I.OWNED_BY = P.ID 
                    AND UPPER(P.NAME) = 'OEM'
                    AND	ROWNUM = 1;		
                v_error_code := common_utils.addErrorMessage(v_error_code, 'IT017');
            EXCEPTION WHEN
                NO_DATA_FOUND THEN
                    NULL;
            END;
        END IF;
      END IF;
    END;
   

    BEGIN
     IF v_bu_name='Hussmann' THEN
        IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NOT NULL
                AND EACH_REC.UPDATES = 'Y' THEN
            BEGIN
                SELECT 1 INTO v_var
                FROM ITEM I,SUPPLIER S
                WHERE S.SUPPLIER_NUMBER =  EACH_REC.OWNER 
                    AND S.ID = I.OWNED_BY 
                    AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
                    AND I.BUSINESS_UNIT_INFO = v_bu_name
                    AND ROWNUM = 1 ;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IT018');
            END;
        ELSIF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NULL AND EACH_REC.UPDATES = 'Y' THEN
                BEGIN
                    SELECT 1 INTO v_var
                    FROM ITEM I,PARTY P
                    WHERE I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER||'#'||EACH_REC.PART_MANUFACTURING_CODE
                        AND I.BUSINESS_UNIT_INFO = v_bu_name
                        AND I.OWNED_BY = P.ID 
                        AND UPPER(P.NAME) = 'OEM'
                        AND	ROWNUM = 1;		
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            v_error_code := common_utils.addErrorMessage(v_error_code, 'IT018');
                 END;
        END IF;
        ELSE 
        IF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NOT NULL
                AND EACH_REC.UPDATES = 'Y' THEN
            BEGIN
                SELECT 1 INTO v_var
                FROM ITEM I,SUPPLIER S
                WHERE S.SUPPLIER_NUMBER =  EACH_REC.OWNER 
                    AND S.ID = I.OWNED_BY 
                    AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER
                    AND I.BUSINESS_UNIT_INFO = v_bu_name
                    AND ROWNUM = 1 ;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                    v_error_code := common_utils.addErrorMessage(v_error_code, 'IT018');
            END;
        ELSIF EACH_REC.ITEM_NUMBER IS NOT NULL AND EACH_REC.OWNER IS NULL AND EACH_REC.UPDATES = 'Y' THEN
           
                BEGIN
                    SELECT 1 INTO v_var
                    FROM ITEM I,PARTY P
                    WHERE I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER
                        AND I.BUSINESS_UNIT_INFO = v_bu_name
                        AND I.OWNED_BY = P.ID 
                        AND UPPER(P.NAME) = 'OEM'
                        AND	ROWNUM = 1;		
                    EXCEPTION
                        WHEN NO_DATA_FOUND THEN
                            v_error_code := common_utils.addErrorMessage(v_error_code, 'IT018');
                 END;
           
        END IF;
        END IF;
    END;

    IF EACH_REC.SERVICE_PART IS NOT NULL THEN
        IF UPPER(EACH_REC.SERVICE_PART) NOT IN ('Y', 'N') THEN
          v_error_code := common_utils.addErrorMessage(v_error_code, 'IT020');  
        END IF;
    END IF;

    IF v_error_code IS NULL THEN
        UPDATE ITEM_STAGING 
        SET
            ERROR_STATUS = 'Y',
            ERROR_CODE = NULL			   	
        WHERE ID = EACH_REC.ID;
    ELSE
        UPDATE ITEM_STAGING 
        SET
            ERROR_STATUS = 'N',
            ERROR_CODE = v_error_code
            WHERE ID = EACH_REC.ID;		   
    END IF;

    COMMIT;

END;	   
END LOOP; 

    BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
        FROM item_staging WHERE ROWNUM = 1;


        BEGIN
            SELECT count(*) INTO v_success_count
            FROM item_staging 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;


        BEGIN
            SELECT count(*) INTO v_error_count
            FROM item_staging 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;


        SELECT count(*) INTO v_count
        FROM item_staging 
        WHERE file_upload_mgt_id = v_file_upload_mgt_id;

        UPDATE file_upload_mgt SET 
            success_records= v_success_count, 
            error_records= v_error_count,
            total_records = v_count
        WHERE id = v_file_upload_mgt_id;

    EXCEPTION
        WHEN OTHERS THEN
            v_error := SUBSTR(SQLERRM, 1, 4000);
            UPDATE file_upload_mgt 
            SET 
                error_message = v_error
            WHERE id = v_file_upload_mgt_id;
    END;
    COMMIT;

END ITEM_Validation;
/
commit
/
