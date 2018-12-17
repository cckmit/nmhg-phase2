CREATE OR REPLACE PACKAGE tav_gim_utilities
AS
  /*
  || Package Name   : TAV_GIM_UTILITIES
  || Purpose        : Package to encapsulate all the common utilties for Global Instance Migration
  || Author         : Prabhu Ramasamy
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */
  /*
  || Function Name  : get_latest_id
  || Purpose        : Function to retrive the the latest id
  || Author         : Prabhu Ramasamy
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||15/12/201      Joseph   Added functions mapping/lookup_function_org_user/prefix
  */



FUNCTION PREFIX_FUNCTION_NUMBER(
    P_IN_TABLE_NAME IN VARCHAR2,
    P_IN_COL_NAME IN VARCHAR2,
    P_IN_COL_VALUE IN NUMBER,
    P_IN_CON_TYPE IN CHAR DEFAULT NULL,
    P_IN_JOB_SEQ_ID IN number DEFAULT -999,
    P_IN_REF_STG_TABLE_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_REF_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_VALUE IN VARCHAR2 DEFAULT NULL)
  RETURN NUMBER;


FUNCTION PREFIX_FUNCTION_VARCHAR(
    P_IN_TABLE_NAME IN VARCHAR2,
    P_IN_COL_NAME IN VARCHAR2,
    P_IN_COL_VALUE IN VARCHAR2,
    P_IN_CON_TYPE IN VARCHAR2,
    P_IN_JOB_SEQ_ID IN number,
    P_IN_REF_STG_TABLE_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_REF_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_VALUE IN VARCHAR2 DEFAULT NULL
    )
  RETURN VARCHAR2;





FUNCTION lookup_function_varchar(
    P_IN_JOB_SEQ_ID IN NUMBER DEFAULT -999,
    P_IN_PK_NAME    IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_VALUE   IN VARCHAR2 DEFAULT NULL,
    p_in_table_name IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_2 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_2 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_KEY_3 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_VAL_3 IN VARCHAR2 DEFAULT NULL
    )
  RETURN varchar2;

FUNCTION lookup_function_number(
    P_IN_JOB_SEQ_ID IN NUMBER DEFAULT -999,
    P_IN_PK_NAME    IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_VALUE   IN VARCHAR2 DEFAULT NULL,
    p_in_table_name IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_2 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_2 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_KEY_3 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_VAL_3 IN VARCHAR2 DEFAULT NULL
    )
  RETURN number;


  FUNCTION CHECK_FULLLOAD_REF_TABS (
    TAB_NAME VARCHAR2)
    RETURN NUMBER;



PROCEDURE proc_lookup_validate_pk
    (
      P_IN_PK_60_id varchar2,
      P_IN_PK_43_ID varchar2,
      P_IN_TABLE_NAME varchar2
    );


FUNCTION UPDATE_MIGRATE_FLAG(
    P_IN_PK_VALUE    in varchar2
    )
  RETURN varchar2;



END TAV_GIM_UTILITIES;
/


CREATE OR REPLACE PACKAGE BODY tav_gim_utilities
AS
  /*
  || Package Name   : TAV_GIM_UTILITIES
  || Purpose        : Package to encapsulate all the common utilties for Global Instance Migration
  || Author         : Prabhu Ramasamy
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */
----------------------------------------------------------------------------------------------

  /*
  || Function Name  : LOOKUP_FUNCTION_NUMBER

  || Purpose        : Function to retrive the the 6.0 ID if availble using functional keys
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */

/* Max Issue id :1.19 */

FUNCTION LOOKUP_FUNCTION_NUMBER(
    P_IN_JOB_SEQ_ID IN NUMBER DEFAULT -999,
    P_IN_PK_NAME    IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_VALUE   IN VARCHAR2 DEFAULT NULL,
    p_in_table_name IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_2 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_2 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_KEY_3 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_VAL_3 IN VARCHAR2 DEFAULT NULL
    )
  RETURN NUMBER
IS
  V_NEW60_ID NUMBER;
  v_ref_func_val_2 number;
  v_exec_string varchar2(4000);
  V_PK_COLUMN_NAME VARCHAR2(40);
  V_PK_CNT number :=0;
  V_ERROR_VALUE NUMBER := -99;
  V_PARTY_KEY  VARCHAR2(400);
  V_PARTY_TYPE  VARCHAR2(400);
  v_60_table_synonym VARCHAR2(40);
  v_param_id number;
  V_OPTION_ID number;
  v_sup_id number :=0; --supplier number lookup
BEGIN

select  /*+ RESULT_CACHE */ synonym_name into v_60_table_synonym from tav_gim_valid_tables where table_name = p_in_table_name;

--dbms_output.put_line('P_IN_PK_NAME: ' || P_IN_PK_NAME || 'P_IN_PK_VALUE: ' || P_IN_PK_VALUE);


IF P_IN_TABLE_NAME = 'ORG_USER' then   ---lookuplogic for org_user when login is NULL starts

if P_IN_FUNCT_VAL_1 is null then
return null; --allowing org_users with null values into the system as claims are falling out due to this
-- TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
--            G_ISSUE_ID               =>    '1.17'                        ,                       --2
--            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       --3
--            G_ISSUE_COL_NAME         =>    P_IN_PK_NAME                  ,                       --4
--            G_ISSUE_COL_VALUE        =>    P_IN_PK_VALUE                 ,                       --5
--            G_ISSUE_TYPE             =>    'LOGIN field empty in the ORG_USER table(TWMS_43)'  , --6
--            G_KEY_COL_NAME_1         =>    p_in_funct_key_1    ,                                 --7
--            G_KEY_COL_VALUE_1        =>    p_in_funct_val_1    ,                                 --8
--            G_KEY_COL_NAME_2         =>    p_in_funct_key_2    ,                                 --9
--            G_KEY_COL_VALUE_2        =>    P_IN_FUNCT_VAL_2    ,                                 --10
--            G_KEY_COL_NAME_3         =>    P_IN_FUNCT_KEY_3    ,                                 --11
--            g_key_col_value_3        =>    P_IN_FUNCT_VAL_3    ,                                 --12
--            G_ORA_ERROR_MESSAGE      =>      SQLERRM             ,                                 --13
--            G_RUN_DATE               =>    systimestamp        ,                                 --14
--            G_BLOCK_NAME             =>   'REPORT'                                       --15
--           );
--        return V_ERROR_VALUE;
end if;

