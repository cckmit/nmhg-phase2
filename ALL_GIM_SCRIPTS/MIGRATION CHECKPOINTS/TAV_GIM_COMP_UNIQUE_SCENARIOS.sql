CREATE OR REPLACE PACKAGE tav_gim_comp_unique_scenarios
IS


PROCEDURE POPULATE_SN_SYNC_TRACKER
(
P_IN_JOB_SEQ_ID IN NUMBER
);
PROCEDURE POPULATE_PRODUCT_LOCALE
(
p_in_job_seq_id in number
);
PROCEDURE POPULATE_SHIPMENT
(
P_IN_JOB_SEQ_ID IN NUMBER
);
PROCEDURE POPULATE_SERVICE_PROC_DEF_COMP
(
P_IN_JOB_SEQ_ID IN NUMBER
);

PROCEDURE POPULATE_FAULT_CODE_DEF_COMPS
(
P_IN_JOB_SEQ_ID IN NUMBER
);

PROCEDURE populate_EVAL_PRECEDENCE_PROP
(
P_IN_JOB_SEQ_ID IN NUMBER
);

PROCEDURE populate_SECTIONS_IN_PYMT_DEFN
(
P_IN_JOB_SEQ_ID IN NUMBER
);

PROCEDURE populate_items_in_group
(
  p_in_job_seq_id IN NUMBER
);

PROCEDURE populate_line_item_groups
(
  p_in_job_seq_id IN NUMBER
);

PROCEDURE populate_i18nitem_text
(
  p_in_job_seq_id IN NUMBER
);

PROCEDURE populate_add_payment_info
(
  p_in_job_seq_id IN NUMBER
);
PROCEDURE populate_MODIFIERS
(
  P_IN_JOB_SEQ_ID in number
);
PROCEDURE POPULATE_WNTY_STG
(
  p_in_job_seq_id IN NUMBER
);
PROCEDURE POPULATE_ITEMMAPPING_STG
(
  p_in_job_seq_id IN NUMBER
);
PROCEDURE populate_policyaudit_stg
(
  P_IN_JOB_SEQ_ID IN NUMBER
);
PROCEDURE populate_invtxn_stg
(
  P_IN_JOB_SEQ_ID IN NUMBER
);

END TAV_GIM_COMP_UNIQUE_SCENARIOS;
/


CREATE OR REPLACE PACKAGE BODY tav_gim_comp_unique_scenarios
IS


PROCEDURE POPULATE_SN_SYNC_TRACKER
(
P_IN_JOB_SEQ_ID IN NUMBER
)
IS
BEGIN

 INSERT
  /*+ APPEND */
INTO SN_SYNC_TRACKER
  (
    ID,
    BUSINESS_ID,
    CREATE_DATE,
    RECORD,
    BODXML,
    ERROR_MESSAGE,
    ERROR_TYPE,
    NO_OF_ATTEMPTS,
    START_TIME,
    SYNC_TYPE,
    UNIQUE_ID_NAME,
    UNIQUE_ID_VALUE,
    UPDATE_DATE,
    VERSION,
    STATUS,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    BUSINESS_UNIT_INFO,
    PROCESSING_STATUS,
    IS_DELETED,
    HIDDEN_BY,
    HIDDEN_ON
  )
SELECT a.ID,
  a.BUSINESS_ID,
  a.CREATE_DATE,
  b.RECORD,
  b.BODXML,
  b.ERROR_MESSAGE,
  a.ERROR_TYPE,
  a.NO_OF_ATTEMPTS,
  a.START_TIME,
  a.SYNC_TYPE,
  a.UNIQUE_ID_NAME,
  a.UNIQUE_ID_VALUE,
  a.UPDATE_DATE,
  a.version,
  a.STATUS,
  a.D_CREATED_ON,
  a.D_INTERNAL_COMMENTS,
  a.D_UPDATED_ON,
  a.D_LAST_UPDATED_BY,
  a.BUSINESS_UNIT_INFO,
  a.PROCESSING_STATUS,
  a.IS_DELETED,
  a.HIDDEN_BY,
  a.HIDDEN_ON
