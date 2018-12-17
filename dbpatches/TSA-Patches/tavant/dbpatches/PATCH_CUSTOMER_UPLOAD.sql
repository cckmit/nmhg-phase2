create or replace PROCEDURE                "CUSTOMER_UPLOAD_PROCEDURE" 
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
	v_address_id		NUMBER;
    v_address_book_id   NUMBER;
    v_customer_id       NUMBER;
	v_bu_name			VARCHAR2(255);
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

            IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALER','THIRD PARTY','NATIONAL ACCOUNT','OEM','DIRECT CUSTOMER') THEN
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
                    P.ID = S.ID AND BOM.BU = v_bu_name AND
                    BOM.ORG = S.ID;
            END IF;
             --code added for SITE
           IF UPPER(EACH_REC.DEALER_SITE)IS NOT NULL THEN
              UPDATE organization_address SET 
              site_number = EACH_REC.DEALER_SITE
              WHERE id = v_party_id;
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
                ADDRESS_LINE1 = EACH_REC.ADDRESS ,
                CITY = EACH_REC.CITY ,
                CONTACT_PERSON_NAME = EACH_REC.CONTACT_PERSON ,
                COUNTRY = EACH_REC.COUNTRY ,
                EMAIL = EACH_REC.EMAIL ,
                PHONE = EACH_REC.PHONE ,
                STATE = EACH_REC.STATE ,
                ZIP_CODE = EACH_REC.POSTAL_CODE ,
                STATUS = UPPER(EACH_REC.STATUS) ,
                D_ACTIVE = v_d_active,
                d_updated_on = SYSDATE,
                d_last_updated_by = v_user
            WHERE ID = v_address_id;

            UPDATE party SET 
                name = each_rec.customer_name,
                d_active = v_d_active,
                d_updated_on = SYSDATE,
                d_last_updated_by = v_user
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
                EMAIL, PHONE, STATE, ZIP_CODE, STATUS, D_ACTIVE, VERSION,
                d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
            VALUES
                (v_address_id, EACH_REC.ADDRESS, EACH_REC.CITY, EACH_REC.CONTACT_PERSON, EACH_REC.COUNTRY,
                EACH_REC.EMAIL, EACH_REC.PHONE, EACH_REC.STATE, EACH_REC.POSTAL_CODE, UPPER(EACH_REC.STATUS), v_d_active, 0,
                SYSDATE, SYSDATE, v_user, 'Upload Management');

            INSERT INTO PARTY
                (ID, NAME, VERSION, ADDRESS, D_ACTIVE,
                d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
            VALUES
                (v_party_id, EACH_REC.CUSTOMER_NAME, 0, v_address_id, v_d_active,
                SYSDATE, SYSDATE, v_user, 'Upload Management');

            UPDATE address SET belongs_to = v_party_id WHERE id = v_address_id;

            INSERT INTO ORGANIZATION (ID, PREFERRED_CURRENCY)
            VALUES (v_party_id, UPPER(EACH_REC.CURRENCY));


            

            INSERT INTO organization_org_addresses (organization, org_addresses) 
            VALUES (v_party_id, v_address_id);
--code added
             IF UPPER(EACH_REC.DEALER_SITE)IS NOT NULL THEN
             INSERT INTO organization_address (id, location, site_number)VALUES(v_address_id,EACH_REC.CITY,EACH_REC.DEALER_SITE);  
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

                
                IF UPPER(EACH_REC.BUSINESS_UNIT) = 'HUSSMANN' AND EACH_REC.CUSTOMER_TYPE = 'DEALER' THEN
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

            IF UPPER(EACH_REC.CUSTOMER_TYPE) = 'DEALER' THEN
                INSERT INTO DEALERSHIP (ID, DEALER_NUMBER, PREFERRED_CURRENCY, STATUS)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER, UPPER(EACH_REC.CURRENCY), UPPER(EACH_REC.STATUS));						
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'THIRD PARTY' THEN
                INSERT INTO THIRD_PARTY (ID, THIRD_PARTY_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'NATIONAL ACCOUNT' THEN
                INSERT INTO NATIONAL_ACCOUNT (ID, NATIONAL_ACCOUNT_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'OEM' THEN
                INSERT INTO ORIGINAL_EQUIP_MANUFACTURER (ID, ORG_EQUIP_MANUF_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'DIRECT CUSTOMER' THEN
                INSERT INTO DIRECT_CUSTOMER (ID, DIRECT_CUSTOMER_NUMBER)
                VALUES (v_party_id, EACH_REC.CUSTOMER_NUMBER);
            ELSIF UPPER(EACH_REC.CUSTOMER_TYPE) = 'SUPPLIER' THEN
                INSERT INTO SUPPLIER (ID, PREFERRED_LOCATION_TYPE, SUPPLIER_NUMBER)
                VALUES (v_party_id, 'BUSINESS', EACH_REC.CUSTOMER_NUMBER);
            END IF;

            IF UPPER(EACH_REC.CUSTOMER_TYPE) IN ('DEALER') THEN
                SELECT party_seq.nextval INTO v_customer_id FROM DUAL;
                INSERT INTO PARTY(id, name, version, address, 
                    d_active, d_created_on, d_updated_on, d_last_updated_by, d_internal_comments)
                VALUES(v_customer_id, each_rec.customer_name, 0, v_address_id,
                    1, SYSDATE, SYSDATE, v_user, 'Upload Management');

                INSERT INTO customer (id, company_name, individual, locale)
                VALUES(v_customer_id, each_rec.customer_name, 0, null);

                INSERT INTO customer_addresses (customer, addresses)
                VALUES(v_customer_id, v_address_id);
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
