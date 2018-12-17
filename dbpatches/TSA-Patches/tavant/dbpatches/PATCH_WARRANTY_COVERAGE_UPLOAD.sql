CREATE TABLE STG_WARRANTY_COVERAGES
(	ID NUMBER, 
	FILE_UPLOAD_MGT_ID NUMBER, 
	BUSINESS_UNIT_INFO VARCHAR2(255 BYTE), 
	SERIAL_NUMBER VARCHAR2(255 BYTE), 
	ITEM_NUMBER VARCHAR2(255 BYTE), 
	PLAN_CODE VARCHAR2(255 BYTE), 
	WARRANTY_START_DATE VARCHAR2(10 BYTE), 
	WARRANTY_END_DATE VARCHAR2(10 BYTE), 
	HOURS_COVERED NUMBER, 
	ORDER_DATE VARCHAR2(10 BYTE), 
	ORDER_NUMBER VARCHAR2(255 BYTE), 
	FORCE_COVERAGE VARCHAR2(100 BYTE), 
	COMMENTS VARCHAR2(255 BYTE), 
	ERROR_STATUS VARCHAR2(20 BYTE), 
	ERROR_CODE VARCHAR2(4000 BYTE), 
	UPLOAD_STATUS VARCHAR2(20 BYTE), 
	UPLOAD_ERROR VARCHAR2(4000 BYTE)
)
/
declare 
v_count number;
v_upload_id number;
begin
  select count(*) into v_count from upload_mgt where name_of_template='warrantyCoveragesUpload';
  if v_count = 0 then
	select upload_mgt_seq.nextval into v_upload_id from dual;
  
    insert into upload_mgt(id,name_of_template,name_to_display,description,template_path,
	  staging_table,backup_table, validation_procedure, population_procedure, upload_procedure,
      columns_to_capture, consume_rows_from, header_row_to_capture)
    values(v_upload_id,'warrantyCoveragesUpload','Warranty Coverages Upload',
	  'Warranty Coverages Upload','.\pages\secure\admin\upload\templates\Template-WarrantyCoverages.xls',
	  'STG_WARRANTY_COVERAGES', NULL,'WNTY_CVG_VALIDATION', null, 'WNTY_CVG_UPLOAD',
      12, 6, 1);

    insert into upload_roles(upload_mgt,roles)
        select id,(select id from role where name='admin')
        from upload_mgt where name_of_template='warrantyCoveragesUpload';

	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT) 
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Serial Number', 'Text', '1', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Item Number', 'Text', '2', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Plan Code', 'Text', '3', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Warranty Start Date', 'Date', '4', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Warranty End Date', 'Date', '5', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Hours Covered', 'Number', '6', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Order Date', 'Date', '7', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Order', 'Text', '8', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'FORCE COVERAGE', 'Text', '9', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Comments', 'Text', '10', v_upload_id);
	INSERT INTO UPLOAD_MGT_META_DATA (ID, COLUMN_NAME, COLUMN_TYPE, COLUMN_ORDER, UPLOAD_MGT)
	VALUES (UPLOAD_MGT_META_DATA_SEQ.nextval, 'Business Unit Info', 'Text', '11', v_upload_id);

    commit;
  end if;
