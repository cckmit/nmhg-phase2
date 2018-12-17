--Purpose    : Procedure to upload inventory items
--Author     : Raghu
--Created On : 12-May-09

create or replace PROCEDURE UPLOAD_INSTALL_BASE_UPLOAD AS

  CURSOR ALL_REC
  IS
  	SELECT *
	FROM STG_INSTALL_BASE
	WHERE
     NVL(ERROR_STATUS, 'N') = 'Y' AND
     ERROR_CODE IS NULL AND 
		 NVL(UPLOAD_STATUS, 'N') = 'N'
		 ORDER BY ID ASC;

	-- ALL GLOBAL VARIABLES DECLARED FOR A PROCEDURE
	v_inv_id			   	            NUMBER		:=  NULL;
	v_type_of				              NUMBER		:=  NULL;
	v_trans_id			   	          NUMBER		:=  NULL;
	v_transacted_item		          NUMBER		:=  NULL;
	v_trans_type			            NUMBER		:=  NULL;
	v_buyer_id				            NUMBER		:=  NULL;
	v_seller_id				            NUMBER		:=  NULL;
    v_competitor_model_id               NUMBER      :=  NULL;
    v_competitor_make_id                NUMBER      :=  NULL;
    v_transaction_type_id               NUMBER      :=  NULL;
    v_competition_type_id               NUMBER      :=  NULL;
    v_inventory_type              VARCHAR2(255)		:=  NULL;
	v_warranty_seq			          NUMBER		:=  NULL;
	v_mark_info_id			          NUMBER		:=  NULL;
	v_cust_add_id			            NUMBER		:=  NULL;
	v_upload_error			          VARCHAR2(4000)	:=	NULL;
	v_warranty_id		 	            NUMBER		:=  NULL;	
	v_policy_id			   	          NUMBER		:=  NULL;	
	v_policy_audit_id	   	        NUMBER		:=  NULL;	
	v_policy_def_id			          NUMBER		:=  NULL;
	v_list_of_values		          NUMBER		:=  NULL;
	v_product				              NUMBER		:=  NULL;
	v_model					              NUMBER		:=  NULL;
	v_months_covered		          NUMBER		:=  NULL;
	IS_POLICY_APPLICABLE	        VARCHAR2(255)		:=  NULL;
	v_warranty_audit_id		        NUMBER		:=  NULL;
	v_waranty_task_instance_id	  NUMBER		:=  NULL;
	V_MULTIDRETRNUMBER		        NUMBER		:=  NULL;
	v_address_trans_id		        NUMBER;
	v_coverage_till_date			    DATE;
	v_ship_coverage_till_date		  DATE;
	V_COVERAGE_END_DATE				    DATE;
	v_current_date			          DATE;