from TG_SYNC_TRACKER a, SYNC_TRACKER B
WHERE A.MIGRATE_FLAG = 'Y'
and b.id = a.old_43_id ;

END POPULATE_SN_SYNC_TRACKER;

------------------------------------------------------------------

PROCEDURE POPULATE_SHIPMENT
(
P_IN_JOB_SEQ_ID IN NUMBER
)
is
BEGIN
INSERT
INTO /*+append*/ TG_SHIPMENT
  (
    ID,
    COMMENTS,
    LOGICAL_SHIPMENT,
    SHIPMENT_DATE,
    TRACKING_ID,
    VERSION,
    DESTINATION,
    SHIPPED_BY,
    CARRIER,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    TRANSIENT_ID,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    OLD_43_ID,
    MIGRATE_FLAG
  )
SELECT SN_SHIPMENT_SEQ.NEXTVAL AS ID,TAB.*
FROM
  (SELECT
    COMMENTS,
    LOGICAL_SHIPMENT,
    SHIPMENT_DATE,
    TRACKING_ID,
    VERSION,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SHIPMENT','DESTINATION',DESTINATION,'R',P_IN_JOB_SEQ_ID,'TG_LOCATION','ID','ID',ID)       AS DESTINATION,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SHIPMENT','SHIPPED_BY',SHIPPED_BY,'R',P_IN_JOB_SEQ_ID,'TG_SERVICE_PROVIDER','ID','ID',ID) AS SHIPPED_BY,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SHIPMENT','CARRIER',CARRIER,'R',P_IN_JOB_SEQ_ID,'TG_CARRIER','ID','ID',ID)                AS CARRIER,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SHIPMENT','D_LAST_UPDATED_BY',D_LAST_UPDATED_BY,'R',P_IN_JOB_SEQ_ID,'TG_ORG_USER','ID','ID',ID) AS D_LAST_UPDATED_BY,
    TRANSIENT_ID,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    ID                                        AS OLD_43_ID,
    TAV_GIM_UTILITIES.UPDATE_MIGRATE_FLAG(ID) AS MIGRATE_FLAG
  FROM SHIPMENT
  WHERE ROWNUM > 0
  ) TAB
WHERE (DESTINATION       !='-99'
OR DESTINATION         !=-99
OR DESTINATION         IS NULL )
AND (SHIPPED_BY        !='-99'
OR SHIPPED_BY          !=-99
OR SHIPPED_BY          IS NULL )
AND (CARRIER           !='-99'
OR CARRIER             !=-99
OR CARRIER             IS NULL )
AND (D_LAST_UPDATED_BY !='-99'
OR D_LAST_UPDATED_BY   !=-99
OR D_LAST_UPDATED_BY   IS NULL );

END POPULATE_SHIPMENT;

---------------------------------------------------------------------



PROCEDURE POPULATE_PRODUCT_LOCALE
(
P_IN_JOB_SEQ_ID IN NUMBER
)
is
BEGIN
INSERT INTO TG_PRODUCT_LOCALE
  (LOCALE,DESCRIPTION,MIGRATE_FLAG
  )
SELECT *
FROM
  (SELECT LOCALE,
    DESCRIPTION,
    'Y' AS MIGRATE_FLAG
  FROM PRODUCT_LOCALE
  where rownum > 0
  ) tab
  where locale not in (select locale from sn_product_locale );

END POPULATE_PRODUCT_LOCALE;

---------------------------------------------------------------------