END IF;    ---end of lookuplogic for org_user when login is NULL


IF P_IN_TABLE_NAME = 'CONFIG_PARAM_OPTIONS_MAPPING' then   ---lookuplogic for config param options starts

    v_option_id:=  TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('CONFIG_PARAM_OPTIONS_MAPPING','OPTION_ID',p_in_funct_val_1,'R',P_IN_JOB_SEQ_ID,'TG_CONFIG_PARAM_OPTION','ID');
    v_param_id := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('CONFIG_PARAM_OPTIONS_MAPPING','PARAM_ID',p_in_funct_val_2,'R',P_IN_JOB_SEQ_ID,'TG_CONFIG_PARAM','ID');


    begin
    select id
    into v_new60_id
    from sn_config_param_options_mappi
    where option_id = v_option_id
    and param_id = v_param_id;
      exception
    WHEN NO_DATA_FOUND THEN
    return null; ---prefix value as value is not availble in 6.0
    when OTHERS then
    V_NEW60_ID := null;
          TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '1.15'                        ,                       --2
            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>    P_IN_PK_NAME                  ,                       --4
            G_ISSUE_COL_VALUE        =>    P_IN_PK_VALUE                 ,                       --5
            G_ISSUE_TYPE             =>    'Issue while looking up config_param_options_mapping in TWMS_60(lookup number fucntion)'  ,    --6
            G_KEY_COL_NAME_1         =>    p_in_funct_key_1    ,                                 --7
            G_KEY_COL_VALUE_1        =>    p_in_funct_val_1    ,                                 --8
            G_KEY_COL_NAME_2         =>    p_in_funct_key_2    ,                                 --9
            G_KEY_COL_VALUE_2        =>    P_IN_FUNCT_VAL_2    ,                                 --10
            G_KEY_COL_NAME_3         =>    P_IN_FUNCT_KEY_3    ,                                 --11
            g_key_col_value_3        =>    P_IN_FUNCT_VAL_3    ,                                 --12
            g_ora_error_message      =>      SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>   'Block 1'                                       --15
           );
        RETURN v_error_value;
    end;