end;
/
begin
  create_upload_error('warrantyCoveragesUpload','en_US','SERIAL NUMBER','FORMAT_1','Data Format of Serial Number is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','FORMAT_2','Data Format of Item Number is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','PLAN CODE','FORMAT_3','Data Format of Plan Code is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY START DATE','FORMAT_4','Data Format of Warranty Start Date is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY END DATE','FORMAT_5','Data Format of Warranty Start Date is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','HOURS COVERED','FORMAT_6','Data Format of Hours Covered is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','ORDER DATE','FORMAT_7','Data Format of Order Date is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','ORDER','FORMAT_8','Data Format of Order is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','FORCE COVERAGE','FORMAT_9','Data Format of Force Coverage is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','COMMENTS','FORMAT_10','Data Format of Comments is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','BUSINESS UNIT INFO','FORMAT_11','Data Format of Business Unit Info is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','WC001','Item Number is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','WC002','Item Number is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','WC003','Item Number is not exist');
  create_upload_error('warrantyCoveragesUpload','en_US','SERIAL NUMBER','WC004','Serial Number is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','SERIAL NUMBER','WC005','Serial Number is not exist');  
  
  create_upload_error('warrantyCoveragesUpload','en_US','PLAN CODE','WC006','Plan Code is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','PLAN CODE','WC007','Plan Code is not exist');
  
  create_upload_error('warrantyCoveragesUpload','en_US','HOURS COVERED','WC008','Hours Covered is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','HOURS COVERED','WC009','Hours Covered is not positive');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY START DATE','WC010','Warranty Start Date is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY START DATE','WC011','Warranty Start Date is not valid');  
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY END DATE','WC012','Warranty End Date is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY END DATE','WC013','Warranty End Date is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY START DATE','WC014','Warranty Start Date cannot be lesser than the Delivery Date for the Serial Number');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY END DATE','WC015','Warranty End Date should be greater than the Warranty Start Date');
 create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY START DATE','WC016','If existing plan with active claims, Warranty End Date cannot be lesser than the Failure Date on the active claims.');
  create_upload_error('warrantyCoveragesUpload','en_US','WARRANTY END DATE','WC017','If existing plan with active claims, Warranty End Date cannot be lesser than the Failure Date on the active claims.');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','WC018','Business Unit Info is not specified');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','WC019','Business Unit Info is not valid');
  create_upload_error('warrantyCoveragesUpload','en_US','ITEM NUMBER','WC020','Business Unit Info is not exist');
  