PROCEDURE POPULATE_SERVICE_PROC_DEF_COMP
(
P_IN_JOB_SEQ_ID IN NUMBER
)
IS
BEGIN
INSERT INTO TG_SERVICE_PROC_DEF_COMPS
SELECT *
FROM
  (SELECT TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SERVICE_PROC_DEF_COMPS','SERVICE_PROCEDURE_DEFINITION',SERVICE_PROCEDURE_DEFINITION,'R',p_in_job_seq_id,'TG_SERVICE_PROCEDURE_DEFINITI','ID') AS SERVICE_PROCEDURE_DEFINITION,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SERVICE_PROC_DEF_COMPS','COMPONENTS',COMPONENTS,'R',p_in_job_seq_id,'TG_ASSEMBLY_DEFINITION','ID')                                                  AS COMPONENTS,
    LIST_INDEX AS     LIST_INDEX,
    'Y'                                                                                                                                  AS MIGRATE_FLAG
  FROM SERVICE_PROC_DEF_COMPS
  ) TAB
WHERE (SERVICE_PROCEDURE_DEFINITION !='-99'
OR SERVICE_PROCEDURE_DEFINITION     !=-99
OR SERVICE_PROCEDURE_DEFINITION     IS NULL )
AND (COMPONENTS                     !='-99'
OR COMPONENTS                       !=-99
OR COMPONENTS                       IS NULL );

END POPULATE_SERVICE_PROC_DEF_COMP;
-----------------------------------------------------------------------------------------------

PROCEDURE POPULATE_FAULT_CODE_DEF_COMPS
(
P_IN_JOB_SEQ_ID IN NUMBER
)
IS
BEGIN
INSERT INTO TG_FAULT_CODE_DEF_COMPS
SELECT *
FROM
  (SELECT TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('FAULT_CODE_DEF_COMPS','FAULT_CODE_DEFINITION',FAULT_CODE_DEFINITION,'R',p_in_job_seq_id,'TG_FAULT_CODE_DEFINITION','ID') AS FAULT_CODE_DEFINITION,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('FAULT_CODE_DEF_COMPS','COMPONENTS',COMPONENTS,'R',p_in_job_seq_id,'TG_ASSEMBLY_DEFINITION','ID')                               AS COMPONENTS,
    LIST_INDEX AS LIST_INDEX,
    'Y'                                                                                                             AS MIGRATE_FLAG
  FROM FAULT_CODE_DEF_COMPS
  ) TAB
WHERE (FAULT_CODE_DEFINITION !='-99'
OR FAULT_CODE_DEFINITION     !=-99
OR FAULT_CODE_DEFINITION     IS NULL )
AND (COMPONENTS              !='-99'
OR COMPONENTS                !=-99
OR COMPONENTS                IS NULL );

END POPULATE_FAULT_CODE_DEF_COMPS;
-----------------------------------------------------------------------------------------------

PROCEDURE POPULATE_EVAL_PRECEDENCE_PROP
(
P_IN_JOB_SEQ_ID IN NUMBER
)
IS
BEGIN
INSERT INTO TG_EVAL_PRECEDENCE_PROPERTIES
SELECT *
FROM
  (SELECT TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('EVAL_PRECEDENCE_PROPERTIES','FOR_CRITERIA',FOR_CRITERIA,'R',P_IN_JOB_SEQ_ID,'TG_CRITERIA_EVALUATION_PRECED','ID','PROP_EXPR',PROP_EXPR) AS FOR_CRITERIA,
    PROPERTIES_ELEMENT_DOMAIN_NAME,
    PROP_EXPR,
    PRECEDENCE AS PRECEDENCE,
    TAV_GIM_UTILITIES.LOOKUP_FUNCTION_varchar(P_IN_JOB_SEQ_ID,'PRECEDENCE',PRECEDENCE,'EVAL_PRECEDENCE_PROPERTIES','FOR_CRITERIA',FOR_CRITERIA,'PRECEDENCE',PRECEDENCE,'PROP_EXPR',PROP_EXPR) AS MIGRATE_FLAG
  FROM EVAL_PRECEDENCE_PROPERTIES
  ) TAB
