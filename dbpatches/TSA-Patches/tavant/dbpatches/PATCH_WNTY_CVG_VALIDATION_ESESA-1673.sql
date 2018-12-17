--Purpose : ESESA-1673, Handle duplicate item numbers, product hierarchy diff between TSA and other BUs
--Author : raghuram.d
--Date : 12/May/2011

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
    
   
	
		
	IF each_rec.serial_number IS NULL THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL02');
	END IF;
		
	IF each_rec.item_number IS NULL THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'NUL03');
    ELSIF v_valid_bu AND NOT COMMON_VALIDATION_UTILS.isValidItemNumber(each_rec.item_number, v_bu_name) THEN
		v_error_code := common_utils.addErrorMessage(v_error_code, 'INV02');
	ELSIF each_rec.serial_number IS NOT NULL THEN
	BEGIN
		SELECT inv.type,inv.of_type,inv.delivery_date,i.model
		INTO v_inventory_type,v_item_id,v_delievery_dt,v_model_id
		FROM inventory_item inv, item i
		WHERE UPPER(inv.serial_number) = UPPER(each_rec.serial_number) 
			AND inv.business_unit_info = v_bu_name AND inv.of_type = i.id
			AND ( lower(i.alternate_item_number) = lower(ltrim(rtrim(each_rec.item_number)))  OR 
				lower(i.item_number) = lower(ltrim(rtrim(each_rec.item_number))) )
			AND i.d_active=1 AND inv.d_active=1;
		
		IF UPPER(v_inventory_type) != 'RETAIL' THEN
			v_error_code := common_utils.addErrorMessage(v_error_code, 'INV12');
		ELSE
			v_valid_serial_number := TRUE;
		END IF ;
	EXCEPTION
		WHEN NO_DATA_FOUND THEN
		  v_error_code := common_utils.addErrorMessage(v_error_code, 'INV06');
	END;
    END IF;
      
      IF v_model_id is not null then
        BEGIN
                SELECT case when p2.item_group_type='PRODUCT' then p2.id else p1.id end
                INTO v_product_id
                FROM item_group m,ITEM_GROUP p1,item_group p2
                WHERE m.id = v_model_id and m.is_part_of=p1.id and p1.is_part_of=p2.id;
              EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  v_error_code := common_utils.addErrorMessage(v_error_code, 'INV02');
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
END WNTY_CVG_VALIDATION;
/