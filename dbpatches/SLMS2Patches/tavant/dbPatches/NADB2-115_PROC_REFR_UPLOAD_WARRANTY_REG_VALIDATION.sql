create or replace
PROCEDURE UPLOAD_WARRANTY_REG_VALIDATION
AS
  CURSOR ALL_REC
  IS
    SELECT * FROM STG_WARRANTY_REGISTRATIONS WHERE ERROR_STATUS IS NULL;
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
    V_ERROR_CODE             VARCHAR2(4000):=NULL;
    V_ALLOW_OTHER_DLRS_STOCK VARCHAR2(10);
    V_UNIT_OWNER_TYPE        VARCHAR(50);
    V_CAP_INST_DLR_DATE      VARCHAR2(10);
    V_ADD_INFO_APPLICABLE    VARCHAR2(10);
    V_COMP_PART_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_INSTALL_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_SERIAL_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_POL_ARRAY DBMS_UTILITY.UNCL_ARRAY;
    V_COMP_PART_COUNT    NUMBER := 0;
    V_COMP_INSTALL_COUNT NUMBER := 0;
    V_COMP_SERIAL_COUNT  NUMBER := 0;
    V_POL_COUNT          NUMBER;
    V_COMMIT_COUNT       NUMBER;
    V_VAR                NUMBER;
    V_FILING_PARTY       NUMBER;
    V_SHIP_DATE DATE;
    V_HOURS_ON_SERV      NUMBER(19) := 0;
    V_FILE_UPLOAD_MGT_ID NUMBER     := 0;
    V_SUCCESS_COUNT      NUMBER     := 0;
    V_ERROR_COUNT        NUMBER     := 0;
    V_COUNT              NUMBER     := 0;
    V_SERIAL_ID          NUMBER(19) := 0;
    V_CURR_OWNER_ID      NUMBER(19) := 0;
    V_COMPONENT_ID       NUMBER(19) := 0;
    V_PENDING_WR         NUMBER     := 0;
    V_BUILD_DATE DATE               := NULL;
    V_PRODUCT           NUMBER(19)            := 0;
    V_MODEL             NUMBER(19)            := 0;
    V_INSTALL_DEALER_ID NUMBER(19)            := NULL;
    V_CERT_STATUS       VARCHAR2(1)           := NULL;
    V_CONDITION         VARCHAR2(255)         :=NULL;
    V_CONTRACT_CODE     VARCHAR2(255)         :=NULL;
    V_MAINTENANCE_CONTRACT  VARCHAR2(255)     :=NULL;
    V_INDUSTRY_CODE     VARCHAR2(255)         :=NULL;
	V_BU				VARCHAR2(255) 		  :=NULL;
  BEGIN
	SELECT FUM.BUSINESS_UNIT_INFO INTO V_BU
	FROM FILE_UPLOAD_MGT FUM WHERE ID=(SELECT DISTINCT FILE_UPLOAD_MGT_ID 
		FROM STG_WARRANTY_REGISTRATIONS WHERE ERROR_STATUS IS NULL);
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR047', ';WR047'),
      ERROR_STATUS            = 'N'
    WHERE BUSINESS_UNIT_INFO IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR048', ';WR048'),
      ERROR_STATUS                                = 'N'
    WHERE INSTR(NVL(TAV.ERROR_CODE,'X'), 'WR047') = 0
    AND NOT EXISTS
      ( SELECT 1 FROM BUSINESS_UNIT WHERE NAME = TAV.BUSINESS_UNIT_INFO
      )
    AND TAV.BUSINESS_UNIT_INFO IS NOT NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE       = 'WR001',
      ERROR_STATUS       = 'N'
    WHERE DEALER_NUMBER IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR002', ';WR002'),
      ERROR_STATUS = 'N'
    WHERE NOT EXISTS
      (SELECT 1
      FROM SERVICE_PROVIDER SP,
        BU_ORG_MAPPING BOM
      WHERE SP.ID                    = BOM.ORG
      AND BOM.BU                     = TAV.BUSINESS_UNIT_INFO
      AND SP.SERVICE_PROVIDER_NUMBER = TAV.DEALER_NUMBER
      )
    AND TAV.DEALER_NUMBER IS NOT NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR003', ';WR003'),
      ERROR_STATUS       = 'N'
    WHERE CUSTOMER_TYPE IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR004', ';WR004'),
      ERROR_STATUS                      = 'N'
    WHERE upper(TAV.CUSTOMER_TYPE) NOT IN
      (SELECT upper(CFO.VALUE)
      FROM CONFIG_PARAM_OPTION CFO,
        CONFIG_VALUE CV,
        CONFIG_PARAM CP
      WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
      AND CV.CONFIG_PARAM       = CP.ID
      AND CP.NAME               = 'customersFilingDR'
      AND CV.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      )
    AND TAV.CUSTOMER_TYPE IS NOT NULL;
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR005', ';WR005'),
      ERROR_STATUS         = 'N'
    WHERE CUSTOMER_NUMBER IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR006', ';WR006'),
      ERROR_STATUS = 'N'
    WHERE NOT EXISTS
      (SELECT C.CUSTOMER_ID
      FROM CUSTOMER C
      WHERE C.CUSTOMER_ID = TAV.CUSTOMER_NUMBER
      )
    AND TAV.CUSTOMER_NUMBER IS NOT NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR007', ';WR007'),
      ERROR_STATUS = 'N'
    WHERE NOT EXISTS
      (SELECT 1
      FROM ADDRESS_BOOK AB,
        SERVICE_PROVIDER SP
      WHERE SP.SERVICE_PROVIDER_NUMBER = TAV.DEALER_NUMBER
      AND SP.ID                        = AB.BELONGS_TO
      AND upper(AB.TYPE)               = upper(TAV.CUSTOMER_TYPE)
      )
    AND TAV.DEALER_NUMBER IS NOT NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR008', ';WR008'),
      ERROR_STATUS                            = 'N'
    WHERE INSTR(NVL(ERROR_CODE,'X'), 'WR007') = 0
    AND INSTR(NVL(ERROR_CODE,'X'), 'WR004')   = 0
    AND NOT EXISTS
      (SELECT 1
      FROM CUSTOMER C,
        CUSTOMER_ADDRESSES CA,
        SERVICE_PROVIDER SP,
        ADDRESS_BOOK AB,
        ADDRESS_BOOK_ADDRESS_MAPPING ABAM
      WHERE ABAM.ADDRESS_BOOK_ID     = AB.ID
      AND AB.TYPE                    = UPPER(TAV.CUSTOMER_TYPE)
      AND AB.BELONGS_TO              = SP.ID
      AND CA.CUSTOMER                = C.ID
      AND ABAM.ADDRESS_ID           IN (CA.ADDRESSES)
      AND C.CUSTOMER_ID              = TAV.CUSTOMER_NUMBER
      AND SP.SERVICE_PROVIDER_NUMBER = TAV.DEALER_NUMBER
      );
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
    || DECODE(ERROR_CODE, NULL, 'WR010', ';WR010'),
      ERROR_STATUS     = 'N'
    WHERE ITEM_NUMBER IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR011', ';WR011'),
      ERROR_STATUS = 'N'
    WHERE NOT EXISTS
      (SELECT I.ID
      FROM ITEM I
      WHERE I.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      AND I.ITEM_TYPE            = 'MACHINE'
      AND I.OWNED_BY             = 1
      AND I.ITEM_NUMBER          = TAV.ITEM_NUMBER
      AND I.D_ACTIVE             = 1
      )
    AND ITEM_NUMBER IS NOT NULL;
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR012', ';WR012'),
      ERROR_STATUS       = 'N'
    WHERE SERIAL_NUMBER IS NULL;
    SELECT DISTINCT OU.BELONGS_TO_ORGANIZATION
    INTO V_FILING_PARTY
    FROM FILE_UPLOAD_MGT FUM,
      ORG_USER OU,
      STG_WARRANTY_REGISTRATIONS TAV
    WHERE TAV.FILE_UPLOAD_MGT_ID = FUM.ID
    AND FUM.UPLOADED_BY          = OU.ID;
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR014', ';WR014'),
      ERROR_STATUS       = 'N'
    WHERE DELIVERY_DATE IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR015', ';WR015'),
      ERROR_STATUS          = 'N'
    WHERE HOURS_ON_TRUCK IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR050', ';WR050'),
      ERROR_STATUS             = 'N'
    WHERE TAV.OPERATOR_NUMBER IS NOT NULL
    AND TAV.OPERATOR_TYPE     IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR051', ';WR051'),
      ERROR_STATUS             = 'N'
    WHERE TAV.OPERATOR_NUMBER IS NULL
    AND TAV.OPERATOR_TYPE     IS NOT NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR016', ';WR016'),
      ERROR_STATUS                    = 'N'
    WHERE TAV.OPERATOR_TYPE          IS NOT NULL
    AND upper(TAV.OPERATOR_TYPE) NOT IN
      (SELECT upper(CFO.VALUE)
      FROM CONFIG_PARAM_OPTION CFO,
        CONFIG_VALUE CV,
        CONFIG_PARAM CP
      WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
      AND CV.CONFIG_PARAM       = CP.ID
      AND CP.NAME               = 'customersFilingDR'
      AND CV.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      );
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR049', ';WR049'),
      ERROR_STATUS                            = 'N'
    WHERE INSTR(NVL(ERROR_CODE,'X'), 'WR016') = 0
    AND TAV.OPERATOR_NUMBER                  IS NOT NULL
    AND TAV.OPERATOR_TYPE                    IS NOT NULL
    AND NOT EXISTS
      (SELECT 1
      FROM CUSTOMER C,
        CUSTOMER_ADDRESSES CA,
        SERVICE_PROVIDER SP,
        ADDRESS_BOOK AB,
        ADDRESS_BOOK_ADDRESS_MAPPING ABAM
      WHERE ABAM.ADDRESS_BOOK_ID     = AB.ID
      AND upper(AB.TYPE)             = UPPER(TAV.OPERATOR_TYPE)
      AND AB.BELONGS_TO              = SP.ID
      AND CA.CUSTOMER                = C.ID
      AND ABAM.ADDRESS_ID           IN (CA.ADDRESSES)
      AND C.CUSTOMER_ID              = TAV.OPERATOR_NUMBER
      AND SP.SERVICE_PROVIDER_NUMBER = TAV.DEALER_NUMBER
      );
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR053', ';WR053'),
      ERROR_STATUS = 'N'
    WHERE TAV.OEM IS NOT NULL
    AND NOT EXISTS
      (SELECT 1
      FROM LIST_OF_VALUES
      WHERE TYPE             = 'OEM'
      AND DESCRIPTION        = TAV.OEM
      AND BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      );
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR020', ';WR020'),
      ERROR_STATUS                     = 'N'
    WHERE TAV.COMPONENT_SERIAL_NUMBER IS NOT NULL
    AND TAV.COMPONENT_PART_NUMBER     IS NULL;
	UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR059', ';WR059'),
      ERROR_STATUS           = 'N'
    WHERE TAV.SERIAL_NUMBER IS NOT NULL
    AND TAV.ITEM_NUMBER IS NOT NULL
    AND EXISTS
      (SELECT 1
      FROM INVENTORY_ITEM II,
        ITEM I
      WHERE II.SERIAL_NUMBER    = TAV.SERIAL_NUMBER
      AND II.SERIALIZED_PART    = 0
      AND II.D_ACTIVE           = 1
      AND II.OF_TYPE            = I.ID
      AND I.ITEM_NUMBER = TAV.ITEM_NUMBER
      AND II.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      AND I.BUSINESS_UNIT_INFO  = TAV.BUSINESS_UNIT_INFO
      AND II.CONDITION_TYPE     = 'SCRAP'
	  AND I.ITEM_TYPE            = 'MACHINE'
      AND I.OWNED_BY             = 1
      AND I.D_ACTIVE             = 1
      );
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR060', ';WR0060'),
      ERROR_STATUS           = 'N'
    WHERE TAV.SERIAL_NUMBER IS NOT NULL
    AND TAV.ITEM_NUMBER IS NOT NULL
    AND EXISTS
      (SELECT 1
      FROM INVENTORY_ITEM II,
        ITEM I
      WHERE II.SERIAL_NUMBER    = TAV.SERIAL_NUMBER
      AND II.SERIALIZED_PART    = 0
      AND II.D_ACTIVE           = 1
      AND II.OF_TYPE            = I.ID
      AND I.ITEM_NUMBER = TAV.ITEM_NUMBER
      AND II.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      AND I.BUSINESS_UNIT_INFO  = TAV.BUSINESS_UNIT_INFO
      AND II.TYPE               = 'RETAIL'
	  AND I.ITEM_TYPE            = 'MACHINE'
      AND I.OWNED_BY             = 1
      AND I.D_ACTIVE             = 1
      );
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR061', ';WR061'),
      ERROR_STATUS          = 'N'
    WHERE CONTRACT_CODE IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR062', ';WR062'),
      ERROR_STATUS                      = 'N'
    WHERE upper(TAV.CONTRACT_CODE) NOT IN
      (SELECT upper(CC.CONTRACT_CODE)
      FROM CONTRACT_CODE CC       
      WHERE
      CC.CONTRACT_CODE = TAV.CONTRACT_CODE
      AND CC.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      )
    AND TAV.CONTRACT_CODE IS NOT NULL;
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR063', ';WR063'),
      ERROR_STATUS          = 'N'
    WHERE MAINTENANCE_CONTRACT IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR064', ';WR064'),
      ERROR_STATUS                      = 'N'
    WHERE upper(TAV.MAINTENANCE_CONTRACT) NOT IN
      (SELECT upper(MC.MAINTENANCE_CONTRACT)
      FROM MAINTENANCE_CONTRACT MC       
      WHERE
      MC.MAINTENANCE_CONTRACT = TAV.MAINTENANCE_CONTRACT
      AND MC.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      )
    AND TAV.MAINTENANCE_CONTRACT IS NOT NULL;
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR065', ';WR065'),
      ERROR_STATUS          = 'N'
    WHERE INDUSTRY_CODE IS NULL;
    UPDATE STG_WARRANTY_REGISTRATIONS TAV
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR066', ';WR066'),
      ERROR_STATUS                      = 'N'
    WHERE upper(TAV.INDUSTRY_CODE) NOT IN
      (SELECT upper(IC.INDUSTRY_CODE)
      FROM INDUSTRY_CODE IC       
      WHERE
      IC.INDUSTRY_CODE = TAV.INDUSTRY_CODE
      AND IC.BUSINESS_UNIT_INFO = TAV.BUSINESS_UNIT_INFO
      )
    AND TAV.INDUSTRY_CODE IS NOT NULL;
    COMMIT;
    UPDATE STG_WARRANTY_REGISTRATIONS
    SET ERROR_CODE = ERROR_CODE
      || DECODE(ERROR_CODE, NULL, 'WR021', ';WR021'),
      ERROR_STATUS                   = 'N'
    WHERE COMPONENT_PART_NUMBER     IS NOT NULL
    AND COMPONENT_INSTALLATION_DATE IS NULL;
    COMMIT;
    DECLARE
    TYPE POL_TYPE
  IS
    TABLE OF VARCHAR2(4000) INDEX BY VARCHAR2(4000);
    V_POL_CODES POL_TYPE;
  BEGIN
    FOR EACH_POL IN
    (SELECT CODE
    FROM POLICY_DEFINITION
    WHERE BUSINESS_UNIT_INFO = V_BU
    )
    LOOP
      V_POL_CODES(EACH_POL.CODE) := EACH_POL.CODE;
    END LOOP;
    FOR EACH_REC IN ALL_REC
    LOOP
      BEGIN
        V_ERROR_CODE      := NULL;
        V_INSTALL_DEALER_ID := NULL;
        V_COMMIT_COUNT    := V_COMMIT_COUNT + 1;
        V_VAR             := 0;
        V_UNIT_OWNER_TYPE := 'DEALER';
        BEGIN
          SELECT UPPER(CFO.VALUE)
          INTO V_ALLOW_OTHER_DLRS_STOCK
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'allowWntyRegOnOthersStock'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_ALLOW_OTHER_DLRS_STOCK := 'FALSE';
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
          INTO V_ADD_INFO_APPLICABLE
          FROM CONFIG_PARAM_OPTION CFO,
            CONFIG_VALUE CV,
            CONFIG_PARAM CP
          WHERE CFO.ID              = CV.CONFIG_PARAM_OPTION
          AND CV.CONFIG_PARAM       = CP.ID
          AND CP.NAME               = 'additionalInformationDetailsApplicable'
          AND CV.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_ADD_INFO_APPLICABLE := 'FALSE';
        END;
        BEGIN
          SELECT II.id,
            II.PENDING_WARRANTY,
            II.SHIPMENT_DATE,
            II.BUILT_ON,
            II.CURRENT_OWNER,
            II.HOURS_ON_MACHINE,
            II.CONDITION_TYPE,
			I.PRODUCT,
			I.MODEL
          INTO V_SERIAL_ID,
            V_PENDING_WR,
            V_SHIP_DATE,
            V_BUILD_DATE,
            V_CURR_OWNER_ID,
            V_HOURS_ON_SERV,
            V_CONDITION,
			V_PRODUCT,
			V_MODEL
          FROM INVENTORY_ITEM II,
            ITEM I
          WHERE II.OF_TYPE          = I.ID
          AND II.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
          AND I.BUSINESS_UNIT_INFO  = EACH_REC.BUSINESS_UNIT_INFO
          AND II.SERIAL_NUMBER      = EACH_REC.SERIAL_NUMBER
          AND I.ITEM_NUMBER = EACH_REC.ITEM_NUMBER
          AND II.SERIALIZED_PART    = 0
          AND II.D_ACTIVE           = 1
          AND II.TYPE               = 'STOCK'
		  AND I.ITEM_TYPE            = 'MACHINE'
		  AND I.OWNED_BY             = 1
		  AND I.D_ACTIVE             = 1;
          SELECT upper(SP.SERVICE_PROVIDER_TYPE)
          INTO V_UNIT_OWNER_TYPE
          FROM SERVICE_PROVIDER SP
          WHERE SP.id = V_CURR_OWNER_ID;
        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          V_UNIT_OWNER_TYPE := 'DEALER';
          V_ERROR_CODE      := common_utils.addErrorMessage(V_ERROR_CODE, 'WR013');
        END;
        IF NOT(V_ALLOW_OTHER_DLRS_STOCK = 'TRUE' OR V_FILING_PARTY = 1 OR V_UNIT_OWNER_TYPE = 'OEM') AND V_FILING_PARTY > 1 AND V_FILING_PARTY <> V_CURR_OWNER_ID THEN
          V_ERROR_CODE                 := common_utils.addErrorMessage(V_ERROR_CODE, 'WR013');
        END IF;
        IF V_PENDING_WR = 1 THEN
          V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR009');
         END IF;
        IF V_ADD_INFO_APPLICABLE        = 'TRUE' THEN
          IF EACH_REC.TRANSACTION_TYPE IS NULL THEN
            V_ERROR_CODE               := common_utils.addErrorMessage(V_ERROR_CODE, 'WR022');
          ELSE
            BEGIN
              SELECT 1
              INTO V_VAR
              FROM TRANSACTION_TYPE TT
              WHERE UPPER(TT.TYPE) = UPPER(EACH_REC.TRANSACTION_TYPE)
              AND TT.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR023');            
            END;
          END IF;
          IF EACH_REC.MARKET_TYPE IS NULL THEN
            V_ERROR_CODE          := common_utils.addErrorMessage(V_ERROR_CODE, 'WR024');
          ELSE
            BEGIN
              SELECT 1
              INTO V_VAR
              FROM MARKET_TYPE MT
              WHERE UPPER(MT.TITLE) = UPPER(EACH_REC.MARKET_TYPE)
              AND MT.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR025');
            END;
          END IF;
          IF EACH_REC.FIRST_TIME_OWNER             IS NULL THEN
            V_ERROR_CODE                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR026');
          ELSIF upper(EACH_REC.FIRST_TIME_OWNER)   <> 'YES' AND upper(EACH_REC.FIRST_TIME_OWNER) <> 'NO' THEN
            V_ERROR_CODE                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR027');
          ELSIF upper(EACH_REC.FIRST_TIME_OWNER)    = 'NO' THEN
            V_ERROR_CODE                           := COMMON_UTILS.ADDERRORMESSAGE(V_ERROR_CODE, 'WR028');
          END IF;
          IF (upper(EACH_REC.FIRST_TIME_OWNER) = 'NO') THEN
            BEGIN
              SELECT 1
              INTO V_VAR
              FROM COMPETITION_TYPE CT
              WHERE UPPER(CT.TYPE) = UPPER(EACH_REC.COMPETITION_TYPE);
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR030');
            END;
            BEGIN
              SELECT 1
              INTO V_VAR
              FROM COMPETITOR_MAKE CM
              WHERE UPPER(CM.MAKE) = UPPER(EACH_REC.COMPETITOR_MAKE);
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR031');
            END;
            BEGIN
              SELECT 1
              INTO V_VAR
              FROM COMPETITOR_MODEL CM
              WHERE UPPER(CM.MODEL) = UPPER(EACH_REC.MODEL_NUMBER);
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR032');
            END;
          END IF; 
        END IF;
        IF EACH_REC.DELIVERY_DATE                         IS NOT NULL AND NOT (COMMON_UTILS.ISVALIDDATE(EACH_REC.DELIVERY_DATE)) THEN
          V_ERROR_CODE                                    := common_utils.addErrorMessage(V_ERROR_CODE, 'WR034');
        ELSIF TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD') >= SYSDATE THEN
          V_ERROR_CODE                                    := common_utils.addErrorMessage(V_ERROR_CODE, 'WR035');
        ELSIF TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD')  < V_BUILD_DATE THEN
          V_ERROR_CODE                                    := common_utils.addErrorMessage(V_ERROR_CODE, 'WR056');
        ELSIF TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD')  < V_SHIP_DATE THEN
          V_ERROR_CODE                                    := common_utils.addErrorMessage(V_ERROR_CODE, 'WR036');
        END IF;
        IF UPPER(V_CAP_INST_DLR_DATE)           = 'TRUE' THEN
          IF EACH_REC.INSTALLING_DEALER_NUMBER IS NULL THEN
            V_ERROR_CODE                       := common_utils.addErrorMessage(V_ERROR_CODE, 'WR017');
          ELSE
            BEGIN
              SELECT SP.id
              INTO V_INSTALL_DEALER_ID
              FROM SERVICE_PROVIDER SP,
                BU_ORG_MAPPING BOM
              WHERE SP.ID                            = BOM.ORG
              AND BOM.BU                             = EACH_REC.BUSINESS_UNIT_INFO
              AND SP.SERVICE_PROVIDER_NUMBER         = EACH_REC.INSTALLING_DEALER_NUMBER
              AND EACH_REC.INSTALLING_DEALER_NUMBER IS NOT NULL;
            EXCEPTION
            WHEN NO_DATA_FOUND THEN
              V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR018');
            END;
          END IF;
          IF EACH_REC.DATE_OF_INSTALLATION IS NULL THEN
            V_ERROR_CODE                   := common_utils.addErrorMessage(V_ERROR_CODE, 'WR019');
          ELSIF NOT (COMMON_UTILS.ISVALIDDATE(EACH_REC.DATE_OF_INSTALLATION)) THEN
            V_ERROR_CODE                                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR037');
          ELSIF TO_DATE(EACH_REC.DATE_OF_INSTALLATION, 'YYYYMMDD') >= SYSDATE THEN
            V_ERROR_CODE                                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR039');
          ELSIF TO_DATE(EACH_REC.DATE_OF_INSTALLATION, 'YYYYMMDD')  < V_BUILD_DATE THEN
            V_ERROR_CODE                                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR058');
          ELSIF TO_DATE(EACH_REC.DATE_OF_INSTALLATION, 'YYYYMMDD')  < V_SHIP_DATE THEN
            V_ERROR_CODE                                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR057');
          ELSIF EACH_REC.DELIVERY_DATE                             IS NOT NULL AND COMMON_UTILS.ISVALIDDATE(EACH_REC.DELIVERY_DATE) AND TO_DATE(EACH_REC.DATE_OF_INSTALLATION, 'YYYYMMDD') > TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD') THEN
            V_ERROR_CODE                                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR038');
          END IF;
        END IF;
        IF EACH_REC.HOURS_ON_TRUCK IS NOT NULL AND NOT(COMMON_UTILS.ISNUMBER(EACH_REC.HOURS_ON_TRUCK)) THEN
          V_ERROR_CODE               := common_utils.addErrorMessage(V_ERROR_CODE, 'WR033');
        END IF;
        IF EACH_REC.COMPONENT_PART_NUMBER IS NOT NULL THEN
          COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_PART_NUMBER,'#$#',V_COMP_PART_ARRAY ,V_COMP_PART_COUNT);
          IF EACH_REC.COMPONENT_SERIAL_NUMBER IS NOT NULL THEN
            COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_SERIAL_NUMBER,'#$#',V_COMP_SERIAL_ARRAY ,V_COMP_SERIAL_COUNT);
            IF EACH_REC.COMPONENT_INSTALLATION_DATE IS NOT NULL THEN
              COMMON_UTILS.ParseAnySeperatorList(EACH_REC.COMPONENT_INSTALLATION_DATE,'#$#',V_COMP_INSTALL_ARRAY ,V_COMP_INSTALL_COUNT);
              IF V_COMP_PART_COUNT <> V_COMP_SERIAL_COUNT THEN
                V_ERROR_CODE       := common_utils.addErrorMessage(V_ERROR_CODE, 'WR040');
              END IF;
              IF V_COMP_PART_COUNT <> V_COMP_INSTALL_COUNT THEN
                V_ERROR_CODE       := common_utils.addErrorMessage(V_ERROR_CODE, 'WR041');
              END IF;
              FOR I IN 1..V_COMP_PART_COUNT
              LOOP
                BEGIN
                  SELECT 1
                  INTO V_VAR
                  FROM ITEM
                  WHERE ITEM_TYPE        = 'PART'
                  AND ITEM_NUMBER        = V_COMP_PART_ARRAY(I)
                  AND OWNED_BY           = 1
                  AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
                EXCEPTION
                WHEN NO_DATA_FOUND THEN
                  V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR042');
                  EXIT
                WHEN INSTR(NVL(V_ERROR_CODE,'X'), 'WR042') > 0;
                END;
              END LOOP;
              IF INSTR(NVL(V_ERROR_CODE,'X'), 'WR040') = 0 AND INSTR(NVL(V_ERROR_CODE,'X'), 'WR041') = 0 AND INSTR(NVL(V_ERROR_CODE,'X'), 'WR042') = 0 THEN
                FOR N                                 IN 1..V_COMP_PART_COUNT
                LOOP
                  BEGIN
                    SELECT II.id
                    INTO V_COMPONENT_ID
                    FROM ITEM I,
                      INVENTORY_ITEM II
                    WHERE I.ITEM_TYPE        = 'PART'
                    AND I.ITEM_NUMBER        = V_COMP_PART_ARRAY(N)
                    AND I.OWNED_BY           = 1
                    AND I.BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO
                    AND II.OF_TYPE           = I.ID
                    AND II.SERIAL_NUMBER     = V_COMP_SERIAL_ARRAY(N)
                    AND II.SERIALIZED_PART   = 1
                    AND II.D_ACTIVE          = 1;
                    SELECT common_utils.addErrorMessage(V_ERROR_CODE, 'WR054')
                    INTO V_ERROR_CODE
                    FROM inventory_item_composition iic,
                      inventory_item ii
                    WHERE iic.PART_OF = ii.id
                    AND iic.PART      = V_COMPONENT_ID
                    AND iic.D_ACTIVE  = 1
                    AND ii.id        <> V_SERIAL_ID
                    AND II.D_ACTIVE   = 1;
                    EXIT
                  WHEN INSTR(NVL(V_ERROR_CODE,'X'), 'WR054') > 0;
                  EXCEPTION
                  WHEN NO_DATA_FOUND THEN
                    V_COMPONENT_ID := 0;
                  END;
                END LOOP;
              END IF;
              FOR I IN 1..V_COMP_INSTALL_COUNT
              LOOP
                IF NOT COMMON_UTILS.ISVALIDDATE(V_COMP_INSTALL_ARRAY(I)) OR TO_DATE(V_COMP_INSTALL_ARRAY(I), 'YYYYMMDD') < V_BUILD_DATE THEN
                  V_ERROR_CODE                                                                                          := common_utils.addErrorMessage(V_ERROR_CODE, 'WR043');
                  EXIT
                WHEN INSTR(NVL(V_ERROR_CODE,'X'), 'WR043') > 0;
                END IF;
              END LOOP;
            END IF;
          END IF;
        END IF;
        IF EACH_REC.NUMBER_OF_MONTHS IS NOT NULL AND NOT (COMMON_UTILS.ISNUMBER(EACH_REC.NUMBER_OF_MONTHS)) THEN
          V_ERROR_CODE               := common_utils.addErrorMessage(V_ERROR_CODE, 'WR044');
        END IF;
        IF EACH_REC.NUMBER_OF_YEARS IS NOT NULL AND NOT (COMMON_UTILS.ISNUMBER(EACH_REC.NUMBER_OF_YEARS)) THEN
          V_ERROR_CODE              := common_utils.addErrorMessage(V_ERROR_CODE, 'WR045');
        END IF;
        SELECT PRODUCT,
          MODEL
        INTO V_PRODUCT,
          V_MODEL
        FROM ITEM
        WHERE ITEM_NUMBER = EACH_REC.ITEM_NUMBER
        AND OWNED_BY      =
          (SELECT ID FROM PARTY WHERE NAME = 'OEM'
          )
        AND BUSINESS_UNIT_INFO = EACH_REC.BUSINESS_UNIT_INFO;
    IF (V_INSTALL_DEALER_ID IS NOT NULL ) 
		THEN 
        SELECT  DECODE(SP.CERTIFIED, 0, 'N', 1, 'Y')
        INTO    V_CERT_STATUS
        FROM SERVICE_PROVIDER SP,
          BU_ORG_MAPPING BOM
        WHERE SP.SERVICE_PROVIDER_NUMBER = EACH_REC.INSTALLING_DEALER_NUMBER
        AND SP.ID                        = BOM.ORG
        AND BOM.BU                       = EACH_REC.BUSINESS_UNIT_INFO;		
		END IF;