WHERE FOR_CRITERIA     !=-99
OR FOR_CRITERIA     IS NULL;

END populate_EVAL_PRECEDENCE_PROP;

-----------------------------------------------------------------

PROCEDURE populate_SECTIONS_IN_PYMT_DEFN
(
P_IN_JOB_SEQ_ID IN NUMBER
)
IS
BEGIN
INSERT INTO TG_SECTIONS_IN_PYMT_DEFN
SELECT *
FROM
  (SELECT TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SECTIONS_IN_PYMT_DEFN','PYMT_DEFN',PYMT_DEFN,'R',P_IN_JOB_SEQ_ID,'TG_PAYMENT_DEFINITION','ID') AS PYMT_DEFN,
    TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('SECTIONS_IN_PYMT_DEFN','PYMT_SECTION',PYMT_SECTION,'R',P_IN_JOB_SEQ_ID,'TG_PAYMENT_SECTION','ID')    AS PYMT_SECTION,
    DISPLAY_POSITION               AS DISPLAY_POSITION,
    'Y'                                                                             AS MIGRATE_FLAG
  FROM SECTIONS_IN_PYMT_DEFN
  ) TAB
WHERE (PYMT_DEFN      !='-99'
OR PYMT_DEFN          !=-99
OR PYMT_DEFN          IS NULL )
AND (PYMT_SECTION     !='-99'
OR PYMT_SECTION       !=-99
OR PYMT_SECTION       IS NULL );

END populate_SECTIONS_IN_PYMT_DEFN;


PROCEDURE populate_add_payment_info
(
  p_in_job_seq_id IN NUMBER
)
AS
v_sql_stmt VARCHAR2(32767);
BEGIN

  BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX LINE_ITEM_GROUP_IDX ON ADD_PAYMENT_INFO(LINE_ITEM_GROUP)';
  EXCEPTION
  WHEN OTHERS THEN
  TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.05'      ,                       --2
            G_TABLE_NAME             =>    'TG_ADD_PAYMENT_INFO'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : LINE_ITEM_GROUP'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
  END;

  BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX ADD_PAYMENT_INFO_IDX ON ADD_PAYMENT_INFO(ADDITIONAL_PAYMENT_INFO)';
  EXCEPTION
  WHEN OTHERS THEN
   TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.06'      ,                       --2
            G_TABLE_NAME             =>    'TG_ADD_PAYMENT_INFO'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : ADDITIONAL_PAYMENT_INFO'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 2'               					              --15
           );
  END;

  v_sql_stmt := 'CREATE TABLE TG_ADD_PAYMENT_INFO AS SELECT A.ID AS ADDITIONAL_PAYMENT_INFO, B.ID AS LINE_ITEM_GROUP, ''Y'' AS MIGRATE_FLAG
                  FROM TG_ADDITIONAL_PAYMENT_INFO A , TG_LINE_ITEM_GROUP B , ADD_PAYMENT_INFO C
                  WHERE A.OLD_43_ID = C.ADDITIONAL_PAYMENT_INFO
                  AND B.OLD_43_ID = C.LINE_ITEM_GROUP';

  BEGIN
    EXECUTE IMMEDIATE v_sql_stmt;
  END;


END populate_add_payment_info;