end;
/   
create or replace PROCEDURE WNTY_CVG_VALIDATION AS 
   CURSOR ALL_REC
   IS
      SELECT *
        FROM stg_warranty_coverages
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
    v_uploaded_by               VARCHAR2(255);
    v_bu_name                   VARCHAR2(255);
    v_inventory_type             VARCHAR2(255);
    v_policy_definition_aduit      VARCHAR2(255);
    v_item_id                   NUMBER := 0;
    v_policy_id                  NUMBER := 0;
    v_wrty_type					  VARCHAR2(100):= null;
    v_action_taken        VARCHAR2(100):= null;
      v_model_id                  NUMBER := NULL;
      v_product_id                  NUMBER := NULL;
    v_delievery_dt				DATE;
    v_min_failure_dt			DATE;
	  v_max_failure_dt			DATE;
    v_active_claim		        BOOLEAN;
    
      v_valid_bu                  BOOLEAN;
      v_valid_policy             BOOLEAN;
       v_valid_serial_number		BOOLEAN;
       v_valid_start_date       	BOOLEAN;
       v_valid_end_date       	BOOLEAN;
    
   
   
	BEGIN
		--MAIN  LOOP FOR ALL ROWS. EACH ROW WILL BE VALIDATED ONE BY ONE
		FOR EACH_REC IN ALL_REC
		LOOP
		
			--RESETING ERROR VARIABLE FOR EACH LOOP.
			v_error_code := NULL;
      v_valid_bu := FALSE;
      v_valid_policy := FALSE;
      v_valid_serial_number := FALSE;
      	v_min_failure_dt := NULL;
			v_max_failure_dt := NULL;
			v_active_claim := FALSE;
      v_valid_start_date := TRUE; 
      v_valid_start_date := TRUE;
            
       v_action_taken  := 'ACTIVE';
      
			
		 BEGIN
          SELECT u.login, f.business_unit_info INTO v_uploaded_by, v_bu_name
          FROM org_user u,file_upload_mgt f
          WHERE u.id = f.uploaded_by  AND f.id in
              (SELECT file_upload_mgt_id FROM stg_warranty_coverages WHERE rownum = 1);
       END;
		--Business Unit is mandatory and the uploaded user belongs to the BU
			IF each_rec.business_unit_info IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL01');
		  ELSIF NOT common_validation_utils.isUserBelongsToBU(each_rec.business_unit_info, v_uploaded_by) THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'INV01');
			ELSE
				v_valid_bu := TRUE;
			END IF;
    
   
      IF each_rec.item_number IS NULL THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL03');
          ELSIF v_valid_bu AND NOT COMMON_VALIDATION_UTILS.isValidItemNumber(each_rec.item_number, v_bu_name) THEN
            v_error_code := common_utils.addErrorMessage(v_error_code, 'INV02');
            ELSE
              BEGIN
                SELECT i.model 
                INTO v_model_id
                FROM ITEM i, PARTY p 
                WHERE ( lower(i.alternate_item_number) = lower(ltrim(rtrim(each_rec.item_number)))  OR 
                lower(i.item_number) = lower(ltrim(rtrim(each_rec.item_number))) )
                AND lower(i.business_unit_info) = lower(v_bu_name)
                AND i.owned_by = p.ID and UPPER(i.MAKE) IN ('IRI','HUSSMANN')
                AND p.NAME = common_utils.constant_oem_name and i.d_active = 1 AND ROWNUM = 1;
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  v_error_code := common_utils.addErrorMessage(v_error_code, 'INV02');
              END;
      END IF;
      
      IF v_model_id is not null then
        BEGIN
                SELECT is_part_of 
                INTO v_product_id
                FROM ITEM_GROUP
                WHERE id = v_model_id;
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  v_error_code := common_utils.addErrorMessage(v_error_code, 'INV02');
         END;
      END IF;
      
     
			IF each_rec.serial_number IS NULL THEN
				v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL02');
			ELSIF v_valid_bu and v_model_id is not null THEN
				BEGIN
					SELECT inv.type,inv.of_type,inv.delivery_date 
					INTO v_inventory_type,v_item_id,v_delievery_dt
					FROM inventory_item inv, item i
					WHERE UPPER(inv.serial_number) = UPPER(each_rec.serial_number) 
						AND inv.business_unit_info = v_bu_name
						AND inv.of_type = i.id AND i.model = v_model_id
						AND i.d_active=1 AND inv.d_active=1;
					v_valid_serial_number := TRUE;
					IF UPPER(v_inventory_type) != 'RETAIL' THEN
						v_error_code := common_utils.addErrorMessage(v_error_code, 'INV12');
					END IF ;
				EXCEPTION
					WHEN NO_DATA_FOUND THEN
						v_valid_serial_number := FALSE;
						v_error_code := common_utils.addErrorMessage(v_error_code, 'INV06');
						NULL;
				END;
			 END IF;
       		
				--ERROR CODE: NUL0006				
				--VALIDATE THAT WARRANTY POLICY CODE  IS NOT NULL 
				--REASON : WARRANTY POLICY CODE TYPE IS NULL
			
        IF EACH_REC.PLAN_CODE IS NULL
					 THEN
					 	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL07');
				  ELSE 
            BEGIN
             	select id,warranty_type into v_policy_id,v_wrty_type
              from policy_definition where upper(code)=upper(each_rec.plan_code) and d_active=1
              and upper(business_unit_info)=upper(v_bu_name)
              and active_till >= sysdate;
              v_valid_policy := TRUE;
            EXCEPTION
                WHEN NO_DATA_FOUND THEN
                v_valid_policy := FALSE;
                v_error_code := common_utils.addErrorMessage(v_error_code, 'INV07');
                NULL;	
            END;
         END IF;	
        
        
      IF v_product_id is not null  and v_valid_policy and upper(each_rec.force_coverage) != 'Y' then
        BEGIN
                SELECT 1 
                INTO v_count
                FROM policy_for_products
                WHERE for_product = v_product_id
                and POLICY_DEFN=v_policy_id;
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  v_error_code := common_utils.addErrorMessage(v_error_code, 'INV07');
         END;
      END IF;
   
				--ERROR CODE: NUL0005				
				--VALIDATE THAT HOURS_COVERED  IS NOT NULL 
				--REASON : HOURS_COVERED TYPE IS NULL
				BEGIN
					 IF UPPER(v_wrty_type) != 'STANDARD' AND EACH_REC.HOURS_COVERED IS NULL
					 THEN
					 	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'NUL06');
					 END IF;				
		   		END;
          
        IF EACH_REC.WARRANTY_START_DATE IS NULL THEN
        	v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL04');
           v_valid_start_date := FALSE; 
		   	END IF;
        
         IF EACH_REC.WARRANTY_END_DATE IS NULL THEN
        	v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL05');
          v_valid_end_date := FALSE; 
		   	 END IF;
        
				--ERROR CODE: INV003				
				--VALIDATE THAT WARRANTY START  DATE IS VALID
				--REASON : WARRANTY START DATE SHOULD BE IN VALID FORMAT
				BEGIN
					 IF EACH_REC.WARRANTY_START_DATE IS NOT NULL AND NOT Common_Utils.isValidDate(EACH_REC.WARRANTY_START_DATE)
					 THEN
					 	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'INV03');
             v_valid_start_date := FALSE; 
					 END IF;				
        END;		

				--ERROR CODE: INV004				
				--VALIDATE THAT WARRANTY END  DATE IS VALID
				--REASON : WARRANTY END DATE SHOULD BE IN VALID FORMAT
				BEGIN
					 IF EACH_REC.WARRANTY_END_DATE IS NOT NULL AND NOT Common_Utils.isValidDate(EACH_REC.WARRANTY_END_DATE)
					 THEN
					 	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'INV04');
             v_valid_end_date := FALSE; 
					 END IF;				
		   	END;	
				
				--ERROR CODE: INV0005				
				--VALIDATE THAT HOURS COVERED IS POSITIVE
				--REASON : HOURS COVERED TYPE SHOULD BE VALID
				BEGIN
					 IF EACH_REC.HOURS_COVERED < 0
					 THEN
					 	 v_error_code := Common_Utils.addErrorMessage(v_error_code, 'INV05');
					 END IF;				
        END;
    
        
        IF  v_valid_serial_number and v_valid_start_date AND TO_DATE(each_rec.warranty_start_date, 'YYYYMMDD') < v_delievery_dt THEN
					v_error_code := common_utils.addErrorMessage(v_error_code, 'INV11');
				ELSIF v_valid_serial_number  and v_valid_start_date and v_valid_end_date AND TO_DATE(each_rec.warranty_start_date, 'YYYYMMDD') > TO_DATE(each_rec.warranty_end_date, 'YYYYMMDD') THEN
					v_error_code := common_utils.addErrorMessage(v_error_code, 'INV10');
				END IF;
        
        IF v_valid_policy and v_valid_serial_number THEN 
				BEGIN
					select min(cl.repair_date),max(cl.repair_date) INTO v_min_failure_dt,v_max_failure_dt
					from claim cl
					inner join claimed_item ci on ci.claim= cl.id
          inner join inventory_item ii on ci.item_ref_inv_item= ii.id
					inner join applicable_policy ap on ci.applicable_policy=ap.id
					inner join policy p on ap.registered_policy= p.id
          inner join policy_definition pd on p.policy_definition=pd.id
          inner join jbpm_variableinstance v on v.LONGVALUE_=cl.id
					inner join jbpm_moduleinstance m on m.PROCESSINSTANCE_=v.PROCESSINSTANCE_
					inner join jbpm_taskinstance j on j.TASKMGMTINSTANCE_=m.ID_
					where upper(pd.code)=upper(each_rec.plan_code) and j.isopen_=1
          and cl.business_unit_info=v_bu_name and UPPER(ii.serial_number) = UPPER(each_rec.serial_number);
					v_active_claim := TRUE;
				EXCEPTION
					WHEN NO_DATA_FOUND THEN
						v_active_claim := FALSE;
						NULL;
				END;
			END IF;
      
      	IF v_active_claim THEN
				IF v_valid_start_date AND TO_DATE(each_rec.warranty_start_date, 'YYYYMMDD') > v_min_failure_dt THEN
					v_error_code := common_utils.addErrorMessage(v_error_code, 'INV08');
				END IF;
				IF v_valid_end_date AND TO_DATE(each_rec.warranty_end_date, 'YYYYMMDD') < v_max_failure_dt THEN
					v_error_code := common_utils.addErrorMessage(v_error_code, 'INV09');
				END IF;
				
			END IF;
      
				
				--UPDATE TABLE WITH ERROR/SUCCESS STATUS
				IF v_error_code IS NULL
				THEN
					UPDATE stg_warranty_coverages
					SET
						ERROR_STATUS = 'Y',
						ERROR_CODE = NULL
					WHERE
						id = EACH_REC.id;						
				ELSE
					UPDATE stg_warranty_coverages
					SET
						ERROR_STATUS = 'N',
						ERROR_CODE = v_error_code
					WHERE
						id = EACH_REC.id;						
				END IF;
				
	
		END LOOP;				

			BEGIN
        SELECT file_upload_mgt_id INTO v_file_upload_mgt_id 
        FROM stg_warranty_coverages WHERE ROWNUM = 1;
    
        -- Success Count
        BEGIN
            SELECT count(*) INTO v_success_count
            FROM stg_warranty_coverages 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'Y';
        EXCEPTION
            WHEN OTHERS THEN
                v_success_count := 0;
        END;
            
        -- Error Count
        BEGIN
            SELECT count(*) INTO v_error_count
            FROM stg_warranty_coverages 
            WHERE file_upload_mgt_id = v_file_upload_mgt_id and error_status = 'N';
        EXCEPTION
            WHEN OTHERS THEN
                v_error_count := 0;
        END;

        -- Total Count
        SELECT count(*) INTO v_count
        FROM stg_warranty_coverages 
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
	COMMIT;
