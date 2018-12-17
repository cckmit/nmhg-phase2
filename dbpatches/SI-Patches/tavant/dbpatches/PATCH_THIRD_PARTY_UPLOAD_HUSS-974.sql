create or replace
PROCEDURE UPLOAD_THIRD_PARTY
IS
   CURSOR ALL_REC
   IS
        SELECT *
    FROM STG_THIRD_PARTY
    WHERE NVL(ERROR_STATUS, 'N') = 'Y'
    AND ERROR_CODE              IS NULL
    AND NVL(UPLOAD_STATUS, 'N')  = 'N'
    ORDER BY ID ASC;

	--ALL GLOBAL VARAIBLE FOR THE PROCEDURE
	v_party_id   		  		NUMBER;
	v_addr_id 			  		NUMBER;
	v_error                    	VARCHAR2 (1000);
	v_third_party_id 			NUMBER;
	v_bu						VARCHAR2 (1000);
  v_address_book_id	  		NUMBER;
	v_add_book_add_map_id		NUMBER;
	
	
	
	
	BEGIN
		FOR EACH_REC IN ALL_REC
		LOOP
			BEGIN
				BEGIN
					SELECT PARTY_SEQ.NEXTVAL
					INTO v_party_id
					FROM DUAL;
				END;

				BEGIN
					SELECT ADDRESS_SEQ.NEXTVAL
					INTO v_addr_id
					FROM DUAL;
				END;
				
				-- GET THE BUSINESS_UNIT NAME
				BEGIN
					SELECT NAME 
					INTO v_bu
					FROM BUSINESS_UNIT WHERE UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT_INFO);
				END;

				--INSERT INTO ADDRESS TABLE
				INSERT INTO ADDRESS 
				( 
					ID, 
					ADDRESS_LINE1, 
					ADDRESS_LINE2, 
					CITY, 
					CONTACT_PERSON_NAME, 
					COUNTRY, 
					EMAIL,
					PHONE, 
					SECONDARY_EMAIL, 
					SECONDARY_PHONE, 
					STATE,  
					VERSION, 
					ZIP_CODE,
					STATUS,
					BELONGS_TO,
					ADDRESS_LINE3,
					ADDRESS_LINE4,
					d_active
										
				)
				VALUES 
				(
					v_addr_id,
					(EACH_REC.addr1), 
					(EACH_REC.addr2),
					(EACH_REC.city),
					(EACH_REC.contact_person),
					(EACH_REC.country),
					(EACH_REC.EMAIL),
					(EACH_REC.phone), 
					NULL, 
					NULL, 
					(EACH_REC.state),  
					0, 
					(EACH_REC.POSTAL_CODE),
					UPPER(EACH_REC.STATUS),
					null,	--v_party_id,
					EACH_REC.ADDR3,
					EACH_REC.ADDR4,
					1
					
				);


				--INSERT INTO PARTY TABLE
				INSERT INTO PARTY 
				( 
					ID, 
					NAME, 
					VERSION, 
					ADDRESS,
					IS_PART_OF_ORGANIZATION,
					d_active
				) 
				VALUES 
				(
					V_Party_Id,
					trim(EACH_REC.THIRD_PARTY_NAME),
					0,
					v_addr_id,
					NULL,
					1
				);
				--update belongs_to field of Address table
				--update address set belongs_to = v_party_id where address.id = v_addr_id;
				
				--INSERT INTO ORGANIZATION TABLE
				INSERT INTO ORGANIZATION
				(
					ID,
					PREFERRED_CURRENCY
				) 
				VALUES 
				(
					v_party_id,
					UPPER(EACH_REC.PREFERRED_CURRENCY)
				);
				
				
				--INSERT INTO BU_ORG_MAPPING TABLE
				INSERT INTO BU_ORG_MAPPING
				(
					ORG,
					BU
				) 
				VALUES 
				(
					v_party_id,
					--'Hussmann'
					v_bu
					
				);
				
				--INSERT INTO SERVICE_PROVIDER TABLE
				INSERT INTO SERVICE_PROVIDER
				(
					ID,
					SERVICE_PROVIDER_NUMBER,
					STATUS,
					SUBMIT_CREDIT
				) 
				VALUES
				(	
					v_party_id,
					(EACH_REC.THIRD_PARTY_NUMBER),
					(UPPER(EACH_REC.STATUS)),
					null
					
				);
				
				
				--ASSIGNING VALUE TO v_third_party_id;
				
				--v_third_party_id := 0;
				--SELECT ID into v_third_party_id FROM SERVICE_PROVIDER WHERE TRIM(EACH_REC.THIRD_PARTY_NUMBER) = SERVICE_PROVIDER_NUMBER;
				
				--INSERT INTO THIRD_PARTY TABLE
				INSERT INTO THIRD_PARTY
				(
					ID,
					THIRD_PARTY_NUMBER
					
				) 
				VALUES
				(	
					v_party_id,
					(EACH_REC.THIRD_PARTY_NUMBER)
					
				);
				
        
      BEGIN
					
					SELECT ID
					INTO v_address_book_id
					FROM ADDRESS_BOOK
					WHERE
						BELONGS_TO = v_party_id AND
						TYPE = 'SELF';
				
				EXCEPTION
				WHEN NO_DATA_FOUND
				THEN
					--GET THE NEW ID FROM SEQUENCE
					SELECT ADDRESS_BOOK_SEQ.NEXTVAL
					INTO v_address_book_id
					FROM DUAL;
				
					--INSERT INTO ADDRESS BOOK
					INSERT INTO ADDRESS_BOOK 
					( 
						ID, 
						TYPE, 
						VERSION, 
						BELONGS_TO,
						D_ACTIVE
					) 
					VALUES 
					(
						v_address_book_id,
						'SELF',
						0,
						v_party_id,
						1
					);
				END;

				--GET THE VALUE FROM THE SEQUENCE
				SELECT ADDRESSBOOK_ADDMAP_SEQ.NEXTVAL-1
				INTO v_add_book_add_map_id
				FROM DUAL;
				
				--INSERT INTO 3RD PARTY SITES NOW
				INSERT INTO ADDRESS_BOOK_ADDRESS_MAPPING
				(
					ID,
					IS_PRIMARY,
					PRIVILEGE,
					TYPE,
					VERSION,
					ADDRESS_BOOK_ID,
					ADDRESS_ID
				)
				VALUES
				(
					v_add_book_add_map_id,
					DECODE(EACH_REC.IS_PRIMARY,'Y',1,0),
					NULL,
					DECODE(EACH_REC.IS_PRIMARY,'Y','HOME','SHIPPING'),	--need to ask, where 'BILLING' will come into picture
					0,
					v_address_book_id,
					v_addr_id
				);