PROCEDURE populate_line_item_groups
(
  p_in_job_seq_id IN NUMBER
)
AS
v_sql_stmt VARCHAR2(32767);
BEGIN

  BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX LINE_ITEM_GRPS_FP_IDX ON LINE_ITEM_GROUPS(FOR_PAYMENT)';
  EXCEPTION
    WHEN OTHERS THEN
      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.08'      ,                       --2
            G_TABLE_NAME             =>    'TG_LINE_ITEM_GROUPS'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : FOR_PAYMENT'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 3'               					              --15
           );
    END;

    BEGIN
      EXECUTE IMMEDIATE 'CREATE INDEX LINE_ITEM_GRPS_LIG ON LINE_ITEM_GROUPS(LINE_ITEM_GROUPS)';
    EXCEPTION
      WHEN OTHERS THEN
         TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.09'      ,                       --2
            G_TABLE_NAME             =>    'TG_LINE_ITEM_GROUPS'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : LINE_ITEM_GROUPS'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 3'               					              --15
           );
    END;


  v_sql_stmt := 'CREATE TABLE TG_LINE_ITEM_GROUPS AS SELECT A.ID AS FOR_PAYMENT, B.ID AS LINE_ITEM_GROUPS , ''Y'' AS MIGRATE_FLAG
                 FROM TG_PAYMENT A , TG_LINE_ITEM_GROUP B, LINE_ITEM_GROUPS C
                 WHERE A.OLD_43_ID = C.FOR_PAYMENT
                 AND B.OLD_43_ID   = C.LINE_ITEM_GROUPS';

  BEGIN
    EXECUTE IMMEDIATE v_sql_stmt;
  END;

END populate_line_item_groups;

PROCEDURE populate_items_in_group
(
  p_in_job_seq_id IN NUMBER
)
AS
v_sql_stmt VARCHAR2(32767);
BEGIN

  BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX ITEMS_IN_GROUP_IG_IDX ON ITEMS_IN_GROUP(ITEM_GROUP)';
  EXCEPTION
    WHEN OTHERS THEN
      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.11'      ,                       --2
            G_TABLE_NAME             =>    'TG_ITEMS_IN_GROUP'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : ITEM_GROUP'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 3'               					              --15
           );
  END;

  BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX ITEMS_IN_GROUP_ITEM_IDX ON ITEMS_IN_GROUP(ITEM)';
  EXCEPTION
    WHEN OTHERS THEN
      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.12'      ,                       --2
            G_TABLE_NAME             =>    'TG_ITEMS_IN_GROUP'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : ITEM'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 3'               					              --15
           );
  END;

  v_sql_stmt := 'CREATE TABLE TG_ITEMS_IN_GROUP AS SELECT A.ID AS ITEM_GROUP,B.ID AS ITEM, ''Y'' AS MIGRATE_FLAG
                 FROM TG_ITEM_GROUP A , TG_ITEM B , ITEMS_IN_GROUP C
                 WHERE A.OLD_43_ID = C.ITEM_GROUP
                 AND B.OLD_43_ID   = C.ITEM';

  BEGIN
    EXECUTE IMMEDIATE v_sql_stmt;
  END;


END populate_items_in_group;



PROCEDURE populate_i18nitem_text
(
  p_in_job_seq_id IN NUMBER
)
AS
v_sql_stmt VARCHAR2(32767);
BEGIN

  BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX I18NITEMTEXT_ITEM_IDX ON I18NITEM_TEXT(ITEM)';
  EXCEPTION
    WHEN OTHERS THEN
      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.15'      ,                       --2
            G_TABLE_NAME             =>    'TG_I18NITEM_TEXT'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : ITEM'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
  END;

  v_sql_stmt := 'CREATE TABLE TG_I18NITEM_TEXT AS SELECT (B.ID + 100000000000000) AS ID, B.LOCALE, B.DESCRIPTION, A.ID AS ITEM, ''Y'' AS MIGRATE_FLAG
                 FROM TG_ITEM A,I18NITEM_TEXT B
                 WHERE A.OLD_43_ID = B.ITEM';

  BEGIN
    EXECUTE IMMEDIATE V_SQL_STMT;
  END;

END populate_i18nitem_text;