ELSIF P_IN_TABLE_NAME = 'PARTY' THEN    --x4        ---lookuplogic for party table starts

   IF P_IN_FUNCT_VAL_1 = 'OEM' THEN    ---to handle OEM record
   PROC_LOOKUP_VALIDATE_PK(1,P_IN_PK_VALUE,P_IN_TABLE_NAME);
   return 1;  --OEM record
   end if;



    BEGIN
        SELECT
        COALESCE(B.SERVICE_PROVIDER_NUMBER,C.SUPPLIER_NUMBER,D.CUSTOMER_ID) ,
        COALESCE( DECODE(B.SERVICE_PROVIDER_NUMBER,NULL,NULL,'SERVICE_PROVIDER'),
                  DECODE(C.SUPPLIER_NUMBER,NULL,NULL,'SUPPLIER'),
                  DECODE(D.CUSTOMER_ID,NULL,NULL,'CUSTOMER'))
        into v_party_key,v_party_type
        from
        PARTY A ,
        SERVICE_PROVIDER B ,
        SUPPLIER C ,
        customer   d
        where a.id = b.id(+)
        and   a.id = c.id(+)
        and   a.id = d.id(+)
        AND   A.NAME = P_IN_FUNCT_VAL_1
        AND   A.ID =  p_in_funct_val_2
        ;
     exception
    WHEN NO_DATA_FOUND THEN
    RETURN NULL; -- allowing filtered deactivated data
    when OTHERS then
    V_NEW60_ID := null;
          TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '1.01'      ,                       --2
            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>    P_IN_PK_NAME                  ,                       --4
            G_ISSUE_COL_VALUE        =>    P_IN_PK_VALUE                 ,                       --5
            G_ISSUE_TYPE             =>    'Issue while retrieving party type from TWMS_43(lookup number fucntion)'  ,    --6
            G_KEY_COL_NAME_1         =>    p_in_funct_key_1    ,                                 --7
            G_KEY_COL_VALUE_1        =>    p_in_funct_val_1    ,                                 --8
            G_KEY_COL_NAME_2         =>    p_in_funct_key_2    ,                                 --9
            G_KEY_COL_VALUE_2        =>    P_IN_FUNCT_VAL_2    ,                                 --10
            G_KEY_COL_NAME_3         =>    P_IN_FUNCT_KEY_3    ,                                 --11
            g_key_col_value_3        =>    P_IN_FUNCT_VAL_3    ,                                 --12
            g_ora_error_message      =>      SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>   'Block 1'                                       --15
           );
        RETURN V_ERROR_VALUE;
        end;


    BEGIN
        select
        A.ID
        INTO V_NEW60_ID
        FROM
        sn_party a ,
        sn_SERVICE_PROVIDER B ,
        sn_SUPPLIER C ,
        sn_customer d
        where a.id = b.id(+)
        AND   A.ID = C.ID(+)
        AND   A.ID = D.ID(+)
        AND   A.NAME = P_IN_FUNCT_VAL_1
        AND COALESCE(B.SERVICE_PROVIDER_NUMBER,C.SUPPLIER_NUMBER,D.CUSTOMER_ID) = V_PARTY_KEY
        and COALESCE( DECODE(B.SERVICE_PROVIDER_NUMBER,NULL,NULL,'SERVICE_PROVIDER'),
                  DECODE(C.SUPPLIER_NUMBER,NULL,NULL,'SUPPLIER'),
                  decode(D.CUSTOMER_ID,NULL,NULL,'CUSTOMER')) = v_party_type;
    exception
    WHEN NO_DATA_FOUND THEN
    return null; ---prefix value as value is not availble in 6.0
    when OTHERS then
    V_NEW60_ID := null;
          TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>    '1.02'                        ,                       --2
            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>    P_IN_PK_NAME                  ,                       --4
            G_ISSUE_COL_VALUE        =>    P_IN_PK_VALUE                 ,                       --5
            G_ISSUE_TYPE             =>    'Issue while looking up party name in TWMS 6.0(lookup number fucntion)'  ,    --6
            G_KEY_COL_NAME_1         =>    p_in_funct_key_1    ,                                 --7
            G_KEY_COL_VALUE_1        =>    p_in_funct_val_1    ,                                 --8
            G_KEY_COL_NAME_2         =>    p_in_funct_key_2    ,                                 --9
            G_KEY_COL_VALUE_2        =>    P_IN_FUNCT_VAL_2    ,                                 --10
            G_KEY_COL_NAME_3         =>    P_IN_FUNCT_KEY_3    ,                                 --11
            g_key_col_value_3        =>    P_IN_FUNCT_VAL_3    ,                                 --12
            g_ora_error_message      =>      SQLERRM             ,                                 --13
            G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>   'Block 1'                                       --15
           );
        RETURN v_error_value;
    end;

    elsif  P_IN_TABLE_NAME = 'SUPPLIER' THEN


        begin
        with sup_43 as
        (
        select a.name,B.SUPPLIER_NUMBER
        from PARTY a ,SUPPLIER B
        where a.id = B.id
        and B.id = p_in_funct_val_1
        )
        select B.id
        into V_NEW60_ID
        from SN_PARTY a ,SN_SUPPLIER B , SUP_43 C
        where a.name = C.name
        and B.SUPPLIER_NUMBER = C.SUPPLIER_NUMBER
        and a.id = B.id;
         exception
    when NO_DATA_FOUND then
    return null; ---prefix value as this value is not availble in 6.0
      when TOO_MANY_ROWS then
      V_NEW60_ID :=0;
                      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
                        (
                        GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
                        G_ISSUE_ID               =>     '1.18'      ,                       --2
                        G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
                        G_ISSUE_COL_NAME         =>     P_IN_PK_NAME                  ,                       --4
                        G_ISSUE_COL_VALUE        =>     P_IN_PK_VALUE                 ,                       --5
                        G_ISSUE_TYPE             =>     'More than one primary key derived for this supplier name/number in TWMS_60 (lookup number function)'  ,    --6
                        G_KEY_COL_NAME_1         =>     p_in_funct_key_1    ,                                 --7
                        G_KEY_COL_VALUE_1        =>     p_in_funct_val_1    ,                                 --8
                        G_KEY_COL_NAME_2         =>     p_in_funct_key_2    ,                                 --9
                        G_KEY_COL_VALUE_2        =>     P_IN_FUNCT_VAL_2    ,                                 --10
                        G_KEY_COL_NAME_3         =>       P_IN_FUNCT_KEY_3    ,                                 --11
                        g_key_col_value_3        =>     P_IN_FUNCT_VAL_3    ,
                        G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
                        g_key_col_value_4        =>      v_exec_string    ,                                --12
                        g_ora_error_message      =>       SQLERRM             ,                                 --13
                        G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
                        G_BLOCK_NAME             =>    'Block 1'                                      --15
                       );
                       RETURN v_error_value;
    when OTHERS then
    V_NEW60_ID :=0;
                  TAV_GIM_INITIAL_SETUP.proc_insert_error_record
                   (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       --1
                    G_ISSUE_ID               =>    '1.33'                        ,                       --2
                    G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       --3
                    G_ISSUE_COL_NAME         =>    P_IN_PK_NAME                  ,                       --4
                    G_ISSUE_COL_VALUE        =>    P_IN_PK_VALUE                 ,                       --5
                    G_ISSUE_TYPE             =>    'Issue while looking up supplier name in TWMS_60(lookup number fucntion)'  ,    --6
                    G_KEY_COL_NAME_1         =>    p_in_funct_key_1    ,                                 --7
                    G_KEY_COL_VALUE_1        =>    p_in_funct_val_1    ,                                 --8
                    G_KEY_COL_NAME_2         =>    p_in_funct_key_2    ,                                 --9
                    G_KEY_COL_VALUE_2        =>    P_IN_FUNCT_VAL_2    ,                                 --10
                    G_KEY_COL_NAME_3         =>    P_IN_FUNCT_KEY_3    ,                                 --11
                    g_key_col_value_3        =>    P_IN_FUNCT_VAL_3    ,                                 --12
                    g_ora_error_message      =>      SQLERRM             ,                                 --13
                    G_RUN_DATE               =>    SYSTIMESTAMP        ,                                 --14
                    G_BLOCK_NAME             =>   'Block 1'                                       --15
                   );
                RETURN v_error_value;
    end;

    else  ---lookuplogic for party table ends --x4

      IF P_IN_TABLE_NAME = 'I18NACTION_DEFINITION'
      OR P_IN_TABLE_NAME = 'I18NASSEMBLY_DEFINITION'
      OR P_IN_TABLE_NAME = 'I18NFAILURE_CAUSE_DEFINITION'
      OR P_IN_TABLE_NAME = 'I18NFAILURE_TYPE_DEFINITION'
      OR P_IN_TABLE_NAME = 'I18NFLR_ROOT_CAUSE_DEFINITION'
      OR P_IN_TABLE_NAME = 'ADDRESS_BOOK'
      OR P_IN_TABLE_NAME = 'CURRENCY_CONVERSION_FACTOR'

      THEN

       IF P_IN_TABLE_NAME = 'I18NACTION_DEFINITION'    THEN
       V_REF_FUNC_VAL_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('I18NACTION_DEFINITION','ACTION_DEFINITION',P_IN_FUNCT_VAL_2,'R',P_IN_JOB_SEQ_ID,'TG_ACTION_DEFINITION','ID','ID',0);
       ELSIF P_IN_TABLE_NAME = 'I18NASSEMBLY_DEFINITION'    THEN
       v_ref_func_val_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('I18NASSEMBLY_DEFINITION','ASSEMBLY_DEFINITION',P_IN_FUNCT_VAL_2,'R',P_IN_JOB_SEQ_ID,'TG_ASSEMBLY_DEFINITION','ID','ID',0);
       ELSIF P_IN_TABLE_NAME = 'I18NFAILURE_CAUSE_DEFINITION'    THEN
       v_ref_func_val_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('I18NFAILURE_CAUSE_DEFINITION','FAILURE_CAUSE_DEFINITION',P_IN_FUNCT_VAL_2,'R',P_IN_JOB_SEQ_ID,'TG_FAILURE_CAUSE_DEFINITION','ID','ID',0);
       ELSIF P_IN_TABLE_NAME = 'I18NFAILURE_TYPE_DEFINITION'    THEN
       v_ref_func_val_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('I18NFAILURE_TYPE_DEFINITION','FAILURE_TYPE_DEFINITION',P_IN_FUNCT_VAL_2,'R',P_IN_JOB_SEQ_ID,'TG_FAILURE_TYPE_DEFINITION','ID','ID',0);
       ELSIF P_IN_TABLE_NAME = 'I18NFLR_ROOT_CAUSE_DEFINITION'    THEN
       V_REF_FUNC_VAL_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('I18NFLR_ROOT_CAUSE_DEFINITION','FAILURE_ROOT_CAUSE_DEFINITION',P_IN_FUNCT_VAL_2,'R',P_IN_JOB_SEQ_ID,'TG_FAILURE_ROOT_CAUSE_DEFINIT','ID','ID',0);
       ELSIF P_IN_TABLE_NAME = 'ADDRESS_BOOK'    THEN
       V_REF_FUNC_VAL_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('ADDRESS_BOOK','BELONGS_TO',P_IN_FUNCT_VAL_2,'R',P_IN_JOB_SEQ_ID,'TG_ORGANIZATION','ID','ID',0);
       ELSIF P_IN_TABLE_NAME = 'CURRENCY_CONVERSION_FACTOR' THEN
       V_REF_FUNC_VAL_2 := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('CURRENCY_CONVERSION_FACTOR','PARENT',p_in_funct_val_3,'R',P_IN_JOB_SEQ_ID,'TG_CURRENCY_EXCHANGE_RATE','ID','ID',0);
       END IF;



          IF P_IN_TABLE_NAME = 'CURRENCY_CONVERSION_FACTOR' THEN

               v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' FROM ' || v_60_table_synonym ||   ' WHERE '
               || P_IN_FUNCT_KEY_1 || '=''' || P_IN_FUNCT_VAL_1 || '''  AND '
               || P_IN_FUNCT_KEY_2 || '=''' || P_IN_FUNCT_VAL_2 || '''  AND '
               || p_in_funct_key_3 || '=''' || V_REF_FUNC_VAL_2 || '''' ;


          ELSE

              v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' FROM ' || v_60_table_synonym ||   ' WHERE '
               || P_IN_FUNCT_KEY_1 || '=''' || P_IN_FUNCT_VAL_1 || '''  AND '
               || p_in_funct_key_2 || ' = ''' || V_REF_FUNC_VAL_2 || '''' ;


          END IF;



      ELSE  --x3

              IF P_IN_FUNCT_KEY_1 is not null and P_IN_FUNCT_KEY_2 is null and P_IN_FUNCT_KEY_3 is null then --x2

                     v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' FROM ' || v_60_table_synonym || ' WHERE ' || p_in_funct_key_1 || '=''' || p_in_funct_val_1 || '''';

              ELSIF p_in_funct_key_1 IS NOT NULL AND p_in_funct_key_2 IS NOT NULL AND p_in_funct_key_3 IS NULL THEN

                     v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' FROM ' || v_60_table_synonym ||
                     ' where '
                     || p_in_funct_key_1 || '=''' || p_in_funct_val_1 || '''  AND '
                     || p_in_funct_key_2 || ' = ''' || p_in_funct_val_2 || '''' ;


              ELSE
                     if P_IN_FUNCT_KEY_1 is not null and P_IN_FUNCT_KEY_2 is not null and P_IN_FUNCT_KEY_3 is not null then --x1

                             v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' FROM ' || v_60_table_synonym ||
                             ' WHERE '
                             || p_in_funct_key_1 || '=''' || p_in_funct_val_1 || ''' AND '
                             || p_in_funct_key_2 || '=''' || p_in_funct_val_2 || ''' AND '
                             || p_in_funct_key_3 || '=''' || p_in_funct_val_3 || '''';

                      end if; --x1

               END IF;  --x2

        END IF; --x3


                if v_exec_string is not null then
                    begin
                      execute immediate v_exec_string into v_new60_id ;
                      exception
                      when NO_DATA_FOUND then
                      RETURN NULL;
                       when TOO_MANY_ROWS then
                      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
                        (
                        GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
                        G_ISSUE_ID               =>     '1.18'      ,                       --2
                        G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
                        G_ISSUE_COL_NAME         =>     P_IN_PK_NAME                  ,                       --4
                        G_ISSUE_COL_VALUE        =>     P_IN_PK_VALUE                 ,                       --5
                        G_ISSUE_TYPE             =>     'More than one primary key derived for this functional key in TWMS_60 (lookup number function)'  ,    --6
                        G_KEY_COL_NAME_1         =>     p_in_funct_key_1    ,                                 --7
                        G_KEY_COL_VALUE_1        =>     p_in_funct_val_1    ,                                 --8
                        G_KEY_COL_NAME_2         =>     p_in_funct_key_2    ,                                 --9
                        G_KEY_COL_VALUE_2        =>     P_IN_FUNCT_VAL_2    ,                                 --10
                        G_KEY_COL_NAME_3         =>       P_IN_FUNCT_KEY_3    ,                                 --11
                        g_key_col_value_3        =>     P_IN_FUNCT_VAL_3    ,
                        G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
                        g_key_col_value_4        =>      v_exec_string    ,                                --12
                        g_ora_error_message      =>       SQLERRM             ,                                 --13
                        G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
                        G_BLOCK_NAME             =>    'Block 1'                                      --15
                       );
                      when OTHERS then
                      TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
                        (
                        GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
                        G_ISSUE_ID               =>     '1.03'      ,                       --2
                        G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
                        G_ISSUE_COL_NAME         =>     P_IN_PK_NAME                  ,                       --4
                        G_ISSUE_COL_VALUE        =>     P_IN_PK_VALUE                 ,                       --5
                        G_ISSUE_TYPE             =>     'Issue while looking up functional key in TWMS_60(lookup number function)'  ,    --6
                        G_KEY_COL_NAME_1         =>     p_in_funct_key_1    ,                                 --7
                        G_KEY_COL_VALUE_1        =>     p_in_funct_val_1    ,                                 --8
                        G_KEY_COL_NAME_2         =>     p_in_funct_key_2    ,                                 --9
                        G_KEY_COL_VALUE_2        =>     P_IN_FUNCT_VAL_2    ,                                 --10
                        G_KEY_COL_NAME_3         =>       P_IN_FUNCT_KEY_3    ,                                 --11
                        g_key_col_value_3        =>     P_IN_FUNCT_VAL_3    ,
                        G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
                        g_key_col_value_4        =>      v_exec_string    ,                                --12
                        g_ora_error_message      =>       SQLERRM             ,                                 --13
                        G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
                        G_BLOCK_NAME             =>    'Block 1'                                      --15
                       );

                      RETURN v_error_value;
                      end;
                  else
                      return v_error_value; --exec string is null
                  end if;
  END IF;  --- end if for all tables(party,config_param_mapping options/all the rest)

                   ---check to avoid PK constraint violation
               select COUNT(1) into V_PK_CNT from tav_gim_lookup_60_43_char where PK_60_ID = V_NEW60_ID;
             --  dbms_output.put_line(V_NEW60_ID || ' Count : ' || V_PK_CNT );
               if V_PK_CNT < 1 then   --x7
                        --dbms_output.put_line('Entering Proc');
                        PROC_LOOKUP_VALIDATE_PK(V_NEW60_ID,P_IN_PK_VALUE,P_IN_TABLE_NAME);
                       --dbms_output.put_line('Out of Proc' );
                      --  dbms_output.put_line(V_NEW60_ID || ' Count : ' || V_PK_CNT );
                        return V_NEW60_ID;
                        --dbms_output.put_line('Return Done');
                else                  --x7


                    --dbms_output.put_line('Into Exception');
                    TAV_GIM_INITIAL_SETUP.proc_insert_error_record
                   (GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
                    G_ISSUE_ID               =>     '1.04'      ,                       --2
                    G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
                    G_ISSUE_COL_NAME         =>     P_IN_PK_NAME                 ,                       --4
                    G_ISSUE_COL_VALUE        =>     P_IN_PK_VALUE                 ,                       --5
                    G_ISSUE_TYPE             =>     'Fucntional key did not return a unique primary key value from TWMS_60 (lookup number function)'  ,    --6
                    G_KEY_COL_NAME_1         =>     p_in_funct_key_1    ,                                 --7
                    G_KEY_COL_VALUE_1        =>     p_in_funct_val_1    ,                                 --8
                    G_KEY_COL_NAME_2         =>     p_in_funct_key_2    ,                                 --9
                    g_key_col_value_2        =>     P_IN_FUNCT_VAL_2    ,                                 --10
                    G_KEY_COL_NAME_3         =>     P_IN_FUNCT_KEY_3    ,                                 --11
                    G_KEY_COL_VALUE_3        =>     P_IN_FUNCT_VAL_3    ,
                    G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
                    g_key_col_value_4        =>      v_exec_string    ,   --12
                    G_KEY_COL_NAME_5         =>     'Derived 6.0 PK value'    ,                                 --11
                    G_KEY_COL_VALUE_5        =>      V_NEW60_ID    ,                                 --12
                    g_ora_error_message      =>       NULL             ,                                 --13
                    G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
                    G_BLOCK_NAME             =>    'Block 1'                                      --15
                   );


                   return v_error_value;
                end if;     --x7

                  ----end check to avoid PK constraint violation


end;  -- end of lookup function number
------------------------------------------

  /*
  || Function Name  : lookup_function_varchar
  || Purpose        : Function to retrive the the 6.0 varchar PK if availble using functional keys
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */

FUNCTION lookup_function_varchar(
    P_IN_JOB_SEQ_ID IN NUMBER DEFAULT -999,
    P_IN_PK_NAME    IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_VALUE   IN VARCHAR2 DEFAULT NULL,
    p_in_table_name IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_1 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_key_2 IN VARCHAR2 DEFAULT NULL,
    p_in_funct_val_2 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_KEY_3 IN VARCHAR2 DEFAULT NULL,
    P_IN_FUNCT_VAL_3 IN VARCHAR2 DEFAULT NULL
    )
  RETURN varchar2
IS
  v_new60_id varchar2(1000);
  v_exec_string varchar2(4000);
  V_PK_COLUMN_NAME varchar2(40);
  V_PK_CNT NUMBER;
  V_60_TABLE_SYNONYM VARCHAR2(40);
  v_for_criteria number;

BEGIN


IF P_IN_TABLE_NAME = 'EVAL_PRECEDENCE_PROPERTIES' THEN  --to set migrate flag based on lookkup


v_for_criteria := TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER('EVAL_PRECEDENCE_PROPERTIES','FOR_CRITERIA',p_in_funct_val_1,'R',P_IN_JOB_SEQ_ID,'TG_CRITERIA_EVALUATION_PRECED','ID','FOR_CRITERIA',0);

    SELECT COUNT(1)
    into V_PK_CNT
    FROM SN_EVAL_PRECEDENCE_PROPERTIES
    WHERE FOR_CRITERIA=V_FOR_CRITERIA
    AND PRECEDENCE    =P_IN_FUNCT_VAL_2
    AND PROP_EXPR     =P_IN_FUNCT_val_3;

    IF V_PK_CNT >0 THEN

      RETURN 'N';
      ELSE
      RETURN 'Y';

    end if;

ELSE

     select  /*+ RESULT_CACHE */ synonym_name into v_60_table_synonym from tav_gim_valid_tables where table_name = p_in_table_name;


        IF p_in_funct_key_1 IS NOT NULL AND p_in_funct_key_2 IS NULL AND p_in_funct_key_3 IS NULL THEN

           v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' from ' || v_60_table_synonym || ' where ' || p_in_funct_key_1 || '=''' || p_in_funct_val_1 || '''';

        ELSIF p_in_funct_key_1 IS NOT NULL AND p_in_funct_key_2 IS NOT NULL AND p_in_funct_key_3 IS NULL THEN

           v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' from ' || v_60_table_synonym ||
           ' where '
           || p_in_funct_key_1 || '=''' || p_in_funct_val_1 || '''  AND '
           || p_in_funct_key_2 || ' = ''' || p_in_funct_val_2 || '''' ;


        ELSE

               if p_in_funct_key_1 IS NOT NULL AND p_in_funct_key_2 IS NOT NULL AND p_in_funct_key_3 IS NOT NULL THEN

                     v_exec_string := 'SELECT ' || P_IN_PK_NAME || ' from ' || v_60_table_synonym ||
                     ' where '
                     || p_in_funct_key_1 || '=''' || p_in_funct_val_1 || ''' AND '
                     || p_in_funct_key_2 || '=''' || p_in_funct_val_2 || ''' AND '
                     || p_in_funct_key_3 || '=''' || p_in_funct_val_3 || '''';
               END IF;

        END IF;



  if v_exec_string is not null then

     begin
    execute immediate v_exec_string into v_new60_id ;
    exception
    when NO_DATA_FOUND then
         v_new60_id := null;
        return null;
    when OTHERS then
        v_new60_id := null;
        TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     '1.05'      ,                       --2
            G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>     P_IN_PK_NAME                ,                       --4
            G_ISSUE_COL_VALUE        =>     P_IN_PK_VALUE                 ,                       --5
            G_ISSUE_TYPE             =>     'Issue while looking up functioanl keys in TWMS 6.0(lookup varchar function)'  ,    --6
            G_KEY_COL_NAME_1         =>     p_in_funct_key_1    ,                                 --7
            G_KEY_COL_VALUE_1        =>     p_in_funct_val_1    ,                                 --8
            G_KEY_COL_NAME_2         =>     p_in_funct_key_2    ,                                 --9
            G_KEY_COL_VALUE_2        =>     P_IN_FUNCT_VAL_2    ,                                 --10
            G_KEY_COL_NAME_3         =>     P_IN_FUNCT_KEY_3    ,                                 --11
            g_key_col_value_3        =>     P_IN_FUNCT_VAL_3    ,
            G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
            g_key_col_value_4        =>      v_exec_string    ,--12
            g_ora_error_message      =>       SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'                                      --15
           );
        RETURN '-99';
    end;


  ELSE
      return '-99';   --execution string is null
  end if;

                select COUNT(1) into V_PK_CNT from tav_gim_lookup_60_43_char where PK_60_ID = V_NEW60_ID;
             --  dbms_output.put_line(V_NEW60_ID || ' Count : ' || V_PK_CNT );
               if V_PK_CNT < 1 then   --x7
                        --dbms_output.put_line('Entering Proc');
                        PROC_LOOKUP_VALIDATE_PK(V_NEW60_ID,P_IN_PK_VALUE,P_IN_TABLE_NAME);
                       --dbms_output.put_line('Out of Proc' );
                      --  dbms_output.put_line(V_NEW60_ID || ' Count : ' || V_PK_CNT );
                        return V_NEW60_ID;
                        --dbms_output.put_line('Return Done');

        else

            TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     '1.06'      ,                       --2
            G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>     P_IN_PK_NAME                  ,                       --4
            G_ISSUE_COL_VALUE        =>     P_IN_PK_VALUE                 ,                       --5
            G_ISSUE_TYPE             =>     'Fucntional key did not return a unique primary key from TWMS_60(lookup varchar function)'  ,    --6
            G_KEY_COL_NAME_1         =>     p_in_funct_key_1    ,                                 --7
            G_KEY_COL_VALUE_1        =>     p_in_funct_val_1    ,                                 --8
            G_KEY_COL_NAME_2         =>     p_in_funct_key_2    ,                                 --9
            g_key_col_value_2        =>     P_IN_FUNCT_VAL_2    ,                                 --10
            G_KEY_COL_NAME_3         =>     P_IN_FUNCT_KEY_3    ,                                 --11
            G_KEY_COL_VALUE_3        =>     P_IN_FUNCT_VAL_3    ,
            G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
            g_key_col_value_4        =>      v_exec_string    ,--12
            G_KEY_COL_NAME_5         =>     'Derived PK value'    ,                                 --11
            G_KEY_COL_VALUE_5        =>      V_NEW60_ID    ,                                 --12
            g_ora_error_message      =>       NULL             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'                                      --15
           );


          RETURN '-99';


      END IF;  ----end check to avoid PK constraint violation



end if;--- end if for eval_precedence properties

end;

  /*
  || Function Name  : PREFIX_FUNCTION_NUMBER
  || Purpose        : Function to prefix numerical PK's to make it 15 digit and also map child table ID's
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */
------------------------------------------
FUNCTION PREFIX_FUNCTION_NUMBER(
    P_IN_TABLE_NAME IN VARCHAR2,
    P_IN_COL_NAME IN VARCHAR2,
    P_IN_COL_VALUE IN NUMBER,
    P_IN_CON_TYPE IN CHAR DEFAULT NULL,
    P_IN_JOB_SEQ_ID IN number DEFAULT -999,
    P_IN_REF_STG_TABLE_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_REF_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_VALUE IN VARCHAR2 DEFAULT NULL)

  RETURN NUMBER
as
  V_IN_SEQ number :=0;
  V_FULL_LOAD number :=0;
  V_QUERY VARCHAR2(4000);

BEGIN


IF P_IN_CON_TYPE = 'P' then

  V_IN_SEQ := P_IN_col_value + 100000000000000;
  RETURN V_IN_SEQ;

ELSE

  --IF P_IN_CON_TYPE = 'R' then

  if P_IN_COL_VALUE is null then
  return null;
  end  if;
  
  

IF P_IN_REF_STG_TABLE_NAME NOT IN  ('TG_SHIPMENT','TG_CUSTOMER','TG_ORGANIZATION','TG_SUPPLIER','TG_DEALERSHIP') THEN
  
execute immediate 'select /*+ RESULT_CACHE */ count(1) from tav_gim_valid_tables where stg_table_name =''' || P_IN_REF_STG_TABLE_NAME || ''' and LOOKUP_FUNCTION is null and load_status like ''FULL%''' into V_FULL_LOAD;