-- 				
-- 				BEGIN
-- 				IF EACH_REC.SITE_NUMBER IS NULL THEN 
-- 					v_SITE_NUMBER := (EACH_REC.THIRD_PARTY_NUMBER)  ||'-' || v_addr_id;
-- 				ELSE
-- 					v_SITE_NUMBER := EACH_REC.SITE_NUMBER;
-- 				END IF;
--             END;
			 
				
				--INSERT INTO ORGANIZATION_ADDRESS TABLE ALSO TO SAVE LOCATION NAME AND SITE NUMBER
				INSERT INTO ORGANIZATION_ADDRESS
				(
					ID,
					LOCATION,
					SITE_NUMBER
				)	
				VALUES
				(
					v_addr_id,
					(EACH_REC.ADDR1) || DECODE(EACH_REC.ADDR2,NULL,'','-'||EACH_REC.ADDR2) || DECODE(EACH_REC.city,NULL,'','-'||EACH_REC.city)  || DECODE(EACH_REC.state,NULL,'','-'||EACH_REC.state) || DECODE(EACH_REC.POSTAL_CODE,NULL,'','-'||EACH_REC.POSTAL_CODE) || DECODE(EACH_REC.country,NULL,'','-'||EACH_REC.country),
					--(EACH_REC.THIRD_PARTY_NUMBER)  ||'-' || v_addr_id
					--EACH_REC.SITE_NUMBER
					EACH_REC.THIRD_PARTY_SITE_CODE
				);
				
				--INSERT INTO MAPPING TABLE TO MAP WITH ORGANIZATION
				INSERT INTO ORGANIZATION_ORG_ADDRESSES
				(
					ORGANIZATION,
					ORG_ADDRESSES
				)
				VALUES
				(
					v_party_id,
					v_addr_id
				);
				
				--UPDATE TAV DC 045 TABLE DEPENDING ON THE STATUS
				UPDATE STG_THIRD_PARTY
				SET 
					UPLOAD_STATUS = 'Y',
					UPLOAD_DATE = SYSDATE
				WHERE 
						id = EACH_REC.id;		
			EXCEPTION
			WHEN OTHERS
			THEN
				
				--FIRST ROLL BACK 
				ROLLBACK;
				
				v_error := SUBSTR (SQLERRM, 1, 900);

				UPDATE  STG_THIRD_PARTY
				SET 
					UPLOAD_STATUS = 'N',
					UPLOAD_ERROR = v_error
				WHERE 
						id = EACH_REC.id;		

			END;
			
			--COMMIT FOR EACH LOOP
			COMMIT;
		END LOOP;
END UPLOAD_THIRD_PARTY;