--Purpose    : WR Upload procedure
--Author     : Rahul Katariya
--Created On : 08/07/2010
--Impact     : WR Upload

CREATE OR REPLACE
PROCEDURE UPLOAD_WARRANTY_REG_UPLOAD
AS
  -- GET ALL THE WARRANTY REGISTRATIONS WHICH ARE MARKED AS VALID BUT HAVE NOT BEEN UPLOADED
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_WARRANTY_REGISTRATIONS
    WHERE ERROR_STATUS          = 'Y'
    AND NVL(UPLOAD_STATUS, 'N') = 'N';
  --GET ALL THE APPLICABLE POLICY CODES ON THIS PARTICULAR INVENTORY SERIAL NUMBER
  CURSOR ALL_ELIGIBLE_POLICY_PLANS(P_PRODUCT VARCHAR2, P_MODEL VARCHAR2, P_BU VARCHAR2, P_REG_DATE DATE, P_HRS_ON_SERVICE NUMBER, P_CONDITION VARCHAR2, P_CERT_STATUS VARCHAR2, P_INSTALL_DEALER NUMBER)
  IS
    SELECT PD.*
    FROM POLICY_DEFINITION PD,
      POLICY_FOR_ITEMCONDITIONS PFI,
      (
      (SELECT PDG.POLICY_DEFN
      FROM POLICY_FOR_DEALER_GROUPS PDG,
        DEALERS_IN_GROUP DIG
      WHERE PDG.FOR_DEALER_GROUPS = DIG.DEALER_GROUP
      AND DIG.DEALER              = P_INSTALL_DEALER
      )
  UNION
    (SELECT PSP.POLICY_DEFN
    FROM POLICY_FOR_SERVICEPROVIDERS PSP
    WHERE PSP.FOR_SERVICE_PROVIDER = P_INSTALL_DEALER
    ) ) DLR_FILTER
    WHERE PD.ID IN
      (SELECT POLICY_DEFN
      FROM POLICY_FOR_PRODUCTS
      WHERE FOR_PRODUCT IN (P_PRODUCT, P_MODEL)
      )
    AND PD.ACTIVE_FROM                                           <= P_REG_DATE
    AND PD.ACTIVE_TILL                                           >= P_REG_DATE
    AND P_HRS_ON_SERVICE                                         <= PD.SERVICE_HRS_COVERED
    AND PD.BUSINESS_UNIT_INFO                                     = P_BU
    AND UPPER(PD.WARRANTY_TYPE)                                   = 'STANDARD'
    AND (DECODE(PD.CERTIFICATION_STATUS, 'NOTCERTIFIED', 'N', 'Y')= P_CERT_STATUS
    OR PD.CERTIFICATION_STATUS                                    = 'ANY')
    AND PD.ID                                                     = PFI.POLICY_DEFN
    AND PFI.FOR_ITEMCONDITION                                     = P_CONDITION
    AND PD.AVAILABILITY_OWNERSHIP_STATE                           = 1 --HARDCODED BECAUSE IT IS 1 FOR DATA MIGRATION
    AND PD.ID                                                     = DLR_FILTER.POLICY_DEFN(+)
    AND PD.D_ACTIVE                                               = 1;
    --GET ALL THE USER ID FOR ADMIN ROLE FOR THE RESPECTIVE BUSINESS UNIT.
    -- CURSOR ALL_ASSIGNEE_ADMINS(V_BUSINESS_UNIT_INFO VARCHAR2)
    -- IS
    -- SELECT U.ID
    -- FROM ORG_USER U, USER_ROLES UR, USER_BU_AVAILABILITY UA
    -- WHERE U.ID = UR.ORG_USER AND U.ID = UA.ORG_USER AND UR.ROLES = UA.ROLE
    -- AND UR.ROLES = (SELECT ID FROM ROLE WHERE NAME = 'dealerWarrantyAdmin') AND UA.BUSINESS_UNIT_INFO = V_BUSINESS_UNIT_INFO
    --  ORDER BY U.ID;
    --GET ALL THE EXTENDED WARRANTY POLICIES.
    CURSOR ALL_EXTENDED_POLICIES(P_BUSINESS_UNIT_INFO VARCHAR2, P_SERIAL_ID VARCHAR2)
    IS
      SELECT POLICY
      FROM EXTENDED_WARRANTY_NOTIFICATION we
      WHERE NOTIFICATION_TYPE  <> 'Completed'
      AND WE.FOR_UNIT           = P_SERIAL_ID
      AND WE.BUSINESS_UNIT_INFO = P_BUSINESS_UNIT_INFO;
    --ALL GLOBAL VARIABLES DECLARED FOR A PROCEDURE
    V_UPLOAD_ERROR        VARCHAR2(4000);
    V_ADMIN_APPRV_IND     VARCHAR2(50);
    V_KICK_OFF_CVRGS      BOOLEAN;
    V_TYPE_OF             NUMBER(19);
    V_PRODUCT             VARCHAR2(255);
    V_MODEL               VARCHAR2(255);
    V_OEM_ID              NUMBER(19);
    V_PARTY_OEM_ID        NUMBER(19);
    V_WARRANTY_ID         NUMBER(19);
    V_MARK_INFO_ID        NUMBER(19);
    V_SALESMAN_ID         NUMBER(19);
    V_MARKET_ID           NUMBER(19);
    V_COMPETITION_TYPE_ID NUMBER(19);
    V_TRANSACTION_TYPE_ID NUMBER(19);
    V_COMPETITOR_MODEL_ID NUMBER(19);
    V_COMPETITOR_MAKE_ID  NUMBER(19);
    V_MAX_COUNT           NUMBER(19);
    V_INV_TYPE            VARCHAR2(255);
    V_TRANS_TYPE          VARCHAR2(255);
    V_SELLER_ID           NUMBER(19);
    V_BUYER_ID            NUMBER(19);
    V_BUILD_DATE DATE;
    V_MFG_ID            NUMBER(19);
    V_INV_ID            NUMBER(19);
    V_SERIAL_NO_EXIST   NUMBER(19);
    V_INSTALL_DEALER_ID NUMBER(19);
    V_CUST_ADD_ID       VARCHAR2(255);
    V_ADDRESS_TRANS_ID  NUMBER(19);
    V_MULTIDRETRNUMBER  NUMBER(19);
    V_OPERATOR_ID       NUMBER(19);
    V_WARRANTY_AUDIT_ID NUMBER(19);
    V_OF_TYPE_ID        NUMBER(19);
    V_SERIAL_ID         NUMBER(19);
    V_INV_ITEM_COMP_ID  NUMBER(19);
    V_HOURS_ON_SERV     NUMBER(19);
    V_BUILT_ON DATE;
    V_INSTALL_DATE DATE;
    V_SHIP_DATE DATE;
    V_REG_DATE DATE;
    V_CONDITION       VARCHAR2(255);
    V_TYPE            VARCHAR2(255);
    V_OWNERSHIP_STATE VARCHAR2(255);
    V_CERT_STATUS     VARCHAR2(1);
    V_POLICY_COVER    NUMBER(19);
    V_SHIP_COVERAGE_TILL_DATE DATE;
    V_COVERAGE_TILL_DATE DATE;
    V_MONTHS_FRM_DELIVERY NUMBER(19);
    V_MONTHS_FRM_SHIPMENT NUMBER(19);
    V_POLICY_DEFN_ID      NUMBER(19);
    V_POLICY_ID           NUMBER(19);
    V_POLICY_AUDIT_ID     NUMBER(19);
    V_ADDTNL_INFO_IND     VARCHAR2(50);
    V_COMP_INV_ID         NUMBER(19);
    V_POL_COUNT           NUMBER(19);
    V_SERIAL_NUM_COUNT    NUMBER(19);
    V_PART_NUM_COUNT      NUMBER(19);
    V_INSTALL_DATE_COUNT  NUMBER(19);
    V_END_CUST_ID         NUMBER(19);
    V_ASSIGNED_TO         NUMBER(19);
    V_WARRANTY_STATUS     VARCHAR2(255);
    V_ASSIGN_COUNT        NUMBER(19);
    V_TMP_ASSIGN_COUNT    NUMBER(19);
    V_TRANS_ID            NUMBER(19);
    V_DEALER_ID           NUMBER(19);
    V_LAST_UPDATED_BY     NUMBER(19);
    V_INVOICE_DATE DATE;
    V_INVOICE_NUM              VARCHAR2(255);
    V_WARANTY_TASK_INSTANCE_ID NUMBER(19);
    V_COVERAGE_END_DATE DATE;
    V_COMP_INSTALL_DATE_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_SERIAL_NUMBER_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_PART_NUMBER_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_POL_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_CUR_RECORD ALL_ELIGIBLE_POLICY_PLANS%ROWTYPE;
  BEGIN
    --GET THE OEM ID FROM PARTY TABLE
    SELECT ID
    INTO V_PARTY_OEM_ID
    FROM PARTY
    WHERE NAME = 'OEM';
    --GET THE MANUAL APPROVAL CONFIG PARAM VALUE
    SELECT NVL(VALUE, 'FALSE')
    INTO V_ADMIN_APPRV_IND
    FROM CONFIG_PARAM_OPTION
    WHERE ID =
      (SELECT CONFIG_PARAM_OPTION
      FROM CONFIG_VALUE
      WHERE CONFIG_PARAM =
        (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'manualApprovalFlowForDR'
        )
      );
    SELECT NVL(VALUE, 'FALSE')
    INTO V_ADDTNL_INFO_IND
    FROM CONFIG_PARAM_OPTION
    WHERE ID =
      (SELECT CONFIG_PARAM_OPTION
      FROM CONFIG_VALUE
      WHERE CONFIG_PARAM =
        (SELECT ID
        FROM CONFIG_PARAM
        WHERE NAME = 'additionalInformationDetailsApplicable'
        )
      );
    -- PUT THE LOGIC TO HANDLE THE MANUAL APPROVAL FLOW FOR WARRANTY REGS BASED ON BU CONFIGURATION.
    -- IF YES THEN ASSIGN TO WARRANTY ADMINISTRATOR
    -- ELSE AUTO APPROVAL BY THE SYSTEM AND CICK OFF THE COVERAGES.
    IF UPPER(V_ADMIN_APPRV_IND) = 'TRUE' THEN
      V_WARRANTY_STATUS        := 'SUBMITTED';
    ELSE
      V_ASSIGNED_TO     := NULL;
      V_WARRANTY_STATUS := 'ACCEPTED';
    END IF;
    FOR EACH_REC IN ALL_REC
    LOOP
      BEGIN
        --RESET THE VALUES
        V_UPLOAD_ERROR             := NULL;
        V_TYPE_OF                  := 0;
        V_PRODUCT                  := NULL;
        V_MODEL                    := NULL;
        V_WARRANTY_ID              := 0;
        V_MARK_INFO_ID             := NULL;
        V_SALESMAN_ID              := 0;
        V_MARKET_ID                := 0;
        V_COMPETITION_TYPE_ID      := 0;
        V_TRANSACTION_TYPE_ID      := 0;
        V_COMPETITOR_MODEL_ID      := 0;
        V_COMPETITOR_MAKE_ID       := 0;
        V_TRANS_TYPE               := NULL;
        V_SELLER_ID                := 0;
        V_BUYER_ID                 := 0;
        V_SERIAL_NO_EXIST          := 0;
        V_INSTALL_DEALER_ID        := 0;
        V_CUST_ADD_ID              := NULL;
        V_ADDRESS_TRANS_ID         := 0;
        V_MULTIDRETRNUMBER         := 0;
        V_OPERATOR_ID              := NULL;
        V_WARRANTY_AUDIT_ID        := 0;
        V_OF_TYPE_ID               := 0;
        V_SERIAL_ID                := 0;
        V_INV_ITEM_COMP_ID         := 0;
        V_HOURS_ON_SERV            := 0;
        V_BUILT_ON                 :=NULL;
        V_INSTALL_DATE             :=NULL;
        V_SHIP_DATE                :=NULL;
        V_REG_DATE                 := sysdate;
        V_CONDITION                :=NULL;
        V_TYPE                     :=NULL;
        V_OWNERSHIP_STATE          :=NULL;
        V_CERT_STATUS              := NULL;
        V_POLICY_COVER             := 1;
        V_SHIP_COVERAGE_TILL_DATE  := NULL;
        V_COVERAGE_TILL_DATE       := NULL;
        V_MONTHS_FRM_DELIVERY      := 0;
        V_MONTHS_FRM_SHIPMENT      := 0;
        V_POLICY_DEFN_ID           := 0;
        V_POLICY_ID                := 0;
        V_POLICY_AUDIT_ID          := 0;
        V_COMP_INV_ID              := 0;
        V_END_CUST_ID              := 0;
        V_ASSIGN_COUNT             := 0;
        V_TMP_ASSIGN_COUNT         := 0;
        V_TRANS_ID                 := 0;
        V_DEALER_ID                := 0;
        V_INVOICE_DATE             := NULL;
        V_INVOICE_NUM              := NULL;
        V_WARANTY_TASK_INSTANCE_ID := NULL;
        V_COVERAGE_END_DATE        := NULL;
        SELECT UPLOADED_BY
        INTO V_LAST_UPDATED_BY
        FROM file_upload_mgt
        WHERE id = EACH_REC.FILE_UPLOAD_MGT_ID;
        --CHECK IF THE END CUSTOMER IS ADDED ELSE DO NOT CONTINUE
        SELECT CUST.ID
        INTO V_END_CUST_ID
        FROM CUSTOMER CUST,
          CUSTOMER_ADDRESSES CUSTADDR,
          ADDRESS_BOOK ADDRBK,
          ADDRESS_BOOK_ADDRESS_MAPPING ADDRBKMPNG,
          SERVICE_PROVIDER SP
        WHERE CUST.ID                  = CUSTADDR.CUSTOMER
        AND CUSTADDR.ADDRESSES         = ADDRBKMPNG.ADDRESS_ID
        AND ADDRBKMPNG.ADDRESS_BOOK_ID = ADDRBK.ID
        AND ADDRBK.BELONGS_TO          = SP.ID
        AND ADDRBK.TYPE                = 'ENDCUSTOMER'
        AND CUST.CUSTOMER_ID           = EACH_REC.CUSTOMER_NUMBER
        AND SP.SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
        --GET THE INSTALLING DEALER ID
        SELECT ID
        INTO V_INSTALL_DEALER_ID
        FROM SERVICE_PROVIDER
        WHERE SERVICE_PROVIDER_NUMBER = EACH_REC.INSTALLING_DEALER_NUMBER;
        --GET THE DEALER ID
        SELECT ID
        INTO V_DEALER_ID
        FROM SERVICE_PROVIDER
        WHERE SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
        IF (EACH_REC.OPERATOR_NUMBER IS NOT NULL) THEN
          SELECT CUST.ID
          INTO V_OPERATOR_ID
          FROM CUSTOMER CUST,
            CUSTOMER_ADDRESSES CUSTADDR,
            ADDRESS_BOOK ADDRBK,
            ADDRESS_BOOK_ADDRESS_MAPPING ADDRBKMPNG,
            SERVICE_PROVIDER SP
          WHERE CUST.ID                  = CUSTADDR.CUSTOMER
          AND CUSTADDR.ADDRESSES         = ADDRBKMPNG.ADDRESS_ID
          AND ADDRBKMPNG.ADDRESS_BOOK_ID = ADDRBK.ID
          AND ADDRBK.BELONGS_TO          = SP.ID
          AND ADDRBK.TYPE                = EACH_REC.OPERATOR_TYPE
          AND CUST.CUSTOMER_ID           = EACH_REC.OPERATOR_NUMBER
          AND SP.SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
        END IF;
        IF V_END_CUST_ID > 0 THEN
          BEGIN
            -- GET THE COMPONENT_SERIAL_NUMBER, COMPONENT_PART_NUMBER, COMPONENT_INSTALLATION_DATE
            COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_SERIAL_NUMBER,'#$#',V_COMP_SERIAL_NUMBER_ARRAY , V_SERIAL_NUM_COUNT);
            COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_PART_NUMBER,'#$#',V_COMP_PART_NUMBER_ARRAY , V_PART_NUM_COUNT);
            COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_INSTALLATION_DATE,'#$#',V_COMP_INSTALL_DATE_ARRAY , V_INSTALL_DATE_COUNT);
            -- CHECK THE LOAD BALANCE FOR EACH ADMIN AND ASSIGN THE WARRANTY TO PARTICULAR ADMIN WITH LEAST LOAD THAN OTHERS
            IF UPPER(V_ADMIN_APPRV_IND) = 'TRUE' THEN
              BEGIN
                SELECT U.ID,
                  COUNT(WT.ID) AS ASSIGNED_CNT
                INTO V_ASSIGNED_TO,
                  V_ASSIGN_COUNT
                FROM ORG_USER U,
                  USER_ROLES UR,
                  USER_BU_AVAILABILITY UA,
                  WARRANTY_TASK_INSTANCE WT
                WHERE U.ID   = UR.ORG_USER
                AND U.ID     = UA.ORG_USER
                AND UR.ROLES = UA.ROLE
                AND U.ID     = WT.ASSIGNED_TO (+)
                AND UR.ROLES =
                  (SELECT ID FROM ROLE WHERE NAME = 'processor'
                  )
                AND UA.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
                AND ROWNUM               <= 1
                GROUP BY U.ID
                ORDER BY ASSIGNED_CNT;
              END;
            END IF;
            -- GET ITEM INFORMATION FOR A UNIT SERIAL NUMBER AND UNIT ITEM NUMBER FOR A BUSINESS_INFO
            SELECT II.ID,
              II.HOURS_ON_MACHINE,
              BUILT_ON,
              SHIPMENT_DATE,
              CONDITION_TYPE,
              TYPE,
              OWNERSHIP_STATE,
              INSTALLATION_DATE
            INTO V_SERIAL_ID,
              V_HOURS_ON_SERV,
              V_BUILT_ON,
              V_SHIP_DATE,
              V_CONDITION,
              V_TYPE,
              V_OWNERSHIP_STATE,
              V_INSTALL_DATE
            FROM INVENTORY_ITEM II,
              ITEM I
            WHERE II.SERIAL_NUMBER    = EACH_REC.SERIAL_NUMBER
            AND II.OF_TYPE            = I.ID
            AND I.ITEM_NUMBER         = EACH_REC.ITEM_NUMBER
            AND II.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
            AND I.BUSINESS_UNIT_INFO  = EACH_REC.BUSINESS_UNIT_INFO
            AND II.SERIALIZED_PART    = 0
            AND II.D_ACTIVE           = 1
            AND II.TYPE               = 'STOCK';
            IF V_SERIAL_ID            > 0 THEN
              FOR I                  IN 1..V_SERIAL_NUM_COUNT
              LOOP
                BEGIN
                  --GET THE OF TYPE ID
                  SELECT ID
                  INTO V_OF_TYPE_ID
                  FROM ITEM
                  WHERE ITEM_NUMBER      = V_COMP_PART_NUMBER_ARRAY(I)
                  AND ITEM_TYPE          = 'PART'
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
                  AND ROWNUM             = 1;
                  SELECT ID
                  INTO V_INV_ID
                  FROM INVENTORY_ITEM
                  WHERE SERIAL_NUMBER    = V_COMP_SERIAL_NUMBER_ARRAY(I)
                  AND OF_TYPE            = V_OF_TYPE_ID
                  AND serialized_part    = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  --START
                EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  --GET THE SEQUENCE FOR INVENTORY
                  SELECT INVENTORY_ITEM_SEQ.NEXTVAL
                  INTO V_INV_ID
                  FROM DUAL;
                  --INSERT THE RECORD INTO INVENTORY ITEM
                  INSERT
                  INTO INVENTORY_ITEM
                    (
                      ID,
                      BUILT_ON,
                      DELIVERY_DATE,
                      HOURS_ON_MACHINE,
                      SERIAL_NUMBER,
                      SHIPMENT_DATE,
                      VERSION,
                      CONDITION_TYPE,
                      TYPE,
                      OF_TYPE,
                      OWNERSHIP_STATE,
                      BUSINESS_UNIT_INFO,
                      PENDING_WARRANTY,
                      D_CREATED_ON,
                      D_INTERNAL_COMMENTS,
                      D_ACTIVE,
                      D_UPDATED_ON,
                      D_LAST_UPDATED_BY,
                      D_CREATED_TIME,
                      D_UPDATED_TIME,
                      SERIALIZED_PART,
                      INSTALLATION_DATE,
                      SOURCE
                    )
                    VALUES
                    (
                      V_INV_ID,
                      V_BUILT_ON,
                      TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD'),
                      V_HOURS_ON_SERV,
                      V_COMP_SERIAL_NUMBER_ARRAY(I),
                      V_SHIP_DATE,
                      1,
                      V_CONDITION,
                      V_TYPE,
                      V_OF_TYPE_ID,
                      V_OWNERSHIP_STATE,
                      EACH_REC.BUSINESS_UNIT_INFO,
                      0,
                      SYSDATE,
                      EACH_REC.BUSINESS_UNIT_INFO
                      || '-Upload',
                      1,
                      SYSDATE,
                      V_LAST_UPDATED_BY,
                      CURRENT_TIMESTAMP,
                      CURRENT_TIMESTAMP,
                      1,
                      TO_DATE(V_COMP_INSTALL_DATE_ARRAY(I),'YYYYMMDD'),
                      'INSTALLBASE'
                    );
                  --GET THE INV_ITEM_COMP_ID
                  SELECT SEQ_INVENTORYITEMCOMPOSITION.NEXTVAL
                  INTO V_INV_ITEM_COMP_ID
                  FROM DUAL;
                  --INSERT THE RECORD INTO INVENTORY_ITEM_COMPOSITION
                  INSERT
                  INTO INVENTORY_ITEM_COMPOSITION
                    (
                      ID,
                      VERSION,
                      PART,
                      PART_OF,
                      D_CREATED_ON,
                      D_INTERNAL_COMMENTS,
                      D_ACTIVE,
                      D_UPDATED_ON,
                      D_LAST_UPDATED_BY,
                      D_CREATED_TIME,
                      D_UPDATED_TIME
                    )
                    VALUES
                    (
                      V_INV_ITEM_COMP_ID,
                      1,
                      V_INV_ID,
                      V_SERIAL_ID,
                      SYSDATE,
                      EACH_REC.BUSINESS_UNIT_INFO
                      || '-Upload',
                      1,
                      SYSDATE,
                      V_LAST_UPDATED_BY,
                      CURRENT_TIMESTAMP,
                      CURRENT_TIMESTAMP
                    );
                END;
              END LOOP;
              IF UPPER
                (
                  V_ADDTNL_INFO_IND
                )
                = 'TRUE' THEN
                BEGIN
                  --GET THE MARKETING INFORMATION SEQUENCE ID
                  SELECT MARKETING_INFORMATION_SEQ.NEXTVAL
                  INTO V_MARK_INFO_ID
                  FROM DUAL;
                  --GET THE MARKET TYPE ID FOR A MARKET TYPE
                  SELECT ID
                  INTO V_MARKET_ID
                  FROM MARKET_TYPE
                  WHERE TITLE            = EACH_REC.MARKET_TYPE
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  --GET THE COMPETITION TYPE ID FOR A GIVEN COMPETITION TYPE
                  SELECT ID
                  INTO V_COMPETITION_TYPE_ID
                  FROM COMPETITION_TYPE
                  WHERE TYPE             = EACH_REC.COMPETITION_TYPE
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  --GET THE TRANSACTION TYPE ID FOR A GIVEN TRANSACTION TYPE
                  SELECT ID
                  INTO V_TRANSACTION_TYPE_ID
                  FROM TRANSACTION_TYPE
                  WHERE TYPE             = EACH_REC.TRANSACTION_TYPE
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  --GET COMPETITOR MODEL ID FOR A GIVEN COMPETITOR MODEL
                  SELECT ID
                  INTO V_COMPETITOR_MODEL_ID
                  FROM COMPETITOR_MODEL
                  WHERE MODEL            = EACH_REC.MODEL_NUMBER
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  --GET COMPETITOR MAKE ID FOR A GIVEN COMPETITOR MAKE
                  SELECT ID
                  INTO V_COMPETITOR_MAKE_ID
                  FROM COMPETITOR_MAKE
                  WHERE MAKE             = EACH_REC.COMPETITOR_MAKE
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  --INSERT INTO MARKETING INFORMATION TABLE
                  INSERT
                  INTO MARKETING_INFORMATION
                    (
                      ID,
                      CUSTOMER_FIRST_TIME_OWNER,
                      MONTHS,
                      VERSION,
                      YEARS,
                      MARKET_TYPE,
                      COMPETITION_TYPE,
                      TRANSACTION_TYPE,
                      IF_PREVIOUS_OWNER,
                      COMPETITOR_MODEL,
                      COMPETITOR_MAKE,
                      D_CREATED_ON,
                      D_INTERNAL_COMMENTS,
                      D_ACTIVE,
                      D_UPDATED_ON,
                      D_LAST_UPDATED_BY,
                      D_CREATED_TIME,
                      D_UPDATED_TIME
                    )
                    VALUES
                    (
                      V_MARK_INFO_ID,
                      0,
                      EACH_REC.NUMBER_OF_MONTHS,
                      1,
                      EACH_REC.NUMBER_OF_YEARS,
                      V_MARKET_ID,
                      V_COMPETITION_TYPE_ID,
                      V_TRANSACTION_TYPE_ID,
                      NULL,
                      V_COMPETITOR_MODEL_ID,
                      V_COMPETITOR_MAKE_ID,
                      SYSDATE,
                      EACH_REC.BUSINESS_UNIT_INFO
                      || '-Upload',
                      1,
                      SYSDATE,
                      V_LAST_UPDATED_BY,
                      CURRENT_TIMESTAMP,
                      CURRENT_TIMESTAMP
                    );
                END;
              END IF;
              --FETCH OEM ID FROM LOVs
              IF EACH_REC.OEM IS NOT NULL THEN
                SELECT ID
                INTO V_OEM_ID
                FROM LIST_OF_VALUES
                WHERE TYPE             = 'OEM'
                AND DESCRIPTION        = EACH_REC.OEM
                AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
              END IF;
              --GET THE SEQUENCE FOR INVENTORY TRANSACTION
              SELECT INVENTORY_TRANSACTION_SEQ.NEXTVAL
              INTO V_TRANS_ID
              FROM DUAL;
              --GET THE TRANSACTION TYPE FROM THE INVENTORY TRANSACTION TYPE
              V_INV_TYPE := 'RETAIL';
              SELECT ID
              INTO V_TRANS_TYPE
              FROM INVENTORY_TRANSACTION_TYPE
              WHERE TRNX_TYPE_KEY = DECODE(V_INV_TYPE, 'STOCK', 'IB','RETAIL', 'DR');
              V_SELLER_ID        := V_DEALER_ID;
              SELECT CUST.ID
              INTO V_BUYER_ID
              FROM CUSTOMER CUST,
                CUSTOMER_ADDRESSES CUSTADDR,
                ADDRESS_BOOK ADDRBK,
                ADDRESS_BOOK_ADDRESS_MAPPING ADDRBKMPNG,
                SERVICE_PROVIDER SP
              WHERE CUST.ID                  = CUSTADDR.CUSTOMER
              AND CUSTADDR.ADDRESSES         = ADDRBKMPNG.ADDRESS_ID
              AND ADDRBKMPNG.ADDRESS_BOOK_ID = ADDRBK.ID
              AND ADDRBK.BELONGS_TO          = SP.ID
              AND ADDRBK.TYPE                = upper(EACH_REC.CUSTOMER_TYPE)
              AND CUST.CUSTOMER_ID           = EACH_REC.CUSTOMER_NUMBER
              AND SP.SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
              V_INVOICE_DATE                := NULL;
              V_INVOICE_NUM                 := NULL;
              --END IF;
              --END;
              --INSERT THE RECORD INTO INVENTORY TRANSACTION
              -- TODO : ASK IF RECORD TO BE INSERTED INTO INVENTORY_TRANSACTION
              --INSERT THE RECORD INTO INVENTORY TRANSACTION
              INSERT
              INTO INVENTORY_TRANSACTION
                (
                  ID,
                  INVOICE_DATE,
                  INVOICE_NUMBER,
                  TRANSACTION_DATE,
                  VERSION,
                  BUYER,
                  TRANSACTED_ITEM,
                  SELLER,
                  INV_TRANSACTION_TYPE,
                  OWNER_SHIP,
                  TRANSACTION_ORDER,
                  STATUS,
                  D_CREATED_ON,
                  D_INTERNAL_COMMENTS,
                  D_ACTIVE,
                  D_UPDATED_ON,
                  D_LAST_UPDATED_BY,
                  D_CREATED_TIME,
                  D_UPDATED_TIME
                )
                VALUES
                (
                  V_TRANS_ID,
                  V_INVOICE_DATE,
                  V_INVOICE_NUM,
                  sysdate,
                  1,
                  V_BUYER_ID,
                  V_SERIAL_ID,
                  V_SELLER_ID,
                  V_TRANS_TYPE,
                  V_SELLER_ID,
                  2,
                  'ACTIVE',
                  SYSDATE,
                  EACH_REC.BUSINESS_UNIT_INFO
                  || '-Upload',
                  1,
                  SYSDATE,
                  V_LAST_UPDATED_BY,
                  CURRENT_TIMESTAMP,
                  CURRENT_TIMESTAMP
                );
              --ADDRESS OF THE CUSTOMER
              --TODO : CHECK : SHOULD IT BE FROM CUSTOMER_ADDRESSES TABLE?
              SELECT ADDRESS
              INTO V_CUST_ADD_ID
              FROM PARTY
              WHERE ID = V_BUYER_ID;
              SELECT ADDRESSFORTRANS_SEQ.NEXTVAL INTO V_ADDRESS_TRANS_ID FROM DUAL;
              --INSERT THE RECORD IN ADDRESS TO TRANSFER TABLE
              INSERT
              INTO ADDRESS_FOR_TRANSFER
                (SELECT V_ADDRESS_TRANS_ID,
                    ADDRESS_LINE1,
                    CITY,
                    CONTACT_PERSON_NAME,
                    COUNTRY,
                    EMAIL,
                    PHONE,
                    SECONDARY_PHONE,
                    STATE,
                    'BILLING', --TODO : CHECK THE VALUE TO BE PASSED
                    0,
                    ZIP_CODE,
                    SYSDATE,
                    EACH_REC.BUSINESS_UNIT_INFO
                    || '-Upload',
                    SYSDATE,
                    V_LAST_UPDATED_BY,
                    CURRENT_TIMESTAMP,
                    CURRENT_TIMESTAMP,
                    1,
                    ADDRESS_LINE2,
                    ADDRESS_LINE3
                  FROM ADDRESS
                  WHERE ID = V_CUST_ADD_ID
                );
              dbms_output.put_line
              (
                'V_OPERATOR_ID ' || V_OPERATOR_ID
              )
              ;
              --GET WARRANTY_TASK_INSTANCE ID  ID
              SELECT WARRANTY_TASK_INSTANCE_SEQ.NEXTVAL
              INTO V_WARANTY_TASK_INSTANCE_ID
              FROM DUAL;
              --GET  MULTIDRETRNUMBER, WHICH IS NEXT SEQ VALUE OF  WARRANTY_TASK_INSTANCE_SEQ
              SELECT WARRANTY_MULTIDRETR_NUMBER_SEQ.NEXTVAL
              INTO V_MULTIDRETRNUMBER
              FROM DUAL;
              --GET THE WARRANTY ID
              SELECT WARRANTY_SEQ.NEXTVAL
              INTO V_WARRANTY_ID
              FROM DUAL;
              --INSERT INTO WARRANTY TABLE
              INSERT
              INTO WARRANTY
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
                  FOR_DEALER,
                  ADDRESS_FOR_TRANSFER,
                  TRANSACTION_TYPE,
                  MULTIDRETRNUMBER,
                  CUSTOMER_TYPE,
                  OPERATOR,
                  INSTALLING_DEALER,
                  OEM,
                  EQUIPMENT_VIN,
                  INSTALLATION_DATE,
                  D_CREATED_ON,
                  D_INTERNAL_COMMENTS,
                  D_ACTIVE,
                  D_UPDATED_ON,
                  D_LAST_UPDATED_BY,
                  D_CREATED_TIME,
                  D_UPDATED_TIME,
                  FLEET_NUMBER
                )
                VALUES
                (
                  V_WARRANTY_ID,
                  TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'),
                  0,
                  1,
                  V_MARK_INFO_ID,
                  V_TRANS_ID,
                  V_BUYER_ID,
                  V_SERIAL_ID,
                  0,
                  V_WARRANTY_STATUS,
                  V_SELLER_ID,
                  V_ADDRESS_TRANS_ID,
                  V_TRANS_TYPE,
                  V_MULTIDRETRNUMBER,
                  EACH_REC.CUSTOMER_TYPE,
                  V_OPERATOR_ID,
                  V_INSTALL_DEALER_ID,
                  V_OEM_ID,
                  EACH_REC.EQUIPMENT_VIN_ID,
                  TO_DATE(EACH_REC.DATE_OF_INSTALLATION,'YYYYMMDD'),
                  SYSDATE,
                  EACH_REC.BUSINESS_UNIT_INFO
                  || '-Upload',
                  1,
                  SYSDATE,
                  V_LAST_UPDATED_BY,
                  CURRENT_TIMESTAMP,
                  CURRENT_TIMESTAMP,
                  EACH_REC.TRUCK_NUMBER
                );
              dbms_output.put_line
              (
                'V_WARRANTY_ID ' || V_WARRANTY_ID
              )
              ;
              --GET WARRANTY_AUDIT_ID
              SELECT WARRANTY_AUDIT_SEQ.NEXTVAL
              INTO V_WARRANTY_AUDIT_ID
              FROM DUAL;
              --INSERT RECORDS INTO WARRANTY_AUDIT TABLE
              INSERT
              INTO WARRANTY_AUDIT
                (
                  ID,
                  FOR_WARRANTY,
                  STATUS,
                  LIST_INDEX,
                  VERSION,
                  D_CREATED_ON,
                  D_INTERNAL_COMMENTS,
                  D_ACTIVE,
                  D_UPDATED_ON,
                  D_LAST_UPDATED_BY,
                  D_CREATED_TIME,
                  D_UPDATED_TIME
                )
                VALUES
                (
                  V_WARRANTY_AUDIT_ID,
                  V_WARRANTY_ID,
                  V_WARRANTY_STATUS,
                  0,
                  1,
                  SYSDATE,
                  EACH_REC.BUSINESS_UNIT_INFO
                  || '-Upload',
                  1,
                  SYSDATE,
                  V_LAST_UPDATED_BY,
                  CURRENT_TIMESTAMP,
                  CURRENT_TIMESTAMP
                );
              -- IF MANUAL APPROVAL FLAG IS TRUE THEN FETCH ASSIGNED_TO AND STATUS VALUE SHOULD BE DRAFT ELSE SUBMITTED
              -- TODO : GET IT COMFIRMED
              --INSERT RECORDS INTO WARRANTY_TASK_INSTANCE TABLE
              INSERT
              INTO WARRANTY_TASK_INSTANCE
                (
                  ID,
                  ACTIVE,
                  STATUS,
                  VERSION,
                  ASSIGNED_TO,
                  WARRANTY_AUDIT,
                  MULTIDRETRNUMBER,
                  BUSINESS_UNIT_INFO,
                  D_CREATED_ON,
                  D_INTERNAL_COMMENTS,
                  D_ACTIVE,
                  D_UPDATED_ON,
                  D_LAST_UPDATED_BY,
                  D_CREATED_TIME,
                  D_UPDATED_TIME
                )
                VALUES
                (
                  V_WARANTY_TASK_INSTANCE_ID,
                  0,
                  V_WARRANTY_STATUS,
                  1,
                  V_ASSIGNED_TO,
                  V_WARRANTY_AUDIT_ID,
                  V_MULTIDRETRNUMBER,
                  EACH_REC.BUSINESS_UNIT_INFO,
                  SYSDATE,
                  EACH_REC.BUSINESS_UNIT_INFO
                  || '-Upload',
                  1,
                  SYSDATE,
                  V_LAST_UPDATED_BY,
                  CURRENT_TIMESTAMP,
                  CURRENT_TIMESTAMP
                );
              --INSERT RECORDS INTO WARRANTY_TASK_INCLUDED_ITEMS TABLE
              INSERT
              INTO WARRANTY_TASK_INCLUDED_ITEMS
                (
                  WARRANTY_TASK,
                  INV_ITEM
                )
                VALUES
                (
                  V_WARANTY_TASK_INSTANCE_ID,
                  V_INV_ID
                );
              --COVERAGES WILL BE KICKED OFF ONLY WHEN EITHER AUTO
              --IF V_KICK_OFF_CVRGS = TRUE
              --THEN
              --GET THE TYPE OF, PRODUCT, MODEL FROM THE ITEM TABLE
              SELECT ID,
                PRODUCT,
                MODEL
              INTO V_TYPE_OF,
                V_PRODUCT,
                V_MODEL
              FROM ITEM
              WHERE ITEM_NUMBER      = EACH_REC.ITEM_NUMBER
              AND OWNED_BY           = V_PARTY_OEM_ID
              AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
              --APPLICABLE POLICY START
              -- APPLY ALL THE ELIGIBLE POLICIES
              SELECT DECODE(SP.CERTIFIED, 0, 'N', 1, 'Y')
              INTO V_CERT_STATUS
              FROM SERVICE_PROVIDER SP,
                BU_ORG_MAPPING BOM
              WHERE SP.SERVICE_PROVIDER_NUMBER = EACH_REC.INSTALLING_DEALER_NUMBER
              AND SP.ID                        = BOM.ORG
              AND BOM.BU                       = EACH_REC.BUSINESS_UNIT_INFO;
              --CHECK IF THERE IS POLICY COVER
              OPEN ALL_ELIGIBLE_POLICY_PLANS(V_PRODUCT, V_MODEL, EACH_REC.BUSINESS_UNIT_INFO, V_REG_DATE, TO_NUMBER(V_HOURS_ON_SERV), V_CONDITION, V_CERT_STATUS, V_INSTALL_DEALER_ID);
              FETCH ALL_ELIGIBLE_POLICY_PLANS INTO V_CUR_RECORD;
              DBMS_OUTPUT.PUT_LINE(V_POLICY_COVER);
              V_POLICY_COVER := ALL_ELIGIBLE_POLICY_PLANS%ROWCOUNT;
              DBMS_OUTPUT.PUT_LINE(V_POLICY_COVER);
              CLOSE ALL_ELIGIBLE_POLICY_PLANS;
              IF V_POLICY_COVER = 0 AND EACH_REC.ADDITIONAL_APPLICABLE_POLICIES IS NOT NULL THEN
                V_POLICY_COVER := 1;
              END IF;
              FOR EACH_PLAN IN ALL_ELIGIBLE_POLICY_PLANS(V_PRODUCT, V_MODEL, EACH_REC.BUSINESS_UNIT_INFO, V_REG_DATE, TO_NUMBER(V_HOURS_ON_SERV), V_CONDITION, V_CERT_STATUS, V_INSTALL_DEALER_ID)
              LOOP
                BEGIN
                  V_POLICY_ID := 0;
                  --CALCULATE DATE INTO V_SHIP_COVERAGE_TILL_DATE, BY ADDING 'MONTHS FROM SHIPMENT'
                  SELECT (ADD_MONTHS(V_SHIP_DATE, EACH_PLAN.MONTHS_FRM_SHIPMENT)-1)
                  INTO V_SHIP_COVERAGE_TILL_DATE
                  FROM DUAL;
                  --CALCULATE DATE INTO V_COVERAGE_TILL_DATE, BY ADDING 'MONTHS FROM DELIVERY'
                  SELECT (ADD_MONTHS(V_REG_DATE, EACH_PLAN.MONTHS_FRM_DELIVERY)-1)
                  INTO V_COVERAGE_TILL_DATE
                  FROM DUAL;
                  -- V_COVERAGE_END_DATE STORES THE VALUE OF V_COVERAGE_TILL_DATE/V_SHIP_COVERAGE_TILL_DATE, WHICHEVER IS LESSER
                  IF V_SHIP_COVERAGE_TILL_DATE < V_COVERAGE_TILL_DATE THEN
                    V_COVERAGE_END_DATE       := V_SHIP_COVERAGE_TILL_DATE;
                  ELSE
                    V_COVERAGE_END_DATE := V_COVERAGE_TILL_DATE;
                  END IF;
                  --GET THE SEQUENCE FOR POLICY
                  SELECT POLICY_SEQ.NEXTVAL
                  INTO V_POLICY_ID
                  FROM DUAL;
                  --GET THE SEQUENCE FOR POLICY AUDIT
                  SELECT POLICY_AUDIT_SEQ.NEXTVAL
                  INTO V_POLICY_AUDIT_ID
                  FROM DUAL;
                  INSERT
                  INTO POLICY
                    (
                      ID,
                      AMOUNT,
                      CURRENCY,
                      POLICY_DEFINITION,
                      WARRANTY,
                      D_CREATED_ON,
                      D_INTERNAL_COMMENTS,
                      D_ACTIVE,
                      D_UPDATED_ON,
                      D_LAST_UPDATED_BY,
                      D_CREATED_TIME,
                      D_UPDATED_TIME
                    )
                    VALUES
                    (
                      V_POLICY_ID,
                      0,    --HARD CODED FOR TSA
                      'USD',--HARD CODED FOR TSA
                      EACH_PLAN.ID,
                      V_WARRANTY_ID,
                      SYSDATE,
                      EACH_REC.BUSINESS_UNIT_INFO
                      || '-Upload',
                      1,
                      SYSDATE,
                      V_LAST_UPDATED_BY,
                      CURRENT_TIMESTAMP,
                      CURRENT_TIMESTAMP
                    );
                  --INSERT THE RECORD INTO WARRANTY POLICY AUDIT
                  INSERT
                  INTO POLICY_AUDIT
                    (
                      ID,
                      STATUS,
                      FROM_DATE,
                      TILL_DATE,
                      FOR_POLICY,
                      SERVICE_HOURS_COVERED,
                      CREATED_ON,
                      COMMENTS,
                      D_CREATED_ON,
                      D_INTERNAL_COMMENTS,
                      D_ACTIVE,
                      D_UPDATED_ON,
                      D_LAST_UPDATED_BY,
                      D_CREATED_TIME,
                      D_UPDATED_TIME
                    )
                    VALUES
                    (
                      V_POLICY_AUDIT_ID,
                      'Active',
                      V_REG_DATE, -- IS IT REG_DATE OR INSTALLATION_DATE
                      V_COVERAGE_END_DATE,
                      V_POLICY_ID,
                      EACH_PLAN.SERVICE_HRS_COVERED,
                      (SYSDATE-TO_DATE(19700101,'YYYYMMDD'))*86400,
                      'Uploaded',
                      SYSDATE,
                      EACH_REC.BUSINESS_UNIT_INFO
                      || '-Upload',
                      1,
                      SYSDATE,
                      V_LAST_UPDATED_BY,
                      CURRENT_TIMESTAMP,
                      CURRENT_TIMESTAMP
                    );
                  --UPDATE THE NOTIFICATION TYPE FOR A POLICY IN EXTENDED WARRANTY NOTIFICATION.
                  UPDATE EXTENDED_WARRANTY_NOTIFICATION
                  SET NOTIFICATION_TYPE  = 'Completed' --TODO : GET IT CONFIRMED BY RAHUL
                  WHERE POLICY           = EACH_PLAN.ID
                  AND FOR_UNIT           = V_SERIAL_ID
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                END;
              END LOOP;
              -- END;
              --APPLICABLE POLICY END
              --ADDITIONAL POLICIES START
              IF EACH_REC.ADDITIONAL_APPLICABLE_POLICIES IS NOT NULL THEN
                BEGIN
                  --GET AN ARRAY OUT OF ALL THE #$# SEPARATED ITEMS
                  COMMON_UTILS.ParseAnySeperatorList(EACH_REC.ADDITIONAL_APPLICABLE_POLICIES,'#$#',V_POL_ARRAY ,V_POL_COUNT);
                  FOR I IN 1..V_POL_COUNT
                  LOOP
                    SELECT ID
                    INTO V_POLICY_DEFN_ID
                    FROM POLICY_DEFINITION
                    WHERE UPPER(CODE)      = V_POL_ARRAY(I)
                    AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
                    AND CURRENTLY_INACTIVE = 0;
                    V_POLICY_ID           := 0;
                    BEGIN
                      --CHECK WHETHER A POLICY IS ALREDY APPLIED FOR A WARRANTY
                      SELECT NVL(ID, 0)
                      INTO V_POLICY_ID
                      FROM POLICY
                      WHERE WARRANTY        = V_WARRANTY_ID
                      AND POLICY_DEFINITION = V_POLICY_DEFN_ID;
                    EXCEPTION
                    WHEN NO_DATA_FOUND THEN
                      V_POLICY_ID := 0;
                    END;
                    IF V_POLICY_ID = 0 THEN
                      BEGIN
                        SELECT MONTHS_FRM_SHIPMENT,
                          MONTHS_FRM_DELIVERY
                        INTO V_MONTHS_FRM_SHIPMENT,
                          V_MONTHS_FRM_DELIVERY
                        FROM POLICY_DEFINITION
                        WHERE CODE             = V_POL_ARRAY(I)
                        AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                        dbms_output.put_line('ship'||V_SHIP_DATE);
                        --CALCULATE DATE INTO v_ship_coverage_till_date, BY ADDING 'MONTHS FROM SHIPMENT'
                        SELECT (ADD_MONTHS(V_SHIP_DATE, V_MONTHS_FRM_SHIPMENT)-1)
                        INTO V_SHIP_COVERAGE_TILL_DATE
                        FROM DUAL;
                        dbms_output.put_line(V_REG_DATE);
                        --CALCULATE DATE INTO v_coverage_till_date, BY ADDING 'MONTHS FROM DELIVERY'
                        SELECT (ADD_MONTHS(V_REG_DATE, -- IS IT REG_DATE OR INSTALLATION_DATE
                          V_MONTHS_FRM_DELIVERY)-1)
                        INTO V_COVERAGE_TILL_DATE
                        FROM DUAL;
                        -- V_COVERAGE_END_DATE stores the value of v_coverage_till_date/v_ship_coverage_till_date, whichever is lesser
                        IF V_SHIP_COVERAGE_TILL_DATE < V_COVERAGE_TILL_DATE THEN
                          V_COVERAGE_END_DATE       := V_SHIP_COVERAGE_TILL_DATE;
                        ELSE
                          V_COVERAGE_END_DATE := V_COVERAGE_TILL_DATE;
                        END IF;
                        --GET THE SEQUENCE FOR POLICY
                        SELECT POLICY_SEQ.NEXTVAL
                        INTO V_POLICY_ID
                        FROM DUAL;
                        --GET THE SEQUENCE FOR POLICY AUDIT
                        SELECT POLICY_AUDIT_SEQ.NEXTVAL
                        INTO V_POLICY_AUDIT_ID
                        FROM DUAL;
                        INSERT
                        INTO POLICY
                          (
                            ID,
                            AMOUNT,
                            CURRENCY,
                            POLICY_DEFINITION,
                            WARRANTY,
                            D_CREATED_ON,
                            D_INTERNAL_COMMENTS,
                            D_ACTIVE,
                            D_UPDATED_ON,
                            D_LAST_UPDATED_BY,
                            D_CREATED_TIME,
                            D_UPDATED_TIME
                          )
                          VALUES
                          (
                            V_POLICY_ID,
                            0,    --HARD CODED AND HAVE TO GET THE CLARIFICATION
                            'USD',--HARD CODED AND HAVE TO GET THE CLARIFICATION
                            V_POLICY_DEFN_ID,
                            V_WARRANTY_ID,
                            SYSDATE,
                            EACH_REC.BUSINESS_UNIT_INFO
                            || '-Upload',
                            1,
                            SYSDATE,
                            V_LAST_UPDATED_BY,
                            CURRENT_TIMESTAMP,
                            CURRENT_TIMESTAMP
                          );
                        --INSERT THE RECORD INTO WARRANTY POLICY AUDIT
                        INSERT
                        INTO POLICY_AUDIT
                          (
                            ID,
                            STATUS,
                            FROM_DATE,
                            TILL_DATE,
                            FOR_POLICY,
                            CREATED_ON,
                            COMMENTS,
                            SERVICE_HOURS_COVERED,
                            D_CREATED_ON,
                            D_INTERNAL_COMMENTS,
                            D_ACTIVE,
                            D_UPDATED_ON,
                            D_LAST_UPDATED_BY,
                            D_CREATED_TIME,
                            D_UPDATED_TIME
                          )
                          VALUES
                          (
                            V_POLICY_AUDIT_ID,
                            'Active',
                            TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), --TODO : CHECK THIS WITH RAHUL
                            V_COVERAGE_END_DATE,
                            V_POLICY_ID,
                            (SYSDATE-TO_DATE(19700101,'YYYYMMDD'))*86400,
                            'Uploaded',
                            EACH_REC.HOURS_ON_MACHINE,
                            SYSDATE,
                            EACH_REC.BUSINESS_UNIT_INFO
                            || '-Upload',
                            1,
                            SYSDATE,
                            V_LAST_UPDATED_BY,
                            CURRENT_TIMESTAMP,
                            CURRENT_TIMESTAMP
                          );
                        --UPDATE THE NOTIFICATION TYPE FOR A POLICY IN EXTENDED WARRANTY NOTIFICATION.
                        UPDATE EXTENDED_WARRANTY_NOTIFICATION
                        SET NOTIFICATION_TYPE  = 'Completed' --TODO : GET IT CONFIRMED BY RAHUL
                        WHERE POLICY           = V_POLICY_DEFN_ID
                        AND FOR_UNIT           = V_SERIAL_ID
                        AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                      END;
                    END IF;
                  END LOOP;
                END;
              END IF;
              --ADDITIONAL POLICIES END
              --APPLY THE EXTENDED WARRANTY POLICIES
              FOR EACH_EXTND_POLICY IN ALL_EXTENDED_POLICIES(EACH_REC.BUSINESS_UNIT_INFO, V_SERIAL_ID)
              LOOP
                BEGIN
                  --CHECK WHETHER A POLICY IS ALREDY APPLIED FOR A WARRANTY
                  SELECT NVL(ID, 0)
                  INTO V_POLICY_ID
                  FROM POLICY
                  WHERE WARRANTY        = V_WARRANTY_ID
                  AND POLICY_DEFINITION = EACH_EXTND_POLICY.POLICY;
                  IF V_POLICY_ID        = 0 THEN
                    BEGIN
                      --START
                      SELECT MONTHS_FRM_SHIPMENT,
                        MONTHS_FRM_DELIVERY
                      INTO V_MONTHS_FRM_SHIPMENT,
                        V_MONTHS_FRM_DELIVERY
                      FROM POLICY_DEFINITION
                      WHERE ID               = EACH_EXTND_POLICY.POLICY
                      AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                      --CALCULATE DATE INTO v_ship_coverage_till_date, BY ADDING 'MONTHS FROM SHIPMENT'
                      SELECT (ADD_MONTHS(V_SHIP_DATE, V_MONTHS_FRM_SHIPMENT)-1)
                      INTO V_SHIP_COVERAGE_TILL_DATE
                      FROM DUAL;
                      --CALCULATE DATE INTO v_coverage_till_date, BY ADDING 'MONTHS FROM DELIVERY'
                      SELECT (ADD_MONTHS(V_REG_DATE, -- IS IT REG_DATE OR INSTALLATION_DATE
                        V_MONTHS_FRM_DELIVERY)-1)
                      INTO V_COVERAGE_TILL_DATE
                      FROM DUAL;
                      -- V_COVERAGE_END_DATE stores the value of v_coverage_till_date/v_ship_coverage_till_date, whichever is lesser
                      IF V_SHIP_COVERAGE_TILL_DATE < V_COVERAGE_TILL_DATE THEN
                        V_COVERAGE_END_DATE       := V_SHIP_COVERAGE_TILL_DATE;
                      ELSE
                        V_COVERAGE_END_DATE := V_COVERAGE_TILL_DATE;
                      END IF;
                      --GET THE SEQUENCE FOR POLICY
                      SELECT POLICY_SEQ.NEXTVAL
                      INTO V_POLICY_ID
                      FROM DUAL;
                      --GET THE SEQUENCE FOR POLICY AUDIT
                      SELECT POLICY_AUDIT_SEQ.NEXTVAL
                      INTO V_POLICY_AUDIT_ID
                      FROM DUAL;
                      INSERT
                      INTO POLICY
                        (
                          ID,
                          AMOUNT,
                          CURRENCY,
                          POLICY_DEFINITION,
                          WARRANTY,
                          D_CREATED_ON,
                          D_INTERNAL_COMMENTS,
                          D_ACTIVE,
                          D_UPDATED_ON,
                          D_LAST_UPDATED_BY,
                          D_CREATED_TIME,
                          D_UPDATED_TIME
                        )
                        VALUES
                        (
                          V_POLICY_ID,
                          0,    --HARD CODED AND HAVE TO GET THE CLARIFICATION
                          'USD',--HARD CODED AND HAVE TO GET THE CLARIFICATION
                          EACH_EXTND_POLICY.POLICY,
                          V_WARRANTY_ID,
                          SYSDATE,
                          EACH_REC.BUSINESS_UNIT_INFO
                          || '-Upload',
                          1,
                          SYSDATE,
                          V_LAST_UPDATED_BY,
                          CURRENT_TIMESTAMP,
                          CURRENT_TIMESTAMP
                        );
                      --INSERT THE RECORD INTO WARRANTY POLICY AUDIT
                      INSERT
                      INTO POLICY_AUDIT
                        (
                          ID,
                          STATUS,
                          FROM_DATE,
                          TILL_DATE,
                          FOR_POLICY,
                          CREATED_ON,
                          COMMENTS,
                          SERVICE_HOURS_COVERED,
                          D_CREATED_ON,
                          D_INTERNAL_COMMENTS,
                          D_ACTIVE,
                          D_UPDATED_ON,
                          D_LAST_UPDATED_BY,
                          D_CREATED_TIME,
                          D_UPDATED_TIME
                        )
                        VALUES
                        (
                          V_POLICY_AUDIT_ID,
                          'Active',
                          TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), --TODO : CHECK THIS WITH RAHUL
                          V_COVERAGE_END_DATE,
                          V_POLICY_ID,
                          (SYSDATE-TO_DATE(19700101,'YYYYMMDD'))*86400,
                          'Uploaded',
                          EACH_REC.HOURS_ON_MACHINE,
                          SYSDATE,
                          EACH_REC.BUSINESS_UNIT_INFO
                          || '-Upload',
                          1,
                          SYSDATE,
                          V_LAST_UPDATED_BY,
                          CURRENT_TIMESTAMP,
                          CURRENT_TIMESTAMP
                        );
                      --UPDATE THE NOTIFICATION TYPE FOR A POLICY IN EXTENDED WARRANTY NOTIFICATION.
                      UPDATE EXTENDED_WARRANTY_NOTIFICATION
                      SET NOTIFICATION_TYPE  = 'Completed' --TODO : GET IT CONFIRMED BY RAHUL
                      WHERE POLICY           = EACH_EXTND_POLICY.POLICY
                      AND FOR_UNIT           = V_SERIAL_ID
                      AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                      --END
                    END;
                  END IF;
                END;
              END LOOP;
              IF upper(V_ADMIN_APPRV_IND) = 'FALSE' THEN
                UPDATE INVENTORY_ITEM
                SET DELIVERY_DATE   = TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'),
                  CURRENT_OWNER     = V_SELLER_ID,
                  TYPE              = 'RETAIL',
                  VIN_NUMBER        = EACH_REC.EQUIPMENT_VIN_ID,
                  OEM               = V_OEM_ID,
                  INSTALLATION_DATE = TO_DATE(EACH_REC.DATE_OF_INSTALLATION,'YYYYMMDD'),
                  FLEET_NUMBER      = EACH_REC.TRUCK_NUMBER,
                  PENDING_WARRANTY  = 0,
                  REGISTRATION_DATE = V_REG_DATE,
                  LATEST_BUYER      = V_BUYER_ID,
                  installing_dealer = V_INSTALL_DEALER_ID,
                  WNTY_START_DATE   = TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'),
                  WNTY_END_DATE     =
                  (SELECT MAX(pa.TILL_DATE)
                  FROM policy_audit pa,
                    policy p
                  WHERE p.warranty = V_WARRANTY_ID
                  AND p.id         = pa.FOR_POLICY
                  ),
                  D_INTERNAL_COMMENTS = D_INTERNAL_COMMENTS
                  || '-Upload',
                  D_UPDATED_ON      = sysdate,
                  D_LAST_UPDATED_BY = V_LAST_UPDATED_BY ,
                  D_UPDATED_TIME    = sysdate,
                  version           = version + 1
                WHERE ID            = V_SERIAL_ID;
              ELSE
                UPDATE INVENTORY_ITEM
                SET PENDING_WARRANTY  = 1,
                  D_INTERNAL_COMMENTS = D_INTERNAL_COMMENTS
                  || '-Upload',
                  D_UPDATED_ON      = sysdate,
                  D_LAST_UPDATED_BY = V_LAST_UPDATED_BY,
                  D_UPDATED_TIME    = sysdate,
                  version           = version + 1
                WHERE ID            = V_SERIAL_ID;
              END IF ;
              UPDATE STG_WARRANTY_REGISTRATIONS
              SET UPLOAD_STATUS = 'Y',
                UPLOAD_ERROR    = NULL,
                UPLOAD_DATE     = SYSDATE -- TODO : CHECK THIS WITH RAHUL
              WHERE ID          = EACH_REC.ID;
              COMMIT;
            END IF;
          EXCEPTION
          WHEN OTHERS THEN
            ROLLBACK;
            V_UPLOAD_ERROR := SUBSTR(SQLERRM, 1, 3500);
            UPDATE STG_WARRANTY_REGISTRATIONS
            SET UPLOAD_STATUS = 'N',
              UPLOAD_ERROR    = V_UPLOAD_ERROR,
              UPLOAD_DATE     = SYSDATE
            WHERE ID          = EACH_REC.ID;
            COMMIT;
            --COMMIT;
          END;
        END IF;
      END;
    END LOOP;
  END;
/
commit
/