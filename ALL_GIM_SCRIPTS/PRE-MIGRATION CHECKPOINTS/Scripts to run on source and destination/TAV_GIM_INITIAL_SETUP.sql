CREATE OR REPLACE PACKAGE                                                                                                                                                                                                                                                             TAV_GIM_INITIAL_SETUP
AS
/*
|| Package Name   : TAV_GIM_INITIAL_SETUP
|| Purpose        : Package to handle Initial Setup for creating staging tables
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/

/*
||Global variable declarations
*/
  g_complete_ok varchar2(50) := 'COMPLETE-OK';
  g_inprog_ok varchar2(50) := 'IN-PROG-OK';
  g_comp_log_error varchar2(50) := 'COMPLETE-LOGGING-ERROR';
  G_COMP_PROC_ERROR varchar2(50) := 'COMPLETE-PROC-ERROR';
  ex_abort                      EXCEPTION;

TYPE t_valid_tables_rec
IS
  RECORD
  (
    v_table_name          VARCHAR2(30),
    v_stg_table_name      VARCHAR2(30)
   );

TYPE t_valid_tables_tab IS TABLE OF t_valid_tables_rec;
  v_valid_tables_tab t_valid_tables_tab;


FUNCTION open_log(
                   pSTATUS_CD     TAV_GIM_MASTER_LOG.CURRENT_STATUS%TYPE,
                   pjob_name      TAV_GIM_MASTER_LOG.job_name%TYPE,
                   pEXEC_START_DT TAV_GIM_MASTER_LOG.EXECUTION_START_DATE%TYPE
                                     DEFAULT SYSDATE
                  )
RETURN NUMBER;

/*
  || Procedure Name : close_log
  || Purpose        : This procedure will be used to calculate end time and execution status
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 04/11/2010
  || Modification History (when, who, what)
  ||
  */

FUNCTION close_log(
                    pjob_seq_id     TAV_GIM_MASTER_LOG.job_seq_id%TYPE,
                    pSTATUS_CD     TAV_GIM_MASTER_LOG.CURRENT_STATUS%TYPE,
                    pjob_name      TAV_GIM_MASTER_LOG.job_name%TYPE
                                      DEFAULT 'UNKNOWN'
                  )
RETURN NUMBER;

/*
  || Procedure Name : proc_insert_error_record
  || Purpose        : This procedure will be used to log exceptions during the migration
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 04/11/2010
  || Modification History (when, who, what)
  ||
  */


PROCEDURE proc_insert_error_record
    (
      gJOB_SEQ_ID NUMBER,
      G_ISSUE_ID varchar2,
      g_TABLE_NAME VARCHAR2,
      g_ISSUE_COL_NAME  VARCHAR2,
      g_ISSUE_COL_VALUE  VARCHAR2,
      g_ISSUE_TYPE  VARCHAR2,
      g_KEY_COL_NAME_1  VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_1 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_2 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_2 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_3 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_3 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_4 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_4 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_5 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_5 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_6 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_6 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_7 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_7 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_8 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_8 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_9 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_9 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_10 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_10 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_11 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_11 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_12 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_12 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_13 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_13 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_14 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_14 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_15 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_15 VARCHAR2 DEFAULT NULL,
      g_ORA_ERROR_MESSAGE VARCHAR2,
      g_RUN_DATE DATE,
      g_BLOCK_NAME VARCHAR2
    );


function GET_COMP_PK (
P_IN_PK_CONSTRAINT in varchar2,
p_in_owner in varchar2
)
return varchar2;


/*
|| Procedure Name : TAV_GIM_CREATE_STAGING_TABLE
|| Purpose        : Procedure for processing all valid tables of Migration
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_create_staging_table(p_in_60_schema_owner IN VARCHAR2,
                                       p_in_table_name     IN VARCHAR2,
                                       p_in_stg_table_name IN VARCHAR2,
                                       njob_seq_id        IN NUMBER);

/*
|| Procedure Name : PROC_CREATE_ALTER_STG_TABLES
|| Purpose        : Procedure for processing all valid tables of Migration
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE proc_create_alter_stg_tables(
p_in_60_schema_owner IN VARCHAR2,
p_in_table_cons_both  in varchar2 default 'B');

/*
|| Procedure Name : TAV_GIM_CREATE_SYNONYM
|| Purpose        : Procedure to create DB LINK/SCHEMA NAME
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_create_synonym( p_in_schema_name IN VARCHAR2 DEFAULT NULL,
                                  p_in_dblink_name IN VARCHAR2 DEFAULT NULL,
                                  p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2);

/*
|| Procedure Name : TAV_GIM_CREATE_SYNONYM_STG
|| Purpose        : Procedure to create DB LINK/SCHEMA NAME
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 02/01/2010
|| Modification History (when, who, what)
||
||
*/

PROCEDURE tav_gim_create_synonym_stg( p_in_schema_name IN VARCHAR2 DEFAULT NULL,
                                  p_in_dblink_name IN VARCHAR2 DEFAULT NULL,
                                  P_OUT_ERROR_CODE OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2);


