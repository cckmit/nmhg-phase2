CREATE TABLE STG_THIRD_PARTY
(
	ID                                      NUMBER,
	FILE_UPLOAD_MGT_ID                      NUMBER,
	THIRD_PARTY_NUMBER                      VARCHAR2(4000),
	THIRD_PARTY_NAME                        VARCHAR2(4000),
	THIRD_PARTY_SITE_CODE                   VARCHAR2(4000),
	PREFERRED_CURRENCY                      VARCHAR2(4000),
	CONTACT_PERSON                          VARCHAR2(4000),
	ADDR1                                   VARCHAR2(4000),
	ADDR2                                   VARCHAR2(4000),
	CITY                                    VARCHAR2(4000),
	STATE                                   VARCHAR2(4000),
	POSTAL_CODE                             VARCHAR2(4000),
	COUNTRY                                 VARCHAR2(4000),
	PHONE                                   VARCHAR2(4000),
	EMAIL                                   VARCHAR2(4000),
	STATUS                                  VARCHAR2(4000),
	IS_PRIMARY                              VARCHAR2(4000),
	BUSINESS_UNIT_INFO                      VARCHAR2(4000),
	ADDR3                                   VARCHAR2(255),
	ADDR4                                   VARCHAR2(255),
	ERROR_STATUS                            VARCHAR2(20),
	ERROR_CODE                              VARCHAR2(4000),
	UPLOAD_STATUS                           VARCHAR2(20),
	UPLOAD_ERROR                            VARCHAR2(4000),
	UPLOAD_DATE                             VARCHAR2(20)
)
/
declare 
v_count number;
begin
  select count(*) into v_count from upload_mgt where name_of_template='thirdPartyUpload';
  if v_count = 0 then

    insert into upload_mgt(id,name_of_template,name_to_display,description,template_path,
	  staging_table,backup_table, validation_procedure, population_procedure, upload_procedure,
      columns_to_capture, consume_rows_from, header_row_to_capture)
    values(upload_mgt_seq.nextval,'thirdPartyUpload','Third Party Upload',
	  'Upload Scheme - Third Party',
      '.\pages\secure\admin\upload\templates\Template-ThirdPartyUpload.xls',
	  'STG_THIRD_PARTY', NULL,
      'UPLOAD_THIRD_PARTY_VALIDATION', null, 'UPLOAD_THIRD_PARTY',
      17, 6, 1);

    insert into upload_roles(upload_mgt,roles)
        select id,(select id from role where name='admin')
        from upload_mgt where name_of_template='thirdPartyUpload';

    commit;
  end if;
end;
/
begin
create_upload_error('thirdPartyUpload','en_US','THIRD PARTY NUMBER','DUP0001','Third Party Number already exists');
create_upload_error('thirdPartyUpload','en_US','THIRD PARTY NUMBER','DUP0002','Duplicate Third Party Number');
create_upload_error('thirdPartyUpload','en_US','STATUS','INV0001','Status is not specified');
create_upload_error('thirdPartyUpload','en_US','PREFERRED CURRENCY','INV0002','Currency is not Valid');
create_upload_error('thirdPartyUpload','en_US','Business Unit Info','INV0003','Business Unit Info is Invalid');
create_upload_error('thirdPartyUpload','en_US','THIRD PARTY NUMBER','NUL0001','Third Party Number is not specified');
create_upload_error('thirdPartyUpload','en_US','THIRD PARTY NAME','NUL0002','Third Party Name is not specified');
create_upload_error('thirdPartyUpload','en_US','PREFERRED CURRENCY','NUL0003','Currency is not specified');
create_upload_error('thirdPartyUpload','en_US','ADDR1','NUL0004','Address Line1 is not specified');
create_upload_error('thirdPartyUpload','en_US','CITY','NUL0005','City is not specified');
create_upload_error('thirdPartyUpload','en_US','STATE','NUL0006','State is not specified');
create_upload_error('thirdPartyUpload','en_US','POSTAL CODE','NUL0007','Zipcode is not specified');
create_upload_error('thirdPartyUpload','en_US','COUNTRY','NUL0008','Country is not specified');
create_upload_error('thirdPartyUpload','en_US','THIRD PARTY SITE CODE','NUL0009','Third Party Site Code is not specified');
end;
/   
create or replace PROCEDURE UPLOAD_THIRD_PARTY_VALIDATION AS 
   CURSOR ALL_REC
   IS
      SELECT *
        FROM STG_THIRD_PARTY
        WHERE NVL(ERROR_STATUS,'N') = 'N'
            AND UPLOAD_STATUS IS NULL
        ORDER BY ID ASC;

	--ALL GLOBAL VARIABLES ARE DEFINED HERE.
    v_error_code			VARCHAR2 (4000)		:=		NULL;
    v_file_upload_mgt_id        NUMBER := 0;
    v_success_count             NUMBER := 0;
    v_error_count               NUMBER := 0;
    v_error                     VARCHAR2(4000) := NULL;
    v_count                     NUMBER := NULL;
   
