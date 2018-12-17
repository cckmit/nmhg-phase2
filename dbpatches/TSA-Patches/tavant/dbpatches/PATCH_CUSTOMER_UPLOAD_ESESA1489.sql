create or replace
PROCEDURE                "CUSTOMER_UPLOAD_PROCEDURE" 
AS
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

            IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALERSHIP','THIRD PARTY','NATIONAL ACCOUNT','ORIGINAL EQUIPMENT MANUFACTURE','DIRECT CUSTOMER','INTER COMPANY') THEN
                SELECT P.ID, P.ADDRESS
                INTO v_party_id, v_address_id
                FROM  SERVICE_PROVIDER SP, BU_ORG_MAPPING BOM, PARTY P
                WHERE UPPER(SP.SERVICE_PROVIDER_NUMBER) = UPPER(EACH_REC.CUSTOMER_NUMBER) AND
                    BOM.BU = v_bu_name AND
                    SP.ID = P.ID AND BOM.ORG = SP.ID;
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('SUPPLIER') THEN
                SELECT P.ID, P.ADDRESS
                INTO v_party_id, v_address_id
                FROM SUPPLIER S,PARTY P,BU_ORG_MAPPING BOM
                WHERE UPPER(S.SUPPLIER_NUMBER) = UPPER(EACH_REC.CUSTOMER_NUMBER) AND
                    P.Id = S.Id And Bom.Bu = V_Bu_Name And
                    BOM.ORG = S.ID;
            End If;
             --code added for SITE NUMBER, FAMILY CODE
            If Upper(Each_Rec.Dealer_Site)Is Not Null Then
              Update Organization_Address Set 
              Site_Number = Each_Rec.Dealer_Site
              Where Id = V_Address_Id;             
              
            END IF;
            IF UPPER(EACH_REC.CUSTOMER_TYPE) NOT IN ('SUPPLIER') THEN
                UPDATE service_provider SET status = UPPER(each_rec.status) WHERE id = v_party_id;
            
                IF UPPER(EACH_REC.DEALER_FAMILY)IS NOT NULL THEN
                   UPDATE SERVICE_PROVIDER SET DEALER_FAMILY_CODE = EACH_REC.DEALER_FAMILY
                    WHERE id = v_party_id;
                END IF;
            END IF;

            UPDATE ADDRESS
            SET
                 ADDRESS_LINE1 = EACH_REC.addressline1 ,
                ADDRESS_LINE2 = EACH_REC.addressline2 ,
                ADDRESS_LINE3 = EACH_REC.addressline3 ,
                ADDRESS_LINE4 = EACH_REC.addressline4 ,
                zipcode_extension=EACH_REC.zip_code_extension,
                secondary_phone=EACH_REC.secondary_phone,
                fax=EACH_REC.fax,
                CITY = EACH_REC.CITY ,
                CONTACT_PERSON_NAME = EACH_REC.CONTACT_PERSON ,
                COUNTRY = EACH_REC.COUNTRY ,
                EMAIL = EACH_REC.EMAIL ,
                PHONE = EACH_REC.PHONE ,
                STATE = EACH_REC.STATE ,
                ZIP_CODE = EACH_REC.POSTAL_CODE ,
                Status = Upper(Each_Rec.Status) ,
                D_Updated_On = Sysdate,
                D_Updated_Time = systimestamp,
                d_last_updated_by = v_user
            WHERE ID = v_address_id;

            UPDATE party SET 
                Name = Each_Rec.Customer_Name,
                d_active = v_d_active,
                D_Updated_On = Sysdate,
                D_Updated_Time = systimestamp,
                d_last_updated_by = v_user,
                customer_classification = EACH_REC.classification
               WHERE id = v_party_id;

            UPDATE organization SET 
                preferred_currency = each_rec.currency
            WHERE id = v_party_id;           

        END IF;

        IF NOT v_is_update THEN

            SELECT PARTY_SEQ.NEXTVAL INTO v_party_id FROM DUAL;
            SELECT ADDRESS_SEQ.NEXTVAL INTO v_address_id FROM DUAL;

            INSERT INTO ADDRESS
                (ID, ADDRESS_LINE1, CITY, CONTACT_PERSON_NAME, COUNTRY,
                Email, Phone, State, Zip_Code, Status, D_Active, Version,
                d_created_on, d_updated_on, D_Created_Time, D_Updated_Time, d_last_updated_by, d_internal_comments,
                ADDRESS_LINE2,ADDRESS_LINE3,ADDRESS_LINE4,zipcode_extension,secondary_phone,fax)
            VALUES
                (v_address_id, EACH_REC.addressline1, EACH_REC.CITY, EACH_REC.first_name || EACH_REC.last_name, EACH_REC.COUNTRY,
                EACH_REC.EMAIL, EACH_REC.PHONE, EACH_REC.STATE, EACH_REC.POSTAL_CODE, UPPER(EACH_REC.STATUS), v_d_active, 0,
                SYSDATE, SYSDATE, systimestamp, systimestamp, v_user, 'Upload Management',
                EACH_REC.addressline2,EACH_REC.addressline3,EACH_REC.addressline4,EACH_REC.zip_code_extension,EACH_REC.secondary_phone,EACH_REC.fax);

            Insert Into Party
                (Id, Name, Version, Address, D_Active,
                D_Created_On, D_Updated_On, D_Created_Time, D_Updated_Time, D_Last_Updated_By, D_Internal_Comments,customer_classification)
            VALUES
                (V_Party_Id, Each_Rec.Customer_Name, 0, V_Address_Id, V_D_Active,
                SYSDATE, SYSDATE, systimestamp, systimestamp, v_user, 'Upload Management',EACH_REC.classification);

            UPDATE address SET belongs_to = v_party_id WHERE id = v_address_id;

            INSERT INTO ORGANIZATION (ID, PREFERRED_CURRENCY)
            VALUES (v_party_id, UPPER(EACH_REC.CURRENCY));            

            INSERT INTO organization_org_addresses (organization, org_addresses) 
            Values (V_Party_Id, V_Address_Id);
            --code added
             IF UPPER(EACH_REC.DEALER_SITE)IS NOT NULL THEN
             INSERT INTO organization_address (id, location, site_number)VALUES( V_Address_Id,
              each_rec.addressline1 ||COALESCE('-'||each_rec.addressline2,'') || COALESCE('-'||each_rec.city,'') 
                || COALESCE('-'||each_rec.state,'') ||COALESCE('-'||each_rec.postal_code,'') || COALESCE('-'||each_rec.country,''),
               EACH_REC.DEALER_SITE);  
             ELSIF UPPER(EACH_REC.DEALER_SITE)is NULL THEN
              INSERT INTO organization_address (id, location, site_number)
              VALUES (v_address_id, each_rec.address || COALESCE('-'||each_rec.city,'') 
                || COALESCE('-'||each_rec.state,'') || COALESCE('-'||each_rec.country,''),
                v_address_id || '-' || each_rec.customer_number);
              END IF;

            IF UPPER(EACH_REC.CUSTOMER_TYPE) NOT IN ('SUPPLIER') THEN
                INSERT INTO SERVICE_PROVIDER
                    (ID, SERVICE_PROVIDER_NUMBER, STATUS,DEALER_FAMILY_CODE)
                VALUES
                    (v_party_id, EACH_REC.CUSTOMER_NUMBER, UPPER(EACH_REC.STATUS),EACH_REC.DEALER_FAMILY);

                
                IF UPPER(EACH_REC.BUSINESS_UNIT) = 'HUSSMANN' AND EACH_REC.CUSTOMER_TYPE = 'DEALERSHIP' THEN
                    UPDATE SERVICE_PROVIDER SET SUBMIT_CREDIT = 'N' WHERE ID = v_party_id;								
                END IF;

                SELECT address_book_seq.nextval INTO v_address_book_id FROM DUAL;
                INSERT INTO address_book
                    (id, type, version, belongs_to, d_active,
                    d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
                VALUES(v_address_book_id, 'SELF', 0, v_party_id, 1,
                    SYSDATE, SYSDATE, v_user, 'Upload Management');

                INSERT INTO address_book_address_mapping(id,is_primary,type,version,address_id,address_book_id,
                    d_active, d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
                VALUES(addr_book_addr_mapp_seq.nextval,1,'SHIPPING',0,v_address_id,v_address_book_id,
                    1, SYSDATE, SYSDATE, v_user, 'Upload Management');
            END IF;

            
            INSERT INTO BU_ORG_MAPPING (ORG, BU)
            VALUES (v_party_id, v_bu_name);

            IF UPPER(EACH_REC.CUSTOMER_TYPE) = 'DEALERSHIP' THEN
                INSERT INTO DEALERSHIP (ID, DEALER_NUMBER, PREFERRED_CURRENCY, STATUS)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER, UPPER(EACH_REC.CURRENCY), UPPER(EACH_REC.STATUS));
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'THIRD PARTY' THEN
                INSERT INTO THIRD_PARTY (ID, THIRD_PARTY_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'NATIONAL ACCOUNT' THEN
                INSERT INTO NATIONAL_ACCOUNT (ID, NATIONAL_ACCOUNT_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'ORIGINAL EQUIPMENT MANUFACTURE' THEN
                INSERT INTO ORIGINAL_EQUIP_MANUFACTURER (ID, ORG_EQUIP_MANUF_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'DIRECT CUSTOMER' THEN
                INSERT INTO DIRECT_CUSTOMER (ID, DIRECT_CUSTOMER_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'SUPPLIER' THEN
                INSERT INTO SUPPLIER (ID, PREFERRED_LOCATION_TYPE, SUPPLIER_NUMBER)
                VALUES (v_party_id, 'BUSINESS', EACH_REC.CUSTOMER_NUMBER);
           ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'INTER COMPANY' THEN
                INSERT INTO inter_company (ID, inter_company_number)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            END IF;

            IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALER') THEN

                INSERT INTO customer (id, company_name, individual, locale)
                VALUES(v_party_id, each_rec.customer_name, 0, null);

                INSERT INTO customer_addresses (customer, addresses)
                VALUES(v_party_id, v_address_id);
            END IF;

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