PROCEDURE populate_MODIFIERS
(
  p_in_job_seq_id IN NUMBER
)
AS
v_sql_stmt VARCHAR2(32767);
BEGIN

  begin
    EXECUTE IMMEDIATE 'CREATE INDEX MODIFIERS_LIG_IDX ON MODIFIERS(LINE_ITEM_GROUP)';
  EXCEPTION
    WHEN OTHERS THEN
      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '3.18'      ,                       --2
            G_TABLE_NAME             =>    'MODIFIERS'               ,                       --3
            G_ISSUE_COL_NAME         =>    NULL                  ,                       --4
            G_ISSUE_COL_VALUE        =>    NULL                 ,
            G_ISSUE_TYPE             =>    'Exception occured while creating index for column : LINE_ITEM_GROUP'  ,    --6
            g_ora_error_message      =>    SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
  END;

  v_sql_stmt := 'CREATE TABLE TG_MODIFIERS AS SELECT a.id AS MODIFIERS, B.id  AS LINE_ITEM_GROUP, ''Y'' AS MIGRATE_FLAG
                 FROM TG_LINE_ITEM A, TG_LINE_ITEM_GROUP B, MODIFIERS C
                 WHERE a.OLD_43_ID = C.MODIFIERS AND B.OLD_43_ID = C.LINE_ITEM_GROUP';
  BEGIN
    EXECUTE IMMEDIATE v_sql_stmt;
  END;
end populate_MODIFIERS;

PROCEDURE POPULATE_WNTY_STG
(
  P_IN_JOB_SEQ_ID IN NUMBER
)
AS
BEGIN
  INSERT
  /*+ APPEND */
  INTO
  TG_WARRANTY
  (
    ID,
    DELIVERY_DATE,
    DRAFT,
    STATUS,
    VERSION,
    FOR_ITEM,
    CUSTOMER,
    FOR_TRANSACTION,
    MARKETING_INFORMATION,
    LIST_INDEX,
    ADDRESS_FOR_TRANSFER,
    FILED_BY,
    FOR_DEALER,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    TRANSACTION_TYPE,
    MULTIDRETRNUMBER,
    CUSTOMER_TYPE,
    MODIFY_DELETE_COMMENTS,
    OPERATOR,
    INSTALLING_DEALER,
    OEM,
    FLEET_NUMBER,
    EQUIPMENT_VIN,
    INSTALLATION_DATE,
    CERTIFIED_INSTALLER,
    NON_CERTIFIED_INSTALLER,
    OPERATOR_TYPE,
    OPERATOR_ADDRESS_FOR_TRANSFER,
    OLD_43_ID,
    MIGRATE_FLAG
  ) (
    SELECT
      100000000000000 + w.ID AS ID,
      w.DELIVERY_DATE,
      w.DRAFT,
      w.STATUS,
      w.VERSION,
      100000000000000 + FOR_ITEM AS FOR_ITEM,
      100000000000000 + CUSTOMER AS CUSTOMER,
      100000000000000 + FOR_TRANSACTION AS FOR_TRANSACTION,
      100000000000000 + MARKETING_INFORMATION AS MARKETING_INFORMATION,
      w.LIST_INDEX,
      100000000000000 + ADDRESS_FOR_TRANSFER AS ADDRESS_FOR_TRANSFER,
      filedby.ID AS FILED_BY,
      100000000000000 + FOR_DEALER AS FOR_DEALER,
      w.D_CREATED_ON,
      w.D_INTERNAL_COMMENTS,
      w.D_UPDATED_ON,
      lup.ID AS D_LAST_UPDATED_BY,
      w.D_CREATED_TIME,
      w.D_UPDATED_TIME,
      w.D_ACTIVE,
      itt.ID AS TRANSACTION_TYPE,
      w.MULTIDRETRNUMBER,
      w.CUSTOMER_TYPE,
      w.MODIFY_DELETE_COMMENTS,
      100000000000000 + OPERATOR AS OPERATOR,
      installing.ID AS INSTALLING_DEALER,
      100000000000000 + OEM AS OEM,
      w.FLEET_NUMBER,
      w.EQUIPMENT_VIN,
      w.INSTALLATION_DATE,
      100000000000000 + CERTIFIED_INSTALLER AS CERTIFIED_INSTALLER,
      100000000000000 + NON_CERTIFIED_INSTALLER AS NON_CERTIFIED_INSTALLER,
      OPERATOR_TYPE,
      100000000000000 + OPERATOR_ADDRESS_FOR_TRANSFER AS OPERATOR_ADDRESS_FOR_TRANSFER,
      w.ID                                        AS OLD_43_ID,
      'Y' AS MIGRATE_FLAG
    FROM
      WARRANTY w
      LEFT OUTER JOIN TG_ORG_USER filedby ON w.FILED_BY = filedby.OLD_43_ID
      LEFT OUTER JOIN TG_ORG_USER lup ON w.D_LAST_UPDATED_BY = lup.OLD_43_ID
      LEFT OUTER JOIN TG_INVENTORY_TRANSACTION_TYPE itt ON w.TRANSACTION_TYPE = itt.OLD_43_ID
      LEFT OUTER JOIN TG_PARTY installing ON w.INSTALLING_DEALER = installing.OLD_43_ID);
      
      commit;
      
    EXECUTE IMMEDIATE 'CREATE INDEX TG_WARRANTY_IDX1 ON TG_WARRANTY(OLD_43_ID)';
      
      