PROCEDURE get_table_constraints_pri(
    p_in_table_name    in varchar2,
    P_IN_STG_TABLE_NAME in varchar2,
    NJOB_SEQ_ID IN NUMBER,
    p_in_owner       in varchar
    );

PROCEDURE get_table_constraints_ref(
    p_in_table_name    in varchar2,
    P_IN_STG_TABLE_NAME in varchar2,
    NJOB_SEQ_ID IN NUMBER,
    p_in_owner       in varchar
    );

END TAV_GIM_INITIAL_SETUP;
/


CREATE OR REPLACE PACKAGE BODY                                                                                                                                                                                                                                                                                                                                                                                                         TAV_GIM_INITIAL_SETUP
AS
/*
|| Package Name   : TAV_GIM_INITIAL_SETUP
|| Purpose        : Package to handle Initial Setup for creating staging tables
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/


/*
  || Procedure Name : open_log
  || Purpose        : This procedure will be used to calculate start time and execution status
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  :  Dec,27, 2010
  || Modification History (when, who, what)
  ||
  */
FUNCTION open_log(
                   pSTATUS_CD     TAV_GIM_MASTER_LOG.CURRENT_STATUS%TYPE,
                   pjob_name      TAV_GIM_MASTER_LOG.job_name%TYPE,
				   pEXEC_START_DT TAV_GIM_MASTER_LOG.EXECUTION_START_DATE%TYPE
                                     DEFAULT SYSDATE
                 )
RETURN NUMBER IS
wsjob_seq_id number :=0;

BEGIN
       SELECT TAV_GIM_JOB_SEQ.NEXTVAL INTO wsjob_seq_id FROM DUAL;

    BEGIN

       INSERT INTO TAV_GIM_MASTER_LOG
                  (
                   job_seq_id,
                   execution_start_date,
                   job_name,
                   current_status
                  )
           VALUES (
                   wsjob_seq_id,
                   pEXEC_START_DT,
                   pjob_name,
                   pSTATUS_CD
                  )
                  ;
       commit;
       RETURN wsjob_seq_id;

      END;


END open_log;


/*
  || Procedure Name : close_log
  || Purpose        : This procedure will be used to calculate end time and execution status
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  :  Dec,27, 2010
  || Modification History (when, who, what)
  ||
  */
FUNCTION close_log(
                    pjob_seq_id     TAV_GIM_MASTER_LOG.job_seq_id%TYPE,
                    pSTATUS_CD     TAV_GIM_MASTER_LOG.CURRENT_STATUS%TYPE,
                    pjob_name  TAV_GIM_MASTER_LOG.job_name%TYPE
                                     DEFAULT 'UNKNOWN'
                  )
RETURN NUMBER IS


   BEGIN
      UPDATE TAV_GIM_MASTER_LOG
			SET execution_ended_date  = SYSTIMESTAMP,
				TIME_TAKEN        		 = to_char(SYSTIMESTAMP-execution_start_date,'hh:mi:ss'),
				current_status        = pSTATUS_CD
         WHERE job_seq_id = pjob_seq_id;
      IF SQL%NOTFOUND THEN
         RAISE NO_DATA_FOUND;
      END IF;
      commit;
      RETURN 1;

END close_log;

 /*-----------------------------------------------------------------------------
 *
 * Procedure:    proc_insert_error_record.sql
 *
 * Purpose:     To log the execution of any program.
 *
 *
 *  Revision History:
 *
 *  Date           Programmer                 Description
 *  ------------   ---------------------   ------------------------------------
 *  Dec,27, 2010   Joseph 		              	Initial Coding
 *-----------------------------------------------------------------------------
*/