BEGIN
    FOR EACH_REC IN ALL_REC
    LOOP
	   BEGIN

				--RESET THE VALUE TO ZERO
			  v_inv_id := 0;
				v_type_of := 0;
				v_trans_id := 0;
				v_transacted_item := 0;
				v_trans_type := 0;
				v_seller_id := 0;
				v_buyer_id := 0;
				v_address_trans_id := 0;
				v_warranty_id := 0;
			  v_policy_id := 0;
				v_policy_audit_id := 0;
				v_policy_def_id :=0;
                v_inventory_type := UPPER(EACH_REC.STOCK_OR_RETAIL);

				--GET THE SEQUENCE FOR INVENTORY
			    SELECT inventory_item_seq.NEXTVAL
				    INTO v_inv_id
				    FROM DUAL;

				--GET THE TYPE OF FROM THE ITEM TABLE
				BEGIN
					SELECT ID
					INTO v_type_of
					FROM ITEM
					WHERE 
          (ITEM_NUMBER = EACH_REC.ITEM_NUMBER OR ALTERNATE_ITEM_NUMBER = EACH_REC.ITEM_NUMBER )
          and business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;
				END;
				
				--GET THE List_Of_Values ID  FROM THE LIST_OF_VALUES TABLE
				BEGIN
					SELECT ID
					INTO v_list_of_values
					FROM list_of_values
					WHERE 
          --TODO: Get/Populate manufacturing_location_code to complete this
          --code = EACH_REC.manufacturing_location_code and 
          business_unit_info = EACH_REC.BUSINESS_UNIT_NAME and rownum<2;--TODO: Remove Rownum after manufacturing_location_code population
				END;
				-- get the PRODUCT/MODEL id from item				
				BEGIN
					SELECT PRODUCT, MODEL 
					INTO v_product, v_model
					FROM ITEM
					WHERE ID = v_type_of AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_NAME; --BU INFO CAN BE REMOVED IN THIS CASE
				END;
				
				--GET CURRENT DATE FROM DUAL
				BEGIN
					SELECT CURRENT_DATE INTO v_current_date FROM dual; 
				END;
				
						--GET WARRANTY POLICY ID
				BEGIN
					 SELECT W.ID
					 INTO v_warranty_id
					 FROM 
					 	  WARRANTY W,
						  INVENTORY_ITEM B
					WHERE
						 W.FOR_ITEM = B.ID AND
						 B.SERIAL_NUMBER = EACH_REC.SERIAL_NUMBER and  B.business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;
        EXCEPTION 
        WHEN OTHERS THEN
          v_warranty_id := 0;
				END;

					
					--Get WARRANTY_AUDIT  ID
				BEGIN
					 SELECT WARRANTY_AUDIT_SEQ.nextval
					 INTO v_warranty_audit_id
					 FROM DUAL;
				END;
				
				--Get WARRANTY_task_instance id  ID
				BEGIN
					 SELECT WARRANTY_TASK_INSTANCE_SEQ.nextval
					 INTO v_waranty_task_instance_id
					 FROM DUAL;
				END;
				
				--GET  MULTIDRETRNUMBER, WHICH IS NEXT SEQ VALUE OF  WARRANTY_TASK_INSTANCE_SEQ
				BEGIN
					SELECT WARRANTY_TASK_INSTANCE_SEQ.NEXTVAL
					INTO V_MULTIDRETRNUMBER
					FROM DUAL;
				END;
				
				--INSERT THE RECORD INTO INVENTORY ITEM
				INSERT INTO INVENTORY_ITEM
				(
					ID,
					BUILT_ON,
					HOURS_ON_MACHINE,
					SERIAL_NUMBER,
					SHIPMENT_DATE,
					VERSION,
					CONDITION_TYPE,
					TYPE,
					OF_TYPE,
					OWNERSHIP_STATE,
					business_unit_info,
					manufacturing_site_inventory,
					factory_order_number,
					PENDING_WARRANTY,
                    d_active
				)
				VALUES
				(
					  v_inv_id,
					  TO_DATE(EACH_REC.MACHINE_BUILD_DATE,'YYYYMMDD'),
					  EACH_REC.HOURS_IN_SERVICE,
					  EACH_REC.SERIAL_NUMBER,
  					  TO_DATE(EACH_REC.SHIPMENT_DATE,'YYYYMMDD'),
					  1,
					  EACH_REC.INVENTORY_ITEM_TYPE,
					  v_inventory_type,
					  v_type_of,
					  1, -- CHANGE THIS LATER ACCORDINGLY
					  EACH_REC.BUSINESS_UNIT_NAME,
					  v_list_of_values,
					  '',
					  0,
					  1
				 );
				 
				 ------------------------------------------------------------------------------------------------------------------------------------
				 --GET THE SEQUENCE FOR INVENTORY TRANSACTION
			    SELECT inventory_transaction_seq.NEXTVAL
				    INTO v_trans_id
				    FROM DUAL;

				--GET THE TRANSACTED ITEM FROM INVENTORY ITEM
				BEGIN
					SELECT ID
					INTO v_transacted_item
					FROM INVENTORY_ITEM
					WHERE SERIAL_NUMBER = EACH_REC.SERIAL_NUMBER and business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;
				END;

				--GET THE TRANSACTION TYPE FROM THE INVENTORY TRANSACTION TYPE
				BEGIN
					SELECT ID
					INTO v_trans_type
					FROM INVENTORY_TRANSACTION_TYPE
					WHERE TRNX_TYPE_KEY = DECODE(v_inventory_type, 'STOCK', 'IB','RETAIL', 'DR');
											--EACH_REC.TYPE = decode(TRNX_TYPE_KEY, 'IB', 'STOCK', 'DR', 'RETAIL') ;
				END;
				 
				 --GET THE BUYER & SELLER
				BEGIN
					--GET THE BUYER & SELLER FOR THE IB TRANSACTION
					IF v_inventory_type='STOCK'
					THEN
						SELECT ID
						INTO v_seller_id
						FROM PARTY
						WHERE
							NAME = Common_Utils.CONSTANT_OEM_NAME;

						SELECT dealer.ID
						INTO v_buyer_id
						FROM DEALERSHIP dealer, ORGANIZATION org, BU_ORG_MAPPING buorg
						WHERE DEALER_NUMBER = EACH_REC.DEALER_NUMBER and 
            dealer.ID = org.ID and org.ID = buorg.ORG and 
            buorg.BU = EACH_REC.BUSINESS_UNIT_NAME;

					END IF;

          --GET THE BUYER & SELLER FOR THE DR TRANSACTION
          IF v_inventory_type='RETAIL'
          THEN

            SELECT dealer.ID
            INTO v_seller_id
            FROM DEALERSHIP dealer, ORGANIZATION org, BU_ORG_MAPPING buorg
            WHERE DEALER_NUMBER = EACH_REC.DEALER_NUMBER and 
            dealer.ID = org.ID and org.ID = buorg.ORG and 
            buorg.BU = EACH_REC.BUSINESS_UNIT_NAME;

            SELECT ID
            INTO v_buyer_id
            FROM CUSTOMER
            WHERE COMPANY_NAME = EACH_REC.END_CUSTOMER_NAME;
          END IF;
        END;
        
        --UPDATE THE INVENTORY TABLE WITH THE SHIPMENT DATE, DELIVERY DATE AND REGISTRATION DATE
        BEGIN
          IF v_inventory_type='STOCK'   
          THEN
            UPDATE INVENTORY_ITEM
            SET 
               
              CURRENT_OWNER = v_buyer_id , LATEST_BUYER = v_buyer_id
            WHERE SERIAL_NUMBER = EACH_REC.SERIAL_NUMBER and business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;
          END IF;
          
          
          IF v_inventory_type='RETAIL'
          THEN
            UPDATE INVENTORY_ITEM
            SET 
              DELIVERY_DATE = TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD'), 
              CURRENT_OWNER =  v_seller_id , LATEST_BUYER = v_buyer_id
            WHERE SERIAL_NUMBER = EACH_REC.SERIAL_NUMBER and business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;
          END IF;

        END;
        
        --INSERT THE RECORD INTO INVENTORY TRANSACTION
        INSERT INTO INVENTORY_TRANSACTION
        (
          ID,
          INVOICE_DATE,
          INVOICE_NUMBER,
          SALES_ORDER_NUMBER,
          TRANSACTION_DATE,
          VERSION,
          BUYER,
          TRANSACTED_ITEM,
          SELLER,
          INV_TRANSACTION_TYPE,
          OWNER_SHIP,
          TRANSACTION_ORDER,
          STATUS,
          d_active
        )
        VALUES
        (
            v_trans_id,
            TO_DATE(EACH_REC.INVOICE_DATE,'YYYYMMDD'),
            EACH_REC.INVOICE_NUMBER,
            EACH_REC.SALES_ORDER_NUMBER,
            TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'),
            1,
            v_buyer_id,
            v_transacted_item,
            v_seller_id,
            v_trans_type,
            DECODE(v_inventory_type,'RETAIL',v_seller_id,'STOCK',v_buyer_id), --CHANGE IT ACCORDINGLY LATER,
            DECODE(v_inventory_type,'RETAIL',2,'STOCK',1),
            'ACTIVE',
            1
         );
         
         IF v_inventory_type='RETAIL'
         THEN
         
          --GET THE MARKETING INFORMATION SEQUENCE ID
          SELECT MARKETING_INFORMATION_SEQ.NEXTVAL
          INTO v_mark_info_id
          FROM DUAL;
          
          --GET THE  WARRANTY ID
          SELECT WARRANTY_SEQ.NEXTVAL
          INTO v_warranty_seq
          FROM DUAL;
          
          --ADDRESS OF THE CUSTOMER
          SELECT ADDRESS 
          INTO v_cust_add_id
          FROM PARTY
          WHERE
            ID = v_buyer_id;
          
          SELECT id INTO v_competitor_model_id
          FROM competitor_model WHERE model = 'UNKNOWN/NOT PROVIDED' 
            AND business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;

          SELECT id INTO v_competitor_make_id
          FROM competitor_make WHERE make = 'UNKNOWN/NOT PROVIDED'
            AND business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;

          SELECT id INTO v_transaction_type_id
          FROM transaction_type WHERE type = 'Cash Sales'
            AND business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;


          SELECT id INTO v_competition_type_id
          FROM competition_type WHERE type = 'UNKNOWN/NOT PROVIDED'
            AND business_unit_info = EACH_REC.BUSINESS_UNIT_NAME;

          --INSERT INTO MARKETING INFORMATION TABLE
          INSERT INTO MARKETING_INFORMATION
          (
            ID, 
            CUSTOMER_FIRST_TIME_OWNER, 
            MONTHS, 
            VERSION, 
            YEARS, 
            MARKET_TYPE, 
            COMPETITION_TYPE, 
            TRANSACTION_TYPE, 
            SALES_MAN,
            if_previous_owner,
            COMPETITOR_MODEL,
            COMPETITOR_MAKE,
            d_active
          )
          VALUES
          (
            v_mark_info_id,
            0,
            NULL, --need to ask
            0, 
            NULL,
            1, --need to ask, how we will decide this attribute value. CHANGE THIS VALUE TO PROBABLY REFLECT SOMETHING RELATED TO MIGARTION OR CAPTURE IT
            v_competition_type_id,
            v_transaction_type_id,  --need to ask, why specifically given this value only, whereas other options r also there
            '',
            NULL,
            v_competitor_model_id,						
            v_competitor_make_id,
            1
          );
          
          SELECT ADDRESSFORTRANS_SEQ.NEXTVAL 
          INTO v_address_trans_id
          FROM DUAL;
          
          
          --insert the record in address to transfer table 
          INSERT INTO ADDRESS_FOR_TRANSFER				
          (
            SELECT 
              v_address_trans_id,
              ADDRESS_LINE1, 
              CITY, 
              CONTACT_PERSON_NAME, 
              COUNTRY, 
              EMAIL, 
              PHONE, 
              SECONDARY_PHONE, 
              STATE, 
              'BILLING',
              0, 
              ZIP_CODE,NULL,NULL,NULL,NULL,NULL,NULL,1
            FROM ADDRESS
            WHERE ID = v_cust_add_id
          );
          
          
          --INSERT INTO WARRANTY TABLE
          INSERT INTO WARRANTY
          (
            ID, 
            DELIVERY_DATE, 
            DRAFT, 
            VERSION, 
            MARKETING_INFORMATION, 
            FOR_TRANSACTION, 
            CUSTOMER, 
            FOR_ITEM, 
            LIST_INDEX, 
            STATUS,
            for_dealer,
            ADDRESS_FOR_TRANSFER,
            TRANSACTION_TYPE,
            d_active
          )
          VALUES
          (
            v_warranty_seq,
            TO_DATE(EACH_REC.WARRANTY_START_DATE,'YYYYMMDD'),   
            0,
            0,
            v_mark_info_id,
            v_trans_id,
            v_buyer_id, 
            v_transacted_item,
            0,
            'SUBMITTED',    
            v_seller_id,
            v_address_trans_id,
            v_trans_type,
            1
          );
          
      --Insert records into WARRANTY_AUDIT table
        INSERT INTO WARRANTY_AUDIT
            (
              ID,
              D_CREATED_ON,
              D_INTERNAL_COMMENTS,
              D_UPDATED_ON,
              STATUS,				
              FOR_WARRANTY,
              LIST_INDEX,			--need to ask
              VERSION,
              d_active
            )
          Values
            (
            v_warranty_audit_id,
            sysdate,
            null,
            sysdate,
            'SUBMITTED',			
            v_warranty_seq,
            0,
            0,
            1
            );

      --Insert records into warranty_task_instance table
        INSERT INTO WARRANTY_TASK_INSTANCE
          (
          ID,
          ACTIVE,    
          D_CREATED_ON,
          D_UPDATED_ON,
          STATUS,   
          VERSION,
          ASSIGNED_TO,    
          WARRANTY_AUDIT,
          MULTIDRETRNUMBER,
          d_active,
          business_unit_info
          )
         VALUES
          (
          v_waranty_task_instance_id,
          0,    --72 REC WITH THIS VALUE
          sysdate,
          sysdate,
          'SUBMITTED',    
          0,
          null,    
          v_warranty_audit_id,
          V_MULTIDRETRNUMBER,
          1,
          EACH_REC.BUSINESS_UNIT_NAME
          );
          
      --Insert records into warranty_task_included_items table
        INSERT INTO WARRANTY_TASK_INCLUDED_ITEMS
          (
          WARRANTY_TASK,
          INV_ITEM
          )
        VALUES
          (
          v_waranty_task_instance_id,
          v_transacted_item
          );
          
          
  -----------------------------------------------------------------------------------------------------------			
          
        
                
      
        --GET ALL THE APPLICABLE POLICY CODES ON THIS PARTICULAR INVENTORY SERIAL NUMBER
          DECLARE CURSOR ALL_POLICY_PLANS
          IS
          SELECT * FROM POLICY_DEFINITION 
              WHERE ID in (SELECT POLICY_DEFN 
                     FROM POLICY_FOR_PRODUCTS 
                     WHERE FOR_PRODUCT in (v_product, v_model)) 
              AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_NAME 
              AND UPPER(WARRANTY_TYPE) = 'STANDARD';
              
          BEGIN
            FOR EACH_PLAN IN ALL_POLICY_PLANS
            LOOP
            BEGIN
  
              --CALCULATE DATE  INTO  v_ship_coverage_till_date, BY ADDING 'MONTHS FROM SHIPMENT'
              BEGIN
                SELECT ADD_MONTHS(TO_DATE(EACH_REC.SHIPMENT_DATE, 'YYYYMMDD'), EACH_PLAN.MONTHS_FRM_SHIPMENT)
                INTO v_ship_coverage_till_date
                FROM DUAL;
              END;
              
              --CALCULATE DATE  INTO  v_coverage_till_date, BY ADDING 'MONTHS FROM DELIVERY'
              BEGIN
                SELECT ADD_MONTHS(TO_DATE(EACH_REC.WARRANTY_START_DATE, 'YYYYMMDD'), EACH_PLAN.MONTHS_FRM_DELIVERY)
                INTO v_coverage_till_date
                FROM DUAL;
              END;
              
              -- V_COVERAGE_END_DATE stores the value  of v_coverage_till_date/v_ship_coverage_till_date, whichever is lesser
              BEGIN
                IF v_ship_coverage_till_date < v_coverage_till_date THEN
                  V_COVERAGE_END_DATE := v_ship_coverage_till_date;
                ELSE
                  V_COVERAGE_END_DATE := v_coverage_till_date;
                END IF;
              END;
              --TAKE ONE  VARIABLE, WHICH WILL RETURN 'TRUE' OR 'FALSE', INITIALIZE IT WITH 'FALSE' 
                IS_POLICY_APPLICABLE := 'FALSE';
                
              -- CHECK,  IS_POLICY_APPLICABLE,  RETURN VALUE, BASED UPON CONDITIONS IT SATISFIES
              
              --CONDITION 1
              IF v_current_date >= EACH_PLAN.ACTIVE_FROM 
                AND v_current_date <= EACH_PLAN.ACTIVE_TILL THEN
                IS_POLICY_APPLICABLE := 'TRUE';
              END IF;
              
              --CONDITION 2
              IF v_current_date >= TO_DATE(EACH_REC.WARRANTY_START_DATE, 'YYYYMMDD')
                 AND v_current_date <= V_COVERAGE_END_DATE THEN
                IS_POLICY_APPLICABLE := 'TRUE';
              END IF;
              
              --CONDITION 3
              IF EACH_REC.HOURS_IN_SERVICE <= EACH_PLAN.SERVICE_HRS_COVERED  
                THEN IS_POLICY_APPLICABLE := 'TRUE';
              END IF;
              
              --CONDITION 4
              IF EACH_PLAN.CURRENTLY_INACTIVE = 0 
                THEN IS_POLICY_APPLICABLE := 'TRUE';
              END IF;
              
              --CONDITION 5
              --IF UPPER(EACH_PLAN.AVAILABILITY_ITEM_CONDITION) =  UPPER(EACH_REC.CONDITION)
              --  THEN IS_POLICY_APPLICABLE := 'TRUE';
              --END IF;
              
              --CONDITION 6       -- NEED CLARIFICATION, WHAT IF THIS VALUE IS OTHER THAN ALL CUSTOMER
              --IF UPPER(EACH_PLAN.COMMENTS) =  UPPER('All customers')
              --	THEN IS_POLICY_APPLICABLE := 'TRUE';
              --END IF;
                
                
              IF IS_POLICY_APPLICABLE = 'TRUE' THEN

              --GET THE SEQUENCE FOR POLICY
              SELECT policy_seq.NEXTVAL
              INTO v_policy_id
              FROM DUAL;

                INSERT INTO POLICY
                (
                  ID,
                  AMOUNT,
                  CURRENCY,
                  POLICY_DEFINITION,
                  WARRANTY,
                  d_active
                )
                VALUES
                (
                  v_policy_id,
                  0,--HARD CODED AND HAVE TO GET THE CLARIFICATION       -- NEED TO ASK, DO WE NEED TO PASS THESE VALUES
                  'USD',--HARD CODED AND HAVE TO GET THE CLARIFICATION
                  --v_policy_def_id,
                  EACH_PLAN.ID,
                  v_warranty_seq,
                  1
                );	
              
              --GET THE SEQUENCE FOR POLICY AUDIT
              SELECT policy_audit_seq.NEXTVAL
              INTO v_policy_audit_id
              FROM DUAL;					

                --INSERT THE RECORD INTO WARRANTY POLICY AUDIT
                INSERT INTO POLICY_AUDIT
                (
                  ID,
                  COMMENTS,
                  CREATED_ON,
                  STATUS,
                  FROM_DATE,
                  TILL_DATE,
                  CREATED_BY,
                  FOR_POLICY,
                  SERVICE_HOURS_COVERED,
                  d_active
                )
                VALUES
                (
                  v_policy_audit_id,
                  'Uploaded as part of Hussmann data cutover',--HARD CODED AND HAVE TO GET THE CLARIFICATION
                  0,--HARD CODED AND HAVE TO GET THE CLARIFICATION   -- NEED TO ASK, CAN V PASS SYSDATE HERE
                  'Active',
                  TO_DATE(EACH_REC.WARRANTY_START_DATE,'YYYYMMDD'),
                  V_COVERAGE_END_DATE,     
                  --TO_DATE(EACH_REC.WARRANTY_END_DATE,'YYYYMMDD'),
                  NULL,
                  v_policy_id,
                  EACH_REC.HOURS_IN_SERVICE,
                  1
                );
                end if;
             END; --end of inner loop begin
            END LOOP; --end of inner loop
          END; --end of inner cursor begin
          END IF;  -- end of check," if transaction_type = 'RETAIL'"	
				
				--UPDATE RECORD WITH ERROR MESSAGE
				UPDATE STG_INSTALL_BASE
				SET
					UPLOAD_STATUS = 'Y',
					UPLOAD_DATE = SYSDATE,
					UPLOAD_ERROR = NULL
				WHERE
					 ID = EACH_REC.ID;

				COMMIT;

			EXCEPTION
			WHEN OTHERS
			THEN
				--FIRST ROLLBACK
				ROLLBACK;

				--GET THE ERROR MESSAGE
				v_upload_error := SUBSTR(SQLERRM,0,3500);

				--UPDATE RECORD WITH ERROR MESSAGE
				UPDATE STG_INSTALL_BASE
				SET
					UPLOAD_STATUS = 'N',
					UPLOAD_DATE = SYSDATE,
					UPLOAD_ERROR = v_upload_error
				WHERE
					 ID = EACH_REC.ID;

				--COMMIT UPDATE STATEMENT
				COMMIT;
			END;
    END LOOP;
    --FINAL COMMIT
    COMMIT;
END UPLOAD_INSTALL_BASE_UPLOAD;
/
COMMIT
/