BEGIN
	--MAIN  LOOP FOR ALL ROWS. EACH ROW WILL BE VALIDATED ONE BY ONE
	FOR EACH_REC IN ALL_REC
	LOOP
	
		--RESETING ERROR VARIABLE FOR EACH LOOP.
		v_error_code := NULL;
		
		--MAIN BEGIN LOOP
		BEGIN				
			--ERROR CODE: NUL0001
			--VALIDATE THAT THIRD_PARTY_NUMBER IS NOT NULL 
			--REASON : THIRD_PARTY_NUMBER IS NULL
			BEGIN
				 IF TRIM(EACH_REC.THIRD_PARTY_NUMBER) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0001');
				 END IF;				
			END;
			
			--ERROR CODE: NUL0002
			--VALIDATE THAT THIRD_PARTY_NAME IS NOT NULL 
			--REASON : THIRD_PARTY_NAME IS NULL
			BEGIN
				 IF TRIM(EACH_REC.THIRD_PARTY_NAME) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0002');
				 END IF;				
			END;
			
			--ERROR CODE: NUL0003
			--VALIDATE THAT PREFERRED_CURRENCY IS NOT NULL 
			--REASON : PREFERRED_CURRENCY IS NULL
			BEGIN
				 IF TRIM(EACH_REC.PREFERRED_CURRENCY) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0003');
				 END IF;				
			END;
			
			--ERROR CODE: NUL0004
			--VALIDATE THAT ADDR1 IS NOT NULL 
			--REASON : ADDR1 IS NULL
			BEGIN
				 IF TRIM(EACH_REC.ADDR1) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0004');
				 END IF;				
			END;
			
			
		--ERROR CODE: NUL0005
			--VALIDATE THAT CITY IS NOT NULL 
			--REASON : CITY IS NULL
			BEGIN
				 IF TRIM(EACH_REC.CITY) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0005');
				 END IF;				
			END;
			
			
			--ERROR CODE: NUL0006
			--VALIDATE THAT STATE IS NOT NULL 
			--REASON : STATE IS NULL
			BEGIN
				 IF TRIM(EACH_REC.STATE) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0006');
				 END IF;				
			END;
			
			
			--ERROR CODE: NUL0007
			--VALIDATE THAT POSTAL_CODE IS NOT NULL 
			--REASON :  POSTAL_CODE IS NULL
			BEGIN
				 IF TRIM(EACH_REC.POSTAL_CODE) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0007');
				 END IF;				
			END;
			
			
			--ERROR CODE: NUL0008
			--VALIDATE THAT COUNTRY IS NOT NULL 
			--REASON : COUNTRY IS NULL
			BEGIN
				 IF TRIM(EACH_REC.COUNTRY) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0008');
				 END IF;				
			END;
	  
		--ERROR CODE: NUL0009
			--VALIDATE THAT THIRD_PARTY_SITE_CODE IS NOT NULL 
			--REASON : THIRD_PARTY_SITE_CODE IS NULL
			BEGIN
				 IF TRIM(EACH_REC.THIRD_PARTY_SITE_CODE) IS NULL
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL0009');
				 END IF;				
			END;
			
			--ERROR CODE: INV0001
			--VALIDATE THAT STATUS IS NOT NULL 
			--REASON : STATUS IS NULL
			BEGIN
				 IF UPPER(EACH_REC.STATUS) NOT IN ('ACTIVE', 'INACTIVE')
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'INV0001');
				 END IF;				
			END;
			
			--ERROR CODE: INV0002
			--VALIDATE THAT STATUS IS NOT NULL 
			--REASON : STATUS IS NULL
			BEGIN
				 IF UPPER(EACH_REC.PREFERRED_CURRENCY) NOT IN ('USD', 'GBP', 'EUR')
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'INV0002');
				 END IF;				
			END;
	  
		BEGIN
				 IF UPPER(EACH_REC.BUSINESS_UNIT_INFO) NOT IN ('HUSSMANN')
				 THEN
					 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'INV0003');
				 END IF;				
			END;
			
			--UPDATE TABLE WITH ERROR/SUCCESS STATUS
			IF v_error_code IS NULL
			THEN
				UPDATE STG_THIRD_PARTY
				SET
					ERROR_STATUS = 'Y',
					ERROR_CODE = NULL
				WHERE
					id = EACH_REC.id;						
			ELSE
				UPDATE STG_THIRD_PARTY
				SET
					ERROR_STATUS = 'N',
					ERROR_CODE = v_error_code
				WHERE
					id = EACH_REC.id;						
			END IF;
			