if V_FULL_LOAD = 1 then
 V_IN_SEQ := P_IN_col_value + 100000000000000;
return V_IN_SEQ;
end if;
  
END IF;

  V_QUERY := 'SELECT ' ||  P_IN_REF_COL_NAME  || ' FROM ' || P_IN_REF_STG_TABLE_NAME || ' WHERE ' || 'OLD_43_ID' || ' = ' || P_IN_COL_VALUE;
  --dbms_output.put_line(v_query);

  BEGIN
    execute immediate V_QUERY into V_IN_SEQ;
    RETURN V_IN_SEQ;
    EXCEPTION
    WHEN OTHERS THEN
       TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       -- 1
            G_ISSUE_ID               =>    '1.07'      ,                                         -- 2
            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       -- 3
            G_ISSUE_COL_NAME         =>    P_IN_PK_COL_NAME                 ,                       -- 4
            G_ISSUE_COL_VALUE        =>    P_IN_PK_COL_VALUE                ,                       -- 5
            G_ISSUE_TYPE             =>    'Referenced value not available in its staging parent table(prefix function number)'  ,      -- 6
            G_KEY_COL_NAME_1         =>    'Parent Table Name'           ,                       -- 7
            G_KEY_COL_VALUE_1        =>    P_IN_REF_STG_TABLE_NAME       ,                       -- 8
            G_KEY_COL_NAME_2         =>    P_IN_COL_NAME                 ,                       -- 4
            G_KEY_COL_VALUE_2        =>    P_IN_COL_VALUE                ,                       -- 5
            G_KEY_COL_NAME_3         =>    'SQL Run'                     ,                       -- 11
            G_KEY_COL_VALUE_3        =>    V_QUERY                       ,                       -- 12
            G_KEY_COL_NAME_4         =>    P_IN_REF_COL_NAME             ,                       -- 9
            G_KEY_COL_VALUE_4        =>    V_IN_SEQ                      ,                       -- 10
            g_ora_error_message      =>    SQLERRM                       ,                       -- 13
            G_RUN_DATE               =>    SYSTIMESTAMP                  ,                       -- 14
            G_BLOCK_NAME             =>    'Block 1'                                            --  15
           );
    RETURN -99;
  END;

