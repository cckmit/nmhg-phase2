CREATE TABLE STG_WNTY_CVG_EXCLUSION
(
	ID                  NUMBER,
	FILE_UPLOAD_MGT_ID  NUMBER,
	POLICY_NAME         VARCHAR2(4000 BYTE),
	DEALER_NUMBER       VARCHAR2(4000 BYTE),
	BUSINESS_UNIT_INFO  VARCHAR2(4000 BYTE),
	IS_REMOVE_EXCLUSION VARCHAR2(20 BYTE),
	ERROR_STATUS        VARCHAR2(20 BYTE),
	ERROR_CODE          VARCHAR2(4000 BYTE),
	UPLOAD_STATUS       VARCHAR2(20 BYTE),
	UPLOAD_ERROR        VARCHAR2(4000 BYTE),
	UPLOAD_DATE         VARCHAR2(20 BYTE)
)
/
declare 
v_count number;
begin
  select count(*) into v_count from upload_mgt where name_of_template='warrantyCoverageExclusion';
  if v_count = 0 then

    insert into upload_mgt(id,name_of_template,name_to_display,description,template_path,
	  staging_table,backup_table, validation_procedure, population_procedure, upload_procedure,
      columns_to_capture, consume_rows_from, header_row_to_capture)
    values(upload_mgt_seq.nextval,'warrantyCoverageExclusion','Warranty Coverage Exclusion Upload',
	  'Upload Scheme - Warranty Coverage Exclusion',
      '.\pages\secure\admin\upload\templates\Template-WarrantyCoveragesExclusion.xls',
	  'STG_WNTY_CVG_EXCLUSION', NULL,
      'WNTY_EXCLUSION_VALIDATION', null, 'WNTY_EXCLUSION_UPLOAD',
      5, 6, 1);

    insert into upload_roles(upload_mgt,roles)
        select id,(select id from role where name='admin')
        from upload_mgt where name_of_template='warrantyCoverageExclusion';

    commit;
  end if;
end;
/
begin
  create_upload_error('warrantyCoverageExclusion','en_US','POLICY NAME','WCE001','Policy Code is not specified');
  create_upload_error('warrantyCoverageExclusion','en_US','DEALER NUMBER','WCE002','Dealer Number is not specified');
  create_upload_error('warrantyCoverageExclusion','en_US','BUSINESS UNIT INFO','WCE003','Business Unit Info is not specified');
  create_upload_error('warrantyCoverageExclusion','en_US','BUSINESS UNIT INFO','WCE005','Business Unit Info is Invalid');
  create_upload_error('warrantyCoverageExclusion','en_US','POLICY NAME','WCE006','Policy code does not exist');

  create_upload_error('warrantyCoverageExclusion','en_US','DEALER NUMBER','WCE007','Dealer Number does not exist');
  create_upload_error('warrantyCoverageExclusion','en_US','POLICY NAME','WCE009','Policy Code is not valid for specified Business Unit');
  create_upload_error('warrantyCoverageExclusion','en_US','DEALER NUMBER','WCE010','Dealer Number is not valid for specified Business Unit');
  create_upload_error('warrantyCoverageExclusion','en_US','IS REMOVE EXCLUSION','WCE004','Is Remove Exclusion is Invalid');  
  create_upload_error('warrantyCoverageExclusion','en_US','POLICY NAME','WCE008','Duplicate Record');