END WNTY_CVG_VALIDATION ;
/
create or replace PROCEDURE WNTY_CVG_UPLOAD
AS
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_WARRANTY_COVERAGES
    WHERE NVL(ERROR_STATUS, 'N') = 'Y'
    AND ERROR_CODE              IS NULL
    AND NVL(UPLOAD_STATUS, 'N')  = 'N'
    ORDER BY ID ASC;
	
  --ALL GLOBAL VARIABLES DECLARED FOR A PROCEDURE
  v_upload_error          VARCHAR2(4000) := NULL;
  v_warranty_id           NUMBER := NULL;
  v_inventory_id          NUMBER;
  v_policy_id             NUMBER  := NULL;
  v_policy_audit_id       NUMBER  := NULL;
  v_policy_def_id         NUMBER  := NULL;
  v_policy_status         varchar2(100)  := NULL;
  v_policy_audit_comments VARCHAR2(4000) :=NULL;
  v_bu_name                   VARCHAR2(255);
   v_uploaded_by               NUMBER  := NULL;
  v_start_dt			DATE;
  v_end_dt			DATE;
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      --RESET THE VALUE TO ZERO
      v_warranty_id     := 0;
      v_policy_id       := NULL;
      v_policy_audit_id := 0;
      v_policy_def_id   :=0;
      v_start_dt			:=NULL;
      v_end_dt			  :=NULL; 
      v_policy_status :='Active';
      
      
      BEGIN
          SELECT u.id, f.business_unit_info INTO v_uploaded_by, v_bu_name
          FROM org_user u,file_upload_mgt f
          WHERE u.id = f.uploaded_by  AND f.id in
              (SELECT file_upload_mgt_id FROM stg_warranty_coverages WHERE rownum = 1);
       END;
     
      --GET THE SEQUENCE FOR POLICY
     
      BEGIN
        SELECT A.ID,
          B.ID
        INTO v_warranty_id,
          v_inventory_id
        FROM WARRANTY A,
          INVENTORY_ITEM B,
          ITEM C
        WHERE A.FOR_ITEM           = B.ID
        AND upper(B.SERIAL_NUMBER) = upper(EACH_REC.SERIAL_NUMBER)
        AND upper(C.item_number)          = upper(EACH_REC.ITEM_NUMBER)
        AND A.list_index           =
          (SELECT MAX(list_index) FROM warranty WHERE for_item=B.id AND d_active=1
          )
        AND b.of_type =C.ID
        AND B.d_active=1;
               
      END;
     
    --GET THE POLICY DEFINITION
      BEGIN
        SELECT ID
        INTO v_policy_def_id
        FROM POLICY_DEFINITION
        WHERE upper(CODE) = upper(EACH_REC.plan_code)
        and upper(business_unit_info)= upper(v_bu_name);
     
       END;
       
      BEGIN
        SELECT ID
        INTO v_policy_id
        FROM POLICY
        WHERE warranty = v_warranty_id and
        policy_definition=v_policy_def_id;
        EXCEPTION
            WHEN OTHERS THEN
                v_policy_id := null;
       END;
      
       --GET THE SEQUENCE FOR POLICY AUDIT
      SELECT policy_audit_seq.NEXTVAL
      INTO v_policy_audit_id
      FROM DUAL;
      
    IF TO_DATE(EACH_REC.WARRANTY_START_DATE,'YYYYMMDD') = TO_DATE(EACH_REC.WARRANTY_END_DATE,'YYYYMMDD') THEN
      
      v_policy_status :='InActive';
    
    END IF;
        
    IF v_policy_id IS NULL THEN
     
      SELECT policy_seq.NEXTVAL
      INTO v_policy_id
      FROM DUAL;
     
     --INSERT THE RECORD INTO WARRANTY POLICY

      INSERT INTO POLICY
        (
          ID ,
          AMOUNT ,
          CURRENCY ,
          POLICY_DEFINITION,
          WARRANTY,
          purchase_date,
          purchase_order_number,           
          d_created_time,
		      d_created_on,
          d_updated_time,
		      d_updated_on,
          d_internal_comments,
          d_active
        )
        VALUES
        (
          v_policy_id ,
         0,
         'USD',
          v_policy_def_id,
          v_warranty_id,
          TO_DATE(EACH_REC.order_date,'YYYYMMDD'), 
          EACH_REC.order_number,
          sysdate,
          sysdate,
          sysdate,
          sysdate,
          null,
          1
        );
			END IF;
      --INSERT THE RECORD INTO WARRANTY POLICY AUDIT
      INSERT
      INTO POLICY_AUDIT
        (
          ID ,
          COMMENTS ,
          CREATED_ON,
          STATUS ,
          FROM_DATE ,
          TILL_DATE ,
          CREATED_BY,
          FOR_POLICY,
          d_created_on,
          SERVICE_HOURS_COVERED,
          d_created_time,
          d_updated_time,
          d_internal_comments,
          d_active
        )
        VALUES
        (
          v_policy_audit_id ,
          EACH_REC.comments,
          to_number(sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (24 * 60 * 60 * 1000)  ,
          v_policy_status  ,
          TO_DATE(EACH_REC.WARRANTY_START_DATE,'YYYYMMDD'),
          TO_DATE(EACH_REC.WARRANTY_END_DATE,'YYYYMMDD') ,
          v_uploaded_by ,
          v_policy_id ,
          sysdate,
          EACH_REC.HOURS_COVERED,
          sysdate,
          sysdate,
          null,
          1
        );
      
      COMMIT;
       BEGIN
               
        select max(pa.till_date) into v_end_dt
        from policy p, policy_audit pa
        where p.id=pa.for_policy
        and p.warranty=v_warranty_id
        and pa.status in ('Active','Terminated') 
        and pa.id in ( select max(id ) from (
        select for_policy,id from policy_audit where for_policy in (select id from policy where warranty = v_warranty_id))
        group by for_policy);
        
       IF v_end_dt is null then
         UPDATE inventory_item SET wnty_end_date =null
         WHERE ID = v_inventory_id;
        ELSE
        UPDATE inventory_item SET wnty_end_date =v_end_dt
         WHERE ID = v_inventory_id;
        END IF;
       
       END;
       
       BEGIN
        select min(pa.from_date)  into v_start_dt
        from policy p, policy_audit pa
        where p.id=pa.for_policy
        and p.warranty=v_warranty_id
        and pa.status in ('Active','Terminated')  
        and pa.id in ( select max(id ) from (
        select for_policy,id from policy_audit where for_policy in (select id from policy where warranty = v_warranty_id))
        group by for_policy);
        
        IF v_start_dt is null then
          UPDATE inventory_item SET wnty_start_date = null
         WHERE ID = v_inventory_id;
        ELSE
        UPDATE inventory_item SET wnty_start_date = v_start_dt
         WHERE ID = v_inventory_id;
        END IF;
              
           
        END;
      
      --UPDATE RECORD WITH ERROR MESSAGE
      UPDATE STG_WARRANTY_COVERAGES
      SET UPLOAD_STATUS = 'Y' ,
        UPLOAD_ERROR    = NULL
      WHERE ID      = EACH_REC.ID;
      COMMIT;
    EXCEPTION
    WHEN OTHERS THEN
      --FIRST ROLLBACK
      ROLLBACK;
      --GET THE ERROR MESSAGE
      v_upload_error := SUBSTR(SQLERRM,0,3500);
      dbms_output.put_line(SQLERRM);
      --UPDATE RECORD WITH ERROR MESSAGE
      UPDATE STG_WARRANTY_COVERAGES
      SET UPLOAD_STATUS = 'N' ,
        UPLOAD_ERROR    = v_upload_error
      WHERE ID      = EACH_REC.ID;
      --COMMIT UPDATE STATEMENT
      COMMIT;
    END;
  END LOOP;
END WNTY_CVG_UPLOAD;
/