PROCEDURE proc_insert_error_record
    (
      gJOB_SEQ_ID NUMBER,
      G_ISSUE_ID varchar2,
      g_TABLE_NAME VARCHAR2,
      g_ISSUE_COL_NAME  VARCHAR2,
      g_ISSUE_COL_VALUE  VARCHAR2,
      g_ISSUE_TYPE  VARCHAR2,
      g_KEY_COL_NAME_1  VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_1 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_2 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_2 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_3 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_3 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_4 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_4 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_5 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_5 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_6 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_6 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_7 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_7 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_8 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_8 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_9 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_9 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_10 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_10 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_11 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_11 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_12 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_12 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_13 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_13 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_14 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_14 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_NAME_15 VARCHAR2 DEFAULT NULL,
      g_KEY_COL_VALUE_15 VARCHAR2 DEFAULT NULL,
      g_ORA_ERROR_MESSAGE VARCHAR2,
      g_RUN_DATE DATE,
      g_BLOCK_NAME VARCHAR2
    )
    IS

  PRAGMA AUTONOMOUS_TRANSACTION;
  v_issue_id VARCHAR2(100);

  BEGIN


	SELECT DECODE(Length(Substr(g_ISSUE_ID,(instr(g_ISSUE_ID,'.',1,1)+1))),
 		1,substr(g_ISSUE_ID,1,(instr(g_ISSUE_ID,'.',1,1)))||'0'||substr(g_ISSUE_ID,(instr(g_ISSUE_ID,'.',1,1)+1)),
		g_ISSUE_ID )
          INTO v_issue_id
          FROM DUAL;


    INSERT
      INTO TAV_GIM_exception_log
     (
        JOB_SEQ_ID          ,
        ISSUE_ID            ,
        TABLE_NAME          ,
        ISSUE_COL_NAME      ,
        ISSUE_COL_VALUE     ,
        ISSUE_TYPE          ,
        KEY_COL_NAME_1      ,
        KEY_COL_VALUE_1     ,
        KEY_COL_NAME_2      ,
        KEY_COL_VALUE_2     ,
        KEY_COL_NAME_3      ,
        KEY_COL_VALUE_3     ,
        KEY_COL_NAME_4      ,
        KEY_COL_VALUE_4     ,
        KEY_COL_NAME_5      ,
        KEY_COL_VALUE_5     ,
        KEY_COL_NAME_6      ,
        KEY_COL_VALUE_6     ,
        KEY_COL_NAME_7      ,
        KEY_COL_VALUE_7     ,
        KEY_COL_NAME_8      ,
        KEY_COL_VALUE_8     ,
        KEY_COL_NAME_9      ,
        KEY_COL_VALUE_9     ,
        KEY_COL_NAME_10     ,
        KEY_COL_VALUE_10    ,
        KEY_COL_NAME_11     ,
        KEY_COL_VALUE_11    ,
        KEY_COL_NAME_12     ,
        KEY_COL_VALUE_12    ,
        KEY_COL_NAME_13     ,
        KEY_COL_VALUE_13    ,
        KEY_COL_NAME_14     ,
        KEY_COL_VALUE_14    ,
        KEY_COL_NAME_15     ,
        KEY_COL_VALUE_15    ,
        ORA_ERROR_MESSAGE   ,
        RUN_DATE            ,
        BLOCK_NAME
     )
    VALUES
     (
        gJOB_SEQ_ID           ,
        V_ISSUE_ID            ,
        g_TABLE_NAME        ,
        g_ISSUE_COL_NAME      ,
        g_ISSUE_COL_VALUE     ,
        g_ISSUE_TYPE          ,
        g_KEY_COL_NAME_1      ,
        g_KEY_COL_VALUE_1     ,
        g_KEY_COL_NAME_2      ,
        g_KEY_COL_VALUE_2     ,
        g_KEY_COL_NAME_3      ,
        g_KEY_COL_VALUE_3     ,
        g_KEY_COL_NAME_4      ,
        g_KEY_COL_VALUE_4     ,
        g_KEY_COL_NAME_5      ,
        g_KEY_COL_VALUE_5     ,
        g_KEY_COL_NAME_6      ,
        g_KEY_COL_VALUE_6     ,
        g_KEY_COL_NAME_7      ,
        g_KEY_COL_VALUE_7     ,
        g_KEY_COL_NAME_8      ,
        g_KEY_COL_VALUE_8     ,
        g_KEY_COL_NAME_9      ,
        g_KEY_COL_VALUE_9     ,
        g_KEY_COL_NAME_10     ,
        g_KEY_COL_VALUE_10    ,
        g_KEY_COL_NAME_11     ,
        g_KEY_COL_VALUE_11    ,
        g_KEY_COL_NAME_12     ,
        g_KEY_COL_VALUE_12    ,
        g_KEY_COL_NAME_13     ,
        g_KEY_COL_VALUE_13    ,
        g_KEY_COL_NAME_14     ,
        g_KEY_COL_VALUE_14    ,
        g_KEY_COL_NAME_15     ,
        g_KEY_COL_VALUE_15    ,
        g_ORA_ERROR_MESSAGE   ,
        g_RUN_DATE            ,
        g_BLOCK_NAME
     );

    COMMIT;

END;


