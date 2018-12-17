create or replace
PROCEDURE MULTIPLE_CUSTOMER_UPLOAD AS
	CURSOR ALL_CUSTOMER
	IS
		SELECT *
		FROM CUSTOMER_STAGING
		WHERE
			NVL(ERROR_STATUS,'N') = 'Y' AND
			NVL(UPLOAD_STATUS,'N') = 'N';

    V_Error_Code		    Varchar2(4000);
    V_Party_Id			    Number;
    V_Address_Id		    Number;
    v_address_book_id   NUMBER;
    V_Customer_Id       Number;
    v_bu_name			      VARCHAR2(255);
    v_d_active          NUMBER(1);
    v_is_update         BOOLEAN;
    v_user              NUMBER;

BEGIN

    SELECT f.business_unit_info, f.uploaded_by INTO v_bu_name, v_user
    FROM file_upload_mgt f
    WHERE f.id = (SELECT file_upload_mgt_id FROM customer_staging WHERE rownum = 1);

   FOR EACH_REC IN ALL_CUSTOMER LOOP
    BEGIN
        IF UPPER(NVL(each_rec.updates,'N')) = 'Y' THEN
            v_is_update := TRUE;
        ELSE
            v_is_update := FALSE;
        END IF;
        IF UPPER(each_rec.status) = 'ACTIVE' THEN
            v_d_active := 1;
        ELSE
            v_d_active := 0;
        END IF;



          IF v_is_update THEN
          
               SELECT P.ID,a.id  INTO V_Party_Id, V_address_id          
               FROM SERVICE_PROVIDER S,
              ORGANIZATION O,Address A,
              BU_ORG_MAPPING BOM,PARTY P
              WHERE S.SERVICE_PROVIDER_NUMBER = UPPER(EACH_REC.CUSTOMER_NUMBER)
              AND a.address_id_on_remote_system=EACH_REC.DEALER_SITE
                 AND S.ID    = O.ID
                 AND BOM.ORG = O.ID
                 AND  S.ID = P.ID;
                 
           If Upper(Each_Rec.Dealer_Site)Is Not Null Then
              Update Organization_Address Set 
              Site_Number = Each_Rec.Dealer_Site
              Where Id = V_Address_Id;             
              
            END IF;
            
            
             
             UPDATE ADDRESS
            SET
                ADDRESS_LINE1 = EACH_REC.ADDRESS ,
                CITY = EACH_REC.CITY ,
                CONTACT_PERSON_NAME = EACH_REC.CONTACT_PERSON ,
                COUNTRY = EACH_REC.COUNTRY ,
                EMAIL = EACH_REC.EMAIL ,
                PHONE = EACH_REC.PHONE ,
                STATE = EACH_REC.STATE ,
                ZIP_CODE = EACH_REC.POSTAL_CODE ,
                Status = Upper(Each_Rec.Status) ,
                D_Active=v_d_active,
                D_Updated_On = Sysdate,
                D_Updated_Time = systimestamp,
                d_last_updated_by = v_user
            WHERE ID = V_address_id;
            
                  

         END IF;
      
        IF NOT v_is_update THEN
        
           SELECT ADDRESS_SEQ.NEXTVAL INTO V_Address_Id FROM DUAL;
        
          SELECT S.ID  INTO V_Party_Id          
        FROM SERVICE_PROVIDER S,
              ORGANIZATION O,
              BU_ORG_MAPPING BOM
        WHERE S.SERVICE_PROVIDER_NUMBER = UPPER(EACH_REC.CUSTOMER_NUMBER)
        AND S.ID    = O.ID
        AND BOM.ORG = O.ID;
       
        
        
          INSERT INTO ADDRESS
                (ID, ADDRESS_LINE1, CITY, CONTACT_PERSON_NAME, COUNTRY,
                Email, Phone, State, Zip_Code, Status, D_Active, Version,BELONGS_TO,
                d_created_on, d_updated_on, D_Created_Time, D_Updated_Time, d_last_updated_by, d_internal_comments,address_id_on_remote_system)
            VALUES
                (V_Address_Id, EACH_REC.ADDRESS, EACH_REC.CITY, EACH_REC.CONTACT_PERSON, EACH_REC.COUNTRY,
                EACH_REC.EMAIL, EACH_REC.PHONE, EACH_REC.STATE, EACH_REC.POSTAL_CODE, UPPER(EACH_REC.STATUS), v_d_active, 0,V_Party_Id,
                SYSDATE, SYSDATE, systimestamp, systimestamp, v_user, 'Upload Management',EACH_REC.dealer_site);
        
         IF UPPER(EACH_REC.DEALER_SITE)IS NOT NULL THEN
             INSERT INTO organization_address (id, location, site_number)VALUES(V_Address_Id,EACH_REC.CITY,EACH_REC.DEALER_SITE);  
          END IF;
        
        
          INSERT INTO organization_org_addresses (organization, org_addresses) 
            Values (V_Party_Id, V_Address_Id);
            
       BEGIN 
              SELECT ID  INTO v_address_book_id
              FROM ADDRESS_BOOK  WHERE BELONGS_TO = V_party_id ;
                         
              EXCEPTION WHEN NO_DATA_FOUND 
              THEN
       
          INSERT INTO address_book
                    (id, type, version, belongs_to, d_active,
                    d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
                VALUES(v_address_book_id, 'SELF', 0, V_party_id, v_d_active, SYSDATE, 
                SYSDATE, v_user, 'Upload Management');   
                
        END;
        
        INSERT INTO address_book_address_mapping(id,is_primary,type,version,address_id,address_book_id,
                    d_active, d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
                VALUES(addr_book_addr_mapp_seq.nextval,1,'SHIPPING',0,V_Address_Id,v_address_book_id,
                    v_d_active, SYSDATE, SYSDATE, v_user, 'Upload Management');
        
                  
        END IF;

        UPDATE CUSTOMER_STAGING
        SET
            UPLOAD_ERROR = NULL,
            UPLOAD_DATE = SYSDATE,
            UPLOAD_STATUS = 'Y'
        WHERE ID = EACH_REC.ID;

    EXCEPTION
        WHEN OTHERS THEN			
            ROLLBACK;
           v_error_code := SUBSTR(SQLERRM,0,3500);
            UPDATE CUSTOMER_STAGING
            SET
                UPLOAD_ERROR = v_error_code,
                UPLOAD_STATUS = 'N'
            WHERE ID = EACH_REC.ID;
    END;		

    COMMIT;
	END LOOP;

END;