END IF;

     TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       -- 1
            G_ISSUE_ID               =>    '1.08'      ,                       -- 2
            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       -- 3
            G_ISSUE_COL_NAME         =>    P_IN_PK_COL_NAME                 ,                       -- 4
            G_ISSUE_COL_VALUE        =>    P_IN_PK_COL_VALUE                ,                       -- 5
            G_ISSUE_TYPE             =>    'Unexpected issue while processing ID in the prefix number function'  , -- 6
            G_KEY_COL_NAME_1         =>    'Parent Table Name'           ,                       -- 7
            G_KEY_COL_VALUE_1        =>    P_IN_REF_STG_TABLE_NAME       ,                       -- 8
            G_KEY_COL_NAME_2         =>    P_IN_REF_COL_NAME             ,                       -- 9
            G_KEY_COL_VALUE_2        =>    V_IN_SEQ                      ,                       -- 10
            G_KEY_COL_NAME_3         =>    'Constraint Type'             ,                       -- 11
            g_key_col_value_3        =>    P_IN_CON_TYPE                 ,                       -- 12
            g_ora_error_message      =>    SQLERRM                       ,                       -- 13
            G_RUN_DATE               =>    systimestamp                  ,                       -- 14
            G_BLOCK_NAME             =>    'Block 1'                                             -- 15
           );