/*
|| Procedure Name : PROC_CREATE_ALTER_STG_TABLES
|| Purpose        : Procedure for processing all valid tables of Migration
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE proc_create_alter_stg_tables
(
p_in_60_schema_owner IN VARCHAR2,
p_in_table_cons_both  in varchar2 default 'B'
)
AS
  CURSOR cur_crt_table
  is
    select TABLE_NAME,STG_TABLE_NAME from TAV_GIM_VALID_TABLES where table_name not in ('ADD_PAYMENT_INFO','LINE_ITEM_GROUPS','ITEMS_IN_GROUP','I18NITEM_TEXT','MODIFIERS');
  v_alter     NUMBER                                   := 0;
  NSTATUS     number                                   := 0;
  NJOB_SEQ_ID number                                   := 0;
  V_ORA_VER  NUMBER                                    := 0;
  v_migr_date DATE                                     := systimestamp;
  v_program_name tav_gim_exception_log.table_name%TYPE := 'CREATE STAGE';
  cur_crt_rec cur_crt_table%rowtype;
  cur_alt_rec_pri cur_crt_table%rowtype;
  cur_alt_rec_ref cur_crt_table%rowtype;

BEGIN


--  SELECT COUNT(1) INTO V_ORA_VER
--  FROM PRODUCT_COMPONENT_VERSION
--  WHERE PRODUCT LIKE 'Oracle%Database%Enterprise%Edition '
--  AND SUBSTR(VERSION,1,2) = '11' ;

  V_ORA_VER := 1;

  if V_ORA_VER = 1 then
  execute immediate 'alter session set DEFERRED_SEGMENT_CREATION=FALSE';
  end if;

  if (p_in_table_cons_both = 'T' or p_in_table_cons_both  = 'B') then   --- to create staging tables
  --open log
  njob_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migr_date);

  FOR cur_crt_rec IN cur_crt_table
  LOOP
    BEGIN
      tav_gim_initial_setup.tav_gim_create_staging_table(p_in_60_schema_owner,cur_crt_rec.table_name,cur_crt_rec.stg_table_name, njob_seq_id);
    END;
  END LOOP;
  ------closing log start---
  nstatus        := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

  end if;

  if (p_in_table_cons_both = 'C' or p_in_table_cons_both  = 'B') then --- to create constraints

  v_program_name := 'CREATE CONSTRAINTS';
  --open log
  njob_seq_id         := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,systimestamp);
  FOR cur_alt_rec_pri IN cur_crt_table
  LOOP
    BEGIN
      tav_gim_initial_setup.get_table_constraints_pri( cur_alt_rec_pri.table_name,cur_alt_rec_pri.stg_table_name, njob_seq_id,p_in_60_schema_owner );
    END;
  END LOOP;
  FOR cur_alt_rec_ref IN cur_crt_table
  LOOP
    BEGIN
      tav_gim_initial_setup.get_table_constraints_ref( cur_alt_rec_ref.table_name,cur_alt_rec_ref.stg_table_name, njob_seq_id,p_in_60_schema_owner);
    END;
  END LOOP;
  ------closing log start---
  nstatus := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

  end if;


EXCEPTION
WHEN OTHERS THEN
  ROLLBACK;
  TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD (GJOB_SEQ_ID => NJOB_SEQ_ID,
                                               g_issue_id => '2.04',
                                               g_table_name => v_program_name,
                                               g_issue_col_name => NULL,
                                               g_issue_col_value => NULL,
                                               g_issue_type => 'Program abended - ISSUE IN THE MAIN BLOCK',
                                               g_ora_error_message => SQLERRM,
                                               g_run_date => SYSTIMESTAMP,
                                               g_block_name => 'Issue in the main block' );
  nstatus := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_comp_proc_error );
END proc_create_alter_stg_tables;



/*
|| Procedure Name : TAV_GIM_CREATE_STAGING_TABLE
|| Purpose        : Procedure for processing all valid tables of Migration
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_create_staging_table(p_in_60_schema_owner IN VARCHAR2,
                                       p_in_table_name     IN VARCHAR2,
                                       p_in_stg_table_name IN VARCHAR2,
                                       njob_seq_id        IN NUMBER)
AS
  v_sql_stmt        VARCHAR2(4000);
  v_alter           NUMBER:=0;
  v_pk_column_name  VARCHAR2(500);
  v_pk_column_count NUMBER;
BEGIN


  SELECT COUNT(A.column_name)
  INTO v_pk_column_count
  FROM user_cons_columns A ,
    user_constraints B
  WHERE A.table_name    = p_in_table_name
  AND A.table_name      = B.table_name
  AND A.constraint_name = B.constraint_name
  AND B.constraint_type = 'P';


  IF V_PK_COLUMN_COUNT = 1 THEN

        SELECT A.column_name
        INTO v_pk_column_name
        FROM user_cons_columns A ,
          user_constraints B
        WHERE A.table_name    = p_in_table_name
        AND A.table_name      = B.table_name
        AND A.CONSTRAINT_NAME = B.CONSTRAINT_NAME
        AND B.constraint_type = 'P';

      v_sql_stmt:= 'CREATE TABLE ' || p_in_stg_table_name || ' NOLOGGING AS SELECT stg.*,' || v_pk_column_name ||' AS OLD_43_ID,''Y'' AS MIGRATE_FLAG  FROM ' || p_in_60_schema_owner ||'.'|| p_in_table_name || ' stg ' || ' WHERE 1=2';

  ELSE

      v_sql_stmt:= 'CREATE TABLE ' || p_in_stg_table_name || ' NOLOGGING AS SELECT stg.*,''Y'' AS MIGRATE_FLAG  FROM ' || p_in_60_schema_owner ||'.' || p_in_table_name || ' stg ' || ' WHERE 1=2';

  END IF;

    BEGIN
      EXECUTE IMMEDIATE v_sql_stmt;
    EXCEPTION
    WHEN OTHERS THEN
      v_alter := 1;
      tav_gim_initial_setup.proc_insert_error_record (
                       gjob_seq_id        => njob_seq_id,
                       g_issue_id         => '2.01',
                       g_table_name       => 'Staging table creation script',
                       g_issue_col_name   => 'SQL',
                       g_issue_col_value  => v_sql_stmt,
                       g_issue_type       => 'Error occured while creating staging table',
                       g_key_col_name_1   => 'Table_Name',
                       g_key_col_value_1  => p_in_table_name,
                       g_key_col_name_2   => 'Staging_Table',
                       g_key_col_value_2  => p_in_stg_table_name,
                       g_ora_error_message=> SQLERRM,
                       g_run_date         => systimestamp,
                       g_block_name       => 'Block 1' );
    END;

    IF v_alter <> 1 THEN

        BEGIN
          UPDATE tav_gim_valid_tables
          SET stg_created_flag = 'Y'
          WHERE upper(table_name)     = upper(p_in_table_name);
          EXCEPTION
          WHEN OTHERS THEN
          tav_gim_initial_setup.proc_insert_error_record (
                            gjob_seq_id         => njob_seq_id,
                            g_issue_id          => '2.02',
                            g_table_name        => 'TAV_GIM_VALID_TABLES',
                            g_issue_col_name    => 'Table_Name',
                            g_issue_col_value   => P_IN_TABLE_NAME,
                            g_issue_type        => 'Error while updating STG_CREATED_FLAG flag',
                            g_key_col_name_1    => 'Table_Name',
                            g_key_col_value_1   => p_in_table_name,
                            g_ora_error_message => SQLERRM,
                            g_run_date          => systimestamp,
                            g_block_name        => 'Block 1' );
        END;
        commit;

    END IF;

EXCEPTION
WHEN OTHERS THEN
tav_gim_initial_setup.proc_insert_error_record (
                          gjob_seq_id         => njob_seq_id,
                          g_issue_id          => '2.03',
                          g_table_name        => 'TAV_GIM_VALID_TABLES',
                          g_issue_col_name    => 'Table_Name',
                          G_ISSUE_COL_VALUE   => P_IN_TABLE_NAME,
                          g_issue_type        => 'Issue at table creation n the main block',
                          g_key_col_name_1    => 'Table_Name',
                          g_key_col_value_1   => p_in_table_name,
                          g_ora_error_message => SQLERRM,
                          g_run_date          => systimestamp,
                          g_block_name        => 'Block 1' );
end TAV_GIM_CREATE_STAGING_TABLE;



/*
|| Procedure Name : TAV_GIM_CREATE_SYNONYM
|| Purpose        : Procedure to create DB LINK/SCHEMA NAME
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 25/01/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_create_synonym( p_in_schema_name IN VARCHAR2 DEFAULT NULL,
                                  p_in_dblink_name IN VARCHAR2 DEFAULT NULL,
                                  p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2)
AS

v_stmt_number NUMBER;
v_error_code NUMBER;
v_error_message VARCHAR2(4000);
v_table_name VARCHAR2(30);
v_stg_table_name VARCHAR2(30);
v_ddl_stmt VARCHAR2(4000);
V_SYNONYM_NAME varchar2(30);
v_syn_exists number;

-- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;

BEGIN

    dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));
    v_stmt_number := 10;


    v_program_name := 'TAV_GIM_CREATE_SYNONYM';
    v_migration_date := SYSTIMESTAMP;
    v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);

    v_stmt_number := 20;
    -- Selecting table name / staging table name from TAV_GIM_VALID_TABLES
    SELECT table_name,stg_table_name
    BULK COLLECT INTO v_valid_tables_tab
    FROM tav_gim_valid_tables
    WHERE exec_order IS NOT NULL
    ORDER BY exec_order;

    v_stmt_number := 30;
    -- Start the processing for SCHEMA NAME
    IF p_in_schema_name IS NOT NULL THEN
      -- Start of the master loop for processing all the tables in TAV_GIM_VALID_TABLES
      FOR i IN v_valid_tables_tab.FIRST..v_valid_tables_tab.LAST LOOP

        -- Assigning iterative variables
        v_table_name := v_valid_tables_tab(i).v_table_name;
        V_STG_TABLE_NAME := V_VALID_TABLES_TAB(I).V_STG_TABLE_NAME;
        v_syn_exists := 0;

        v_stmt_number := 40;
        -- Framing the synonym name based on staging table name
--        SELECT REPLACE(v_stg_table_name,'TG_','SN_')
--          INTO v_synonym_name
--        FROM DUAL;

        select 'SN_' ||SUBSTR(v_stg_table_name,4)
        into V_SYNONYM_NAME
        FROM DUAL;


        select COUNT(1)
        into v_syn_exists
        from USER_SYNONYMS
        where upper(SYNONYM_NAME) = upper(v_synonym_name);

        if V_SYN_EXISTS > 0 then

         EXECUTE IMMEDIATE 'DROP SYNONYM ' || v_synonym_name;

        end if;


        v_stmt_number := 50;
        -- Framing the CREATE SYNONYM DDL statement
        v_ddl_stmt := 'CREATE SYNONYM ' || v_synonym_name || ' FOR ' || p_in_schema_name || '.' || v_table_name;

        --dbms_output.put_line(v_ddl_stmt);

        v_stmt_number := 60;
        EXECUTE IMMEDIATE v_ddl_stmt;

       v_stmt_number := 70;
       -- Updating TAV_GIM_MASTER_TABLES with the SYNONYM NAME for the processed table
       UPDATE tav_gim_valid_tables
       SET synonym_name = v_synonym_name
       WHERE table_name = v_table_name;

       COMMIT;

      END LOOP;

         EXECUTE IMMEDIATE 'CREATE OR REPLACE SYNONYM SN_SHIPMENT_SEQ FOR ' || p_in_schema_name || '.SHIPMENT_SEQ' ;
      -- End of the master loop for processing all the tables in TAV_GIM_VALID_TABLES
    END IF;
    -- End of processing for SCHEMA NAME

    v_stmt_number := 80;
    -- Start the processing for DBLINK
    IF p_in_dblink_name IS NOT NULL THEN
      FOR i IN v_valid_tables_tab.FIRST..v_valid_tables_tab.LAST LOOP

         -- Assigning iterative variables
         v_table_name := v_valid_tables_tab(i).v_table_name;
         v_stg_table_name := v_valid_tables_tab(i).v_stg_table_name;

        v_stmt_number := 90;
        -- Framing the synonym name based on staging table name
--        SELECT REPLACE(v_stg_table_name,'TG_','SN_')
--          INTO v_synonym_name
--        FROM DUAL;


        select 'SN_' ||SUBSTR(v_stg_table_name,4)
        into v_synonym_name
        FROM DUAL;

        select COUNT(1)
        into v_syn_exists
        from USER_SYNONYMS
        where upper(SYNONYM_NAME) = upper(v_synonym_name);

        if V_SYN_EXISTS > 0 then

         EXECUTE IMMEDIATE 'DROP SYNONYM ' || v_synonym_name;

        end if;

       v_stmt_number := 100;
       -- Framing the CREATE SYNONYM DDL statement
       v_ddl_stmt := 'CREATE SYNONYM ' || v_synonym_name || ' FOR ' || v_table_name || '@' || p_in_dblink_name;

       --dbms_output.put_line(v_ddl_stmt);

        EXECUTE IMMEDIATE v_ddl_stmt;

       v_stmt_number := 110;
       -- Updating TAV_GIM_MASTER_TABLES with the SYNONYM NAME for the processed table
       UPDATE tav_gim_valid_tables
       SET synonym_name = v_synonym_name
       WHERE table_name = v_table_name;

       COMMIT;

      END LOOP;

      EXECUTE IMMEDIATE 'CREATE OR REPLACE SYNONYM SN_SHIPMENT_SEQ FOR SHIPMENT_SEQ' || '@' || p_in_dblink_name;

    END IF;


   -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );

   p_out_error_code := 0;
   p_out_error_message := 'Procedure TAV_GIM_CREATE_SYNONYM executed successfully';

EXCEPTION
WHEN OTHERS THEN
 tav_gim_initial_setup.proc_insert_error_record (
                            gjob_seq_id         => v_job_seq_id,
                            g_issue_id          => '4.01',
                            G_TABLE_NAME        => 'TAV_GIM_VALID_TABLES',
                            G_ISSUE_COL_NAME    => 'Cause:',
                            G_ISSUE_COL_VALUE   => 'Synonym creation failed',
                            G_ISSUE_TYPE        => 'Error while drop/creating synonyms',
                            g_key_col_name_1    => 'Procedure_Name',
                            g_key_col_value_1   => 'tav_gim_create_synonym',
                            g_ora_error_message => SQLERRM,
                            g_run_date          => systimestamp,
                            g_block_name        => 'Block 1' );
  -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_log_error );
END;


/*
  || Function Name  : GET_COMP_PK
  || Purpose        : Function to retrieve composite PK's seperated by comma's
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 23/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */

function GET_COMP_PK (
P_IN_PK_CONSTRAINT in varchar2,
p_in_owner in varchar2
)
return varchar2
as
V_PK VARCHAR2(100);
begin
for i in
(
select COLUMN_NAME from ALL_CONS_COLUMNS where CONSTRAINT_NAME = P_IN_PK_CONSTRAINT and OWNER = P_IN_OWNER
order by position
)
LOOP
V_PK := V_PK || ',' || I.COLUMN_NAME;
END LOOP;
V_PK := ltrim(V_PK,',');
return V_PK;
EXCEPTION when OTHERS then
null;
END;


--------------------------------------------
  /*
  || Procedure Name  : get_table_constraints_pri
  || Purpose        : Procedure to add primary constraints to the staging tables
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 23/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */


PROCEDURE get_table_constraints_pri(
    P_IN_TABLE_NAME    in varchar2,
    P_IN_STG_TABLE_NAME in varchar2,
    NJOB_SEQ_ID         NUMBER,
    p_in_owner       in varchar
    )
AS
  v_query_1   varchar2(4000) := null;
  v_sql_string VARCHAR2(32000) := NULL;
  v_exec_string VARCHAR2(32000) := NULL;

begin

v_query_1 := 'alter table ' || p_in_stg_table_name || ' add (';