DBMS_OUTPUT.PUT_LINE(V_PRODUCT || V_MODEL || EACH_REC.BUSINESS_UNIT_INFO || TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD')|| TO_NUMBER(V_HOURS_ON_SERV) || V_CONDITION || V_CERT_STATUS|| V_INSTALL_DEALER_ID);
        BEGIN
        OPEN ALL_ELIGIBLE_POLICY_PLANS(V_PRODUCT, V_MODEL, EACH_REC.BUSINESS_UNIT_INFO, TO_DATE(EACH_REC.DELIVERY_DATE, 'YYYYMMDD'), TO_NUMBER(V_HOURS_ON_SERV), V_CONDITION, V_CERT_STATUS, V_INSTALL_DEALER_ID);

        IF ALL_ELIGIBLE_POLICY_PLANS%NOTFOUND THEN
          V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR055');
        END IF;
        CLOSE ALL_ELIGIBLE_POLICY_PLANS;
        EXCEPTION WHEN OTHERS THEN
        V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR055');
        END;

        IF EACH_REC.ADDITIONAL_APPLICABLE_POLICIES IS NOT NULL THEN
          BEGIN
            COMMON_UTILS.ParseAnySeperatorList(EACH_REC.ADDITIONAL_APPLICABLE_POLICIES,'#$#',V_POL_ARRAY ,V_POL_COUNT);
            FOR I IN 1..V_POL_COUNT
            LOOP
              IF NOT V_POL_CODES.EXISTS(V_POL_ARRAY(I)) THEN
                V_ERROR_CODE := common_utils.addErrorMessage(V_ERROR_CODE, 'WR046');
                EXIT
              WHEN INSTR(NVL(V_ERROR_CODE,'X'), 'WR046') > 0;
              END IF;
            END LOOP;
          END;
        END IF;
        IF EACH_REC.REQUEST_FOR_EXTENSION          IS NOT NULL THEN
          IF upper(EACH_REC.REQUEST_FOR_EXTENSION) <> 'YES' AND upper(EACH_REC.REQUEST_FOR_EXTENSION) <> 'NO' THEN
            V_ERROR_CODE                           := common_utils.addErrorMessage(V_ERROR_CODE, 'WR052');
          END IF;
        END IF;
        IF V_ERROR_CODE IS NULL AND EACH_REC.ERROR_CODE IS NULL THEN
          UPDATE STG_WARRANTY_REGISTRATIONS
          SET ERROR_STATUS = 'Y',
            ERROR_CODE     = NULL
          WHERE ID         = EACH_REC.ID;
        ELSE
          UPDATE STG_WARRANTY_REGISTRATIONS
          SET ERROR_STATUS = 'N',
            ERROR_CODE     = ERROR_CODE
            || DECODE (ERROR_CODE,NULL, V_ERROR_CODE,','
            || V_ERROR_CODE)
          WHERE ID = EACH_REC.ID;
        END IF;
        COMMIT;
      END;
    END LOOP;
    COMMIT;
  END;
  BEGIN
    SELECT file_upload_mgt_id
    INTO v_file_upload_mgt_id
    FROM STG_WARRANTY_REGISTRATIONS
    WHERE ROWNUM = 1;
    BEGIN
      SELECT COUNT(*)
      INTO v_success_count
      FROM STG_WARRANTY_REGISTRATIONS
      WHERE file_upload_mgt_id = v_file_upload_mgt_id
      AND ERROR_STATUS         = 'Y';
    EXCEPTION
    WHEN OTHERS THEN
      v_success_count := 0;
    END;
    BEGIN
      SELECT COUNT(*)
      INTO v_error_count
      FROM STG_WARRANTY_REGISTRATIONS
      WHERE file_upload_mgt_id = v_file_upload_mgt_id
      AND ERROR_STATUS         = 'N';
    EXCEPTION
    WHEN OTHERS THEN
      v_error_count := 0;
    END;
    SELECT COUNT(*)
    INTO v_count
    FROM STG_WARRANTY_REGISTRATIONS
    WHERE file_upload_mgt_id = v_file_upload_mgt_id;
    UPDATE file_upload_mgt
    SET success_records= v_success_count,
      error_records    = v_error_count,
      total_records    = v_count
    WHERE id           = v_file_upload_mgt_id;
  EXCEPTION
  WHEN OTHERS THEN
    v_error_code := SUBSTR(SQLERRM, 1, 4000);
    UPDATE file_upload_mgt
    SET error_message = v_error_code
    WHERE id          = v_file_upload_mgt_id;
  END;
  COMMIT;
EXCEPTION
WHEN OTHERS THEN
  DBMS_OUTPUT.PUT_LINE(DBMS_UTILITY.FORMAT_ERROR_BACKTRACE);
END UPLOAD_WARRANTY_REG_VALIDATION;