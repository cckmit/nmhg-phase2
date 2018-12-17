create or replace
PROCEDURE                 "UPLOAD_INSTALL_BASE_UPLOAD" 
AS
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_INSTALL_BASE
    WHERE NVL(ERROR_STATUS, 'N') = 'Y'
    AND ERROR_CODE              IS NULL
    AND NVL(UPLOAD_STATUS, 'N')  = 'N'
    ORDER BY ID ASC;

  v_inv_id                   NUMBER := NULL;
  v_type_of                  NUMBER := NULL;
  v_product                  NUMBER := NULL;
  v_model                    NUMBER := NULL;
  v_bu_name                  VARCHAR2(255);
  v_transacted_item          NUMBER := NULL;
  v_trans_type               NUMBER := NULL;
  v_oem_id                   NUMBER;
  v_dealer_id                NUMBER;
  v_end_customer_id          NUMBER;
  v_cust_addr_id             NUMBER;
  v_address_book_id          NUMBER;
  v_invoice_date             VARCHAR2(255);
  v_warranty_start_date      VARCHAR2(255);
  v_trans_id                 NUMBER := NULL;
  v_warranty_seq             NUMBER := NULL;
  v_mark_info_id             NUMBER := NULL;
  v_address_trans_id         NUMBER;
  v_warranty_audit_id        NUMBER := NULL;
  v_waranty_task_instance_id NUMBER := NULL;
  v_condition_type           VARCHAR2(255);
  v_competitor_model_id      NUMBER        := NULL;
  v_competitor_make_id       NUMBER        := NULL;
  v_transaction_type_id      NUMBER        := NULL;
  v_competition_type_id      NUMBER        := NULL;
  v_inventory_type           VARCHAR2(255) := NULL;
  v_source_warehouse         NUMBER;
  v_manufacturing_site       NUMBER;
  v_datasource_attr          NUMBER;
  v_engine_serno_attr        NUMBER;
  v_attr_value               NUMBER;
  v_count                    NUMBER;
  v_ownership_state_id       NUMBER;
  V_MULTIDRETRNUMBER         NUMBER := NULL;
  v_current_date DATE;
  v_buyer_id           NUMBER         := NULL;
  v_seller_id          NUMBER         := NULL;
  v_upload_error       VARCHAR2(4000) := NULL;
  v_warranty_id        NUMBER         := NULL;
  v_policy_id          NUMBER         := NULL;
  v_policy_audit_id    NUMBER         := NULL;
  v_policy_def_id      NUMBER         := NULL;
  v_list_of_values     NUMBER         := NULL;
  v_months_covered     NUMBER         := NULL;
  IS_POLICY_APPLICABLE VARCHAR2(255)  := NULL;
  v_coverage_till_date DATE;
  v_ship_coverage_till_date DATE;
  V_COVERAGE_END_DATE DATE;
  v_months_frm_shipment NUMBER;
  v_months_frm_delivery NUMBER;
  v_hours_in_service varchar2(255);  
  v_registration_date DATE;  
  v_serialized_part    NUMBER := 0;
  v_source             VARCHAR2(255)  := NULL;
  v_vin_number         VARCHAR2(255)  := NULL;
  v_installing_dealer  NUMBER := NULL;
  v_operator           NUMBER := NULL;
  v_fleet_number       VARCHAR2(255)  := NULL;  
  v_equipment_vin      VARCHAR2(255)  := NULL;
  v_uploaded_by		NUMBER := NULL;	
  v_max_list_index NUMBER := -1;
  v_wnty_start_date DATE  := NULL;
  V_Wnty_End_Date Date    := Null;
  
 v_item_group_id	NUMBER;
 V_Mod_Cat		Number;
 v_price_matrix_id	NUMBER;
 V_Split_Type 		VARCHAR2(20);
 v_split_percentage	NUMBER;
 V_Sca_Amount		Number;
 V_Sea_Amount		Number;
 V_Grid_Amount		Number;
 v_grid_amount_curr	VARCHAR2(20);
 v_currency VARCHAR2(20);
 
 V_Dcap_Dealer_Group  Dealers_In_Group.Dealer_Group%Type;
 v_dcap_scheme_id            DEALER_SCHEME.ID%TYPE;
 