begin
with w_cons as
(
select distinct
case
when length('TG_' || a.CONSTRAINT_NAME)>30 then ('TG_' || SUBSTR(a.CONSTRAINT_NAME,4,30))
else ('TG_' || a.CONSTRAINT_NAME)
end  CONSTRAINT_NAME,
A.constraint_type,
a.r_constraint_name fk_constraint_name,
tav_gim_initial_setup.get_comp_pk(a.CONSTRAINT_NAME,a.OWNER) column_name,
decode(a.status,'ENABLED','ENABLE','DISABLE') status
FROM ALL_CONSTRAINTS A
WHERE A.TABLE_NAME = P_IN_TABLE_NAME
AND A.CONSTRAINT_TYPE IN ('P')
AND A.OWNER = p_in_owner
)
SELECT
('CONSTRAINT "' || A.CONSTRAINT_NAME || '" PRIMARY KEY (' || A.COLUMN_NAME ||   ') ' || A.STATUS)
STRING_CONS
into v_sql_string
FROM W_CONS A;
EXCEPTION
  WHEN OTHERS THEN
  NULL;
  END;

IF v_sql_string IS NOT NULL THEN

  V_EXEC_STRING := V_QUERY_1 || V_SQL_STRING || ')';
  begin
  execute immediate V_EXEC_STRING;
  EXCEPTION
  when OTHERS then
          TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              => NJOB_SEQ_ID,
            G_ISSUE_ID               => '1.12',
            G_TABLE_NAME             => 'Add PK Constraint',
            G_ISSUE_COL_NAME         => 'SQL',
            G_ISSUE_COL_VALUE        =>  V_EXEC_STRING,
            G_ISSUE_TYPE             => 'Error while adding primary key table constraints',
            G_KEY_COL_NAME_1         => 'Table_Name',
            G_KEY_COL_VALUE_1        =>  P_IN_TABLE_NAME,
            G_KEY_COL_NAME_2         => 'Stage Table Name',
            g_key_col_value_2        => P_IN_STG_TABLE_NAME,
            g_ora_error_message      => SQLERRM,
            G_RUN_DATE               => systimestamp,
            g_block_name             => 'Block 1'
           );
  END;