RETURN -99;

END;

-------------------------------------------------------

/*
  || Function Name  : PREFIX_FUNCTION_VARCHAR
  || Purpose        : Function to retrieve varchar PK's and also map child table ID's
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 01/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */

FUNCTION PREFIX_FUNCTION_VARCHAR(
    P_IN_TABLE_NAME IN VARCHAR2,
    P_IN_COL_NAME IN VARCHAR2,
    P_IN_COL_VALUE IN VARCHAR2,
    P_IN_CON_TYPE IN VARCHAR2,
    P_IN_JOB_SEQ_ID IN number,
    P_IN_REF_STG_TABLE_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_REF_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_NAME IN VARCHAR2 DEFAULT NULL,
    P_IN_PK_COL_VALUE IN VARCHAR2 DEFAULT NULL
    )
  RETURN VARCHAR2

IS

  V_EXEC_STRING VARCHAR2(4000);
  V_QUERY varchar2(4000);
  V_RET_VALUE varchar2(1000);
  V_NEW60_ID varchar2(1000);
  V_60_TABLE_SYNONYM varchar2(50);
  v_pk_cnt number:=0;

BEGIN



IF P_IN_CON_TYPE = 'P' then


 select  /*+ RESULT_CACHE */ SYNONYM_NAME into V_60_TABLE_SYNONYM from TAV_GIM_VALID_TABLES where TABLE_NAME = P_IN_TABLE_NAME;


   v_exec_string := 'SELECT ' || P_IN_COL_NAME || ' from ' || v_60_table_synonym || ' where ' || P_IN_COL_NAME || '=''' || P_IN_COL_VALUE || '''';

    begin

    execute immediate v_exec_string into v_new60_id ;
    exception
    when NO_DATA_FOUND then
        return P_IN_COL_VALUE;
    when OTHERS then
        v_new60_id := null;
        TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     '1.18'      ,                       --2
            G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>     P_IN_COL_NAME                  ,                       --4
            G_ISSUE_COL_VALUE        =>     P_IN_COL_VALUE                 ,                       --5
            G_ISSUE_TYPE             =>     'Issue while looking up varchar primary key in TWMS 6.0(prefix varchar function)'  ,    --6
            G_KEY_COL_NAME_1         =>     'SQL RUN'    ,                                 --11
            g_key_col_value_1        =>      v_exec_string    ,--12
            g_ora_error_message      =>       SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'                                      --15
           );
        return '-99';

    end;


           select COUNT(1) into V_PK_CNT from tav_gim_lookup_60_43_char where PK_60_ID = V_NEW60_ID;

               if V_PK_CNT < 1 then   --x7

                        PROC_LOOKUP_VALIDATE_PK(V_NEW60_ID,P_IN_COL_VALUE,P_IN_TABLE_NAME);

                        return V_NEW60_ID;

               else

                      TAV_GIM_INITIAL_SETUP.proc_insert_error_record
                     (GJOB_SEQ_ID              =>     P_IN_JOB_SEQ_ID               ,                       --1
                      G_ISSUE_ID               =>     '1.19'      ,                       --2
                      G_TABLE_NAME             =>     P_IN_TABLE_NAME               ,                       --3
                      G_ISSUE_COL_NAME         =>     P_IN_COL_NAME                  ,                       --4
                      G_ISSUE_COL_VALUE        =>     P_IN_COL_VALUE                 ,                       --5
                      G_ISSUE_TYPE             =>     'Varchar primary key did not return a unique varchar primary key from TWMS_60 (prefix varchar function)'  ,    --6
                      G_KEY_COL_NAME_4         =>     'SQL RUN'    ,                                 --11
                      G_KEY_COL_VALUE_4        =>      V_EXEC_STRING    ,--12
                      G_KEY_COL_NAME_5         =>     'TEMS_60 PK value'    ,                                 --11
                      G_KEY_COL_VALUE_5        =>      V_NEW60_ID    ,                                 --12
                      g_ora_error_message      =>       NULL             ,                                 --13
                      G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
                      G_BLOCK_NAME             =>    'Block 1'                                      --15
                     );


                     RETURN '-99';


              END IF;  ----end check to avoid PK constraint violation




ELSE

--    IF P_IN_CON_TYPE = 'R' then

      if P_IN_COL_VALUE is null then
       return null;
      end  if;


      V_QUERY := 'SELECT ' ||  P_IN_REF_COL_NAME  || ' FROM ' || P_IN_REF_STG_TABLE_NAME || ' WHERE ' || 'OLD_43_ID' ||  '=''' || P_IN_COL_VALUE || '''';

      BEGIN
      EXECUTE IMMEDIATE V_QUERY INTO V_RET_VALUE;
      RETURN V_RET_VALUE;
      EXCEPTION
      WHEN OTHERS THEN
          TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>    P_IN_JOB_SEQ_ID               ,                       -- 1
            G_ISSUE_ID               =>    '1.09'      ,                       -- 2
            G_TABLE_NAME             =>    P_IN_TABLE_NAME               ,                       -- 3
            G_ISSUE_COL_NAME         =>    P_IN_PK_COL_NAME                 ,                       -- 4
            G_ISSUE_COL_VALUE        =>    P_IN_PK_COL_VALUE                ,                       -- 5
            G_ISSUE_TYPE             =>    'Referenced value not availble in its staging parent table(prefix function varchar) '  ,      -- 6
            G_KEY_COL_NAME_1         =>    'Parent Table Name'           ,                       -- 7
            G_KEY_COL_VALUE_1        =>    P_IN_REF_STG_TABLE_NAME       ,                       -- 8
            G_KEY_COL_NAME_2         =>    P_IN_COL_NAME                 ,                       -- 4
            G_KEY_COL_VALUE_2        =>    P_IN_COL_VALUE                ,                       -- 5
            G_KEY_COL_NAME_3         =>    'SQL Run'                     ,                       -- 11
            G_KEY_COL_VALUE_3        =>    V_QUERY                       ,                       -- 12
            G_KEY_COL_NAME_4         =>    P_IN_REF_COL_NAME             ,                       -- 9
            G_KEY_COL_VALUE_4        =>    V_RET_VALUE                   ,                       --  10
            g_ora_error_message      =>    SQLERRM                       ,                       -- 13
            G_RUN_DATE               =>    SYSTIMESTAMP                  ,                       -- 14
            G_BLOCK_NAME             =>    'Block 1'                                            --  15
           );
          RETURN '-99';
      END;

end if;

END;
------------------------------------------

 /*-----------------------------------------------------------------------------
 *
 * Procedure:    proc_lookup_validate_number.sql
 *
 * Purpose:     To store PK entries for tables with 1 PK ti eminate PK constraint violation
 *
 *
 *  Revision History:
 *
 *  Date           Programmer                 Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan,14, 2011   Joseph T      Written      To store PK values inorder to eliminate PK constraint violation
 *-----------------------------------------------------------------------------
*/

PROCEDURE proc_lookup_validate_pk
    (
      P_IN_PK_60_id varchar2,
      P_IN_PK_43_ID varchar2,
      p_in_table_name varchar2
    )
    IS

  PRAGMA AUTONOMOUS_TRANSACTION;

  BEGIN

    begin
    insert
    INTO tav_gim_lookup_60_43_char
     (
      PK_60_ID,
      PK_43_ID,
      TABLE_NAME
     )
    VALUES
     (
       P_IN_PK_60_id,
       P_IN_PK_43_ID,
       p_in_table_name
     );

    commit;

    -- dbms_output.put_line('No exception');
    exception
    when OTHERS then
        --  dbms_output.put_line('Excetpin occured in the inner block');
          TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>  -100,
            G_ISSUE_ID               => '1.14',
            G_TABLE_NAME             => P_IN_TABLE_NAME,
            G_ISSUE_COL_NAME         => 'Table being inserted into:',
            G_ISSUE_COL_VALUE        =>  'tav_gim_lookup_60_43_char',
            G_ISSUE_TYPE             => 'Error loading tav_gim_lookup_60_43_char table',
            G_KEY_COL_NAME_1         => '6.0 ID',
            G_KEY_COL_VALUE_1        =>  P_IN_PK_60_id,
            G_KEY_COL_NAME_2         => '4.3 ID',
            g_key_col_value_2        => P_IN_PK_43_ID,
            g_ora_error_message      => SQLERRM,
            G_RUN_DATE               => SYSTIMESTAMP,
            g_block_name             => 'Global Temp Table'
           );
     END;

exception
when others then
--dbms_output.put_line('Excetpin occured somewhere');
NULL;
END proc_lookup_validate_pk;


-------------------------------------
  /*
  || Function Name  : UPDATE_MIGRATE_FLAG
  || Purpose        : Function to update migrate flag when PK count = 1
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 20/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */


FUNCTION UPDATE_MIGRATE_FLAG(
    P_IN_PK_VALUE            IN VARCHAR2
    )
 RETURN varchar2
is
  V_PK_CNT NUMBER;

BEGIN


        select COUNT(1) into V_PK_CNT from tav_gim_lookup_60_43_char where PK_43_ID = P_IN_PK_VALUE;

        if V_PK_CNT > 0 then
                return 'N';
        else
               return 'Y';
        end if;

END;

  /*
  || Function Name  : CHECK_FULLLOAD_REF_TABS

  || Purpose        : Function to verify if the referred tables are fully loaded
  || Author         : Aswin S
  || Version        : Initial Write-Up
  || Creation Date  : 06/15/2011
  || Modification History (when, who, what)
  ||
  ||
  */

FUNCTION CHECK_FULLLOAD_REF_TABS (tab_name VARCHAR2) RETURN NUMBER IS
  v_full_load NUMBER :=0;
  CURSOR C1 IS SELECT DISTINCT rc.table_name,load_status from USER_CONSTRAINTS uc,USER_CONSTRAINTS rc,USER_CONS_COLUMNS rcc,TAV_GIM_VALID_TABLES tg
    WHERE uc.TABLE_NAME = tab_name
    AND uc.R_CONSTRAINT_NAME = rc.CONSTRAINT_NAME
    AND rcc.CONSTRAINT_NAME = uc.CONSTRAINT_NAME
    AND tg.STG_TABLE_NAME = rc.TABLE_NAME;
  BEGIN
    FOR EACH_REC IN C1 LOOP
      IF EACH_REC.load_status not in ('FULL','FULL-UPLOAD') THEN
        v_full_load := 1;
      END IF;
    END LOOP;
    RETURN v_full_load;
  END;


END TAV_GIM_UTILITIES;
/
