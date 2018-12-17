--Purpose    : Used for Customer validation
--Author     : Priyank Gupta
--Created On : 14-Mar-09

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--THIS IS THE PROCEDURE TO VALIDATE THE UPLOADED CUSTOMER DATA AND TO UPLOAD IT IN BASE TABLES. IT VALIDATES AND UPLOAD FOLLOWING
--CUSTOMER TYPES.
--1. DEALER 	2. SUPPLIER	3. THIRD PARTY		4. NATIONAL ACCOUNT	5. DIRECT CUSTOMER		6. OEM
--
--DATE		: 15 FEB 2009
--AUTHOR		: PRIYANK GUPTA
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE
PROCEDURE CUSTOMER_VALIDATION_PROCEDURE
AS

	CURSOR ALL_CUSTOMER 
	IS
		SELECT *
		FROM CUSTOMER_STAGING
		WHERE
			NVL(ERROR_STATUS,'N') = 'N';

	v_error_code		VARCHAR2(4000);
	v_var				NUMBER;

	BEGIN
		
		FOR EACH_REC IN ALL_CUSTOMER 
		LOOP

			v_error_code := NULL;
			
			BEGIN
				IF EACH_REC.CUSTOMER_NUMBER IS NULL
				THEN
					
					SELECT DECODE(v_error_code,NULL,'DNF001',v_error_code||',DNF001')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF EACH_REC.CUSTOMER_NAME IS NULL
				THEN
					SELECT DECODE(v_error_code,NULL,'DNF002',v_error_code||',DNF002')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF EACH_REC.ADDRESS1 IS NULL
				THEN
					
					SELECT DECODE(v_error_code,NULL,'DNF003',v_error_code||',DNF003')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF EACH_REC.CITY IS NULL
				THEN
					
					SELECT DECODE(v_error_code,NULL,'DNF004',v_error_code||',DNF004')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF EACH_REC.COUNTRY IS NULL
				THEN
					
					SELECT DECODE(v_error_code,NULL,'DNF005',v_error_code||',DNF005')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF EACH_REC.BUSINESS_UNIT IS NULL
				THEN
					
					SELECT DECODE(v_error_code,NULL,'DNF005',v_error_code||',DNF006')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;

			BEGIN
				IF UPPER(NVL(EACH_REC.CUSTOMER_TYPE,'X')) NOT IN ('DEALER','SUPPLIER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER')
				THEN
					
					SELECT DECODE(v_error_code,NULL,'INV001',v_error_code||',INV001')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
        
				IF NOT (COMMON_VALIDATION_UTILS.isValidCurrency(UPPER(NVL(EACH_REC.CURRENCY,'X'))))
				THEN
					
					SELECT DECODE(v_error_code,NULL,'INV002',v_error_code||',INV002')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF UPPER(NVL(EACH_REC.STATUS,'X')) NOT IN ('ACTIVE','INACTIVE')
				THEN
					
					SELECT DECODE(v_error_code,NULL,'INV003',v_error_code||',INV003')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				IF UPPER(NVL(EACH_REC.UPDATES,'X'))  NOT IN ('Y','N')
				THEN
					
					SELECT DECODE(v_error_code,NULL,'INV004',v_error_code||',INV004')
					INTO v_error_code
					FROM DUAL;
				END IF;
			END;
			
			BEGIN
				SELECT 1
				INTO v_var
				FROM 
					BUSINESS_UNIT
				WHERE
					UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT);
			EXCEPTION
			WHEN NO_DATA_FOUND
			THEN
				
				SELECT DECODE(v_error_code,NULL,'INV005',v_error_code||',INV005')
				INTO v_error_code
				FROM DUAL;
			END;

  --ERROR CODE: DUP001
  --CHECK IF THE RECORD IS UNIQUE WITH THE PROVIDED ID IN IT'S OWN TYPES
			BEGIN
				SELECT 1
				INTO v_var
				FROM CUSTOMER_STAGING
				WHERE
					CUSTOMER_NUMBER = EACH_REC.CUSTOMER_NUMBER AND
					CUSTOMER_TYPE = EACH_REC.CUSTOMER_TYPE AND
          ID <> EACH_REC.ID AND 
					ROWNUM = 1;

				SELECT DECODE(v_error_code,NULL,'DUP001',v_error_code||',DUP001')
				INTO v_error_code
				FROM DUAL;
			EXCEPTION
			WHEN NO_DATA_FOUND
			THEN
				
        NULL;
			END;			
			
			BEGIN
				IF EACH_REC.CUSTOMER_TYPE IN ('DEALER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER')
				THEN
					BEGIN
						SELECT 1
						INTO v_var
						FROM 
							SERVICE_PROVIDER SP,
							BU_ORG_MAPPING BOM
						WHERE
							SERVICE_PROVIDER_NUMBER = EACH_REC.CUSTOMER_NUMBER AND
							UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N' AND
							BOM.ORG = SP.ID AND
							UPPER(BOM.BU) = UPPER(EACH_REC.BUSINESS_UNIT) AND
							ROWNUM = 1;

						SELECT DECODE(v_error_code,NULL,'DUP001',v_error_code||',DUP002')
						INTO v_error_code
						FROM DUAL;

					EXCEPTION
					WHEN NO_DATA_FOUND
					THEN
						
            NULL;
					END;	
				ELSIF EACH_REC.CUSTOMER_TYPE IN ('SUPPLIER')
        THEN
					BEGIN	
						SELECT 1
						INTO v_var
						FROM 
							SUPPLIER S,
							BU_ORG_MAPPING BOM
						WHERE
							S.SUPPLIER_NUMBER = EACH_REC.CUSTOMER_NUMBER AND
							UPPER(NVL(EACH_REC.UPDATES,'N')) = 'N' AND
							S.ID = BOM.ORG AND
							UPPER(BOM.BU) = UPPER(EACH_REC.BUSINESS_UNIT) AND
							ROWNUM = 1 ;

						SELECT DECODE(v_error_code,NULL,'DUP001',v_error_code||',DUP002')
						INTO v_error_code
						FROM DUAL;

					EXCEPTION
					WHEN NO_DATA_FOUND
					THEN
						
            NULL;
					END;
				END IF;
			END;
			
			BEGIN
				IF v_error_code IS NULL
				THEN
					BEGIN
            UPDATE CUSTOMER_STAGING
            SET 
              ERROR_CODE = NULL,
              ERROR_STATUS = 'Y'
            WHERE
              ID = EACH_REC.ID;					
          END;
				ELSE
          BEGIN
            UPDATE CUSTOMER_STAGING
            SET 
              ERROR_CODE = v_error_code,
              ERROR_STATUS = 'N'
            WHERE
              ID = EACH_REC.ID;										
          END;
				END IF;				
			END;

			COMMIT;

		END LOOP;	
	END;
/
COMMIT
/