end if;
end;


--------------------------------------------
  /*
  || Procedure Name  : get_table_constraints_ref
  || Purpose        : Procedure to add reference constraints to the staging tables
  || Author         : Joseph Tharakan
  || Version        : Initial Write-Up
  || Creation Date  : 23/12/2010
  || Modification History (when, who, what)
  ||
  ||
  */


PROCEDURE get_table_constraints_ref(
    P_IN_TABLE_NAME    in varchar2,
    P_IN_STG_TABLE_NAME in varchar2,
    NJOB_SEQ_ID         NUMBER,
    p_in_owner       in varchar
    )
AS
  v_query_1   varchar2(4000) := null;
  v_sql_string VARCHAR2(32000) := NULL;
  v_sql_string_1 VARCHAR2(32000) := NULL;
  v_exec_string VARCHAR2(32000) := NULL;
  V_LEN number :=0 ;


begin

v_query_1 := 'alter table ' || p_in_stg_table_name || ' add (';


FOR i IN
(
with w_cons as
(
select distinct
case
when length('TG_' || a.CONSTRAINT_NAME)>30 then ('TG_' || SUBSTR(a.CONSTRAINT_NAME,4,30))
else ('TG_' || a.CONSTRAINT_NAME)
end  CONSTRAINT_NAME,
A.CONSTRAINT_TYPE,
a.owner,
a.r_constraint_name fk_constraint_name,
a.TABLE_NAME,
tav_gim_initial_setup.get_comp_pk(a.CONSTRAINT_NAME,a.OWNER) column_name,
decode(a.status,'ENABLED','ENABLE','DISABLE') status,
b.table_name referenced_table,
B.COLUMN_NAME REFERENCED_COLUMN
FROM ALL_CONSTRAINTS A , ALL_CONS_COLUMNS B
WHERE A.TABLE_NAME = p_in_table_name
and a.constraint_type in ('R','U')
AND A.R_CONSTRAINT_NAME = B.CONSTRAINT_NAME(+)
AND A.OWNER = p_in_owner
and b.OWNER(+) = p_in_owner
order by a.constraint_type
)
SELECT
(
decode(constraint_type,'R',
('CONSTRAINT "' || A.CONSTRAINT_NAME || '" FOREIGN KEY (' || A.COLUMN_NAME || ')'
|| ' REFERENCES '  || B.STG_TABLE_NAME || '(' || A.REFERENCED_COLUMN || ') ' || A.STATUS || ','),
('CONSTRAINT "' || A.CONSTRAINT_NAME || '" UNIQUE (' || a.column_name ||   ') ' || A.STATUS || ',')
)
) STRING_CONS,
A.CONSTRAINT_TYPE
FROM W_CONS A,TAV_GIM_VALID_TABLES B
WHERE A.REFERENCED_TABLE = B.TABLE_NAME(+)
)

LOOP
v_sql_string   := v_sql_string || i.string_cons;
END loop;


IF v_sql_string IS NOT NULL THEN

  SELECT RTRIM(v_sql_string,',') INTO v_sql_string_1 FROM dual;
  V_EXEC_STRING := V_QUERY_1 || V_SQL_STRING_1 || ')';
  begin
  execute immediate V_EXEC_STRING;
  EXCEPTION
  when OTHERS then
          TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              => NJOB_SEQ_ID,
            G_ISSUE_ID               => '1.13',
            G_TABLE_NAME             => 'Add FK Constraint',
            G_ISSUE_COL_NAME         => 'SQL',
            G_ISSUE_COL_VALUE        =>  V_EXEC_STRING,
            G_ISSUE_TYPE             => 'Error while adding foreign key constraints',
            G_KEY_COL_NAME_1         => 'Table_Name',
            G_KEY_COL_VALUE_1        =>  P_IN_TABLE_NAME,
            G_KEY_COL_NAME_2         => 'Stage Table Name',
            g_key_col_value_2        => P_IN_STG_TABLE_NAME,
            g_ora_error_message      => SQLERRM,
            G_RUN_DATE               => systimestamp,
            g_block_name             => 'Block 1'
           );
  END;
