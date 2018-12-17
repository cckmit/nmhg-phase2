--Purpose    : WR Upload procedure
--Author     : Rahul Katariya
--Created On : 26/07/2010
--Impact     : WR Upload

CREATE OR REPLACE
PROCEDURE UPLOAD_WARRANTY_REG_UPLOAD
AS
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_WARRANTY_REGISTRATIONS
    WHERE ERROR_STATUS          = 'Y'
    AND NVL(UPLOAD_STATUS, 'N') = 'N';
  CURSOR ALL_ELIGIBLE_POLICY_PLANS(P_PRODUCT VARCHAR2, P_MODEL VARCHAR2, P_BU VARCHAR2, P_DEL_DATE DATE, P_HRS_ON_SERVICE NUMBER, P_CONDITION VARCHAR2, P_CERT_STATUS VARCHAR2, P_INSTALL_DEALER NUMBER)
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
    AND PD.ACTIVE_FROM                                           <= P_DEL_DATE
    AND PD.ACTIVE_TILL                                           >= P_DEL_DATE
    AND P_HRS_ON_SERVICE                                         <= PD.SERVICE_HRS_COVERED
    AND PD.BUSINESS_UNIT_INFO                                     = P_BU
    AND UPPER(PD.WARRANTY_TYPE)                                   = 'STANDARD'
    AND (DECODE(PD.CERTIFICATION_STATUS, 'NOTCERTIFIED', 'N', 'Y')= P_CERT_STATUS
    OR PD.CERTIFICATION_STATUS                                    = 'ANY')
    AND PD.ID                                                     = PFI.POLICY_DEFN
    AND PFI.FOR_ITEMCONDITION                                     = P_CONDITION
    AND PD.AVAILABILITY_OWNERSHIP_STATE                           = 1 --HARDCODED BECAUSE IT IS 1 FOR DATA MIGRATION
    AND PD.ID                                                     = DLR_FILTER.POLICY_DEFN(+)
    AND PD.D_ACTIVE                                               = 1
    AND NOT EXISTS
      (SELECT 1
      FROM POLICY_FEES
      WHERE POLICY        = PD.id
      AND is_transferable = 0
      AND amount          > 0
      );
    CURSOR ALL_EXTENDED_POLICIES(P_BUSINESS_UNIT_INFO VARCHAR2, P_SERIAL_ID NUMBER)
    IS
      SELECT POLICY
      FROM EXTENDED_WARRANTY_NOTIFICATION we
      WHERE NOTIFICATION_TYPE  <> 'Completed'
      AND WE.FOR_UNIT           = P_SERIAL_ID
      AND WE.BUSINESS_UNIT_INFO = P_BUSINESS_UNIT_INFO;
    V_UPLOAD_ERROR        VARCHAR2(4000);
    V_ADMIN_APPRV_IND     VARCHAR2(10);
    V_PERFORM_D2D         VARCHAR2(10);
    V_ALLOW_OTR_DLRS_STK  VARCHAR2(10);
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
    V_CONDITION       VARCHAR2(255);
    V_TYPE            VARCHAR2(255);
    V_OWNERSHIP_STATE VARCHAR2(255);
    V_CERT_STATUS     VARCHAR2(1);
    V_SHIP_COVERAGE_TILL_DATE DATE;
    V_COVERAGE_TILL_DATE DATE;
    V_MONTHS_FRM_DELIVERY NUMBER(19);
    V_MONTHS_FRM_SHIPMENT NUMBER(19);
    V_POLICY_DEFN_ID      NUMBER(19);
    V_POLICY_ID           NUMBER(19);
    V_POLICY_AUDIT_ID     NUMBER(19);
    V_ADDTNL_INFO_IND     VARCHAR2(10);
    V_CAP_INST_DLR_DATE   VARCHAR2(10);
    V_POL_COUNT           NUMBER(19);
    V_SERIAL_NUM_COUNT    NUMBER(19);
    V_PART_NUM_COUNT      NUMBER(19);
    V_INSTALL_DATE_COUNT  NUMBER(19);
    V_END_CUST_ID         NUMBER(19);
    V_ASSIGNED_TO         NUMBER(19);
    V_WARRANTY_STATUS     VARCHAR2(255);
    V_ASSIGN_COUNT        NUMBER(19);
    V_TRANS_ID            NUMBER(19);
    V_DEALER_ID           NUMBER(19);
    V_LAST_UPDATED_BY     NUMBER(19);
    V_TRANSACTION_ORDER   NUMBER;
    V_CURR_OWNER_TYPE     VARCHAR(50);
    V_INVOICE_DATE DATE;
    V_INVOICE_NUM              VARCHAR2(255);
    V_WARANTY_TASK_INSTANCE_ID NUMBER(19);
    V_COVERAGE_END_DATE DATE;
    V_COMP_INSTALL_DATE_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_SERIAL_NUMBER_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_PART_NUMBER_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_POL_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_CUR_RECORD ALL_ELIGIBLE_POLICY_PLANS%ROWTYPE;
    V_IS_RED_CVG        VARCHAR(20);
    V_IS_REQ_EXTN       VARCHAR(20);
    V_WNTY_EXTN_REQ_ID  NUMBER(19);
    V_MAX_LIST_INDEX    NUMBER;
    V_OPR_ADDR_TRANS_ID NUMBER(19);
    V_OPR_ADDR_ID       NUMBER(19);
  BEGIN
    SELECT ID INTO V_PARTY_OEM_ID FROM PARTY WHERE NAME = 'OEM';
    FOR EACH_REC IN ALL_REC
    LOOP
      BEGIN
        V_UPLOAD_ERROR             := NULL;
        V_TYPE_OF                  := 0;
        V_PRODUCT                  := NULL;
        V_MODEL                    := NULL;
        V_WARRANTY_ID              := 0;
        V_MARK_INFO_ID             := NULL;
        V_SALESMAN_ID              := 0;
        V_MARKET_ID                := NULL;
        V_COMPETITION_TYPE_ID      := NULL;
        V_TRANSACTION_TYPE_ID      := NULL;
        V_COMPETITOR_MODEL_ID      := NULL;
        V_COMPETITOR_MAKE_ID       := NULL;
        V_TRANS_TYPE               := NULL;
        V_SELLER_ID                := 0;
        V_BUYER_ID                 := 0;
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
        V_CONDITION                :=NULL;
        V_TYPE                     :=NULL;
        V_OWNERSHIP_STATE          :=NULL;
        V_CERT_STATUS              := NULL;
        V_SHIP_COVERAGE_TILL_DATE  := NULL;
        V_COVERAGE_TILL_DATE       := NULL;
        V_MONTHS_FRM_DELIVERY      := 0;
        V_MONTHS_FRM_SHIPMENT      := 0;
        V_POLICY_DEFN_ID           := 0;
        V_POLICY_ID                := 0;
        V_POLICY_AUDIT_ID          := 0;
        V_END_CUST_ID              := 0;
        V_ASSIGN_COUNT             := 0;
        V_TRANS_ID                 := NULL;
        V_DEALER_ID                := 0;
        V_SERIAL_NUM_COUNT         := 0;
        V_INVOICE_DATE             := NULL;
        V_INVOICE_NUM              := NULL;
        V_WARANTY_TASK_INSTANCE_ID := NULL;
        V_COVERAGE_END_DATE        := NULL;
        V_CURR_OWNER_TYPE          := NULL;
        V_ASSIGNED_TO              := NULL;
        V_IS_RED_CVG               := 'FALSE';
        V_IS_REQ_EXTN              := 'FALSE';
        V_MAX_LIST_INDEX           := 0;
        V_OPR_ADDR_ID              := NULL;
        V_OPR_ADDR_TRANS_ID        := NULL;
        SELECT UPLOADED_BY
        INTO V_LAST_UPDATED_BY
        FROM file_upload_mgt
        WHERE id = EACH_REC.FILE_UPLOAD_MGT_ID;
        BEGIN
          SELECT UPPER(CFO.VALUE)
          INTO V_ALLOW_OTR_DLRS_STK
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'allowWntyRegOnOthersStock'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_ALLOW_OTR_DLRS_STK := 'FALSE';
        END;
        BEGIN
          SELECT UPPER(CFO.VALUE)
          INTO V_CAP_INST_DLR_DATE
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'enableDealerAndInstallationDate'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_CAP_INST_DLR_DATE := 'FALSE';
        END;
        BEGIN
          SELECT UPPER(CFO.VALUE)
          INTO V_ADDTNL_INFO_IND
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'additionalInformationDetailsApplicable'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_ADDTNL_INFO_IND := 'FALSE';
        END;
        BEGIN
          SELECT UPPER(CFO.VALUE)
          INTO V_ADMIN_APPRV_IND
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'manualApprovalFlowForDR'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_ADMIN_APPRV_IND := 'FALSE';
        END;
        BEGIN
          SELECT UPPER(CFO.VALUE)
          INTO V_PERFORM_D2D
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'performD2DOnWR'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_PERFORM_D2D := 'FALSE';
        END;
        IF UPPER(V_ADMIN_APPRV_IND) = 'TRUE' THEN
          V_WARRANTY_STATUS        := 'SUBMITTED';
        ELSE
          V_WARRANTY_STATUS := 'ACCEPTED';
        END IF;
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
        AND UPPER(ADDRBK.TYPE)         = UPPER(EACH_REC.CUSTOMER_TYPE)
        AND CUST.CUSTOMER_ID           = EACH_REC.CUSTOMER_NUMBER
        AND SP.SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
        SELECT ID
        INTO V_INSTALL_DEALER_ID
        FROM SERVICE_PROVIDER
        WHERE SERVICE_PROVIDER_NUMBER = EACH_REC.INSTALLING_DEALER_NUMBER;
        SELECT ID
        INTO V_DEALER_ID
        FROM SERVICE_PROVIDER
        WHERE SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
        IF (EACH_REC.OPERATOR_NUMBER IS NOT NULL) THEN
          BEGIN
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
            AND UPPER(ADDRBK.TYPE)         = UPPER(EACH_REC.OPERATOR_TYPE)
            AND CUST.CUSTOMER_ID           = EACH_REC.OPERATOR_NUMBER
            AND SP.SERVICE_PROVIDER_NUMBER = EACH_REC.DEALER_NUMBER;
          EXCEPTION
          WHEN NO_DATA_FOUND THEN
            V_OPERATOR_ID := NULL;
          END;
        END IF;
        IF V_END_CUST_ID > 0 THEN
          BEGIN
            IF (EACH_REC.COMPONENT_SERIAL_NUMBER IS NOT NULL AND EACH_REC.COMPONENT_PART_NUMBER IS NOT NULL AND EACH_REC.COMPONENT_INSTALLATION_DATE IS NOT NULL) THEN
              COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_SERIAL_NUMBER,'#$#',V_COMP_SERIAL_NUMBER_ARRAY , V_SERIAL_NUM_COUNT);
              COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_PART_NUMBER,'#$#',V_COMP_PART_NUMBER_ARRAY , V_PART_NUM_COUNT);
              COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_INSTALLATION_DATE,'#$#',V_COMP_INSTALL_DATE_ARRAY , V_INSTALL_DATE_COUNT);
            END IF;
            --            Load balancing is not needed as inbox is shared
            --            IF UPPER(V_ADMIN_APPRV_IND) = 'TRUE' THEN
            --              BEGIN
            --                SELECT U.ID,
            --                  COUNT(WT.ID) AS ASSIGNED_CNT
            --                INTO V_ASSIGNED_TO,
            --                  V_ASSIGN_COUNT
            --                FROM ORG_USER U,
            --                  USER_ROLES UR,
            --                  USER_BU_AVAILABILITY UA,
            --                  WARRANTY_TASK_INSTANCE WT
            --                WHERE U.ID   = UR.ORG_USER
            --                AND U.ID     = UA.ORG_USER
            --                AND UR.ROLES = UA.ROLE
            --                AND U.ID     = WT.ASSIGNED_TO (+)
            --                AND UR.ROLES =
            --                  (SELECT ID FROM ROLE WHERE NAME = 'processor'
            --                  )
            --                AND UA.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
            --                AND ROWNUM               <= 1
            --                GROUP BY U.ID
            --                ORDER BY ASSIGNED_CNT;
            --              END;
            --            END IF;
            SELECT II.ID,
              II.HOURS_ON_MACHINE,
              BUILT_ON,
              SHIPMENT_DATE,
              CONDITION_TYPE,
              TYPE,
              OWNERSHIP_STATE,
              INSTALLATION_DATE,
              II.CURRENT_OWNER
            INTO V_SERIAL_ID,
              V_HOURS_ON_SERV,
              V_BUILT_ON,
              V_SHIP_DATE,
              V_CONDITION,
              V_TYPE,
              V_OWNERSHIP_STATE,
              V_INSTALL_DATE,
              V_SELLER_ID
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
            SELECT upper(SP.SERVICE_PROVIDER_TYPE)
            INTO V_CURR_OWNER_TYPE
            FROM SERVICE_PROVIDER SP
            WHERE SP.ID    = V_SELLER_ID;
            IF V_SERIAL_ID > 0 THEN
              FOR I       IN 1..V_SERIAL_NUM_COUNT
              LOOP
                BEGIN
                  SELECT ID
                  INTO V_OF_TYPE_ID
                  FROM ITEM
                  WHERE ITEM_NUMBER      = V_COMP_PART_NUMBER_ARRAY(I)
                  AND ITEM_TYPE          = 'PART'
                  AND OWNED_BY           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
                  AND ROWNUM             = 1;
                  SELECT ID
                  INTO V_INV_ID
                  FROM INVENTORY_ITEM
                  WHERE SERIAL_NUMBER    = V_COMP_SERIAL_NUMBER_ARRAY(I)
                  AND OF_TYPE            = V_OF_TYPE_ID
                  AND serialized_part    = 1
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  SELECT INVENTORY_ITEM_SEQ.NEXTVAL INTO V_INV_ID FROM DUAL;
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
                      'UNITREGISTRATION'
                    );
                  SELECT SEQ_INVENTORYITEMCOMPOSITION.NEXTVAL INTO V_INV_ITEM_COMP_ID FROM DUAL;
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
                  SELECT MARKETING_INFORMATION_SEQ.NEXTVAL INTO V_MARK_INFO_ID FROM DUAL;
                  SELECT ID
                  INTO V_MARKET_ID
                  FROM MARKET_TYPE
                  WHERE TITLE            = EACH_REC.MARKET_TYPE
                  AND D_ACTIVE           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  SELECT ID
                  INTO V_TRANSACTION_TYPE_ID
                  FROM TRANSACTION_TYPE
                  WHERE TYPE                           = EACH_REC.TRANSACTION_TYPE
                  AND D_ACTIVE                         = 1
                  AND BUSINESS_UNIT_INFO               = EACH_REC.BUSINESS_UNIT_INFO;
                  IF (upper(EACH_REC.FIRST_TIME_OWNER) = 'NO') THEN
                    SELECT ID
                    INTO V_COMPETITION_TYPE_ID
                    FROM COMPETITION_TYPE
                    WHERE TYPE             = EACH_REC.COMPETITION_TYPE
                    AND D_ACTIVE           = 1
                    AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                    SELECT ID
                    INTO V_COMPETITOR_MODEL_ID
                    FROM COMPETITOR_MODEL
                    WHERE MODEL            = EACH_REC.MODEL_NUMBER
                    AND D_ACTIVE           = 1
                    AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                    SELECT ID
                    INTO V_COMPETITOR_MAKE_ID
                    FROM COMPETITOR_MAKE
                    WHERE MAKE             = EACH_REC.COMPETITOR_MAKE
                    AND D_ACTIVE           = 1
                    AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                  END IF;
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
                      DECODE(upper(EACH_REC.FIRST_TIME_OWNER), 'YES', 1, 'NO', 0),
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
              IF EACH_REC.OEM IS NOT NULL THEN
                SELECT ID
                INTO V_OEM_ID
                FROM LIST_OF_VALUES
                WHERE TYPE             = 'OEM'
                AND DESCRIPTION        = EACH_REC.OEM
                AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
              END IF;
              V_INV_TYPE := 'RETAIL';
              SELECT ID
              INTO V_TRANS_TYPE
              FROM INVENTORY_TRANSACTION_TYPE
              WHERE TRNX_TYPE_KEY = DECODE(V_INV_TYPE, 'STOCK', 'IB','RETAIL', 'DR');
              V_INVOICE_DATE     := NULL;
              V_INVOICE_NUM      := NULL;
              SELECT MAX(transaction_order)
              INTO V_TRANSACTION_ORDER
              FROM inventory_transaction it
              WHERE it.transacted_item = V_SERIAL_ID;
              -- Create D2D transaction
              IF upper(V_PERFORM_D2D) = 'TRUE' AND V_SELLER_ID <> V_DEALER_ID AND (upper(V_ALLOW_OTR_DLRS_STK) = 'TRUE' OR V_CURR_OWNER_TYPE = 'OEM') THEN
                V_TRANSACTION_ORDER  := V_TRANSACTION_ORDER + 1;
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
                    INVENTORY_TRANSACTION_SEQ.NEXTVAL,
                    V_INVOICE_DATE,
                    V_INVOICE_NUM,
                    sysdate,
                    1,
                    V_DEALER_ID,
                    V_SERIAL_ID,
                    V_DEALER_ID,
                    10,
                    V_SELLER_ID,
                    V_TRANSACTION_ORDER,
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
              END IF;
              V_SELLER_ID := V_DEALER_ID;
              V_BUYER_ID  := V_END_CUST_ID;
              IF upper
                (
                  V_ADMIN_APPRV_IND
                )
                = 'FALSE' THEN
                SELECT INVENTORY_TRANSACTION_SEQ.NEXTVAL INTO V_TRANS_ID FROM DUAL;
                V_TRANSACTION_ORDER := V_TRANSACTION_ORDER + 1;
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
                    V_TRANSACTION_ORDER,
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
              END IF;
              SELECT ADDRESS INTO V_CUST_ADD_ID FROM PARTY WHERE ID = V_BUYER_ID;
              SELECT ADDRESSFORTRANS_SEQ.NEXTVAL INTO V_ADDRESS_TRANS_ID FROM DUAL;
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
                    'BILLING',
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
              IF V_OPERATOR_ID IS NOT NULL THEN
                SELECT ADDRESS INTO V_OPR_ADDR_ID FROM PARTY WHERE ID = V_OPERATOR_ID;
                SELECT ADDRESSFORTRANS_SEQ.NEXTVAL INTO V_OPR_ADDR_TRANS_ID FROM DUAL;
                INSERT
                INTO ADDRESS_FOR_TRANSFER
                  (SELECT V_OPR_ADDR_TRANS_ID,
                      ADDRESS_LINE1,
                      CITY,
                      CONTACT_PERSON_NAME,
                      COUNTRY,
                      EMAIL,
                      PHONE,
                      SECONDARY_PHONE,
                      STATE,
                      'SHIPPING',
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
                    WHERE ID = V_OPR_ADDR_ID
                  );
              END IF;
              SELECT WARRANTY_TASK_INSTANCE_SEQ.NEXTVAL
              INTO V_WARANTY_TASK_INSTANCE_ID
              FROM DUAL;
              SELECT WARRANTY_MULTIDRETR_NUMBER_SEQ.NEXTVAL
              INTO V_MULTIDRETRNUMBER
              FROM DUAL;
              BEGIN
                SELECT NVL(MAX(LIST_INDEX), -1) + 1
                INTO V_MAX_LIST_INDEX
                FROM warranty
                WHERE for_item = V_SERIAL_ID;
              EXCEPTION
              WHEN NO_DATA_FOUND THEN
                V_MAX_LIST_INDEX := 0;
              END;
              SELECT WARRANTY_SEQ.NEXTVAL INTO V_WARRANTY_ID FROM DUAL;
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
                  FLEET_NUMBER,
                  FILED_BY,
                  OPERATOR_ADDRESS_FOR_TRANSFER
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
                  V_MAX_LIST_INDEX,
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
                  EACH_REC.TRUCK_NUMBER,
                  V_LAST_UPDATED_BY,
                  V_OPR_ADDR_TRANS_ID
                );
              SELECT WARRANTY_AUDIT_SEQ.NEXTVAL INTO V_WARRANTY_AUDIT_ID FROM DUAL;
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
                  1,
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
              INSERT
              INTO WARRANTY_TASK_INCLUDED_ITEMS
                (
                  WARRANTY_TASK,
                  INV_ITEM
                )
                VALUES
                (
                  V_WARANTY_TASK_INSTANCE_ID,
                  V_SERIAL_ID
                );
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
              SELECT DECODE(SP.CERTIFIED, 0, 'N', 1, 'Y')
              INTO V_CERT_STATUS
              FROM SERVICE_PROVIDER SP,
                BU_ORG_MAPPING BOM
              WHERE SP.SERVICE_PROVIDER_NUMBER = EACH_REC.INSTALLING_DEALER_NUMBER
              AND SP.ID                        = BOM.ORG
              AND BOM.BU                       = EACH_REC.BUSINESS_UNIT_INFO;
              OPEN ALL_ELIGIBLE_POLICY_PLANS(V_PRODUCT, V_MODEL, EACH_REC.BUSINESS_UNIT_INFO, TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), TO_NUMBER(V_HOURS_ON_SERV), V_CONDITION, V_CERT_STATUS, V_INSTALL_DEALER_ID);
              FETCH ALL_ELIGIBLE_POLICY_PLANS INTO V_CUR_RECORD;
              CLOSE ALL_ELIGIBLE_POLICY_PLANS;
              FOR EACH_PLAN IN ALL_ELIGIBLE_POLICY_PLANS(V_PRODUCT, V_MODEL, EACH_REC.BUSINESS_UNIT_INFO, TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), TO_NUMBER(V_HOURS_ON_SERV), V_CONDITION, V_CERT_STATUS, V_INSTALL_DEALER_ID)
              LOOP
                UPLOAD_WARRANTY_COVERAGE(EACH_PLAN.ID, V_WARRANTY_ID, V_SERIAL_ID, V_SHIP_DATE, TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), EACH_REC.BUSINESS_UNIT_INFO, V_LAST_UPDATED_BY, NULL, V_IS_RED_CVG);
                IF V_IS_RED_CVG  = 'TRUE' THEN
                  V_IS_REQ_EXTN := 'TRUE';
                END IF;
              END LOOP;
              IF EACH_REC.ADDITIONAL_APPLICABLE_POLICIES IS NOT NULL THEN
                BEGIN
                  COMMON_UTILS.ParseAnySeperatorList(EACH_REC.ADDITIONAL_APPLICABLE_POLICIES,'#$#',V_POL_ARRAY ,V_POL_COUNT);
                  FOR I IN 1..V_POL_COUNT
                  LOOP
                    SELECT ID
                    INTO V_POLICY_DEFN_ID
                    FROM POLICY_DEFINITION
                    WHERE UPPER(CODE)      = UPPER(V_POL_ARRAY(I))
                    AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
                    AND CURRENTLY_INACTIVE = 0
                    AND TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD') BETWEEN ACTIVE_FROM AND ACTIVE_TILL;
                    UPLOAD_WARRANTY_COVERAGE(V_POLICY_DEFN_ID, V_WARRANTY_ID, V_SERIAL_ID, V_SHIP_DATE, TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), EACH_REC.BUSINESS_UNIT_INFO, V_LAST_UPDATED_BY, NULL, V_IS_RED_CVG);
                    IF V_IS_RED_CVG  = 'TRUE' THEN
                      V_IS_REQ_EXTN := 'TRUE';
                    END IF;
                  END LOOP;
                END;
              END IF;
              FOR EACH_EXTND_POLICY IN ALL_EXTENDED_POLICIES(EACH_REC.BUSINESS_UNIT_INFO, V_SERIAL_ID)
              LOOP
                UPLOAD_WARRANTY_COVERAGE(EACH_EXTND_POLICY.POLICY, V_WARRANTY_ID, V_SERIAL_ID, V_SHIP_DATE, TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'), EACH_REC.BUSINESS_UNIT_INFO, V_LAST_UPDATED_BY, NULL, V_IS_RED_CVG);
                IF V_IS_RED_CVG  = 'TRUE' THEN
                  V_IS_REQ_EXTN := 'TRUE';
                END IF;
              END LOOP;
              IF upper ( V_ADMIN_APPRV_IND ) = 'FALSE' THEN
                IF V_IS_REQ_EXTN             = 'TRUE' THEN
                  BEGIN
                    SELECT REQUEST_WNTY_CVG_SEQ.NEXTVAL INTO V_WNTY_EXTN_REQ_ID FROM dual;
                    INSERT
                    INTO request_wnty_cvg
                      (
                        ID,
                        INVENTORY_ITEM,
                        STATUS,
                        D_ACTIVE,
                        BUSINESS_UNIT_INFO,
                        ORDER_NUMBER,
                        D_INTERNAL_COMMENTS,
                        REQUESTED_BY,
                        UPDATED_ON_DATE
                      )
                      VALUES
                      (
                        V_WNTY_EXTN_REQ_ID,
                        V_SERIAL_ID,
                        DECODE(NVL(UPPER(EACH_REC.REQUEST_FOR_EXTENSION), 'NO'), 'YES','SUBMITTED', 'NO', 'EXTENSION_NOT_REQUESTED'),
                        1,
                        EACH_REC.BUSINESS_UNIT_INFO,
                        NULL,
                        'Uploaded',
                        V_SELLER_ID,
                        sysdate
                      );
                    INSERT
                    INTO request_wnty_cvg_audit
                      (
                        ID,
                        REQUEST_WNTY_CVG,
                        COMMENTS,
                        STATUS,
                        ASSIGNED_TO,
                        ASSIGNED_BY,
                        D_CREATED_ON,
                        D_UPDATED_ON,
                        D_CREATED_TIME,
                        D_UPDATED_TIME,
                        D_INTERNAL_COMMENTS,
                        D_LAST_UPDATED_BY,
                        D_ACTIVE
                      )
                      VALUES
                      (
                        REQUEST_WNTY_CVG_AUDIT_SEQ.nextval,
                        V_WNTY_EXTN_REQ_ID,
                        'INITIAL',
                        'WAITING_FOR_YOUR_RESPONSE',
                        V_LAST_UPDATED_BY,
                        NULL,
                        sysdate,
                        sysdate,
                        CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP,
                        'Uploaded',
                        V_LAST_UPDATED_BY,
                        1
                      );
                    INSERT
                    INTO request_wnty_cvg_audit
                      (
                        ID,
                        REQUEST_WNTY_CVG,
                        COMMENTS,
                        STATUS,
                        ASSIGNED_TO,
                        ASSIGNED_BY,
                        D_CREATED_ON,
                        D_UPDATED_ON,
                        D_CREATED_TIME,
                        D_UPDATED_TIME,
                        D_INTERNAL_COMMENTS,
                        D_LAST_UPDATED_BY,
                        D_ACTIVE
                      )
                      VALUES
                      (
                        REQUEST_WNTY_CVG_AUDIT_SEQ.nextval,
                        V_WNTY_EXTN_REQ_ID,
                        'Uploaded',
                        DECODE(NVL(UPPER(EACH_REC.REQUEST_FOR_EXTENSION), 'NO'), 'YES','SUBMITTED', 'NO', 'EXTENSION_NOT_REQUESTED'),
                        NULL,
                        V_LAST_UPDATED_BY,
                        sysdate,
                        sysdate,
                        CURRENT_TIMESTAMP,
                        CURRENT_TIMESTAMP,
                        'Uploaded',
                        V_LAST_UPDATED_BY,
                        1
                      );
                  END;
                END IF;
                UPDATE INVENTORY_ITEM
                SET DELIVERY_DATE   = TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'),
                  CURRENT_OWNER     = V_SELLER_ID,
                  TYPE              = 'RETAIL',
                  VIN_NUMBER        = EACH_REC.EQUIPMENT_VIN_ID,
                  OEM               = V_OEM_ID,
                  INSTALLATION_DATE = TO_DATE(EACH_REC.DATE_OF_INSTALLATION,'YYYYMMDD'),
                  FLEET_NUMBER      = EACH_REC.TRUCK_NUMBER,
                  PENDING_WARRANTY  = 0,
                  REGISTRATION_DATE = sysdate,
                  LATEST_BUYER      = V_BUYER_ID,
                  INSTALLING_DEALER = V_INSTALL_DEALER_ID,
                  OPERATOR          = V_OPERATOR_ID,
                  WNTY_START_DATE   = TO_DATE(EACH_REC.DELIVERY_DATE,'YYYYMMDD'),
                  WNTY_END_DATE     =
                  (SELECT MAX(PA.TILL_DATE)
                  FROM POLICY_AUDIT PA,
                    POLICY P
                  WHERE P.WARRANTY = V_WARRANTY_ID
                  AND P.ID         = PA.FOR_POLICY
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
                UPLOAD_DATE     = SYSDATE
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
          END;
        END IF;
      END;
    END LOOP;
  END;
/
commit
/