end;
/   
create or replace
PROCEDURE WNTY_EXCLUSION_VALIDATION AS 
   CURSOR ALL_REC
   IS
      SELECT *
        FROM STG_WNTY_CVG_EXCLUSION
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
    v_policy_id                 Number:=null;
    v_dealer_id                  Number:=null;
    v_bu                         VARCHAR2(4000) := NULL;
   
	BEGIN
		--MAIN  LOOP FOR ALL ROWS. EACH ROW WILL BE VALIDATED ONE BY ONE
		FOR EACH_REC IN ALL_REC
		LOOP
		
			--RESETING ERROR VARIABLE FOR EACH LOOP.
			v_error_code := NULL;
			
			--MAIN BEGIN LOOP
			BEGIN				
				--ERROR CODE: WCE001
				--VALIDATE THAT POLICY_NAME IS NOT NULL 
				--REASON : POLICY_NAME IS NULL
				BEGIN
					 IF TRIM(EACH_REC.POLICY_NAME) IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'WCE001');
					 END IF;				
		   		END;
				
				--ERROR CODE: WCE002
				--VALIDATE THAT DEALER_NUMBER IS NOT NULL 
				--REASON : DEALER_NUMBER IS NULL
				BEGIN
					 IF TRIM(EACH_REC.DEALER_NUMBER) IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'WCE002');
					 END IF;				
		   		END;
				
				--ERROR CODE: WCE003
				--VALIDATE THAT BUSINESS_UNIT_INFO IS NOT NULL 
				--REASON : BUSINESS_UNIT_INFO IS NULL
				BEGIN
					 IF TRIM(EACH_REC.BUSINESS_UNIT_INFO) IS NULL
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'WCE003');
					 END IF;				
		   		END;
				
				--ERROR CODE: WCE004
				--VALIDATE THAT IS_REMOVE_EXCLUSION 
				
				 BEGIN
					 IF UPPER(TRIM(EACH_REC.IS_REMOVE_EXCLUSION)) NOT IN ('Y', 'N', NULL)
					 THEN
					 	 v_error_code := Common_Utils.ADDERROR(v_error_code, 'WCE004');
					 END IF;				
		   		END;
				
				
			--ERROR CODE: WCE005
				--VALIDATE BUSINESS_UNIT_INFO
				BEGIN
					SELECT NAME 
					INTO v_bu
					FROM BUSINESS_UNIT WHERE UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT_INFO);
          EXCEPTION
					  WHEN NO_DATA_FOUND THEN
						v_error_code := common_utils.addErrorMessage(v_error_code, 'WCE005');
						NULL;	
				END;
          
        --ERROR CODE: WCE006
				--VALIDATE POLICY_NAME
        BEGIN
					select id into v_policy_id
					from policy_definition where code=each_rec.POLICY_NAME and d_active=1;
				EXCEPTION
					  WHEN NO_DATA_FOUND THEN
						v_error_code := common_utils.addErrorMessage(v_error_code, 'WCE006');
						NULL;	
				END;
				
        
        --ERROR CODE: WCE007
				--VALIDATE DEALER_NUMBER
        BEGIN
					select id into v_dealer_id
					from service_provider where service_provider_number=each_rec.DEALER_NUMBER;
				EXCEPTION
					  WHEN NO_DATA_FOUND THEN
						v_error_code := common_utils.addErrorMessage(v_error_code, 'WCE007');
						NULL;	
				END;
        
        
        
				
				--UPDATE TABLE WITH ERROR/SUCCESS STATUS
				IF v_error_code IS NULL
				THEN
					UPDATE STG_WNTY_CVG_EXCLUSION
					SET
						ERROR_STATUS = 'Y',
						ERROR_CODE = NULL
					WHERE
						id = EACH_REC.id;						
				ELSE
					UPDATE STG_WNTY_CVG_EXCLUSION
					SET
						ERROR_STATUS = 'N',
						ERROR_CODE = v_error_code
					WHERE
						id = EACH_REC.id;						
				END IF;
				
END;		
		END LOOP;				