end if;
END;





/*
|| Procedure Name : TAV_GIM_CREATE_SYNONYM
|| Purpose        : Procedure to create synonyms for staging across DB LINK/SCHEMA NAME
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/02/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_create_synonym_stg( p_in_schema_name IN VARCHAR2 DEFAULT NULL,
                                  p_in_dblink_name IN VARCHAR2 DEFAULT NULL,
                                  p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2)
AS

v_stmt_number NUMBER;
v_error_code NUMBER;
v_error_message VARCHAR2(4000);
v_table_name VARCHAR2(30);
v_stg_table_name VARCHAR2(30);
v_ddl_stmt VARCHAR2(4000);
v_synonym_name VARCHAR2(30);

-- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;

BEGIN

    dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));
    v_stmt_number := 10;


    v_program_name := 'TAV_GIM_CREATE_SYNONYM_STG';
    v_migration_date := SYSTIMESTAMP;
    v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);

    v_stmt_number := 20;
    -- Selecting table name / staging table name from TAV_GIM_VALID_TABLES
    SELECT table_name,stg_table_name
    BULK COLLECT INTO v_valid_tables_tab
    FROM tav_gim_valid_tables
    WHERE exec_order IS NOT NULL
    ORDER BY exec_order;

    v_stmt_number := 30;
    -- Start the processing for SCHEMA NAME
    IF p_in_schema_name IS NOT NULL THEN
      -- Start of the master loop for processing all the tables in TAV_GIM_VALID_TABLES
      FOR i IN v_valid_tables_tab.FIRST..v_valid_tables_tab.LAST LOOP

        -- Assigning iterative variables
        v_table_name := v_valid_tables_tab(i).v_table_name;
        v_stg_table_name := v_valid_tables_tab(i).v_stg_table_name;

        v_stmt_number := 40;
        -- Framing the synonym name based on staging table name

        SELECT 'ST_' ||SUBSTR(v_stg_table_name,4)
        INTO v_table_name
        FROM DUAL;

        v_stg_table_name :=v_synonym_name;

        v_stmt_number := 50;
        -- Framing the CREATE SYNONYM DDL statement
        v_ddl_stmt := 'CREATE SYNONYM ' || v_synonym_name || ' FOR ' || p_in_schema_name || '.' || v_table_name;

        --dbms_output.put_line(v_ddl_stmt);

        v_stmt_number := 60;
        EXECUTE IMMEDIATE v_ddl_stmt;

       v_stmt_number := 70;
       -- Updating TAV_GIM_MASTER_TABLES with the SYNONYM NAME for the processed table
       UPDATE tav_gim_valid_tables
       SET synonym_name = v_synonym_name
       WHERE table_name = v_table_name;

       COMMIT;

      END LOOP;
      -- End of the master loop for processing all the tables in TAV_GIM_VALID_TABLES
    END IF;
    -- End of processing for SCHEMA NAME

    v_stmt_number := 80;
    -- Start the processing for DBLINK
    IF p_in_dblink_name IS NOT NULL THEN
      FOR i IN v_valid_tables_tab.FIRST..v_valid_tables_tab.LAST LOOP

         -- Assigning iterative variables
         v_table_name := v_valid_tables_tab(i).v_table_name;
         v_stg_table_name := v_valid_tables_tab(i).v_stg_table_name;

        v_stmt_number := 90;
        -- Framing the synonym name based on staging table name
        SELECT 'ST_' ||SUBSTR(v_stg_table_name,4)
        INTO v_table_name
        FROM DUAL;

        v_stg_table_name :=v_synonym_name;

       v_stmt_number := 100;
       -- Framing the CREATE SYNONYM DDL statement
       v_ddl_stmt := 'CREATE SYNONYM ' || v_synonym_name || ' FOR ' || v_table_name || '@' || p_in_dblink_name;

       --dbms_output.put_line(v_ddl_stmt);

        EXECUTE IMMEDIATE v_ddl_stmt;

       v_stmt_number := 110;
       -- Updating TAV_GIM_MASTER_TABLES with the SYNONYM NAME for the processed table
       UPDATE tav_gim_valid_tables
       SET synonym_name = v_synonym_name
       WHERE table_name = v_table_name;

       COMMIT;

      END LOOP;
    END IF;


   -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );

   p_out_error_code := 0;
   p_out_error_message := 'Procedure TAV_GIM_CREATE_SYNONYM executed successfully';

EXCEPTION
WHEN OTHERS THEN
dbms_output.put_line('Exception Occured' || SUBSTR(SQLERRM,1,255));
  -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_log_error );
END;


END TAV_GIM_INITIAL_SETUP;
/