END;



PROCEDURE POPULATE_ITEMMAPPING_STG
(
  P_IN_JOB_SEQ_ID IN NUMBER
)
AS
BEGIN
  INSERT
  /*+ APPEND */
INTO
  TG_ITEM_MAPPING
  (
    ID,
    FROM_DATE,
    TO_DATE,
    VERSION,
    TO_ITEM,
    FROM_ITEM,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    SUPPLIER_SITECODE,
    OLD_43_ID,
    MIGRATE_FLAG
  ) (
    SELECT
      100000000000000 + im.ID AS ID,
      FROM_DATE,
      TO_DATE,
      im.VERSION,
      100000000000000 + TO_ITEM AS TO_ITEM,
      100000000000000 + FROM_ITEM AS FROM_ITEM,
      im.D_CREATED_ON,
      im.D_INTERNAL_COMMENTS,
      im.D_UPDATED_ON,
      lup.ID AS D_LAST_UPDATED_BY,
      im.D_CREATED_TIME,
      im.D_UPDATED_TIME,
      im.D_ACTIVE,
      SUPPLIER_SITECODE,
      im.ID AS OLD_43_ID,
      'Y' AS MIGRATE_FLAG
    FROM
      ITEM_MAPPING im
      LEFT OUTER JOIN TG_ORG_USER lup ON im.D_LAST_UPDATED_BY = lup.OLD_43_ID);
      
    commit;
      
    EXECUTE IMMEDIATE 'CREATE INDEX TG_ITEM_MAPPING_IDZ ON TG_ITEM_MAPPING(OLD_43_ID)';
      
      
      
END;

PROCEDURE POPULATE_POLICYAUDIT_STG
(
  P_IN_JOB_SEQ_ID IN NUMBER
)
AS
BEGIN
  INSERT
  /*+ APPEND */
  INTO
  TG_POLICY_AUDIT
  (
    ID,
    COMMENTS,
    CREATED_ON,
    SERVICE_HOURS_COVERED,
    STATUS,
    FROM_DATE,
    TILL_DATE,
    CREATED_BY,
    FOR_POLICY,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    CREATED_TIME,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    OLD_43_ID,
    MIGRATE_FLAG
  ) (
    SELECT
      100000000000000 + pa.ID AS ID,
      COMMENTS,
      CREATED_ON,
      SERVICE_HOURS_COVERED,
      STATUS,
      FROM_DATE,
      TILL_DATE,
      created.ID AS CREATED_BY,
      100000000000000 + FOR_POLICY AS FOR_POLICY,
      pa.D_CREATED_ON,
      pa.D_INTERNAL_COMMENTS,
      pa.D_UPDATED_ON,
      lup.ID AS D_LAST_UPDATED_BY,
      pa.CREATED_TIME,
      pa.D_CREATED_TIME,
      pa.D_UPDATED_TIME,
      pa.D_ACTIVE,
      pa.ID AS OLD_43_ID,
      'Y' AS MIGRATE_FLAG
    FROM
      POLICY_AUDIT pa
      LEFT OUTER JOIN TG_ORG_USER lup ON pa.D_LAST_UPDATED_BY = lup.OLD_43_ID
      LEFT OUTER JOIN TG_ORG_USER created ON pa.CREATED_BY = created.OLD_43_ID);
      
      
    commit;
      
    EXECUTE IMMEDIATE 'CREATE INDEX TG_POLICY_AUDIT_IDZ ON TG_POLICY_AUDIT(OLD_43_ID)';
      
      
