--Purpose    : Adding validation of duplicate dealer site for the same customers
--Author     : Kuldeep Patil
--Created On : 03-Aug-2011
--Impact     : None

create or replace
PROCEDURE MULTIPLE_CUSTOMER_UPLOAD
AS
  CURSOR ALL_CUSTOMER
  IS
    SELECT *
    FROM CUSTOMER_STAGING
    WHERE NVL(ERROR_STATUS,'N') = 'Y'
    AND NVL(UPLOAD_STATUS,'N')  = 'N';
  V_Error_Code      VARCHAR2(4000);
  V_Party_Id        NUMBER;
  V_Address_Id      NUMBER;
  v_address_book_id NUMBER;
  V_Customer_Id     NUMBER;
  v_bu_name         VARCHAR2(255);
  v_d_active        NUMBER(1);
  v_is_update       BOOLEAN;
  v_user            NUMBER;
BEGIN
  SELECT f.business_unit_info,
    f.uploaded_by
  INTO v_bu_name,
    v_user
  FROM file_upload_mgt f
  WHERE f.id =
    (SELECT file_upload_mgt_id FROM customer_staging WHERE rownum = 1
    );
  FOR EACH_REC IN ALL_CUSTOMER
  LOOP
    BEGIN
      IF UPPER(NVL(each_rec.updates,'N')) = 'Y' THEN
        v_is_update                      := TRUE;
      ELSE
        v_is_update := FALSE;
      END IF;
      IF UPPER(each_rec.status) = 'ACTIVE' THEN
        v_d_active             := 1;
      ELSE
        v_d_active := 0;
      END IF;
--      dbms_output.put_line('1');
      IF V_IS_UPDATE THEN
          SELECT t4.id,
            a.id
          INTO V_Party_Id,
            V_address_id
          FROM SERVICE_PROVIDER T1 ,
            ORGANIZATION_ORG_ADDRESSES T2,
            ORGANIZATION_ADDRESS T3 ,
            PARTY T4,
            address a
          WHERE T1.ID                 = T2.ORGANIZATION
          AND T3.ID                   = T2.ORG_ADDRESSES
          AND T4.ID                   = T1.ID
          AND UPPER(SERVICE_PROVIDER_NUMBER) = UPPER(EACH_REC.CUSTOMER_NUMBER)
          AND T3.site_number          = EACH_REC.DEALER_SITE
          AND t3.id                   = a.id;

        UPDATE ADDRESS
        SET ADDRESS_LINE1 = EACH_REC.addressline1 ,
           ADDRESS_LINE2 = EACH_REC.addressline2 ,
          ADDRESS_LINE3 = EACH_REC.addressline3 ,
          ADDRESS_LINE4 = EACH_REC.addressline4 ,
          zipcode_extension=EACH_REC.zip_code_extension,
          secondary_phone=EACH_REC.secondary_phone,
          fax=EACH_REC.fax,
          CITY                = EACH_REC.CITY ,
          CONTACT_PERSON_NAME = EACH_REC.CONTACT_PERSON ,
          COUNTRY             = EACH_REC.COUNTRY ,
          EMAIL               = EACH_REC.EMAIL ,
          PHONE               = EACH_REC.PHONE ,
          STATE               = EACH_REC.STATE ,
          ZIP_CODE            = EACH_REC.POSTAL_CODE ,
          Status              = Upper(Each_Rec.Status) ,
          D_Active            =v_d_active,
          D_Updated_On        = Sysdate,
          D_Updated_Time      = systimestamp,
          d_last_updated_by   = v_user
        WHERE ID              = V_address_id;
      END IF;
      IF NOT V_IS_UPDATE THEN
--      dbms_output.put_line('3');
        SELECT ADDRESS_SEQ.NEXTVAL INTO V_Address_Id FROM DUAL;
        SELECT DISTINCT S.ID
        INTO V_Party_Id
        FROM SERVICE_PROVIDER S,
          ORGANIZATION O,
          BU_ORG_MAPPING BOM
        WHERE UPPER(S.SERVICE_PROVIDER_NUMBER) = UPPER(EACH_REC.CUSTOMER_NUMBER)
        AND S.ID                        = O.ID
        AND BOM.ORG                     = O.ID;