END;		
	END LOOP;				
--RUN UPDATE QUERY TO UPDATE ALL DUPLICATE RECORD IN A TABLE WITH AN ERROR CODE
   UPDATE STG_THIRD_PARTY
   SET 
	   ERROR_STATUS = 'N',
	   ERROR_CODE = ERROR_CODE || ';' || DECODE (ERROR_CODE,NULL, 'DUP0001','DUP0001')
   WHERE 
  THIRD_PARTY_NUMBER 	IN 
		(
				  SELECT   
						   THIRD_PARTY_NUMBER
				  FROM THIRD_PARTY
		 );
   
   UPDATE STG_THIRD_PARTY
   SET 
	   ERROR_STATUS = 'N',
	   ERROR_CODE = ERROR_CODE || ';' ||DECODE (ERROR_CODE,NULL, 'DUP0002','DUP0002')
   WHERE 
  THIRD_PARTY_NUMBER IN 
		(
		SELECT   
						   THIRD_PARTY_NUMBER
				  FROM STG_THIRD_PARTY
				  GROUP BY 
						THIRD_PARTY_NUMBER
				  HAVING COUNT (*) > 1
		 );
			COMMIT;
		BEGIN
	SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
	FROM stg_third_party WHERE ROWNUM = 1;

	-- Success Count
	BEGIN
		SELECT count(*) INTO v_success_count
		FROM stg_third_party 
		WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
	EXCEPTION
		WHEN OTHERS THEN
			v_success_count := 0;
	END;
		
	-- Error Count
	BEGIN
		SELECT count(*) INTO v_error_count
		FROM stg_third_party 
		WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
	EXCEPTION
		WHEN OTHERS THEN
			v_error_count := 0;
	END;

	-- Total Count
	SELECT count(*) INTO v_count
	FROM stg_third_party 
	WHERE file_upload_mgt_id = v_file_upload_mgt_id;

	UPDATE file_upload_mgt SET 
		success_records= v_success_count, 
		error_records= v_error_count,
		total_records = v_count
	WHERE id = v_file_upload_mgt_id;
	
EXCEPTION
	WHEN OTHERS THEN
		v_error := SUBSTR(SQLERRM, 1, 4000);
		UPDATE file_upload_mgt SET 
			error_message = v_error
		WHERE id = v_file_upload_mgt_id;
END;
		
END UPLOAD_THIRD_PARTY_VALIDATION;
/
create or replace PROCEDURE UPLOAD_THIRD_PARTY
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
				v_party_id,
				(EACH_REC.THIRD_PARTY_NAME),
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
			SELECT ADDR_BOOK_ADDR_MAPP_SEQ.NEXTVAL
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
				(EACH_REC.ADDR1) || DECODE(EACH_REC.ADDR2,NULL,'','-'||EACH_REC.ADDR2) 
					|| DECODE(EACH_REC.city,NULL,'','-'||EACH_REC.city)  
					|| DECODE(EACH_REC.state,NULL,'','-'||EACH_REC.state) 
					|| DECODE(EACH_REC.POSTAL_CODE,NULL,'','-'||EACH_REC.POSTAL_CODE) 
					|| DECODE(EACH_REC.country,NULL,'','-'||EACH_REC.country),
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