END;

PROCEDURE POPULATE_INVTXN_STG
(
  P_IN_JOB_SEQ_ID IN NUMBER
)
AS
BEGIN
  INSERT
  /*+ APPEND */
INTO
  TG_INVENTORY_TRANSACTION
  (
    ID,
    INVOICE_DATE,
    INVOICE_NUMBER,
    SALES_ORDER_NUMBER,
    TRANSACTION_DATE,
    TRANSACTION_ORDER,
    VERSION,
    SELLER,
    BUYER,
    INV_TRANSACTION_TYPE,
    OWNER_SHIP,
    TRANSACTED_ITEM,
    STATUS,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    D_ACTIVE,
    SHIP_TO_SITE_NUMBER,
    HOURS_ON_MACHINE,
    OLD_43_ID,
    MIGRATE_FLAG
  )(
    SELECT
      100000000000000 + txn.ID AS ID,
      INVOICE_DATE,
      INVOICE_NUMBER,
      SALES_ORDER_NUMBER,
      TRANSACTION_DATE,
      TRANSACTION_ORDER,
      txn.VERSION,
      seller.ID AS SELLER,
      buyer.ID AS BUYER,
      itt.ID AS INV_TRANSACTION_TYPE,
      ownership.ID AS OWNER_SHIP,
      100000000000000 + TRANSACTED_ITEM AS TRANSACTED_ITEM,
      STATUS,
      txn.D_CREATED_ON,
      txn.D_INTERNAL_COMMENTS,
      txn.D_UPDATED_ON,
      lup.ID AS D_LAST_UPDATED_BY,
      txn.D_CREATED_TIME,
      txn.D_UPDATED_TIME,
      txn.D_ACTIVE,
      SHIP_TO_SITE_NUMBER,
      HOURS_ON_MACHINE,
      txn.ID AS OLD_43_ID,
      'Y' AS MIGRATE_FLAG
    FROM
      INVENTORY_TRANSACTION txn
      LEFT OUTER JOIN TG_PARTY seller ON txn.SELLER = seller.OLD_43_ID
      LEFT OUTER JOIN TG_PARTY buyer ON txn.BUYER = buyer.OLD_43_ID
      LEFT OUTER JOIN TG_INVENTORY_TRANSACTION_TYPE itt ON txn.INV_TRANSACTION_TYPE = itt.OLD_43_ID
      LEFT OUTER JOIN TG_PARTY ownership ON txn.OWNER_SHIP = ownership.OLD_43_ID
      LEFT OUTER JOIN TG_ORG_USER lup ON txn.D_LAST_UPDATED_BY = lup.OLD_43_ID);
      
      commit;
      
    EXECUTE IMMEDIATE 'CREATE INDEX TG_INVENTORY_TRANSACTION_IDX ON TG_INVENTORY_TRANSACTION(OLD_43_ID)';
      
      
END;

END TAV_GIM_COMP_UNIQUE_SCENARIOS;
/
