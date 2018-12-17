--Purpose    : Used for Customer upload
--Author     : Priyank Gupta
--Created On : 14-Mar-09

------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
--THIS IS THE PROCEDURE TO UPLOAD CUSTOMER DATA . IT  UPLOADS FOLLOWING
--CUSTOMER TYPES.
--1. DEALER 	2. SUPPLIER	3. THIRD PARTY		4. NATIONAL ACCOUNT	5. DIRECT CUSTOMER		6. OEM
--
--DATE		: 15 FEB 2009
--AUTHOR		: PRIYANK GUPTA
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
CREATE OR REPLACE
PROCEDURE CUSTOMER_UPLOAD_PROCEDURE
AS
	CURSOR ALL_CUSTOMER
	IS
		SELECT *
		FROM CUSTOMER_STAGING
		WHERE
			NVL(ERROR_STATUS,'N') = 'Y' AND
			NVL(UPLOAD_STATUS,'N') = 'N';

	v_error_code		VARCHAR2(4000);
	v_party_id			NUMBER;
	is_present			NUMBER;
	v_address_id		NUMBER;
	v_bu_name			VARCHAR2(1000);

	BEGIN
		FOR EACH_REC IN ALL_CUSTOMER
		LOOP
			BEGIN
				
          is_present := 0;
          
          IF NVL(EACH_REC.UPDATES,'N') = 'Y'
          THEN
            BEGIN
              IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALER','SUPPLIER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER')
              THEN
                BEGIN
  
                  SELECT P.ID, P.ADDRESS
                  INTO v_party_id, v_address_id
                  FROM 
                    SERVICE_PROVIDER SP,
                    BU_ORG_MAPPING BOM, 
                    PARTY P
                  WHERE
                    UPPER(SP.SERVICE_PROVIDER_NUMBER) = UPPER(EACH_REC.CUSTOMER_NUMBER) AND
                    UPPER(BOM.BU) = UPPER(EACH_REC.BUSINESS_UNIT) AND
                    SP.ID = P.ID AND
                    BOM.ORG = SP.ID;
  
                EXCEPTION
                WHEN NO_DATA_FOUND
                THEN
                  
                  NULL;
                END;
              ELSIF EACH_REC.CUSTOMER_TYPE IN ('SUPPLIER')
              THEN
                BEGIN
                  SELECT P.ID, P.ADDRESS
                  INTO v_party_id, v_address_id
                  FROM 
                    SUPPLIER S,
                    PARTY P,
                    BU_ORG_MAPPING BOM
                  WHERE
                    UPPER(S.SUPPLIER_NUMBER) = UPPER(EACH_REC.CUSTOMER_NUMBER) AND
                    P.ID = S.ID AND
                    UPPER(BOM.BU) = UPPER(EACH_REC.BUSINESS_UNIT) AND
                    BOM.ORG = S.ID;
                EXCEPTION
                WHEN NO_DATA_FOUND
                THEN
                  
                  NULL;
                END;
              END IF;
            END;
          END IF;
  
          
          IF is_present = 0
          THEN
            BEGIN
              SELECT PARTY_SEQ.NEXTVAL
              INTO v_party_id
              FROM DUAL;
            END;
  
            BEGIN
              SELECT ADDRESS_SEQ.NEXTVAL
              INTO v_address_id
              FROM DUAL;
            END;
          END IF;
  
          
          
          BEGIN
          IF is_present <> 0
          THEN
            BEGIN
              BEGIN
                
                UPDATE ADDRESS
                SET
                  ADDRESS_LINE1 = EACH_REC.ADDRESS1 ,
                  ADDRESS_LINE2 = EACH_REC.ADDRESS2 ,
                  CITY = EACH_REC.CITY ,
                  CONTACT_PERSON_NAME = EACH_REC.CONTACT_PERSON ,
                  COUNTRY = EACH_REC.COUNTRY ,
                  EMAIL = EACH_REC.EMAIL ,
                  PHONE = EACH_REC.PHONE ,
                  STATE = EACH_REC.STATE ,
                  ZIP_CODE = EACH_REC.POSTAL_CODE ,
                  STATUS = UPPER(EACH_REC.STATUS) ,
                  D_ACTIVE = DECODE(UPPER(EACH_REC.STATUS),'INACTIVE',0,1)
                WHERE
                  ID = v_address_id;
              END;
  
              BEGIN
                
                UPDATE ORGANIZATION
                SET
                  PREFERRED_CURRENCY = UPPER(EACH_REC.CURRENCY)
                WHERE
                  ID = v_party_id;
              END;
            END;			
          ELSE
            BEGIN
              
              INSERT INTO ADDRESS
              (
                ID,
                ADDRESS_LINE1,
                ADDRESS_LINE2 ,
                CITY,
                CONTACT_PERSON_NAME,
                COUNTRY,
                EMAIL,
                PHONE,
                STATE,
                ZIP_CODE,
                STATUS,
                D_ACTIVE, VERSION
              )
              VALUES
              (
                v_address_id,
                EACH_REC.ADDRESS1 ,
                EACH_REC.ADDRESS2 ,
                EACH_REC.CITY ,
                EACH_REC.CONTACT_PERSON ,
                EACH_REC.COUNTRY ,
                EACH_REC.EMAIL ,
                EACH_REC.PHONE ,
                EACH_REC.STATE ,
                EACH_REC.POSTAL_CODE ,
                UPPER(EACH_REC.STATUS) ,
                DECODE(UPPER(EACH_REC.STATUS),'INACTIVE',0,1), 1
              );
  
            -- Party creation
              BEGIN
                INSERT INTO PARTY
                (
                  ID,
                  NAME,
                  VERSION,
                  ADDRESS,
                  D_ACTIVE,
                  D_INTERNAL_COMMENTS
                )
                VALUES
                (
                  v_party_id,
                  EACH_REC.CUSTOMER_NAME,
                  0,
                  v_address_id,
                  DECODE(UPPER(EACH_REC.STATUS),'INACTIVE',0,1),
                  'UI UPLOAD ON :: '||SYSDATE
                );
              END;
              
            -- Organization and Service Provider creation
              IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALER','SUPPLIER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER')
              THEN
                BEGIN
                  INSERT INTO ORGANIZATION
                  (
                    ID,
                    PREFERRED_CURRENCY
                  )
                  VALUES
                  (
                    v_party_id,
                    UPPER(EACH_REC.CURRENCY)
                  );
  
                END;
  
                IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALER', 'THIRD PARTY', 'NATIONAL ACCOUNT', 'OEM', 'DIRECT CUSTOMER')
                THEN
                    INSERT INTO SERVICE_PROVIDER
                    (
                      ID,
                      SERVICE_PROVIDER_NUMBER,
                      STATUS
                    )
                    VALUES
                    (
                      v_party_id,
                      EACH_REC.CUSTOMER_NUMBER,
                      UPPER(EACH_REC.STATUS)
                    );
                    
                    -- In case of Hussmann dealer, we need to populate the Submit Credit also
                    BEGIN
                      IF UPPER(EACH_REC.BUSINESS_UNIT) = 'HUSSMANN' AND EACH_REC.CUSTOMER_TYPE = 'DEALER'
                      THEN
                        
                        UPDATE SERVICE_PROVIDER
                        SET SUBMIT_CREDIT = 'N'
                        WHERE
                          ID = v_party_id;								
                      END IF;
                    END;
                  END IF;
                END IF;
                  
  
            -- BU Organization Mapping creation
              BEGIN
  
                SELECT NAME
                INTO v_bu_name
                FROM Business_unit
                WHERE
                  UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT);
  
                INSERT INTO BU_ORG_MAPPING 
                (
                  ORG,
                  BU
                )
                VALUES
                (
                  v_party_id,
                  v_bu_name
                );
              END;
  
              IF UPPER(EACH_REC.CUSTOMER_TYPE) = 'DEALER'
              THEN
                BEGIN
                  INSERT INTO DEALERSHIP
                  (
                    ID,
                    DEALER_NUMBER,
                    PREFERRED_CURRENCY,
                    STATUS
                  )
                  VALUES
                  (
                    v_party_id,
                    EACH_REC.CUSTOMER_NUMBER,
                    UPPER(EACH_REC.CURRENCY),
                    UPPER(EACH_REC.STATUS)
                  );						
                END;						
              ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'THIRD PARTY'
              THEN
                BEGIN
                  INSERT INTO THIRD_PARTY
                  (
                    ID,
                    THIRD_PARTY_NUMBER
                  )
                  VALUES
                  (
                    v_party_id,
                    EACH_REC.CUSTOMER_NUMBER
                  );
                END;					
              ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'NATIONAL ACCOUNT'
              THEN
                BEGIN
                  INSERT INTO NATIONAL_ACCOUNT
                  (
                    ID,
                    NATIONAL_ACCOUNT_NUMBER
                  )
                  VALUES
                  (
                    v_party_id,
                    EACH_REC.CUSTOMER_NUMBER
                  );
                END;
              ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'OEM'
              THEN
                BEGIN
                  INSERT INTO ORIGINAL_EQUIP_MANUFACTURER
                  (
                    ID,
                    ORG_EQUIP_MANUF_NUMBER
                  )
                  VALUES
                  (
                    v_party_id,
                    EACH_REC.CUSTOMER_NUMBER
                  );
                END;
              ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'SUPPLIER'
              THEN
                BEGIN
                  INSERT INTO SUPPLIER
                  (
                    ID,
                    PREFERRED_LOCATION_TYPE,
                    SUPPLIER_NUMBER
                  )
                  VALUES
                  (
                    v_party_id,
                    'BUSINESS',
                    EACH_REC.CUSTOMER_NUMBER
                  );
                END;
              ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'DIRECT CUSTOMER'
              THEN
                BEGIN
                  INSERT INTO DIRECT_CUSTOMER
                  (
                    ID,
                    DIRECT_CUSTOMER_NUMBER
                  )
                  VALUES
                  (
                    v_party_id,
                    EACH_REC.CUSTOMER_NUMBER
                  );
                END;
              END IF;
  
            END;
              END IF;
        END;
        
      UPDATE CUSTOMER_STAGING
      SET
        UPLOAD_ERROR = NULL,
        UPLOAD_DATE = SYSDATE,
        UPLOAD_STATUS = 'Y'
      WHERE
        ID = EACH_REC.ID;
        
    EXCEPTION
		WHEN OTHERS
		THEN
			
			ROLLBACK;
			
			v_error_code := SUBSTR(SQLERRM,0,3500);
			
			UPDATE CUSTOMER_STAGING
			SET
				UPLOAD_ERROR = v_error_code,
				UPLOAD_STATUS = 'N'
			WHERE
				ID = EACH_REC.ID;
		END;		

		COMMIT;

	END LOOP;
END;
/
COMMIT
/