BEGIN
  SELECT id
  INTO v_oem_id
  FROM party
  WHERE name = common_utils.constant_oem_name;
  SELECT id
  INTO v_ownership_state_id
  FROM ownership_state
  WHERE name='First Owner';

  FOR each_rec IN all_rec
  Loop
  
    Begin

 
      v_bu_name             := common_validation_utils.getValidBusinessUnitName(each_rec.business_unit_name);
      v_inventory_type      := UPPER(EACH_REC.STOCK_OR_RETAIL);
      v_condition_type      := UPPER(NVL(each_rec.inventory_item_type, 'NEW'));
      v_invoice_date        := NVL(each_rec.invoice_date, each_rec.shipment_date);
      v_warranty_start_date := NVL(each_rec.warranty_start_date, each_rec.delivery_date);
      v_hours_in_service    := NVL(each_rec.hours_in_service, '0');
      v_warranty_id         := 0;
      v_policy_id           := 0;
      v_policy_audit_id     := 0;
      v_policy_def_id       :=0;
      v_end_customer_id     := NULL;
      v_cust_addr_id        := NULL;
      v_address_book_id     := NULL;
      V_Manufacturing_Site  := Null;
           

  
	    select uploaded_by into v_uploaded_by from file_upload_mgt where id = each_rec.file_upload_mgt_id;
      SELECT id,
        product,
        model
      INTO v_type_of,
        v_product,
        v_model
      FROM item
      WHERE (UPPER(item_number)       = UPPER(each_rec.item_number)
      OR UPPER(alternate_item_number) = UPPER(each_rec.item_number))
      AND business_unit_info          = v_bu_name
	    AND item_type 				  = 'MACHINE'
      AND owned_by 		              = v_oem_id -- Added to fix ESESA-838 / ESESA-839
      AND ROWNUM                      =1;
      
      BEGIN
        SELECT id
        INTO v_source_warehouse
        FROM source_warehouse
        WHERE business_unit_info=v_bu_name
        AND UPPER(code)         =UPPER(each_rec.ship_from_warehouse);
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
	  
		  IF (upper(v_bu_name) = 'HUSSMANN') then
			select id into v_source_warehouse from source_warehouse where business_unit_info = 'Hussmann'  and name = 'BGN' ;
		  END IF;
      END;

      IF each_rec.manufacturing_site IS NOT NULL THEN
        BEGIN
          SELECT id
          INTO v_manufacturing_site
          FROM list_of_values
          WHERE business_unit_info=v_bu_name
          AND UPPER(code)         =UPPER(each_rec.manufacturing_site)
          AND type                ='MANUFACTURINGSITEINVENTORY';
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_manufacturing_site := NULL;
        END;
      End If;
      
      SELECT inventory_item_seq.NEXTVAL INTO v_inv_id FROM DUAL;

    
      INSERT
      INTO inventory_item
        (
          id,
          built_on,
          hours_on_machine,
          serial_number,
          shipment_date,
          version,
          condition_type,
          type,
          of_type,
          ownership_state,
          business_unit_info,
          manufacturing_site_inventory,
          factory_order_number,
          pending_warranty,
          d_active,
          source_warehouse,
          d_updated_on,
          d_created_on,
          d_updated_time,
          d_created_time,
		  d_internal_comments,
		  d_last_updated_by,
	      serialized_part
        )
        VALUES
        (
          v_inv_id,
          TO_DATE(each_rec.machine_build_date,'YYYYMMDD'),
          v_hours_in_service,
          each_rec.serial_number,
          TO_DATE(each_rec.shipment_date,'YYYYMMDD'),
          1,
          v_condition_type,
          v_inventory_type,
          v_type_of,
          v_ownership_state_id,
          v_bu_name,
          v_manufacturing_site,
          each_rec.factory_order_number,
          0,
          1,
          v_source_warehouse,
          sysdate,
          sysdate,
          CAST( sysdate AS TIMESTAMP),
          CAST( sysdate AS TIMESTAMP),
		  'InstallBase Upload',
	      v_uploaded_by,
	      v_serialized_part
        );

        
  
        
      SELECT dealer.id
      INTO v_dealer_id
      FROM service_provider dealer,
        organization org,
        bu_org_mapping buorg
      WHERE UPPER(service_provider_number) = UPPER(each_rec.dealer_number)
      AND dealer.id                        = org.id
      AND org.id                           = buorg.org
      AND buorg.bu                         = v_bu_name;
      
      IF v_inventory_type                  ='RETAIL' THEN
        BEGIN
          IF each_rec.end_customer_number IS NOT NULL THEN
            SELECT c.id
            INTO v_end_customer_id
            FROM customer c,
              customer_addresses ca,
              address_book_address_mapping abam,
              address_book ab
            WHERE c.customer_id         = each_rec.end_customer_number
            AND c.id                    = ca.customer
            AND ca.addresses            = abam.address_id
            AND abam.address_book_id    = ab.id
            AND ab.belongs_to           = v_dealer_id
            AND ab.type                 = 'ENDCUSTOMER'
            AND ROWNUM                  = 1;
          ELSIF UPPER(each_rec.country) = 'US' THEN
            SELECT c.id
            INTO v_end_customer_id
            FROM customer c,
              customer_addresses ca,
              address a
            WHERE c.id                 = ca.customer
            AND ca.addresses           = a.id
            AND UPPER(c.company_name)  = UPPER(each_rec.end_customer_name)
            AND UPPER(a.address_line1) = UPPER(each_rec.address_line1)
            AND UPPER(a.email)         = UPPER(each_rec.e_mail)
            AND UPPER(a.country)       = UPPER(each_rec.country)
            AND UPPER(a.state)         = UPPER(each_rec.state)
            AND UPPER(a.city)          = UPPER(each_rec.city)
            AND UPPER(a.zip_code)      = UPPER(each_rec.zipcode)
            AND ROWNUM                 = 1;
          ELSE
            SELECT c.id
            INTO v_end_customer_id
            FROM customer c,
              customer_addresses ca,
              address a
            WHERE c.id                 = ca.customer
            AND ca.addresses           = a.id
            AND UPPER(c.company_name)  = UPPER(each_rec.end_customer_name)
            AND UPPER(a.address_line1) = UPPER(each_rec.address_line1)
            AND UPPER(a.email)         = UPPER(each_rec.e_mail)
            AND UPPER(a.country)       = UPPER(each_rec.country)
            AND UPPER(a.city)          = UPPER(each_rec.city)
            AND ROWNUM                 = 1;
          END IF;
          
          SELECT addresses
          INTO v_cust_addr_id
          FROM customer_addresses
          WHERE customer                   = v_end_customer_id
          AND ROWNUM                       = 1;
          
          IF each_rec.end_customer_number IS NOT NULL THEN
            UPDATE party
            SET name = each_rec.end_customer_name
            WHERE id = v_end_customer_id;

            UPDATE customer
            SET company_name = each_rec.end_customer_name,
              customer_id    = each_rec.end_customer_number,
              locale         = each_rec.prefered_language
            WHERE id         = v_end_customer_id;
        
            UPDATE address
            SET address_line1     = each_rec.address_line1,
              address_line2       = each_rec.address_line2,
              address_line3       = each_rec.address_line3,
              country             = each_rec.country,
              state               = each_rec.state,
              city                = each_rec.city,
              zip_code            = each_rec.zipcode,
              contact_person_name = each_rec.contact_person_name,
              email               = each_rec.e_mail,
              phone               = each_rec.phone_number,
              secondary_phone     = each_rec.fax_number,
              version             = version+1,
              d_updated_on        = SYSDATE
            WHERE id              = v_cust_addr_id;
    
          END IF;
          
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
 
          BEGIN
            SELECT id
            INTO v_address_book_id
            FROM address_book
            WHERE belongs_to = v_dealer_id
            AND type         = 'ENDCUSTOMER'
            AND ROWNUM       = 1;
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
            SELECT address_book_seq.nextval INTO v_address_book_id FROM DUAL;
            INSERT
            INTO address_book
              (
                id,
                type,
                version,
                belongs_to,
                d_internal_comments,
                d_active,
                d_updated_on,
                d_created_on,
                d_updated_time,
                d_created_time
              )
              VALUES
              (
                v_address_book_id,
                'ENDCUSTOMER',
                0,
                v_dealer_id,
                'InstallBase Upload',
                1,
                sysdate,
                sysdate,
                CAST( sysdate AS TIMESTAMP),
                CAST( sysdate AS TIMESTAMP)
              );
          END;
          SELECT address_seq.nextval INTO v_cust_addr_id FROM DUAL;
          INSERT
          INTO address
            (
              id,
              address_line1,
              address_line2,
              address_line3,
              country,
              state,
              city,
              zip_code,
              contact_person_name,
              email,
              phone,
              secondary_phone,
              version,
              d_active,
              d_internal_comments,
              d_updated_on,
              d_created_on,
              d_updated_time,
              d_created_time
            )
            VALUES
            (
              v_cust_addr_id,
              each_rec.address_line1,
              each_rec.address_line2,
              each_rec.address_line3,
              UPPER(each_rec.country),
              UPPER(each_rec.state),
              UPPER(each_rec.city),
              each_rec.zipcode,
              each_rec.contact_person_name,
              each_rec.e_mail,
              each_rec.phone_number,
              each_rec.fax_number,
              0,
              1,
              'InstallBase Upload',
              sysdate,
              sysdate,
              CAST( sysdate AS TIMESTAMP),
              CAST( sysdate AS TIMESTAMP)
            );
          SELECT party_seq.nextval INTO v_end_customer_id FROM DUAL;
          INSERT
          INTO PARTY
            (
              id,
              name,
              version,
              address,
              d_active,
              d_internal_comments,
              d_updated_on,
              d_created_on,
              d_updated_time,
              d_created_time
            )
            VALUES
            (
              v_end_customer_id,
              each_rec.end_customer_name,
              0,
              v_cust_addr_id,
              1,
              'InstallBase Upload',
              sysdate,
              sysdate,
              CAST( sysdate AS TIMESTAMP),
              CAST( sysdate AS TIMESTAMP)
            );
          INSERT
          INTO customer
            (
              id,
              company_name,
              individual,
              locale,
              customer_id
            )
            VALUES
            (
              v_end_customer_id,
              each_rec.end_customer_name,
              0,
              each_rec.prefered_language,
              each_rec.end_customer_number
            );
          UPDATE address SET belongs_to = v_end_customer_id WHERE id = v_cust_addr_id;
          INSERT
          INTO customer_addresses
            (
              customer,
              addresses
            )
            VALUES
            (
              v_end_customer_id,
              v_cust_addr_id
            );
          INSERT
          INTO address_book_address_mapping
            (
              id,
              is_primary,
              type,
              version,
              address_id,
              address_book_id,
              d_created_on,
              d_internal_comments,
              d_active
            )
            VALUES
            (
              addressbook_addmap_seq.nextval,
              1,
              'SHIPPING',
              0,
              v_cust_addr_id,
              v_address_book_id,
              SYSDATE,
              'InstallBase Upload',
              1
            );
        END;
      END IF;
      
     
      BEGIN
        IF v_inventory_type='STOCK' THEN
          UPDATE inventory_item
          SET current_owner = v_dealer_id ,
            latest_buyer    = v_dealer_id
          WHERE id          = v_inv_id;
        END IF;
        IF v_inventory_type='RETAIL' THEN
          UPDATE inventory_item
          SET delivery_date = TO_DATE(each_rec.delivery_date, 'YYYYMMDD'),
            current_owner   = v_dealer_id ,
            latest_buyer    = v_end_customer_id
          WHERE id          = v_inv_id;
        END IF;
      END;
      v_transacted_item := v_inv_id;
      SELECT id
      INTO v_trans_type
      FROM inventory_transaction_type
      WHERE trnx_type_key = 'IB';
      SELECT inventory_transaction_seq.NEXTVAL INTO v_trans_id FROM DUAL;
      INSERT
      INTO inventory_transaction
        (
          id,
          invoice_date,
          invoice_number,
          sales_order_number,
          transaction_date,
          version,
          buyer,
          transacted_item,
          seller,
          inv_transaction_type,
          owner_ship,
          transaction_order,
          status,
          ship_to_site_number,
          d_active,
          d_updated_on,
          d_created_on,
          d_updated_time,
          d_created_time,
		  d_last_updated_by,
		  hours_on_machine
        )
        VALUES
        (
          v_trans_id,
          TO_DATE(v_invoice_date,'YYYYMMDD'),
          each_rec.invoice_number,
          each_rec.sales_order_number,
          TO_DATE(each_rec.shipment_date,'YYYYMMDD'),
          0,
          v_dealer_id,
          v_transacted_item,
          v_oem_id,
          v_trans_type,
          v_dealer_id,
          1,
          'ACTIVE',
          each_rec.dealer_site_number,
          1,
          sysdate,
          sysdate,
          CAST( sysdate AS TIMESTAMP),
          CAST( sysdate AS TIMESTAMP),
		  v_uploaded_by,
		  v_hours_in_service
        );
     
     --Starts
    If each_rec.Dcap_Model_Code is not null Then
    BEGIN
      select b.preferred_currency
      into v_currency
      from inventory_transaction a, organization b
      where a.inv_transaction_type = 1
      and a.transacted_item =V_Inv_Id
      and a.buyer = b.id;
      
      -- finding model category id
      Select id
      Into  v_mod_cat
      From  Model_Category
      Where Model_Code = Each_Rec.Dcap_Model_Code
      and   business_unit_info = Each_Rec.business_unit_name;      
   
   -- finding dealer group based on dealer, scheme, BU
      Select Ds.Id
      into  v_dcap_scheme_id
      From Purpose P, 
           Dealer_Scheme_Purposes Dsp, 
           Dealer_Scheme Ds
      Where P.Name = 'DCAP' 
      And P.Id = Dsp.Purposes 
      And Dsp.Dealer_Scheme = Ds.Id;
              
      Select Dig.Dealer_Group
      into  v_dcap_dealer_group
      From  Dealer_Group Dg,
            dealers_in_group dig
      Where Dg.Business_Unit_Info = Each_Rec.Business_Unit_Name               
      And   Dg.Scheme = v_dcap_scheme_id
      And   Dig.Dealer_Group = Dg.Id
      and   dig.dealer = v_dealer_id;    
      
   --find price matrix by using model category
      Select Id,
            Grid_Amount,
            Grid_Amount_Curr
      Into   V_Price_Matrix_Id,
             V_Grid_Amount,
             V_Grid_Amount_Curr 
      From   Price_Matrix 
      Where  Model_Category = v_mod_cat
      And    Grid_Amount_Curr = V_Currency
      And    Dealer_Group = V_Dcap_Dealer_Group
      And    From_Date < To_Date(Each_Rec.Shipment_Date, 'YYYYMMDD')
      And    To_Date > To_Date(Each_Rec.SHIPMENT_DATE, 'YYYYMMDD');

      --find split type and percentage from model category split by using price matrix
      Select Sp.Name,
            Msp.Split_Percentage
      Into  V_Split_Type,
            V_Split_Percentage 
      From  Model_Category_Split Msp,
            Split_Type Sp
      Where Price_Matrix = V_Price_Matrix_Id 
	    And Sp.Id = Msp.Split_Type 
      And Rownum = 1;
      
      Begin
        If V_Split_Type='SEA' Then
          V_Sea_Amount:=V_Grid_Amount*V_Split_Percentage/100;
          V_Sca_Amount:=V_Grid_Amount*(100-V_Split_Percentage)/100;
                
        Else
          V_Sca_Amount:=V_Grid_Amount*V_Split_Percentage/100;
          V_Sea_Amount:=V_Grid_Amount*(100-V_Split_Percentage)/100;              
        End If;      
      End;        
        
      insert into inventory_dcap_detail
      (
        Id, 
        Accrual_Flag, 
        Model_Category, 
        Sca_Amount, 
        Sca_Curr, 
        Sea_Amount, 
        Sea_Curr, 
        Inventory_Item, 
        Sca_Paid_Flag,
        Sea_Paid_Flag, 
        Sca_Purge_Flag, 
        Sea_Purge_Flag
      )
      Values
      (
        Inv_Dcap_Detail_Seq.Nextval, 
        '1', 
        V_Mod_Cat, 
        V_Sca_Amount, 
        V_Currency,
        V_Sea_Amount, 
        V_Currency, 
        V_Inv_Id, 
        '0',
        '0', 
        '0', 
        '0' 
      );
     
	END;
  End If;
     
     

      IF each_rec.engine_serial_number IS NOT NULL THEN
        BEGIN
          SELECT id
          INTO v_engine_serno_attr
          FROM attribute
          WHERE business_unit_info=v_bu_name
          AND name                ='EngineSerialNo'
          AND ROWNUM              =1;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          SELECT attribute_seq.nextval INTO v_engine_serno_attr FROM DUAL;
          INSERT
          INTO attribute
            (
              id,
              name,
              version,
              d_active,
              business_unit_info,
              d_internal_comments,
              d_updated_on,
              d_created_on,
              d_updated_time,
              d_created_time
            )
            VALUES
            (
              v_engine_serno_attr,
              'EngineSerialNo',
              0,
              1,
              v_bu_name,
              'InstallBase Upload',
              sysdate,
              sysdate,
              CAST( sysdate AS TIMESTAMP),
              CAST( sysdate AS TIMESTAMP)
            );
        End;
        
        
        SELECT attr_value_seq.nextval INTO v_attr_value FROM DUAL;
        INSERT
        INTO attr_value
          (
            id,
            version,
            attribute,
            value,
            d_internal_comments,
            d_active,
            d_updated_on,
            d_created_on,
            d_updated_time,
            d_created_time
          )
          VALUES
          (
            v_attr_value,
            0,
            v_engine_serno_attr,
            each_rec.engine_serial_number,
            'Install Base Upload',
            1,
            sysdate,
            sysdate,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP)
          );
        INSERT INTO inv_item_attr_value
          (id
          ) VALUES
          (v_attr_value
          );
        INSERT
        INTO inv_item_attr_vals
          (
            inv_item_id,
            inv_item_attr_val_id
          )
          VALUES
          (
            v_inv_id,
            v_attr_value
          );
      End If;
      

      BEGIN
        SELECT id
        INTO v_datasource_attr
        FROM attribute
        WHERE business_unit_info=v_bu_name
        AND name                ='DataSource'
        AND ROWNUM              =1;
      EXCEPTION
      WHEN NO_DATA_FOUND THEN
        SELECT attribute_seq.nextval INTO v_datasource_attr FROM DUAL;
        INSERT
        INTO attribute
          (
            id,
            name,
            version,
            d_active,
            business_unit_info,
            d_internal_comments,
            d_updated_on,
            d_created_on,
            d_updated_time,
            d_created_time
          )
          VALUES
          (
            v_datasource_attr,
            'DataSource',
            0,
            1,
            v_bu_name,
            'InstallBase Upload',
            sysdate,
            sysdate,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP)
          );
      END;
      
      SELECT attr_value_seq.nextval INTO v_attr_value FROM DUAL;
      INSERT
      INTO attr_value
        (
          id,
          version,
          attribute,
          value,
          d_internal_comments,
          d_active,
          d_updated_on,
          d_created_on,
          d_updated_time,
          d_created_time
        )
        VALUES
        (
          v_attr_value,
          0,
          v_datasource_attr,
          'Upload Management',
          'Install Base Upload',
          1,
          sysdate,
          sysdate,
          CAST( sysdate AS TIMESTAMP),
          CAST( sysdate AS TIMESTAMP)
        );

      INSERT INTO inv_item_attr_value
        (id
        ) VALUES
        (v_attr_value
        );
      INSERT
      INTO inv_item_attr_vals
        (
          inv_item_id,
          inv_item_attr_val_id
        )
        VALUES
        (
          v_inv_id,
          v_attr_value
        );
        
      IF v_inventory_type='RETAIL' THEN
        SELECT id
        INTO v_trans_type
        FROM inventory_transaction_type
        WHERE trnx_type_key = 'DR' ;
        SELECT inventory_transaction_seq.NEXTVAL INTO v_trans_id FROM DUAL;
        INSERT
        INTO inventory_transaction
          (
            id,
            invoice_date,
            invoice_number,
            sales_order_number,
            transaction_date,
            version,
            buyer,
            transacted_item,
            seller,
            inv_transaction_type,
            owner_ship,
            transaction_order,
            status,
            d_active,
            d_updated_on,
            d_created_on,
            d_updated_time,
            d_created_time,
			d_last_updated_by,
			hours_on_machine
          )
          VALUES
          (
            v_trans_id,
            TO_DATE(v_invoice_date,'YYYYMMDD'),
            each_rec.invoice_number,
            each_rec.sales_order_number,
            TO_DATE(each_rec.delivery_date,'YYYYMMDD'),
            1,
            v_end_customer_id,
            v_transacted_item,
            v_dealer_id,
            v_trans_type,
            v_dealer_id,
            2,
            'ACTIVE',
            1,
            sysdate,
            sysdate,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP),
			V_Uploaded_By,
			v_hours_in_service
          );
          
          
        SELECT address INTO v_cust_addr_id FROM party WHERE id = v_end_customer_id;
        BEGIN
          SELECT id
          INTO v_competitor_model_id
          FROM competitor_model
          WHERE model            = 'UNKNOWN/NOT PROVIDED'
          AND business_unit_info = v_bu_name;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_competitor_model_id := NULL;
        END;
        BEGIN
          SELECT id
          INTO v_competitor_make_id
          FROM competitor_make
          WHERE make             = 'UNKNOWN/NOT PROVIDED'
          AND business_unit_info = v_bu_name;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_competitor_model_id := NULL;
        END;
        BEGIN
          SELECT id
          INTO v_transaction_type_id
          FROM transaction_type
          WHERE type             = 'Cash Sales'
          AND business_unit_info = v_bu_name;
          
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_competitor_model_id := NULL;
        END;
        BEGIN
          SELECT id
          INTO v_competition_type_id
          FROM competition_type
          WHERE type             = 'UNKNOWN/NOT PROVIDED'
          AND business_unit_info = v_bu_name;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          v_competitor_model_id := NULL;
        End;
 
        SELECT marketing_information_seq.nextval INTO v_mark_info_id FROM DUAL;
        INSERT
        INTO MARKETING_INFORMATION
          (
            id,
            customer_first_time_owner,
            months,
            version,
            years,
            market_type,
            competition_type,
            transaction_type,
            sales_man,
            if_previous_owner,
            competitor_model,
            competitor_make,
            d_active,
            d_updated_on,
            d_created_on,
            d_updated_time,
            d_created_time
          )
          VALUES
          (
            v_mark_info_id,
            0,
            NULL, --need to ask
            0,
            NULL,
            NULL, --need to ask, how we will decide this attribute value. CHANGE THIS VALUE TO PROBABLY REFLECT SOMETHING RELATED TO MIGARTION OR CAPTURE IT
            v_competition_type_id,
            v_transaction_type_id, --need to ask, why specifically given this value only, whereas other options r also there
            '',
            NULL,
            v_competitor_model_id,
            v_competitor_make_id,
            1,
            sysdate,
            sysdate,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP)
          );
       
        SELECT addressfortrans_seq.nextval INTO v_address_trans_id FROM DUAL;
        INSERT INTO ADDRESS_FOR_TRANSFER
        SELECT v_address_trans_id,
          address_line1,
          city,
          contact_person_name,
          country,
          email,
          phone,
          secondary_phone,
          state,
          'BILLING',
          0,
          zip_code,
          NULL,
          NULL,
          NULL,
          NULL,
          NULL,
          NULL,
          1,
          address_line2,
          address_line3
        FROM ADDRESS
        WHERE ID = v_cust_addr_id;
        SELECT warranty_seq.nextval INTO v_warranty_seq FROM DUAL;
        INSERT
        INTO WARRANTY
          (
            id,
            delivery_date,
            draft,
            version,
            marketing_information,
            for_transaction,
            customer,
            for_item,
            list_index,
            status,
            for_dealer,
            address_for_transfer,
            transaction_type,
            customer_type,
            d_active,
            d_updated_on,
            d_created_on,
            d_updated_time,
            d_created_time,
		filed_by,
	    d_internal_comments,
	    d_last_updated_by,
	    multidretrnumber,
	    modify_delete_comments,
	    operator,
	    installing_dealer,
	    oem,
	    fleet_number,
	    equipment_vin,
	    installation_date,
	    certified_installer,
	    non_certified_installer,
	    operator_type,
	    operator_address_for_transfer
          )
          VALUES
          (
            v_warranty_seq,
            TO_DATE(v_warranty_start_date,'YYYYMMDD'),
            0,
            0,
            v_mark_info_id,
            v_trans_id,
            v_end_customer_id,
            v_transacted_item,
            0,
            'ACCEPTED',
            v_dealer_id,
            v_address_trans_id,
            v_trans_type,
            'EndCustomer',
            1,
            sysdate,
            sysdate,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP),
	    v_uploaded_by,
	    'InstallBase Upload',
	    v_uploaded_by,
	    v_multidretrnumber,
	    null,
	    v_operator,
	    v_installing_dealer,
	    null,
	    v_fleet_number,
	    v_equipment_vin,
	    TO_DATE(each_rec.installation_date,'YYYYMMDD'),
	    null,
	    null,
	    null,
	    null
          );

        SELECT warranty_audit_seq.nextval INTO v_warranty_audit_id FROM DUAL;
        INSERT
        INTO WARRANTY_AUDIT
          (
            id,
            d_created_on,
            d_internal_comments,
            d_updated_on,
            status,
            for_warranty,
            list_index, --need to ask
            version,
            d_active,
            d_updated_time,
            d_created_time,
			d_last_updated_by
          )
          VALUES
          (
            v_warranty_audit_id,
            sysdate,
            NULL,
            sysdate,
            'SUBMITTED',
            v_warranty_seq,
            0,
            0,
            1,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP),
			v_uploaded_by
          );
          

	  INSERT
        INTO WARRANTY_AUDIT
          (
            id,
            d_created_on,
            d_internal_comments,
            d_updated_on,
            status,
            for_warranty,
            list_index, --need to ask
            version,
            d_active,
            d_updated_time,
            d_created_time,
			d_last_updated_by
          )
          VALUES
          (
            warranty_audit_seq.nextval,
            sysdate,
            NULL,
            sysdate,
            'ACCEPTED',
            v_warranty_seq,
            1,
            0,
            1,
            CAST( sysdate AS TIMESTAMP),
            CAST( sysdate AS TIMESTAMP),
			v_uploaded_by
          );
          

        SELECT CURRENT_DATE INTO v_current_date FROM dual;
        IF UPPER(NVL(each_rec.apply_coverage, 'Y')) = 'Y' THEN
          DECLARE
            CURSOR all_policy_plans
            IS
              SELECT *
              FROM policy_definition
              WHERE ID IN
                (SELECT policy_defn
                FROM policy_for_products
                WHERE for_product IN (v_product, v_model)
                )
            AND business_unit_info            = v_bu_name
            AND UPPER(warranty_type)          = 'STANDARD'
            AND currently_inactive            = 0
            AND availability_ownership_state IN
              (SELECT id FROM ownership_state WHERE name IN ('First Owner', 'Both')
              );
          BEGIN
            FOR each_plan IN all_policy_plans
            LOOP
              BEGIN
                v_months_frm_shipment   := each_plan.months_frm_shipment;
                v_months_frm_delivery   := each_plan.months_frm_delivery;
                IF v_months_frm_shipment > 60000 THEN
                  v_months_frm_shipment := 60000;
                END IF;
                IF v_months_frm_delivery > 60000 THEN
                  v_months_frm_delivery := 60000;
                END IF;
                SELECT ADD_MONTHS(TO_DATE(each_rec.shipment_date, 'YYYYMMDD'), v_months_frm_shipment)-1
                INTO v_ship_coverage_till_date
                FROM DUAL;
                SELECT ADD_MONTHS(TO_DATE(v_warranty_start_date, 'YYYYMMDD'), v_months_frm_delivery)-1
                INTO v_coverage_till_date
                FROM DUAL;
                IF v_ship_coverage_till_date < v_coverage_till_date THEN
                  v_coverage_end_date       := v_ship_coverage_till_date;
                ELSE
                  v_coverage_end_date := v_coverage_till_date;
                END IF;
                is_policy_applicable   := 'TRUE';
                IF TO_DATE(v_warranty_start_date, 'YYYYMMDD') < each_plan.active_from 
                        OR TO_DATE(v_warranty_start_date, 'YYYYMMDD') > each_plan.active_till THEN
                  is_policy_applicable := 'FALSE';
                END IF;
                IF v_hours_in_service > each_plan.service_hrs_covered THEN
                  is_policy_applicable      := 'FALSE';
                END IF;
                SELECT COUNT(*)
                INTO v_count
                FROM applicable_customer_types
                WHERE policy_definition = each_plan.id
                AND type                = 'EndCustomer';
                IF v_count              = 0 THEN
                  is_policy_applicable := 'FALSE';
                END IF;
                SELECT COUNT(*)
                INTO v_count
                FROM policy_for_itemconditions
                WHERE policy_defn       = each_plan.id
                AND lower(for_itemcondition)   = lower(v_condition_type);
                IF v_count              = 0 THEN
                  is_policy_applicable := 'FALSE';
                END IF;

				

				if ( V_COVERAGE_END_DATE  < TO_DATE(v_warranty_start_date,'YYYYMMDD')) then
					V_COVERAGE_END_DATE := TO_DATE(v_warranty_start_date,'YYYYMMDD');
				end if ;

                IF is_policy_applicable = 'TRUE' THEN
                  SELECT policy_seq.NEXTVAL INTO v_policy_id FROM DUAL;
                  INSERT
                  INTO POLICY
                    (
                      id,
                      amount,
                      currency,
                      policy_definition,
                      warranty,
                      d_active,
                      d_updated_on,
                      d_created_on,
                      d_updated_time,
                      d_created_time,
					  d_last_updated_by
                    )
                    VALUES
                    (
                      v_policy_id,
                      0,    --HARD CODED AND HAVE TO GET THE CLARIFICATION       -- NEED TO ASK, DO WE NEED TO PASS THESE VALUES
                      'USD',--HARD CODED AND HAVE TO GET THE CLARIFICATION
                      each_plan.id,
                      v_warranty_seq,
                      1,
                      sysdate,
                      sysdate,
                      CAST( sysdate AS TIMESTAMP),
                      CAST( sysdate AS TIMESTAMP),
					  v_uploaded_by
                    );
                    
                  SELECT policy_audit_seq.NEXTVAL INTO v_policy_audit_id FROM DUAL;
                  INSERT
                  INTO POLICY_AUDIT
                    (
                      id,
                      comments,
                      d_internal_comments,
                      created_on,
                      status,
                      from_date,
                      till_date,
                      created_by,
                      for_policy,
                      service_hours_covered,
                      d_active,
                      d_updated_on,
                      d_created_on,
                      d_updated_time,
                      d_created_time,
					  d_last_updated_by
                    )
                    VALUES
                    (
                      v_policy_audit_id,
                      NULL,
                      'Uploaded through InstallBase Upload',--HARD CODED AND HAVE TO GET THE CLARIFICATION
                      0,                                    --HARD CODED AND HAVE TO GET THE CLARIFICATION   -- NEED TO ASK, CAN V PASS SYSDATE HERE
                      'Active',
                      TO_DATE(v_warranty_start_date,'YYYYMMDD'),
                      V_COVERAGE_END_DATE,
                      NULL,
                      v_policy_id,
                      each_plan.service_hrs_covered,
                      1,
                      sysdate,
                      sysdate,
                      CAST( sysdate AS TIMESTAMP),
                      CAST( sysdate AS TIMESTAMP),
					  v_uploaded_by
                    );
                    
                END IF;
              END;    --end of inner loop begin    

            END LOOP; --end of inner loop
          END;        --end of inner cursor begin
        END IF;       --end of apply coverage
      END IF;         -- end of check," if transaction_type = 'RETAIL'"
      
          v_max_list_index := -1;
          BEGIN
            SELECT MAX(list_index)
            INTO v_max_list_index
            FROM warranty
            WHERE for_item = v_inv_id;

            SELECT MIN(from_date)
            INTO v_wnty_start_date
            FROM INVENTORY_ITEM a,
              WARRANTY b,
              POLICY c,
              POLICY_AUDIT d
            WHERE b.for_item = a.id
            AND c.warranty   = b.id
            AND d.for_policy = c.id
            AND a.id         = v_inv_id
            AND c.d_active   = 1
            AND d.d_active   = 1
            AND b.list_index = v_max_list_index
            AND d.status     ='Active'
            AND d.id         =
              (SELECT MAX(id) FROM policy_audit WHERE for_policy=c.id
              );

            SELECT MAX(till_date)
            INTO v_wnty_end_date
            FROM INVENTORY_ITEM a,
              WARRANTY b,
              POLICY c,
              POLICY_AUDIT d
            WHERE b.for_item = a.id
            AND c.warranty   = b.id
            AND d.for_policy = c.id
            AND a.id         = v_inv_id
            AND c.d_active   = 1
            AND d.d_active   = 1
            AND b.list_index = v_max_list_index
            AND d.status     ='Active'
            AND d.id         =
              (SELECT MAX(id) FROM policy_audit WHERE for_policy=c.id
              );
            UPDATE INVENTORY_ITEM
            SET wnty_start_date = v_wnty_start_date,
              wnty_end_date     = v_wnty_end_date
            WHERE ID            = v_inv_id;
          EXCEPTION
          WHEN no_data_found THEN
            
            NULL;
          End;
    
          
      UPDATE stg_install_base
      SET upload_status = 'Y',
        upload_date     = SYSDATE,
        upload_error    = NULL
      WHERE id          = each_rec.id;
      COMMIT;
    EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      v_upload_error := SUBSTR(SQLERRM,0,3500);
      UPDATE stg_install_base
      SET upload_status = 'N',
        upload_date     = SYSDATE,
        upload_error    = v_upload_error
      WHERE id          = each_rec.id;
      COMMIT;
    END;
  END LOOP;
  COMMIT;
END UPLOAD_INSTALL_BASE_UPLOAD;