--RUN UPDATE QUERY TO UPDATE ALL DUPLICATE RECORD IN A TABLE WITH AN ERROR CODE
	  
     UPDATE STG_WNTY_CVG_EXCLUSION
       SET 
	   	   ERROR_STATUS = 'N',
       	   ERROR_CODE = ERROR_CODE || ';' ||DECODE (ERROR_CODE,NULL, 'WCE008','WCE008')
       WHERE 
      POLICY_NAME IN 
			(
            SELECT   
					  		   POLICY_NAME
					  FROM STG_WNTY_CVG_EXCLUSION
            WHERE ERROR_STATUS is null
            GROUP BY 
					  		POLICY_NAME,DEALER_NUMBER
                  	  HAVING COUNT (*) > 1
			 );
				COMMIT;
			BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
        FROM STG_WNTY_CVG_EXCLUSION WHERE ROWNUM = 1;
    
        -- Success Count
        BEGIN
            SELECT count(*) INTO v_success_count
            FROM STG_WNTY_CVG_EXCLUSION 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;
            
        -- Error Count
        BEGIN
            SELECT count(*) INTO v_error_count
            FROM STG_WNTY_CVG_EXCLUSION 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;

        -- Total Count
        SELECT count(*) INTO v_count
        FROM STG_WNTY_CVG_EXCLUSION 
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
	
			
	END;
/
create or replace
PROCEDURE WNTY_EXCLUSION_UPLOAD 
IS
   CURSOR ALL_REC
   IS
        SELECT *
    FROM STG_WNTY_CVG_EXCLUSION
    WHERE NVL(ERROR_STATUS, 'N') = 'Y'
    AND ERROR_CODE              IS NULL
    AND NVL(UPLOAD_STATUS, 'N')  = 'N'
    ORDER BY ID ASC;

	--ALL GLOBAL VARAIBLE FOR THE PROCEDURE
	v_dealer_id   		  		NUMBER;
	v_policy_id 			  		NUMBER;
    v_existing_cvg          NUMBER := 0;
	v_error                    	VARCHAR2 (1000);
	v_bu						VARCHAR2 (1000);
  
BEGIN
	FOR EACH_REC IN ALL_REC
	LOOP
		BEGIN
  
   BEGIN
		SELECT NAME 
		INTO v_bu
		FROM BUSINESS_UNIT WHERE UPPER(NAME) = UPPER(EACH_REC.BUSINESS_UNIT_INFO);
	  END;
	  
	  BEGIN
		select id into v_policy_id
		from policy_definition where code=each_rec.POLICY_NAME and d_active=1;
	  END;
	  
	  BEGIN
		select id into v_dealer_id
		from service_provider where service_provider_number=each_rec.DEALER_NUMBER;
	  END;
	  
	  BEGIN
		select count(*) into v_existing_cvg
		from POLICY_NOT_FOR_PROVIDERS 
		WHERE POLICY_DEFN=v_policy_id AND FOR_SERVICE_PROVIDER =v_dealer_id;
	  END;
	  
		
			IF UPPER(EACH_REC.IS_REMOVE_EXCLUSION) != 'Y' THEN	
	 
	--INSERT INTO POLICY_NOT_FOR_PROVIDERS TABLE
		IF v_existing_cvg =0 then
		  INSERT INTO POLICY_NOT_FOR_PROVIDERS  
		  VALUES 
		  (
			v_policy_id,
			v_dealer_id 
		  );
		  END IF;
	ELSE 
	  BEGIN
		DELETE FROM POLICY_NOT_FOR_PROVIDERS
		WHERE POLICY_DEFN=v_policy_id AND FOR_SERVICE_PROVIDER =v_dealer_id;
	  END;
	END IF;

			
			--UPDATE STG_WNTY_CVG_EXCLUSION TABLE DEPENDING ON THE STATUS
			UPDATE STG_WNTY_CVG_EXCLUSION
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

			UPDATE  STG_WNTY_CVG_EXCLUSION
			SET 
				UPLOAD_STATUS = 'N',
				UPLOAD_ERROR = v_error
			WHERE 
					id = EACH_REC.id;		

		END;
		
		--COMMIT FOR EACH LOOP
		COMMIT;
	END LOOP;
END;
/