--        dbms_output.put_line('4');
        INSERT
        INTO ADDRESS
          (
            ID,
            ADDRESS_LINE1,
            CITY,
            CONTACT_PERSON_NAME,
            COUNTRY,
            Email,
            Phone,
            State,
            Zip_Code,
            Status,
            D_Active,
            Version,
            BELONGS_TO,
            d_created_on,
            d_updated_on,
            D_Created_Time,
            D_Updated_Time,
            d_last_updated_by,
            d_internal_comments,
            address_id_on_remote_system
          )
          VALUES
          (
            V_Address_Id,
            EACH_REC.addressline1,
            EACH_REC.CITY,
            EACH_REC.CONTACT_PERSON,
            EACH_REC.COUNTRY,
            EACH_REC.EMAIL,
            EACH_REC.PHONE,
            EACH_REC.STATE,
            EACH_REC.POSTAL_CODE,
            UPPER(EACH_REC.STATUS),
            v_d_active,
            0,
            V_Party_Id,
            SYSDATE,
            SYSDATE,
            systimestamp,
            systimestamp,
            v_user,
            'Upload Management',
            EACH_REC.dealer_site,
			 EACH_REC.addressline2,
            EACH_REC.addressline3,
            EACH_REC.addressline4,
            EACH_REC.zip_code_extension,
            EACH_REC.secondary_phone,
            EACH_REC.fax
          );
--          dbms_output.put_line('5');
        IF UPPER(EACH_REC.DEALER_SITE)IS NOT NULL THEN
--        dbms_output.put_line('6');
          INSERT
          INTO organization_address
            (
              id,
              location,
              site_number
            )
            VALUES
            (
              V_Address_Id,
              each_rec.addressline1 ||COALESCE('-'||each_rec.addressline2,'') || COALESCE('-'||each_rec.city,'') 
                || COALESCE('-'||each_rec.state,'') ||COALESCE('-'||each_rec.postal_code,'') || COALESCE('-'||each_rec.country,''),
              EACH_REC.DEALER_SITE
            );
        END IF;
--        dbms_output.put_line('7');
        INSERT
        INTO organization_org_addresses
          (
            organization,
            org_addresses
          )
          VALUES
          (
            V_Party_Id,
            V_Address_Id
          );
        BEGIN
--        DBMS_OUTPUT.PUT_LINE('8');
          SELECT ab.ID
          INTO v_address_book_id
          FROM ADDRESS_BOOK AB,
            ADDRESS A,
            ADDRESS_BOOK_ADDRESS_MAPPING ABM
          WHERE AB.ID        = ABM.ADDRESS_BOOK_ID
          AND ABM.ADDRESS_ID = A.ID
          AND ab.BELONGS_TO  = V_PARTY_ID
          AND a.id           = V_Address_Id ;
--          dbms_output.put_line('9');
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
--        dbms_output.put_line('10');
          SELECT address_book_seq.nextval INTO v_address_book_id FROM dual;
          INSERT
          INTO address_book
            (
              id,
              type,
              version,
              belongs_to,
              d_active,
              d_created_on,
              d_updated_on,
              d_last_updated_by,
              d_internal_comments,
              d_created_time,
              d_updated_time
            )
            VALUES
            (
              v_address_book_id,
              'SELF',
              0,
              V_party_id,
              v_d_active,
              SYSDATE,
              SYSDATE,
              v_user,
              'Upload Management',
              systimestamp,
              systimestamp
            );
--            dbms_output.put_line('11');
        END;
        INSERT
        INTO address_book_address_mapping
          (
            id,
            is_primary,
            type,
            version,
            address_id,
            address_book_id,
            d_active,
            d_created_on,
            d_updated_on,
            d_last_updated_by,
            d_internal_comments,
            d_created_time,
            d_updated_time
          )
          VALUES
          (
            addr_book_addr_mapp_seq.nextval,
            1,
            'SHIPPING',
            0,
            V_Address_Id,
            v_address_book_id,
            v_d_active,
            SYSDATE,
            SYSDATE,
            v_user,
            'Upload Management',
            systimestamp,
            systimestamp
          );
--          dbms_output.put_line('12');
      END IF;
      UPDATE CUSTOMER_STAGING
      SET UPLOAD_ERROR = NULL,
        UPLOAD_DATE    = SYSDATE,
        UPLOAD_STATUS  = 'Y'
      WHERE ID         = EACH_REC.ID;
    EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
--      dbms_output.put_line('12');
      v_error_code := SUBSTR(SQLERRM,0,3500);
      UPDATE CUSTOMER_STAGING
      SET UPLOAD_ERROR = v_error_code,
        UPLOAD_STATUS  = 'N'
      WHERE ID         = EACH_REC.ID;
    END;
    COMMIT;
  END LOOP;
END;
/