CREATE OR REPLACE PACKAGE                                                                                                                                                                                                                                                                                                                                                                                                                                                                               TAV_GIM_PROCESS_MIGRATION AS
/*
|| Package Name   : TAV_GIM_PROCESS_MIGRATION
|| Purpose        : Package to bundle master procedures for data migration
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
TYPE t_valid_tables_rec
IS
  RECORD
  (
    v_table_name          VARCHAR2(30),
    v_stg_table_name      VARCHAR2(30),
    v_lookup_function     VARCHAR2(500)
   );

TYPE t_valid_tables_tab IS TABLE OF t_valid_tables_rec;
  v_valid_tables_tab t_valid_tables_tab;

TYPE t_table_column_names IS TABLE OF VARCHAR2(500);
  v_table_column_name t_table_column_names;

TYPE t_stg_table_column_names IS TABLE OF VARCHAR2(500);
  v_stg_table_column_name t_stg_table_column_names;

TYPE t_varchar_tab_array IS TABLE OF VARCHAR2(500);

v_varchar_tab_array t_varchar_tab_array;

/*
|| Procedure Name : TAV_GIM_PROCESS_MASTER
|| Purpose        : Procedure for processing all valid tables of Migration
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_process_master(p_out_error_code OUT NUMBER,
                                 p_out_error_message OUT VARCHAR2);
                                 
/*
|| Procedure Name : TAV_GIM_UPDATE_CLAIM_LOB/TAV_GIM_UPDATE_DOC_LOB
|| Purpose        : To update blobs
|| Author         : Joseph
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/                                 
procedure TAV_GIM_UPDATE_CLAIM_LOB;

procedure TAV_GIM_UPDATE_DOC_LOB;




/*
|| Function Name  : ISPRIMARYKEY
|| Purpose        : Function to determine whether a given column is a primary key
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION isprimarykey(p_in_table_name IN VARCHAR2,p_in_column_name IN VARCHAR2 ) RETURN NUMBER;
/*
|| Function Name  : ISFOREIGNKEY
|| Purpose        : Function to determine whether a given column is a primary key
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION isforeignkey(p_in_table_name IN VARCHAR2,p_in_column_name IN VARCHAR2 ) RETURN NUMBER;

/*
|| Function Name  : GET_LOOKUP_FUNCTION
|| Purpose        : Function to retrieve the lookup function entry from master table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_lookup_function(p_in_table_name IN VARCHAR2) RETURN VARCHAR2;

/*
|| Function Name  : GET_REF_CONSTRAINT_NAME
|| Purpose        : Function to retrieve reference constraint name for a given table and column name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_ref_constraint_name(p_in_table_name IN VARCHAR2, p_in_column_name IN VARCHAR2) RETURN VARCHAR2;

/*
|| Function Name  : GET_COLUMN_DATATYPE
|| Purpose        : Function to retrieve data type of a given column name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_column_datatype(p_in_table_name IN VARCHAR2, p_in_column_name IN VARCHAR2) RETURN VARCHAR2;

/*
|| Function Name  : GET_PK_COLUMN_STRING
|| Purpose        : Function to frame the prefix function string
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_pk_column_string(p_in_table_name IN VARCHAR2,
                              p_in_column_name IN VARCHAR2,
                              p_in_constraint_type IN VARCHAR2,
                              p_in_job_seq_id IN NUMBER,
                              p_in_ref_stg_table_name IN VARCHAR2 DEFAULT NULL,
                              p_in_ref_column_name IN VARCHAR2 DEFAULT NULL) RETURN VARCHAR2;

/*
|| Function Name  : GET_LOOKUP_FUNCTION_STR
|| Purpose        : Function to frame the lookup function string
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_lookup_function_str(p_in_table_name IN VARCHAR2,
                                 p_in_column_name IN VARCHAR2,
                                 p_in_lookup_function_entry IN VARCHAR2,
                                 p_in_job_seq_id IN NUMBER) RETURN VARCHAR2;

/*
|| Function Name  : IS_CYCLIC_REF_COLUMN
|| Purpose        : Function to determine whether the table columns are having cyclic references
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION is_cyclic_ref_column(p_in_table_name IN VARCHAR2,p_in_column_name IN VARCHAR2) RETURN NUMBER;

/*
|| Function Name  : GET_PK_COLUMN_NAME
|| Purpose        : Function to retrieve the primary key column name for a given table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_pk_column_name(p_in_table_name IN VARCHAR2) RETURN VARCHAR2;

/*
|| Procedure Name : INSERT_STAGING_TABLES
|| Purpose        : Procedure to insert data into staging tables
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE insert_staging_tables(p_in_select_query IN VARCHAR2,
                                p_in_insert_column_str IN VARCHAR2,
                                p_in_table_name IN VARCHAR2,
                                p_in_stg_table_name IN VARCHAR2,
                                p_out_error_code OUT NUMBER,
                                p_out_error_message OUT VARCHAR2);

/*
|| Function Name  : ISCOMPOSITEKEY
|| Purpose        : Function to determine whether the table has composite keys
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION iscompositekey(p_in_table_name IN VARCHAR2) RETURN NUMBER;

/*
|| Procedure Name : CREATE_INDEX_GATHER_STATISTICS
|| Purpose        : Procedure to create index and gather statistics
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE create_index_gather_statistics(p_in_stg_table_name IN VARCHAR2, p_out_error_code OUT NUMBER, p_out_error_message OUT VARCHAR2);

/*
|| Function Name  : GET_EXECUTION_ORDER
|| Purpose        : Function to retrieve the execution order of the given table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_execution_order(p_in_table_name IN VARCHAR2) RETURN NUMBER;

/*
|| Function Name  : GET_SOURCE_TABLE_NAME
|| Purpose        : Function to retrieve the source table name for a given staging table name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_source_table_name(p_in_stg_table_name IN VARCHAR2) RETURN VARCHAR2;

/*
|| Function Name  : GET_STAGING_TABLE_NAME
|| Purpose        : Function to retrieve the staging table name for a given source table name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_staging_table_name(p_in_table_name IN VARCHAR2) RETURN VARCHAR2;

/*
|| Function Name  : GET_MIGRATION_FLAG_ENTRY
|| Purpose        : Function to retrieve string for Migration Flag function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_migration_flag_entry(p_in_column_value IN VARCHAR2) RETURN VARCHAR2;

/*
|| Procedure Name  : TAV_GIM_UPDATE_CYCLIC_REF
|| Purpose        : Procedure to udte cyclic/self references to stage and to 6.0
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/
procedure tav_gim_update_cyclic_ref_60;

PROCEDURE tav_gim_update_cyclic_ref;

/*
|| Procedure Name  : TAV_GIM_UPDATE_CYCLIC_REF
|| Purpose        : Procedure to udte cyclic/self references to stage and to 6.0
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/

procedure TAV_GIM_UPDATE_BLOBS_60;

procedure TAV_GIM_UPDATE_BLOBS;

/*
|| Procedure Name  : TAV_GIM_POPULATE_MASTER
|| Purpose        : Procedure to populate 6.0 tables from staging
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_populate_master(p_out_error_code OUT NUMBER,p_out_error_message OUT VARCHAR2);

/*
|| Procedure Name : INSERT_60_TABLES
|| Purpose        : Procedure to insert data into 6.0 tables
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE insert_60_tables(p_in_insert_query IN VARCHAR2,
                           p_in_table_name IN VARCHAR2,
                           p_out_error_code OUT NUMBER,
                           p_out_error_message OUT VARCHAR2);


/*
|| Function Name  : GET_MIGRATION_FLAG_STR
|| Purpose        : Function to retrieve string for Migration Flag function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
/*
FUNCTION get_migration_flag_str(p_in_table_name IN VARCHAR2, p_in_stg_table_name IN VARCHAR2, p_in_job_seq_id IN NUMBER) RETURN VARCHAR2;
*/
/*
|| Function Name  : GET_CONSTRAINT_KEY_COUNT
|| Purpose        : Function to retrieve count of primary key or foreign key
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_constraint_key_count(p_in_table_name IN VARCHAR2, p_in_constraint_type IN VARCHAR2) RETURN VARCHAR2;

/*
|| Procedure Name : UPDATE_MIGRATE_FLAG
|| Purpose        : Procedure to update migrate flag
|| Author         : Joseph
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE update_migrate_flag(p_in_table_name IN VARCHAR2,
                              p_in_stg_table_name IN VARCHAR2,
                              p_out_error_code OUT NUMBER,
                              p_out_error_message OUT VARCHAR2);



/*
|| Procedure Name : UPDATE_CONFIG_VALUE
|| Purpose        : Procedure to update config value table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 18/03/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE update_config_value(p_out_error_code OUT NUMBER,
                              p_out_error_message OUT VARCHAR2);

/*
|| Function Name  : GET_SYNONYM_NAME
|| Purpose        : Function to retrieve the framed synonym name for a given table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 27/01/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_synonym_name(p_in_table_name IN VARCHAR2) RETURN VARCHAR2;


END TAV_GIM_PROCESS_MIGRATION;
/


CREATE OR REPLACE PACKAGE BODY TAV_GIM_PROCESS_MIGRATION
AS
/*
|| Package Name   : TAV_GIM_PROCESS_MIGRATION
|| Purpose        : Package to bundle master procedures for data migration
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/

/*
|| Procedure Name : TAV_GIM_PROCESS_MASTER
|| Purpose        : Procedure for processing all valid tables of Migration
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE tav_gim_process_master(
    p_out_error_code OUT NUMBER,
    p_out_error_message OUT VARCHAR2)
AS

  v_table_name              VARCHAR2(30);
  v_stg_table_name          VARCHAR2(30);
  v_column_name             VARCHAR2(500);
  v_function_str            VARCHAR2(500);
  v_prefix_mapping_func_str VARCHAR2(500);
  v_columns_str             VARCHAR2(32767);
  v_column_datatype         VARCHAR2(100);
  v_select_stmt             VARCHAR2(32767) := ' SELECT ';
  v_from_stmt               VARCHAR2(32767) := ' FROM ';
  v_nested_select_stmt      VARCHAR2(100)   := 'SELECT * FROM (';
  v_nested_from_stmt        VARCHAR2(100)   := ') TAB ';
  v_nested_where_stmt       VARCHAR2(4000) ;
  v_nested_where_condtn1    VARCHAR2(500) := ' !=''-99'' ';
  v_nested_where_condtn2    VARCHAR2(500) := ' !=-99 ';
  v_nested_where_condtn3    VARCHAR2(500) := ' IS NULL ';
  v_new_id_column           VARCHAR2(500);
  v_43pk_column             VARCHAR2(500);
  v_sql_stmt                VARCHAR2(32767);
  v_ref_constraint_name     VARCHAR2(100);
  v_ref_table_name          VARCHAR2(30);
  v_ref_column_name         VARCHAR2(100);
  v_ref_stg_table           VARCHAR2(30);
  v_cyclic_ref_cnt          NUMBER;
  v_column_data_type        VARCHAR2(500);
  v_error_code              NUMBER;
  v_error_message           VARCHAR2(4000);
  v_pk_fk_flag              VARCHAR2(10);
  v_lookup_function_entry   VARCHAR2(500);
  v_src_table_cnt           NUMBER;
  v_stg_table_cnt           NUMBER;
  v_run_seq_id              NUMBER := 0;
  v_migrate_flag_str        VARCHAR2(4000);
  v_procedure_name          VARCHAR2(4000);
  v_ddl_stmt                VARCHAR2(32767);
  v_insert_column_str 	    VARCHAR2(32767);
  v_old_43_id_cnt	    NUMBER;
-- Open and Close Log variables
  v_status		NUMBER := 0;
  V_JOB_SEQ_ID		NUMBER := 0;
  v_migration_date	TIMESTAMP ;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;
  v_insert_staging_exc EXCEPTION;
  v_update_migrate_flag_exc EXCEPTION;
  v_update_config_value_exc EXCEPTION;
  v_stmt_number NUMBER := 0;
BEGIN

  dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));

  v_stmt_number := 10;
  -- Incrementing RUN SEQ ID for iterative runs
  v_run_seq_id := v_run_seq_id + 1;
  -- Selecting details from TAV_GIM_VALID_TABLES for processing
  SELECT val_tab.table_name,
    val_tab.stg_table_name,
    val_tab.lookup_function
    BULK COLLECT
  into V_VALID_TABLES_TAB
  FROM TAV_GIM_VALID_TABLES VAL_TAB
  where VAL_TAB.LOAD_STATUS = 'NOT PROCESSED'
  AND VAL_TAB.exec_order > 0
--  and val_tab.table_name = 'CLAIM'
--  and VAL_TAB.exec_order = 428
  ORDER BY val_tab.exec_order;

-- Start of master loop for processing all the tables in TAV_GIM_VALID_TABLES--
  FOR i IN v_valid_tables_tab.FIRST..v_valid_tables_tab.LAST
  LOOP

    v_stmt_number := 20;

    -- Opening Log Statistics
    v_program_name := v_valid_tables_tab(i).v_table_name;
    v_migration_date := SYSTIMESTAMP;
    v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);

    dbms_output.put_line('Table Processed : ' || v_program_name);
    -- Initializing the table name and staging table name beign processed
    v_table_name        := v_valid_tables_tab(i).v_table_name;
    v_stg_table_name    := v_valid_tables_tab(i).v_stg_table_name;

    v_stmt_number := 30;
    -- Start of Checking whether the table belongs to a special scenario to be handled
    IF v_table_name IN ('EVAL_PRECEDENCE_PROPERTIES',
                        'FAULT_CODE_DEF_COMPS',
                        'SECTIONS_IN_PYMT_DEFN',
                        'SERVICE_PROC_DEF_COMPS',
                        'ADD_PAYMENT_INFO',
                        'LINE_ITEM_GROUPS',
                        'ITEMS_IN_GROUP',
                        'I18NITEM_TEXT',
                        'MODIFIERS',
                        'PRODUCT_LOCALE',
                        'SHIPMENT'                       
                        )   
                        
    OR  (V_TABLE_NAME IN ('WARRANTY'
                        ,'ITEM_MAPPING'
                        ,'POLICY_AUDIT'
                        ,'INVENTORY_TRANSACTION'
                        ) and TAV_GIM_UTILITIES.CHECK_FULLLOAD_REF_TABS(V_TABLE_NAME) = 0)
                        
    THEN

    v_stmt_number := 40;
    -- Selecting the procedure name from TAV_GIM_VALID_TABLES to handle specific cases
    SELECT procedure_name
      INTO v_procedure_name
    FROM tav_gim_valid_tables
      WHERE table_name = v_table_name;

     v_stmt_number := 50;
     -- Selecting the number of records from source table
    EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ' || v_table_name  INTO v_src_table_cnt;

    v_stmt_number := 60;
    -- Updating master table with number of records in the source table
    UPDATE tav_gim_valid_tables
      SET src_table_cnt = v_src_table_cnt,
          stg_table_cnt = 0,
          run_seq_id = v_run_seq_id
       WHERE table_name = v_table_name;

    COMMIT;
    v_stmt_number := 70;
    -- Framing the executable section for invoking the procedure
    v_ddl_stmt := 'BEGIN ' || v_procedure_name || '(' || v_job_seq_id || ');' || ' END;' ;

    v_stmt_number := 80;
    -- Invoke the procedure for the given table
    EXECUTE IMMEDIATE v_ddl_stmt;

     -- Calling the procedure to update migrate flag
      v_stmt_number := 81;
      tav_gim_process_migration.update_migrate_flag(p_in_table_name => v_table_name,
                              p_in_stg_table_name => v_stg_table_name,
                              p_out_error_code => v_error_code,
                              p_out_error_message => v_error_message);
      IF v_error_code <> 0 THEN
        RAISE v_update_migrate_flag_exc;
      END IF;

    v_stmt_number := 90;
    -- Selecting the number of records from staging table
    EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ' || v_stg_table_name  INTO v_stg_table_cnt;

    v_stmt_number := 100;
    -- Updating the master table for load status
    IF v_src_table_cnt = v_stg_table_cnt THEN
      v_stmt_number := 110;
      UPDATE tav_gim_valid_tables
      SET load_status = 'FULL',
          stg_table_cnt = v_stg_table_cnt,
          run_seq_id = v_run_seq_id
       WHERE table_name = v_table_name   ;
    ELSE
     v_stmt_number := 120;
    UPDATE tav_gim_valid_tables
      SET load_status = 'PARTIAL',
          stg_table_cnt = v_stg_table_cnt,
          run_seq_id = v_run_seq_id
          WHERE table_name = v_table_name   ;
    END IF;

    COMMIT;

    ELSE

    v_stmt_number := 130;
    -- Initializing the iteration variables
    v_nested_where_stmt := NULL;
    v_columns_str := NULL;
    v_43pk_column  := NULL;
    v_migrate_flag_str := NULL;
    v_insert_column_str := NULL;

    v_stmt_number := 140;
    -- Selecting the columns of the current table processed
    SELECT column_name
    BULK COLLECT INTO v_table_column_name
    FROM user_tab_columns
    WHERE table_name = v_table_name
    ORDER BY column_id;

    v_stmt_number := 150;
    --Start of LOOP to process the columns of the current table processed
    FOR col_name IN v_table_column_name.FIRST..v_table_column_name.LAST
    LOOP

      v_stmt_number := 160;
      v_pk_fk_flag := 'N';

      -- Start of Framing the Insert Column String
      IF v_insert_column_str IS NULL THEN
      	v_insert_column_str := v_table_column_name(col_name);
      ELSE
      	v_insert_column_str := v_insert_column_str || ',' || v_table_column_name(col_name);
      END IF;

      -- Assigning the PL/SQL table type record value to a local variable for processing the column names
      v_column_name := v_table_column_name(col_name);

      /*Check whether the column is CYCLIC REFERENCE
        IF NO THEN, proceed with further processing
        IF YES THEN, make the column string as NULL
      */
      -- Start of the Check for CYCLIC REFERENCE , IF NO Proceed
      IF(tav_gim_process_migration.is_cyclic_ref_column(v_table_name,v_column_name)= 0) THEN
        -- Start the processing for Primary Keys
        IF(tav_gim_process_migration.isprimarykey(v_table_name,v_column_name) > 0 and tav_gim_process_migration.isforeignkey(v_table_name,v_column_name) = 0) THEN

          v_pk_fk_flag := 'Y';

          -- Framing the OLD_43_PK column for reference purposes in staging tables
          v_43pk_column   := v_column_name || ' AS OLD_43_ID';

          -- Framing the Migrate Flag String
          v_migrate_flag_str := tav_gim_process_migration.get_migration_flag_entry(v_column_name) || ' AS MIGRATE_FLAG';
          v_stmt_number := 170;
          -- Selecting the name of the lookup function for the current table processed
          SELECT tav_gim_process_migration.get_lookup_function(v_table_name)
          INTO v_lookup_function_entry
          FROM DUAL;

          v_stmt_number := 180;
          /* IF LOOKUP_FUNCTION ENTRY is available,
              Frame the LOOKUP_FUNCTION
              make use of LOOKUP_FUNCTION for the primary key column
            ELSE
              Check the column data type
              IF NUMBER
                Make use of PREFIX_FUNCTION to form the column string
              IF VARCHAR2
                Make use of PREFIX_FUNCTION_VARCHAR to form the column string
          */
          IF v_lookup_function_entry IS NOT NULL THEN
            v_function_str := tav_gim_process_migration.get_lookup_function_str(v_table_name,v_column_name,v_lookup_function_entry,v_job_seq_id);
            v_column_name := 'NVL('|| v_function_str ||',' ||tav_gim_process_migration.get_pk_column_string(v_table_name,v_column_name,'P',v_job_seq_id) || ') AS ' || v_column_name;
          ELSE
            v_column_name := tav_gim_process_migration.get_pk_column_string(v_table_name,v_column_name,'P',v_job_seq_id) || ' AS ' ||v_column_name;
          END IF;

          v_stmt_number := 190;
          -- Start Framing the WHERE condition
          IF v_nested_where_stmt IS NULL THEN
            v_nested_where_stmt := '(' || v_table_column_name(col_name) || v_nested_where_condtn1 || ' OR ' || v_table_column_name(col_name) || v_nested_where_condtn2 || ')';
          ELSE
            v_nested_where_stmt := v_nested_where_stmt || ' AND ' || '(' || v_table_column_name(col_name) || v_nested_where_condtn1 || ' OR ' || v_table_column_name(col_name) || v_nested_where_condtn2 || ')';
          END IF;
          -- End of framing the WHERE condition
        END IF;
        -- End the processing for Primary Keys

        v_stmt_number := 200;
        -- Start the processing for Foreign Keys
        IF(tav_gim_process_migration.isforeignkey(v_table_name,v_column_name) > 0 ) THEN
        /*
        Check whether the column is COMPOSITE KEY
				IF NOT THEN
					Frame the OLD_43_ID column
				END IF;
        */

        -- Check whether the Foreign Key is a Composite Key, if else then do not frame the  OLD_43_ID in staging table
        IF (tav_gim_process_migration.iscompositekey(v_table_name)=0 AND tav_gim_process_migration.isprimarykey(v_table_name,v_column_name)>0) THEN
          IF (v_43pk_column IS NULL AND v_pk_fk_flag = 'N') THEN
            -- Framing the column and alias name for the storing the 4.3 primary key column into OLD_43_ID in staging table
            v_43pk_column   := v_column_name || ' AS OLD_43_ID';

            -- Framing the Migrate Flag String
            v_migrate_flag_str := tav_gim_process_migration.get_migration_flag_entry(v_column_name) || ' AS MIGRATE_FLAG';
          END IF;
        END IF;

          v_stmt_number := 210;
          -- Selecting the reference constraint name for the given table name and column name
          SELECT tav_gim_process_migration.get_ref_constraint_name(v_table_name,v_column_name)
          INTO v_ref_constraint_name
          FROM DUAL;

          v_stmt_number := 220;
          -- Selecting the Reference table name / column name
          SELECT table_name,
            column_name
          INTO v_ref_table_name,
            v_ref_column_name
          FROM user_cons_columns
          WHERE constraint_name = v_ref_constraint_name;

          v_stmt_number := 230;
          -- Selecting the Staging Table for the Reference table
          SELECT stg_table_name
            INTO v_ref_stg_table
            FROM tav_gim_valid_tables
            WHERE table_name = v_ref_table_name;

            v_stmt_number := 240;
            -- Framing the column string for PREFIX_FUNCTION
            v_column_name := tav_gim_process_migration.get_pk_column_string(v_table_name,v_column_name,'R',v_job_seq_id,v_ref_stg_table,v_ref_column_name) || ' AS ' ||v_column_name;

           v_stmt_number := 250;
            -- Start Framing the WHERE condition
          IF v_nested_where_stmt IS NULL THEN
          v_nested_where_stmt := '(' || v_table_column_name(col_name) || v_nested_where_condtn1 || ' OR ' || v_table_column_name(col_name) || v_nested_where_condtn2 || ' OR ' || v_table_column_name(col_name) || v_nested_where_condtn3 || ')';
          ELSE
          v_nested_where_stmt := v_nested_where_stmt || ' AND ' || '(' || v_table_column_name(col_name) || v_nested_where_condtn1 || ' OR ' || v_table_column_name(col_name) || v_nested_where_condtn2 || ' OR ' || v_table_column_name(col_name) || v_nested_where_condtn3 || ')';
          END IF;
          -- End of framing the WHERE condition
        END IF;
        -- End the processing for Foreign Keys
        v_stmt_number := 260;
        -- Check for previously appended column strings and framing the column string as NULL
        IF v_columns_str  IS NULL THEN
            v_columns_str  := v_column_name || ',';
        ELSE
            v_columns_str := v_columns_str || v_column_name || ',';
        END IF;
        -- End of check for previously appended column strings and framing the column string as NULL
      ELSE
        v_stmt_number := 270;
       -- Check for previously appended column strings and framing the column string as NULL
        IF v_columns_str  IS NULL THEN
            v_columns_str := 'NULL AS ' || v_column_name || ',';
        ELSE
            v_columns_str := v_columns_str || ' NULL AS ' || v_column_name || ',';
        END IF;
      END IF;
      -- End of Checking whether the column is CYCLIC REFERENCES
    END LOOP;
    --End of inner loop one to process the columns of a given table
    v_stmt_number := 280;
    -- Appending the 4.3 ID column to store in the staging table
    IF v_43pk_column IS NOT NULL THEN
      v_columns_str    := v_columns_str || v_43pk_column || ',' || v_migrate_flag_str;
    ELSE
      v_columns_str := v_columns_str || '''Y'' AS MIGRATE_FLAG';
      --v_columns_str := rtrim(v_columns_str,',');
    END IF;

    v_stmt_number := 290;
    -- Framing the final SQL statement
    IF v_nested_where_stmt IS NOT NULL THEN
      V_NESTED_WHERE_STMT := ' WHERE ' || V_NESTED_WHERE_STMT;
      v_sql_stmt := v_nested_select_stmt || v_select_stmt || v_columns_str || v_from_stmt || v_table_name || ' WHERE ROWNUM > 0 ' || v_nested_from_stmt || v_nested_where_stmt;
    else
      v_sql_stmt := v_nested_select_stmt || v_select_stmt || v_columns_str || v_from_stmt || v_table_name || ' WHERE ROWNUM > 0 ' || v_nested_from_stmt ;
    END IF;


    -- Deleting the Previous Entry in the query tracker
    v_stmt_number := 300;
    DELETE FROM tav_gim_query_tracker WHERE
    table_name = v_table_name;


    v_stmt_number := 310;
   -- Inserting the new entry in the query tracker for SELECT query
    INSERT INTO tav_gim_query_tracker(table_name,select_query)
    VALUES (v_table_name,v_sql_stmt);

   v_stmt_number := 320;
    -- Selecting the number of records from source table
    EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ' || v_table_name  INTO v_src_table_cnt;

    v_stmt_number := 330;
    -- Updating master table with number of records in the source table
    UPDATE tav_gim_valid_tables
      SET src_table_cnt = v_src_table_cnt,
          stg_table_cnt = 0,
          run_seq_id = v_run_seq_id
       WHERE table_name = v_table_name;

    COMMIT;

    v_stmt_number := 340;
    -- Truncating the Global Temperory Tables for Clearing Primary Key columns
    EXECUTE IMMEDIATE 'TRUNCATE TABLE TAV_GIM_LOOKUP_60_43_CHAR';

     /* Check whether the staging table is having OLD_43_ID column
      *	 If YES,
      *	 	then append the OLD_43_ID
      */
      -- Checking the existence of OLD_43_ID column in the staging table
      SELECT count(column_name)
        INTO v_old_43_id_cnt
      FROM user_tab_columns
      WHERE table_name = v_stg_table_name
      AND column_name = 'OLD_43_ID';

      -- Appending OLD_43_ID column to insert column str
      IF v_old_43_id_cnt > 0 THEN
      	v_insert_column_str := v_insert_column_str || ',' || 'OLD_43_ID';
      END IF;

      -- Appending the COLUMN NAME MIGRATE_FLAG to insert column str
      	v_insert_column_str := v_insert_column_str || ',' || 'MIGRATE_FLAG';
      -- End of Framing the Insert Column String

    v_stmt_number := 350;
    -- Calling the Procedure to INSERT data into STAGING tables
    tav_gim_process_migration.insert_staging_tables( p_in_select_query => v_sql_stmt,
                                                     p_in_insert_column_str => v_insert_column_str,
                                                     p_in_table_name => v_table_name,
                                                     p_in_stg_table_name => v_stg_table_name,
                                                     p_out_error_code => v_error_code,
                                                     p_out_error_message => v_error_message);
    IF v_error_code <> 0 THEN
      RAISE v_insert_staging_exc;
    END IF;

      -- Calling the procedure to update migrate flag
      v_stmt_number := 360;
      tav_gim_process_migration.update_migrate_flag(p_in_table_name => v_table_name,
                              p_in_stg_table_name => v_stg_table_name,
                              p_out_error_code => v_error_code,
                              p_out_error_message => v_error_message);
      IF v_error_code <> 0 THEN
        RAISE v_update_migrate_flag_exc;
      END IF;
      
         v_stmt_number := 410;
    
    
  if v_table_name = 'CONFIG_VALUE' then
    
  v_stmt_number := 430;
  tav_gim_process_migration.update_config_value(p_out_error_code => v_error_code,
                                                p_out_error_message => v_error_message);

  IF v_error_code <> 0 THEN
    RAISE v_update_config_value_exc;
  end if;
  
  V_STMT_NUMBER := 440;
  COMMIT;
  
  end if; 
      
      

    -- Selecting the number of records from staging table
    v_stmt_number := 370;
    EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ' || v_stg_table_name  INTO v_stg_table_cnt;

    -- Updating the master table for load status
    v_stmt_number := 380;
    IF v_src_table_cnt = v_stg_table_cnt THEN
      v_stmt_number := 390;
      UPDATE tav_gim_valid_tables
      SET load_status = 'FULL',
          stg_table_cnt = v_stg_table_cnt,
          run_seq_id = v_run_seq_id
       WHERE table_name = v_table_name   ;
    ELSE
     v_stmt_number := 400;
    UPDATE tav_gim_valid_tables
      SET load_status = 'PARTIAL',
          stg_table_cnt = v_stg_table_cnt,
          run_seq_id = v_run_seq_id
          WHERE table_name = v_table_name   ;
    END IF;


    COMMIT;

    END IF;

   -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );
  END LOOP;
  -- End of Master loop for all the tables

   p_out_error_code := 0;
   p_out_error_message := 'Procedure TAV_GIM_PROCESS_MASTER executed successfully';

  dbms_output.put_line('procedure end time : ' || to_char( sysdate, 'hh24:mi:ss' ));
EXCEPTION
WHEN v_update_config_value_exc THEN
  ROLLBACK;
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'Exception Occured while UPDATING CONFIG VALUE.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => v_error_message,
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in UPDATE CONFIG VALUE.' );
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
WHEN v_insert_staging_exc THEN
  ROLLBACK;
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'Exception Occured while INSERT INTO STAGING TABLE.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => v_error_message,
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in INSERT STAGING TABLE.' );
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
WHEN v_update_migrate_flag_exc THEN
  ROLLBACK;
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'Exception Occured while UPDATING MIGRATE FLAG.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => v_error_message,
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in UPDATE MIGRATE FLAG.' );
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
WHEN OTHERS THEN
  ROLLBACK;
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'Exception occured while processing table in Master Procedure.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in TAV_GIM_PROCESS_MIGRATION' );

  v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
END tav_gim_process_master;




/*
|| Function Name  : ISPRIMARYKEY
|| Purpose        : Function to determine whether a given column is a primary key
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION isPrimaryKey(
    p_in_table_name  IN VARCHAR2,
    p_in_column_name IN VARCHAR2 )
  RETURN NUMBER
AS
  v_column_exist_cnt NUMBER := 0;
BEGIN
  SELECT COUNT(A.column_name)
  INTO v_column_exist_cnt
  FROM user_cons_columns A ,
    user_constraints b
  WHERE A.table_name    = p_in_table_name
  AND A.table_name      = b.table_name
  AND b.constraint_type = 'P'
  AND A.column_name     = p_in_column_name
  AND a.constraint_name = b.constraint_name;
  RETURN v_column_exist_cnt;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END isPrimaryKey;

/*
|| Function Name  : ISFOREIGNKEY
|| Purpose        : Function to determine whether a given column is a primary key
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION isForeignKey(
    p_in_table_name  IN VARCHAR2,
    p_in_column_name IN VARCHAR2 )
  RETURN NUMBER
AS
  v_column_exist_cnt NUMBER := 0;
BEGIN
  SELECT COUNT(A.column_name)
  INTO v_column_exist_cnt
  FROM user_cons_columns A ,
    user_constraints b
  WHERE A.table_name    = p_in_table_name
  AND A.table_name      = b.table_name
  AND b.constraint_type = 'R'
  AND A.column_name     = p_in_column_name
  AND a.constraint_name = b.constraint_name;
  RETURN v_column_exist_cnt;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END isForeignKey;

/*
|| Function Name  : GET_LOOKUP_FUNCTION
|| Purpose        : Function to retrieve the lookup function entry from master table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_lookup_function(
    p_in_table_name IN VARCHAR2)
  RETURN VARCHAR2
AS
  v_function_str VARCHAR2(500);
BEGIN
  SELECT lookup_function
  INTO v_function_str
  FROM tav_gim_valid_tables
  WHERE table_name = p_in_table_name;
  RETURN v_function_str;
EXCEPTION
WHEN OTHERS THEN
  RETURN NULL;
END get_lookup_function;

/*
|| Function Name  : GET_REF_CONSTRAINT_NAME
|| Purpose        : Function to retrieve reference constraint name for a given table and column name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_ref_constraint_name(
    p_in_table_name  IN VARCHAR2,
    p_in_column_name IN VARCHAR2)
  RETURN VARCHAR2
AS
  v_ref_constraint_name VARCHAR2(500);
BEGIN
  SELECT b.r_constraint_name
  INTO v_ref_constraint_name
  FROM user_cons_columns A ,
    user_constraints b
  WHERE A.table_name    = p_in_table_name
  AND A.table_name      = b.table_name
  AND A.column_name     = p_in_column_name
  AND A.constraint_name = b.constraint_name
  AND b.constraint_type = 'R';
  RETURN v_ref_constraint_name;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END get_ref_constraint_name;

/*
|| Function Name  : GET_COLUMN_DATATYPE
|| Purpose        : Function to retrieve data type of a given column name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_column_datatype(
    p_in_table_name  IN VARCHAR2,
    p_in_column_name IN VARCHAR2)
  RETURN VARCHAR2
AS
  v_data_type VARCHAR2(500);
BEGIN
  SELECT data_type
  INTO v_data_type
  FROM user_tab_columns
  WHERE table_name = p_in_table_name
  AND column_name  = p_in_column_name;
  RETURN v_data_type;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END get_column_datatype;

/*
|| Function Name  : GET_PK_COLUMN_STRING
|| Purpose        : Function to frame the prefix function string
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_pk_column_string(p_in_table_name		IN VARCHAR2,
                              p_in_column_name	IN VARCHAR2,
                              p_in_constraint_type	IN VARCHAR2,
                              p_in_job_seq_id		IN NUMBER,
                              p_in_ref_stg_table_name	IN VARCHAR2 DEFAULT NULL,
                              p_in_ref_column_name	IN VARCHAR2 DEFAULT NULL
                              )
  RETURN VARCHAR2
AS
  v_column_data_type VARCHAR2(500);
  v_function_str VARCHAR2(500);
  V_PK_COLUMN_STR    VARCHAR2(4000);
  v_ref_pk_col varchar2(1000);
BEGIN

  SELECT tav_gim_process_migration.get_column_datatype(p_in_table_name,p_in_column_name)
  INTO v_column_data_type
  FROM dual;
  IF v_column_data_type = 'NUMBER' THEN
    v_function_str := 'TAV_GIM_UTILITIES.PREFIX_FUNCTION_NUMBER';
  ELSE
    v_function_str := 'TAV_GIM_UTILITIES.PREFIX_FUNCTION_VARCHAR';
  END IF;

   IF P_IN_CONSTRAINT_TYPE = 'P' then

   v_ref_pk_col := p_in_column_name;

  ELSE
--  P_IN_CONSTRAINT_TYPE = 'R' THEN

    begin
    SELECT A.column_name
    INTO v_ref_pk_col
    FROM user_cons_columns A ,
    user_constraints B
    WHERE A.table_name    = p_in_table_name
    AND A.table_name      = B.table_name
    AND A.constraint_name = B.constraint_name
    AND B.CONSTRAINT_TYPE = 'P'
    AND ROWNUM = 1;
    EXCEPTION
    WHEN OTHERS THEN
    v_ref_pk_col := p_in_column_name;
    end;

  END IF;

  v_pk_column_str := v_function_str || '(' || ''''||p_in_table_name ||'''' || ',' || ''''||p_in_column_name ||'''' ||','|| p_in_column_name ||','||''''|| p_in_constraint_type ||'''' ||','|| p_in_job_seq_id ||','|| ''''|| p_in_ref_stg_table_name||'''' ||','||''''|| p_in_ref_column_name ||''''  || ',' || ''''||v_ref_pk_col ||'''' ||','|| v_ref_pk_col  || ')' ;

  RETURN v_pk_column_str;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END get_pk_column_string;

/*
|| Function Name  : GET_LOOKUP_FUNCTION_STR
|| Purpose        : Function to frame the lookup function string
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_lookup_function_str(p_in_table_name IN VARCHAR2,
                                 p_in_column_name IN VARCHAR2,
                                 p_in_lookup_function_entry IN VARCHAR2,
                                 p_in_job_seq_id IN NUMBER) RETURN VARCHAR2
AS
v_lookup_function_str VARCHAR2(4000);
v_column_datatype VARCHAR2(500);
BEGIN

  SELECT tav_gim_process_migration.get_column_datatype(p_in_table_name,p_in_column_name)
  INTO v_column_datatype
  FROM DUAL;

  IF v_column_datatype = 'NUMBER' THEN
    v_lookup_function_str := 'TAV_GIM_UTILITIES.LOOKUP_FUNCTION_NUMBER';
  ELSE
    v_lookup_function_str := 'TAV_GIM_UTILITIES.LOOKUP_FUNCTION_VARCHAR';
  END IF;

  v_lookup_function_str := v_lookup_function_str || '(' || p_in_job_seq_id ||',' ||''''||p_in_column_name ||'''' ||','|| p_in_column_name || p_in_lookup_function_entry ||')';

  RETURN v_lookup_function_str;

EXCEPTION
WHEN OTHERS THEN
NULL;
END get_lookup_function_str;

/*
|| Function Name  : IS_CYCLIC_REF_COLUMN
|| Purpose        : Function to determine whether the table columns are having cyclic references
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/

FUNCTION is_cyclic_ref_column(
    p_in_table_name  IN VARCHAR2,
    p_in_column_name IN VARCHAR2)
  RETURN NUMBER
AS
  v_cyclic_ref_cnt NUMBER;
BEGIN
  SELECT COUNT(cyc_ref_table)
  INTO v_cyclic_ref_cnt
  FROM tav_gim_cyclic_ref
  WHERE cyc_ref_table = p_in_table_name
  AND cyc_ref_column  = p_in_column_name;
  RETURN v_cyclic_ref_cnt;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END is_cyclic_ref_column;
/*
|| Function Name  : GET_PK_COLUMN_NAME
|| Purpose        : Function to retrieve the primary key column name for a given table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_pk_column_name(
    p_in_table_name IN VARCHAR2)
  RETURN VARCHAR2
AS
  v_pk_column_name VARCHAR2(500);
BEGIN
  BEGIN
    SELECT A.column_name
    INTO v_pk_column_name
    FROM user_cons_columns A ,
      user_constraints B
    WHERE A.table_name    = p_in_table_name
    AND A.table_name      = B.table_name
    AND A.constraint_name = B.constraint_name
    AND B.constraint_type = 'P';
  EXCEPTION
  WHEN OTHERS THEN
    v_pk_column_name := NULL;
  END;
  RETURN v_pk_column_name;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END get_pk_column_name ;

/*
|| Procedure Name : INSERT_STAGING_TABLES
|| Purpose        : Procedure to insert data into staging tables
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE insert_staging_tables(
    p_in_select_query   IN VARCHAR2,
    p_in_insert_column_str IN VARCHAR2,
    p_in_table_name     IN VARCHAR2,
    p_in_stg_table_name IN VARCHAR2,
    p_out_error_code OUT NUMBER,
    p_out_error_message OUT VARCHAR2)
AS
  v_sql_stmt VARCHAR2(32767);
  v_stmt_number NUMBER := 0;
  v_error_code NUMBER;
  v_error_message VARCHAR2(4000);
  v_check_str VARCHAR2(4000);
  v_user_exception EXCEPTION;
  V_INSERT_COLUMN_STR varchar2(32767);
  V_CNT_APPEND number := 0;
BEGIN


   v_stmt_number := 10;
   V_INSERT_COLUMN_STR := P_IN_INSERT_COLUMN_STR;
   v_check_str := substr(p_in_select_query,1,3000);

      select COUNT(1)
      into V_CNT_APPEND
      from DUAL where
      v_check_str like '%PREFIX_FUNCTION_VARCHAR%'
      or v_check_str like '%LOOKUP_FUNCTION%';


  V_STMT_NUMBER := 11;
  V_SQL_STMT := 'INSERT INTO ' || P_IN_STG_TABLE_NAME || '(' || V_INSERT_COLUMN_STR ||')' ||' ' || P_IN_SELECT_QUERY;
  
  if V_CNT_APPEND > 0 or length(V_SQL_STMT) > 32750 or p_in_table_name = 'CUSTOM_REPORT_APPLICABLE_PARTS' then
   V_SQL_STMT := 'INSERT INTO ' || P_IN_STG_TABLE_NAME || '(' || V_INSERT_COLUMN_STR ||')' ||' ' || P_IN_SELECT_QUERY;
   else
  V_SQL_STMT := 'INSERT /*+ APPEND */ INTO ' || P_IN_STG_TABLE_NAME || '(' || V_INSERT_COLUMN_STR ||')' ||' ' || P_IN_SELECT_QUERY; 
  
  end if;

  v_stmt_number := 12;
  UPDATE tav_gim_query_tracker
  SET insert_query = v_sql_stmt
  WHERE table_name = p_in_table_name;

  COMMIT;

  v_stmt_number := 13;
  EXECUTE IMMEDIATE v_sql_stmt;


commit; ---xxx

  v_stmt_number := 14;
  tav_gim_process_migration.create_index_gather_statistics(p_in_stg_table_name => p_in_stg_table_name,
                                                           p_out_error_code => v_error_code,
                                                           p_out_error_message => v_error_message);
  IF v_error_code <> 0 THEN
    RAISE v_user_exception;
  END IF;

  p_out_error_code := 0;
  p_out_error_message := 'Procedure INSERT_STAGING_TABLES executed successfully';

EXCEPTION
WHEN v_user_exception THEN
  p_out_error_code := v_error_code;
  p_out_error_message := 'Exception occured in CREATE_INDEX_GATHER_STATISTICS. Statement Number : ' || v_stmt_number ||'. ' || v_error_message;
WHEN OTHERS THEN
  p_out_error_code := SQLCODE;
  p_out_error_message := 'Exception occured while inserting into staging table. Statement Number : ' || v_stmt_number ||'. ' ||SUBSTR(SQLERRM,1,255);
END INSERT_STAGING_TABLES;


/*
|| Function Name  : ISCOMPOSITEKEY
|| Purpose        : Function to determine whether the table has composite keys
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION iscompositekey(p_in_table_name IN VARCHAR2) RETURN NUMBER
AS
v_ck_column_count NUMBER;

BEGIN

SELECT COUNT(table_name) INTO
v_ck_column_count FROM
tav_gim_composite_key_tables
WHERE table_name = p_in_table_name;

RETURN v_ck_column_count;

EXCEPTION
WHEN OTHERS THEN NULL;
END iscompositekey;

/*
|| Procedure Name : CREATE_INDEX_GATHER_STATISTICS
|| Purpose        : Procedure to create index and gather statistics
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
PROCEDURE create_index_gather_statistics(p_in_stg_table_name IN VARCHAR2, p_out_error_code OUT NUMBER, p_out_error_message OUT VARCHAR2)
AS
v_sql_stmt VARCHAR2(4000);
v_stmt_number NUMBER := 0;
v_schema_name VARCHAR2(500);
v_source_table_name VARCHAR2(30);
v_exec_order NUMBER;
v_index_name  VARCHAR2(30);
v_index_cnt NUMBER;
BEGIN
	-- Selecting the source table name from tav_gim_valid_tables for a given staging table
  v_stmt_number := 10;
  SELECT tav_gim_process_migration.get_source_table_name(p_in_stg_table_name)
    INTO v_source_table_name
  FROM DUAL;

  /* Check whether the table has composite key OR no primary keys
   * If YES, then not necessary to create indexes
   */
  -- Checking for the composite key, if no, then proceed
  v_stmt_number := 20;
  IF (tav_gim_process_migration.iscompositekey(v_source_table_name)=0 ) THEN

    IF (tav_gim_process_migration.get_pk_column_name(v_source_table_name) IS NOT NULL) THEN
    -- Selecting the execution order for framing the index_name
    v_stmt_number := 30;
    SELECT tav_gim_process_migration.get_execution_order(v_source_table_name)
    INTO v_exec_order
    FROM DUAL;
    -- Framing the index name , example TG_5_OLD_43_ID
    v_stmt_number := 40;
    v_index_name := 'TG_' || v_exec_order || '_' || 'OLD_43_ID';

    -- Selecting the count of index name for the given index name
    v_stmt_number := 50;
    SELECT COUNT(index_name)
      INTO v_index_cnt
      FROM user_indexes
      WHERE table_name = p_in_stg_table_name
      AND index_name = v_index_name;

    /* Check whether the index already exists or not
      If index already exists,
        then do not proceed
      else
        create index
    */
    -- Start the Check for index already exists
    v_stmt_number := 60;
    IF v_index_cnt > 0 THEN
      v_sql_stmt := 'DROP INDEX ' || v_index_name;
      EXECUTE IMMEDIATE v_sql_stmt;
    END IF;
    -- End of the Check for index already exists

      -- Framing the create index statement
      v_stmt_number := 70;
      v_sql_stmt := 'CREATE INDEX ' || v_index_name || ' ON ' || p_in_stg_table_name || '(OLD_43_ID)';

      v_stmt_number := 80;
      EXECUTE IMMEDIATE v_sql_stmt;
      -- Selecting the schema name for gathering statistics
      v_stmt_number := 90;
      SELECT USER INTO v_schema_name FROM DUAL;
      -- Gathering Statistics for a given table in the given schema
      v_stmt_number := 100;
      DBMS_STATS.GATHER_TABLE_STATS(v_schema_name,p_in_stg_table_name);

   END IF;
  END IF;
  -- End of Checking for the composite key
  p_out_error_code := 0;
  p_out_error_message := 'Procedure CREATE_INDEX_GATHER_STATISTICS executed successfully';
EXCEPTION
WHEN OTHERS THEN
p_out_error_code := SQLCODE;
p_out_error_message := 'Exception occured while creating index or gathering statistics. Statement Number : ' || v_stmt_number ||SUBSTR(SQLERRM,1,255);

END create_index_gather_statistics;

/*
|| Function Name  : GET_EXECUTION_ORDER
|| Purpose        : Function to retrieve the execution order of the given table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_execution_order(p_in_table_name IN VARCHAR2) RETURN NUMBER
AS
v_exec_order NUMBER;
BEGIN

SELECT exec_order
  INTO v_exec_order
FROM tav_gim_valid_tables
WHERE table_name = p_in_table_name;

RETURN v_exec_order;
EXCEPTION
WHEN OTHERS THEN
NULL;
END get_execution_order;

/*
|| Function Name  : GET_SOURCE_TABLE_NAME
|| Purpose        : Function to retrieve the source table name for a given staging table name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_source_table_name(p_in_stg_table_name IN VARCHAR2) RETURN VARCHAR2
AS
v_source_table_name VARCHAR2(30);
BEGIN
  SELECT table_name
    INTO v_source_table_name
  FROM tav_gim_valid_tables
  WHERE stg_table_name = p_in_stg_table_name;

  RETURN v_source_table_name;
EXCEPTION
WHEN OTHERS THEN
NULL;
END get_source_table_name;
/*
|| Function Name  : GET_STAGING_TABLE_NAME
|| Purpose        : Function to retrieve the staging table name for a given source table name
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_staging_table_name(p_in_table_name IN VARCHAR2) RETURN VARCHAR2
AS
v_stg_table_name VARCHAR2(30);
BEGIN
  SELECT stg_table_name
    INTO v_stg_table_name
  FROM tav_gim_valid_tables
  WHERE table_name = p_in_table_name;

  return v_stg_table_name;

EXCEPTION
WHEN OTHERS THEN
NULL;
END get_staging_table_name;

/*
|| Function Name  : GET_MIGRATION_FLAG_ENTRY
|| Purpose        : Function to retrieve string for Migration Flag function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 01/12/2010
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_migration_flag_entry(p_in_column_value IN VARCHAR2) RETURN VARCHAR2
AS
v_migrate_flag_str VARCHAR2(4000);
BEGIN
  v_migrate_flag_str := 'TAV_GIM_UTILITIES.UPDATE_MIGRATE_FLAG(' || p_in_column_value || ')';

  RETURN v_migrate_flag_str;

EXCEPTION
WHEN OTHERS THEN
NULL;
END get_migration_flag_entry;


/*
|| Procedure Name  : TAV_GIM_UPDATE_CYCLIC_REF_60
|| Purpose        : Procedure to udte cyclic/self references to target
|| Author         : Joseph
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/

procedure TAV_GIM_UPDATE_CYCLIC_REF_60 as
NJOB_SEQ_ID number;
NSTATUS number;
V_PROGRAM_NAME varchar2(100);
---1
begin

V_PROGRAM_NAME := 'UPDATE 60_ADDRESS(D_LAST_UPDATED_BY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,SYSTIMESTAMP);
BEGIN
UPDATE SN_ADDRESS a
SET A.D_LAST_UPDATED_BY =
  (SELECT B.D_LAST_UPDATED_BY FROM
   TG_ADDRESS B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ADDRESS C where C.id = A.ID
  );
   exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,                       --3
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---2
V_PROGRAM_NAME := 'UPDATE 60_ADDRESS(BELONGS_TO)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
begin
UPDATE SN_ADDRESS A
set a.BELONGS_TO =
  (SELECT B.BELONGS_TO FROM
   TG_ADDRESS B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ADDRESS C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---3

V_PROGRAM_NAME := 'UPDATE 60_PARTY(IS_PART_OF_ORGANIZATION)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_PARTY a
set a.IS_PART_OF_ORGANIZATION =
  (SELECT b.IS_PART_OF_ORGANIZATION FROM
   TG_PARTY B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_PARTY C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME, --3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---4
V_PROGRAM_NAME := 'UPDATE 60_CLAIM(RECOVERY_INFO)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_CLAIM a
set a.RECOVERY_INFO =
  (SELECT b.RECOVERY_INFO FROM
   TG_CLAIM B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_CLAIM C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := TAV_GIM_INITIAL_SETUP.CLOSE_LOG (NJOB_SEQ_ID,TAV_GIM_INITIAL_SETUP.G_COMPLETE_OK );
--5
V_PROGRAM_NAME := 'UPDATE 60_RECOVERY_CLAIM_INFO(RECOVERY_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_RECOVERY_CLAIM_INFO a
set a.RECOVERY_CLAIM =
  (SELECT b.RECOVERY_CLAIM FROM
   TG_RECOVERY_CLAIM_INFO B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_RECOVERY_CLAIM_INFO C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
 NSTATUS := TAV_GIM_INITIAL_SETUP.CLOSE_LOG (NJOB_SEQ_ID,TAV_GIM_INITIAL_SETUP.G_COMPLETE_OK );
--6
V_PROGRAM_NAME := 'UPDATE 60_CREDIT_MEMO(DCAP_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_CREDIT_MEMO a
set a.DCAP_CLAIM =
  (SELECT b.DCAP_CLAIM FROM
   TG_CREDIT_MEMO B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_CREDIT_MEMO C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
  NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

--6.1
V_PROGRAM_NAME := 'UPDATE 60_CREDIT_MEMO(RECOVERY_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_CREDIT_MEMO a
set a.RECOVERY_CLAIM =
  (SELECT b.RECOVERY_CLAIM FROM
   TG_CREDIT_MEMO B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_CREDIT_MEMO C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.21'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
  NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );



---7
V_PROGRAM_NAME := 'UPDATE 60_RECOVERY_PAYMENT(FOR_RECOVERY_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_RECOVERY_PAYMENT a
set a.FOR_RECOVERY_CLAIM =
  (SELECT b.FOR_RECOVERY_CLAIM FROM
   TG_RECOVERY_PAYMENT B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_RECOVERY_PAYMENT C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---8
V_PROGRAM_NAME := 'UPDATE 60_PARTY(D_LAST_UPDATED_BY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_PARTY a
set a.D_LAST_UPDATED_BY =
  (SELECT b.D_LAST_UPDATED_BY FROM
   TG_PARTY B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_PARTY C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
END;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---9
V_PROGRAM_NAME := 'UPDATE 60_MARKET(PARENT_ID)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_MARKET a
set a.PARENT_ID =
  (SELECT b.PARENT_ID FROM
   TG_MARKET B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_MARKET C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---10
V_PROGRAM_NAME := 'UPDATE 60_USER_CLUSTER(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_USER_CLUSTER a
set a.IS_PART_OF =
  (SELECT b.IS_PART_OF FROM
   TG_USER_CLUSTER B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_USER_CLUSTER C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---11
V_PROGRAM_NAME := 'UPDATE 60_ROLE_GROUP(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_ROLE_GROUP a
set a.IS_PART_OF =
  (SELECT b.IS_PART_OF FROM
   TG_ROLE_GROUP B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ROLE_GROUP C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---12
V_PROGRAM_NAME := 'UPDATE 60_ITEM_GROUP(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_ITEM_GROUP a
set a.IS_PART_OF =
  (SELECT b.IS_PART_OF FROM
   TG_ITEM_GROUP B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ITEM_GROUP C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---13
V_PROGRAM_NAME := 'UPDATE 60_DEALER_GROUP(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_DEALER_GROUP a
set a.IS_PART_OF =
  (SELECT b.IS_PART_OF FROM
   TG_DEALER_GROUP B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_DEALER_GROUP C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---14
V_PROGRAM_NAME := 'UPDATE 60_ORG_USER(SUPERVISOR)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_ORG_USER a
set a.SUPERVISOR =
  (SELECT b.SUPERVISOR FROM
   TG_ORG_USER B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ORG_USER C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
END;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---15
V_PROGRAM_NAME := 'UPDATE 60_ORG_USER(D_LAST_UPDATED_BY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_ORG_USER a
set a.D_LAST_UPDATED_BY =
  (SELECT b.D_LAST_UPDATED_BY FROM
   TG_ORG_USER B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ORG_USER C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
END;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---16
V_PROGRAM_NAME := 'UPDATE 60_ASSEMBLY(IS_PART_OF_ASSEMBLY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE SN_ASSEMBLY a
set a.IS_PART_OF_ASSEMBLY =
  (SELECT b.IS_PART_OF_ASSEMBLY FROM
   TG_ASSEMBLY B
   where B.id = a.ID
  )
WHERE EXISTS
  (select 1 from TG_ASSEMBLY C where C.id = A.ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
----------exception for the whole block----
exception
      when OTHERS then
           tav_gim_initial_setup.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>     '-99'               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID         ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );


END TAV_GIM_UPDATE_CYCLIC_REF_60;



/*
|| Procedure Name  : TAV_GIM_UPDATE_CYCLIC_REF
|| Purpose        : Procedure to udte cyclic/self references
|| Author         : Joseph
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/

procedure TAV_GIM_UPDATE_CYCLIC_REF as
NJOB_SEQ_ID number;
NSTATUS number;
V_PROGRAM_NAME varchar2(100);
---1
begin

V_PROGRAM_NAME := 'UPDATE TG_ADDRESS(D_LAST_UPDATED_BY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin
UPDATE TG_ADDRESS a
set a.D_LAST_UPDATED_BY =
  (select C.id from
   ADDRESS B,TG_ORG_USER C
   where B.id = a.OLD_43_ID
   and B.D_LAST_UPDATED_BY = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from ADDRESS C where C.id = a.OLD_43_ID
  );
   exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,                       --3
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---2
V_PROGRAM_NAME := 'UPDATE TG_ADDRESS(BELONGS_TO)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
begin
UPDATE TG_ADDRESS a
set a.BELONGS_TO =
  (select C.id from
   ADDRESS B,TG_PARTY C
   where B.id = a.OLD_43_ID
   and B.BELONGS_TO = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from ADDRESS C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---3

V_PROGRAM_NAME := 'UPDATE TG_PARTY(IS_PART_OF_ORGANIZATION)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
update TG_PARTY a
set a.IS_PART_OF_ORGANIZATION =
  (select C.id from
   PARTY B,TG_ORGANIZATION C
   where B.id = a.OLD_43_ID
   and B.IS_PART_OF_ORGANIZATION = C.OLD_43_ID
  )
where exists
  (select 1 from PARTY C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME, --3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---4
V_PROGRAM_NAME := 'UPDATE TG_CLAIM(RECOVERY_INFO)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_CLAIM a
set a.RECOVERY_INFO =
  (select C.id from
   CLAIM B,TG_RECOVERY_INFO C
   where B.id = a.OLD_43_ID
   and B.RECOVERY_INFO = C.OLD_43_ID
  )
where exists
  (select 1 from CLAIM C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;

NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---5
V_PROGRAM_NAME := 'UPDATE TG_RECOVERY_CLAIM_INFO(RECOVERY_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_RECOVERY_CLAIM_INFO a
set a.RECOVERY_CLAIM =
  (select C.id from
   RECOVERY_CLAIM_INFO B,TG_RECOVERY_CLAIM C
   where B.id = a.OLD_43_ID
   and B.RECOVERY_CLAIM = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from RECOVERY_CLAIM_INFO C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
 NSTATUS := TAV_GIM_INITIAL_SETUP.CLOSE_LOG (NJOB_SEQ_ID,TAV_GIM_INITIAL_SETUP.G_COMPLETE_OK );
---6
V_PROGRAM_NAME := 'UPDATE TG_CREDIT_MEMO(DCAP_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_CREDIT_MEMO a
set a.DCAP_CLAIM =
  (select C.id from
   CREDIT_MEMO B,TG_DCAP_CLAIM C
   where B.id = a.OLD_43_ID
   and B.DCAP_CLAIM = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from CREDIT_MEMO C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
  NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---6.1
V_PROGRAM_NAME := 'UPDATE TG_CREDIT_MEMO(RECOVERY_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_CREDIT_MEMO a
set a.RECOVERY_CLAIM =
  (select C.id from
   CREDIT_MEMO B,TG_RECOVERY_CLAIM C
   where B.id = a.OLD_43_ID
   and B.RECOVERY_CLAIM = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from CREDIT_MEMO C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
  NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );

---7
V_PROGRAM_NAME := 'UPDATE TG_RECOVERY_PAYMENT(FOR_RECOVERY_CLAIM)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_RECOVERY_PAYMENT a
set a.FOR_RECOVERY_CLAIM =
  (select C.id from
   RECOVERY_PAYMENT B,TG_RECOVERY_CLAIM C
   where B.id = a.OLD_43_ID
   and B.FOR_RECOVERY_CLAIM = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from RECOVERY_PAYMENT C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---8
V_PROGRAM_NAME := 'UPDATE TG_PARTY(D_LAST_UPDATED_BY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_PARTY a
set a.D_LAST_UPDATED_BY =
  (select C.id from
   PARTY B,TG_ORG_USER C
   where B.id = a.OLD_43_ID
   and B.D_LAST_UPDATED_BY = C.OLD_43_ID
  )
WHERE EXISTS
  (select 1 from PARTY C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
END;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---9
V_PROGRAM_NAME := 'UPDATE TG_MARKET(PARENT_ID)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_MARKET a
set a.PARENT_ID =
  (select C.id from
   MARKET B,TG_MARKET C
   where B.id = a.OLD_43_ID
   and B.PARENT_ID = C.OLD_43_ID
  )
where exists
  (select 1 from MARKET C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---10
V_PROGRAM_NAME := 'UPDATE TG_USER_CLUSTER(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_USER_CLUSTER a
set a.IS_PART_OF =
  (select C.id from
   USER_CLUSTER B,TG_USER_CLUSTER C
   where B.id = a.OLD_43_ID
   and B.IS_PART_OF = C.OLD_43_ID
  )
where exists
  (select 1 from USER_CLUSTER C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---11
V_PROGRAM_NAME := 'UPDATE TG_ROLE_GROUP(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_ROLE_GROUP a
set a.IS_PART_OF =
  (select C.id from
   ROLE_GROUP B,TG_ROLE_GROUP C
   where B.id = a.OLD_43_ID
   and B.IS_PART_OF = C.OLD_43_ID
  )
where exists
  (select 1 from ROLE_GROUP C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---12
V_PROGRAM_NAME := 'UPDATE TG_ITEM_GROUP(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_ITEM_GROUP a
set a.IS_PART_OF =
  (select C.id from
   ITEM_GROUP B,TG_ITEM_GROUP C
   where B.id = a.OLD_43_ID
   and B.IS_PART_OF = C.OLD_43_ID
  )
where exists
  (select 1 from ITEM_GROUP C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---13
V_PROGRAM_NAME := 'UPDATE TG_DEALER_GROUP(IS_PART_OF)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_DEALER_GROUP a
set a.IS_PART_OF =
  (select C.id from
   DEALER_GROUP B,TG_DEALER_GROUP C
   where B.id = a.OLD_43_ID
   and B.IS_PART_OF = C.OLD_43_ID
  )
where exists
  (select 1 from DEALER_GROUP C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---14
V_PROGRAM_NAME := 'UPDATE TG_ORG_USER(SUPERVISOR)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_ORG_USER a
set a.SUPERVISOR =
  (select C.id from
   ORG_USER B,TG_ORG_USER C
   where B.id = a.OLD_43_ID
   and B.SUPERVISOR = C.OLD_43_ID
  )
where exists
  (select 1 from ORG_USER C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'        ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
END;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---15
V_PROGRAM_NAME := 'UPDATE TG_ORG_USER(D_LAST_UPDATED_BY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_ORG_USER a
set a.D_LAST_UPDATED_BY =
  (select C.id from
   ORG_USER B,TG_ORG_USER C
   where B.id = a.OLD_43_ID
   and B.D_LAST_UPDATED_BY = C.OLD_43_ID
  )
where exists
  (select 1 from ORG_USER C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'       ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
END;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
---16
V_PROGRAM_NAME := 'UPDATE TG_ASSEMBLY(IS_PART_OF_ASSEMBLY)';
NJOB_SEQ_ID     := tav_gim_initial_setup.OPEN_LOG (tav_gim_initial_setup.G_INPROG_OK,V_PROGRAM_NAME,systimestamp);
BEGIN
UPDATE TG_ASSEMBLY a
set a.IS_PART_OF_ASSEMBLY =
  (select C.id from
   ASSEMBLY B,TG_ASSEMBLY C
   where B.id = a.OLD_43_ID
   and B.IS_PART_OF_ASSEMBLY = C.OLD_43_ID
  )
where exists
  (select 1 from ASSEMBLY C where C.id = a.OLD_43_ID
  );
 exception
      when OTHERS then
           tav_gim_initial_setup.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );
end;
NSTATUS := tav_gim_initial_setup.close_log (njob_seq_id,tav_gim_initial_setup.g_complete_ok );
----------exception for the whole block----
exception
      when OTHERS then
           tav_gim_initial_setup.PROC_INSERT_ERROR_RECORD
           (GJOB_SEQ_ID              =>     '-99'               ,                       --1
            G_ISSUE_ID               =>     NJOB_SEQ_ID         ,                       --2
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,--3
            G_ISSUE_TYPE             =>     'Issue while updating cyclic reference'  ,    --6
            g_ora_error_message      =>     SQLERRM             ,                                 --13
            G_RUN_DATE               =>     SYSTIMESTAMP        ,                                 --14
            G_BLOCK_NAME             =>    'Block 1'               					              --15
           );


END TAV_GIM_UPDATE_CYCLIC_REF;

/*
|| Procedure Name  : TAV_GIM_UPDATE_BLOBS_60
|| Purpose        : Procedure to populate blobs in the target
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/




procedure TAV_GIM_UPDATE_DOC_LOB
as
NJOB_SEQ_ID number;
NSTATUS number;
V_PROGRAM_NAME varchar2(100);
begin

V_PROGRAM_NAME := 'UPDATE SN_DOCUMENT(CONTENT)';
NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin
UPDATE SN_DOCUMENT A SET A.CONTENT =
(
SELECT C.CONTENT FROM TG_DOCUMENT B , DOCUMENT C
WHERE A.ID = B.ID
AND B.OLD_43_ID = C.ID
)
WHERE EXISTS
(
SELECT 1 FROM TG_DOCUMENT D WHERE D.ID = A.ID
);
 exception
      when OTHERS then
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
            g_ora_error_message      =>     SQLERRM             ,
            G_RUN_DATE               =>     SYSTIMESTAMP        ,
            G_BLOCK_NAME             =>    'Block 1'
           );
END;
NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );


end TAV_GIM_UPDATE_DOC_LOB;


procedure TAV_GIM_UPDATE_CLAIM_LOB
as
NJOB_SEQ_ID number;
NSTATUS number;
V_PROGRAM_NAME varchar2(100);
begin

V_PROGRAM_NAME := 'UPDATE SN_CLAIM_AUDIT(PREV_CLAIM_SNAPSHOT_STRING)';
NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin

UPDATE SN_CLAIM_AUDIT A SET A.PREV_CLAIM_SNAPSHOT_STRING =
(
SELECT C.PREV_CLAIM_SNAPSHOT_STRING FROM TG_CLAIM_AUDIT B , CLAIM_AUDIT C,TG_CLAIM stgclaim
WHERE A.ID = B.ID
AND B.OLD_43_ID = C.ID
AND A.for_claim = stgclaim.id 
--and C.PREV_CLAIM_SNAPSHOT_STRING is not null
and INSTR(NVL(B.INTERNAL_COMMENTS, 'X'), 'XML ID UPDATED') = 0
AND instr(nvl(B.D_INTERNAL_COMMENTS, 'X'), 'XML_ID_UPDATED_61') = 0
AND INSTR(NVL(B.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
and 
((stgclaim.filed_on_date >=add_months(sysdate,-6)  and stgclaim.state not in  ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')) 
or (stgclaim.filed_on_date <add_months(sysdate,-6) and stgclaim.state 
  not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED', 'DRAFT_DELETED', 'DEACTIVATED')))
)
where length(a.id) > 13;
 exception
      when OTHERS then
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.02'      ,
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
            g_ora_error_message      =>     SQLERRM             ,
            G_RUN_DATE               =>     SYSTIMESTAMP        ,
            G_BLOCK_NAME             =>    'Block 1'
           );
end;
NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );

end TAV_GIM_UPDATE_CLAIM_LOB;


procedure TAV_GIM_UPDATE_BLOBS_60
as
NJOB_SEQ_ID number;
NSTATUS number;
V_PROGRAM_NAME varchar2(100);
begin

V_PROGRAM_NAME := 'UPDATE SN_DOCUMENT(CONTENT)';
NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin
UPDATE SN_DOCUMENT A SET A.CONTENT =
(
SELECT C.CONTENT FROM TG_DOCUMENT B , DOCUMENT C
WHERE A.ID = B.ID
AND B.OLD_43_ID = C.ID
)
WHERE EXISTS
(
SELECT 1 FROM TG_DOCUMENT D WHERE D.ID = A.ID
);
 exception
      when OTHERS then
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
            g_ora_error_message      =>     SQLERRM             ,
            G_RUN_DATE               =>     SYSTIMESTAMP        ,
            G_BLOCK_NAME             =>    'Block 1'
           );
end;
NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );


V_PROGRAM_NAME := 'UPDATE SN_CLAIM_AUDIT(PREV_CLAIM_SNAPSHOT_STRING)';
NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin

UPDATE SN_CLAIM_AUDIT A SET A.PREV_CLAIM_SNAPSHOT_STRING =
(
SELECT C.PREV_CLAIM_SNAPSHOT_STRING FROM TG_CLAIM_AUDIT B , CLAIM_AUDIT C
WHERE A.ID = B.ID
AND B.OLD_43_ID = C.ID
)
WHERE EXISTS
(
SELECT 1 FROM TG_CLAIM_AUDIT D WHERE D.ID = A.ID
);
 exception
      when OTHERS then
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.02'      ,
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
            g_ora_error_message      =>     SQLERRM             ,
            G_RUN_DATE               =>     SYSTIMESTAMP        ,
            G_BLOCK_NAME             =>    'Block 1'
           );
end;
NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
V_PROGRAM_NAME := 'UPDATE SN_SYNC_TRACKER(bodxml)';
NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin

UPDATE SN_SYNC_TRACKER A SET A.bodxml =
(
SELECT C.bodxml FROM TG_SYNC_TRACKER B , SYNC_TRACKER C
WHERE A.ID = B.ID
AND B.OLD_43_ID = C.ID
)
WHERE EXISTS
(
SELECT 1 FROM TG_SYNC_TRACKER D WHERE D.ID = A.ID
);
exception
      when OTHERS then
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
            g_ora_error_message      =>     SQLERRM             ,
            G_RUN_DATE               =>     SYSTIMESTAMP        ,
            G_BLOCK_NAME             =>    'Block 1'
           );
end;
NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
V_PROGRAM_NAME := 'UPDATE SN_SYNC_TRACKER(record)';
NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
begin
UPDATE SN_SYNC_TRACKER A SET A.record =
(
SELECT C.record FROM TG_SYNC_TRACKER B , SYNC_TRACKER C
WHERE A.ID = B.ID
AND B.OLD_43_ID = C.ID
)
WHERE EXISTS
(
SELECT 1 FROM TG_SYNC_TRACKER D WHERE D.ID = A.ID
);
  exception
      when OTHERS then
           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
            g_ora_error_message      =>     SQLERRM             ,
            G_RUN_DATE               =>     SYSTIMESTAMP        ,
            G_BLOCK_NAME             =>    'Block 1'
           );
end;
NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );

----
----V_PROGRAM_NAME := 'UPDATE SN_PARTS_UPLOAD(CONTENT)';
----NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
----begin
----
----  UPDATE SN_PARTS_UPLOAD a
----SET A.CONTENT =
----  (select b.content
----   from  TG_PARTS_UPLOAD B
----   where B.id = a.ID
----  )
----WHERE EXISTS
----  (select 1 from TG_PARTS_UPLOAD C where C.id = a.ID
----  );
---- exception
----      when OTHERS then
----           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
----           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
----            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
----            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
----            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
----            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
----            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
----            g_ora_error_message      =>     SQLERRM             ,
----            G_RUN_DATE               =>     SYSTIMESTAMP        ,
----            G_BLOCK_NAME             =>    'Block 1'
----           );
----end;
----NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
----
----V_PROGRAM_NAME := 'UPDATE SN_PARTS_UPLOAD_EMAIL(ERROR_FILE)';
----NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
----begin
----UPDATE SN_PARTS_UPLOAD_EMAIL a
----SET A.ERROR_FILE =
----  (select b.ERROR_FILE
----   from  TG_PARTS_UPLOAD_EMAIL B
----   where B.id = a.ID
----  )
----WHERE EXISTS
----  (select 1 from TG_PARTS_UPLOAD_EMAIL C where C.id = a.ID
----  );
----   exception
----      when OTHERS then
----           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
----           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
----            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
----            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
----            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
----            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
----            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
----            g_ora_error_message      =>     SQLERRM             ,
----            G_RUN_DATE               =>     SYSTIMESTAMP        ,
----            G_BLOCK_NAME             =>    'Block 1'
----           );
----end;
----NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
----
----V_PROGRAM_NAME := 'UPDATE SN_FILE_UPLOAD_MGT(ERROR_FILE_CONTENT)';
----NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
----begin
----  UPDATE SN_FILE_UPLOAD_MGT a
----SET A.ERROR_FILE_CONTENT =
----  (select b.ERROR_FILE_CONTENT
----   from  TG_FILE_UPLOAD_MGT B
----   where B.id = a.ID
----  )
----WHERE EXISTS
----  (select 1 from TG_FILE_UPLOAD_MGT C where C.id = a.ID
----  );
----  exception
----      when OTHERS then
----           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
----           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
----            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
----            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
----            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
----            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
----            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
----            g_ora_error_message      =>     SQLERRM             ,
----            G_RUN_DATE               =>     SYSTIMESTAMP        ,
----            G_BLOCK_NAME             =>    'Block 1'
----           );
----end;
----NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
----
----V_PROGRAM_NAME := 'UPDATE SN_FILE_UPLOAD_MGT(FILE_CONTENT)';
----NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
----begin
----  UPDATE SN_FILE_UPLOAD_MGT a
----SET A.FILE_CONTENT =
----  (select b.FILE_CONTENT
----   from  TG_FILE_UPLOAD_MGT B
----   where B.id = a.ID
----  )
----WHERE EXISTS
----  (select 1 from TG_FILE_UPLOAD_MGT C where C.id = a.ID
----  );
----   exception
----      when OTHERS then
----           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
----           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
----            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
----            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
----            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
----            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
----            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
----            g_ora_error_message      =>     SQLERRM             ,
----            G_RUN_DATE               =>     SYSTIMESTAMP        ,
----            G_BLOCK_NAME             =>    'Block 1'
----           );
----end;
----NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
----
------V_PROGRAM_NAME := 'UPDATE SN_DOCUMENT_DELETED_RECS(CONTENT)';
------NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
------begin
------  UPDATE SN_DOCUMENT_DELETED_RECS a
------SET A.CONTENT =
------  (select b.CONTENT
------   from  TG_DOCUMENT_DELETED_RECS B
------   where B.id = a.OLD_43_ID
------  )
------WHERE EXISTS
------  (select 1 from TG_DOCUMENT_DELETED_RECS C where C.id = a.OLD_43_ID
------  );
------   exception
------      when OTHERS then
------           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
------           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
------            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
------            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
------            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
------            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
------            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
------            g_ora_error_message      =>     SQLERRM             ,
------            G_RUN_DATE               =>     SYSTIMESTAMP        ,
------            G_BLOCK_NAME             =>    'Block 1'
------           );
------end;
------NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
----
---------------exception for the whole proc starts-------------------------------
--
--
--exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
--           (GJOB_SEQ_ID              =>     '-99'               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID         ,
--            G_TABLE_NAME             =>     'BLOB Updation (TAV_GIM_UPDATE_BLOBS_60)'               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     'BLOB MAIN',
--            G_ISSUE_TYPE             =>     'Issue in the main block while updating blobs'  ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.G_COMP_PROC_ERROR );


END TAV_GIM_UPDATE_BLOBS_60;


/*
|| Procedure Name  : TAV_GIM_UPDATE_BLOBS
|| Purpose        : Procedure to populate blobs in the staging table
|| Author         : Joseph Tharakan
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/


procedure TAV_GIM_UPDATE_BLOBS
as
NJOB_SEQ_ID number;
NSTATUS number;
V_PROGRAM_NAME varchar2(100);
begin
--
--V_PROGRAM_NAME := 'UPDATE TG_DOCUMENT(CONTENT)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--UPDATE TG_document a
--SET A.CONTENT =
--  (select b.content
--   from  DOCUMENT B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from DOCUMENT C where C.id = a.OLD_43_ID
--  );
-- exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--
--V_PROGRAM_NAME := 'UPDATE TG_UPLOAD_HISTORY(INPUT_FILE)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--
--UPDATE TG_UPLOAD_HISTORY a
--SET A.INPUT_FILE =
--  (select b.INPUT_FILE
--   from  UPLOAD_HISTORY B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from UPLOAD_HISTORY C where C.id = a.OLD_43_ID
--  );
-- exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--V_PROGRAM_NAME := 'UPDATE TG_UPLOAD_HISTORY(ERROR_FILE)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--
--UPDATE TG_UPLOAD_HISTORY a
--SET A.ERROR_FILE =
--  (select b.ERROR_FILE
--   from  UPLOAD_HISTORY B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from UPLOAD_HISTORY C where C.id = a.OLD_43_ID
--  );
--exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--V_PROGRAM_NAME := 'UPDATE TG_PARTS_UPLOAD(ERROR_FILE)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--UPDATE TG_PARTS_UPLOAD a
--SET A.ERROR_FILE =
--  (select b.ERROR_FILE
--   from  PARTS_UPLOAD B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from PARTS_UPLOAD C where C.id = a.OLD_43_ID
--  );
--  exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--
--V_PROGRAM_NAME := 'UPDATE TG_PARTS_UPLOAD(CONTENT)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--
--  UPDATE TG_PARTS_UPLOAD a
--SET A.CONTENT =
--  (select b.content
--   from  PARTS_UPLOAD B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from PARTS_UPLOAD C where C.id = a.OLD_43_ID
--  );
-- exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--
--V_PROGRAM_NAME := 'UPDATE TG_PARTS_UPLOAD_EMAIL(ERROR_FILE)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--UPDATE TG_PARTS_UPLOAD_EMAIL a
--SET A.ERROR_FILE =
--  (select b.ERROR_FILE
--   from  PARTS_UPLOAD_EMAIL B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from PARTS_UPLOAD_EMAIL C where C.id = a.OLD_43_ID
--  );
--   exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--
--V_PROGRAM_NAME := 'UPDATE TG_FILE_UPLOAD_MGT(ERROR_FILE_CONTENT)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--  UPDATE TG_FILE_UPLOAD_MGT a
--SET A.ERROR_FILE_CONTENT =
--  (select b.ERROR_FILE_CONTENT
--   from  FILE_UPLOAD_MGT B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from FILE_UPLOAD_MGT C where C.id = a.OLD_43_ID
--  );
--  exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
--
--V_PROGRAM_NAME := 'UPDATE TG_FILE_UPLOAD_MGT(FILE_CONTENT)';
--NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
--begin
--  UPDATE TG_FILE_UPLOAD_MGT a
--SET A.FILE_CONTENT =
--  (select b.FILE_CONTENT
--   from  FILE_UPLOAD_MGT B
--   where B.id = a.OLD_43_ID
--  )
--WHERE EXISTS
--  (select 1 from FILE_UPLOAD_MGT C where C.id = a.OLD_43_ID
--  );
--   exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
--           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
--            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
--            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--end;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
----
----V_PROGRAM_NAME := 'UPDATE TG_DOCUMENT_DELETED_RECS(CONTENT)';
----NJOB_SEQ_ID     := TAV_GIM_INITIAL_SETUP.open_log (TAV_GIM_INITIAL_SETUP.g_inprog_ok,v_program_name,SYSTIMESTAMP);
----begin
----  UPDATE TG_DOCUMENT_DELETED_RECS a
----SET A.CONTENT =
----  (select b.CONTENT
----   from  DOCUMENT_DELETED_RECS B
----   where B.id = a.OLD_43_ID
----  )
----WHERE EXISTS
----  (select 1 from DOCUMENT_DELETED_RECS C where C.id = a.OLD_43_ID
----  );
----   exception
----      when OTHERS then
----           TAV_GIM_INITIAL_SETUP.proc_insert_error_record
----           (GJOB_SEQ_ID              =>     NJOB_SEQ_ID               ,
----            G_ISSUE_ID               =>     NJOB_SEQ_ID || '.01'      ,
----            G_TABLE_NAME             =>     V_PROGRAM_NAME               ,
----            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
----            G_ISSUE_COL_VALUE        =>     V_PROGRAM_NAME,
----            G_ISSUE_TYPE             =>     'Issue while updating blob columns' ,
----            g_ora_error_message      =>     SQLERRM             ,
----            G_RUN_DATE               =>     SYSTIMESTAMP        ,
----            G_BLOCK_NAME             =>    'Block 1'
----           );
----end;
--commit;
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.g_complete_ok );
--
-------------exception for the whole proc starts-------------------------------
--
--
--exception
--      when OTHERS then
--           TAV_GIM_INITIAL_SETUP.PROC_INSERT_ERROR_RECORD
--           (GJOB_SEQ_ID              =>     '-99'               ,
--            G_ISSUE_ID               =>     NJOB_SEQ_ID         ,
--            G_TABLE_NAME             =>     'BLOB Updation'               ,
--            G_ISSUE_COL_NAME         =>     'TABLE(COLUMN)',
--            G_ISSUE_COL_VALUE        =>     'BLOB MAIN',
--            G_ISSUE_TYPE             =>     'Issue in the main block while updating blobs'  ,
--            g_ora_error_message      =>     SQLERRM             ,
--            G_RUN_DATE               =>     SYSTIMESTAMP        ,
--            G_BLOCK_NAME             =>    'Block 1'
--           );
--NSTATUS := TAV_GIM_INITIAL_SETUP.close_log (njob_seq_id,TAV_GIM_INITIAL_SETUP.G_COMP_PROC_ERROR );
null;
END TAV_GIM_UPDATE_BLOBS;



/*--- end of  TAV_GIM_UPDATE_BLOBS-----*/

/*
|| Procedure Name  : TAV_GIM_POPULATE_MASTER
|| Purpose        : Procedure to populate 6.0 tables from staging
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE tav_gim_populate_master(p_out_error_code OUT NUMBER,p_out_error_message OUT VARCHAR2)
AS

  v_insert_stmt VARCHAR2(100) := ' INSERT /*+ APPEND */ INTO ';
  v_select_stmt VARCHAR2(100) := ' SELECT ';
  v_from_stmt VARCHAR2(100) := ' FROM ';
  v_where_condtn1 VARCHAR2(100) := ' WHERE MIGRATE_FLAG = ''Y''';
  v_open_bracket VARCHAR2(10) := '(';
  v_close_bracket VARCHAR2(10) := ')';
  v_space_delimiter VARCHAR2(10) := ' ';
  v_synonym_name VARCHAR2(30);

  v_stmt_number NUMBER := 0;
  v_error_code NUMBER;
  V_ERROR_MESSAGE varchar2(4000);
  v_procedure_name varchar2(4000);
  v_table_name VARCHAR2(30);
  v_stg_table_name VARCHAR2(30);
  V_COLUMN_STR varchar2(4000);
  v_ddl_stmt varchar2(4000);
  v_column_name VARCHAR2(100);
  v_insert_column_str VARCHAR2(4000);
  v_select_column_str VARCHAR2(4000);
  v_sql_stmt VARCHAR2(4000);
  v_src_stg_table_cnt NUMBER;

  v_insert_60_tables_exc EXCEPTION;
  v_update_migrate_flag_exc EXCEPTION;

  -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	DATE ;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;
BEGIN

  dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));

  v_stmt_number := 10;
  -- Selecting the tables names from valid table lists
SELECT table_name
    BULK COLLECT INTO v_varchar_tab_array
  FROM TAV_GIM_VALID_TABLES
  where EXEC_ORDER > 0
--  AND exec_order > 0
  AND LOAD_STATUS NOT LIKE 'NOT%PROCESSED'
  and load_status not like '%-UPLOAD'
--  and table_name = 'CLAIM_AUDIT'
  ORDER BY exec_order;

   dbms_output.put_line('Table Count : ' || v_varchar_tab_array.COUNT);

  v_stmt_number := 20;
  -- Start of Master loop for processing each table
  FOR i IN v_varchar_tab_array.FIRST..v_varchar_tab_array.LAST LOOP

    -- Initializing Iterative Variables
    v_stmt_number := 30;
    v_table_name := v_varchar_tab_array(i);
    v_program_name := v_varchar_tab_array(i);

    v_migration_date := SYSTIMESTAMP;
    v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
    v_insert_column_str := NULL;
    v_select_column_str := NULL;

    -- Retrieving Staging table name for the given source table name
    v_stmt_number := 40;
    v_stg_table_name := tav_gim_process_migration.get_staging_table_name(v_table_name);

    -- Retrieving Synonym name for a give table name
    v_stmt_number := 50;
    v_synonym_name := tav_gim_process_migration.get_synonym_name(v_table_name);
    
    
   
 IF  V_TABLE_NAME = 'SYNC_TRACKER'  THEN      --running separate procs for latge blobs
      
--       IF V_TABLE_NAME = 'SYNC_TRACKER' 
--       THEN  V_PROCEDURE_NAME := 'TAV_GIM_COMP_UNIQUE_SCNEARIOS.POPULATE_SN_SYNC_TRACKER';
--       ELSIF V_TABLE_NAME = 'DOCUMENT' 
--       THEN  V_PROCEDURE_NAME := 'TAV_GIM_COMP_UNIQUE_SCNEARIOS.POPULATE_SN_DOCUMENT';
--       ELSE  V_PROCEDURE_NAME := 'TAV_GIM_COMP_UNIQUE_SCNEARIOS.POPULATE_SN_CLAIM_AUDIT';   
--       END IF;
       
       V_PROCEDURE_NAME := 'TAV_GIM_COMP_UNIQUE_SCENARIOS.POPULATE_SN_SYNC_TRACKER';
       
       v_ddl_stmt := 'BEGIN ' || v_procedure_name || '(' || v_job_seq_id || ');' || ' END;' ;
       
       v_stmt_number := 55;
        -- Invoke the procedure for the given table
        EXECUTE IMMEDIATE v_ddl_stmt;
        
       
          V_STMT_NUMBER := 57;  
    
        COMMIT;    
        
  ELSE   --else for sync tracker if loop
   
    
        -- Selecting columns from data dictionary for the given table
    v_stmt_number := 40;
    SELECT column_name
      BULK COLLECT INTO v_table_column_name
    FROM user_tab_columns
      WHERE table_name = v_table_name
      ORDER BY column_id;



   v_stmt_number := 50;
    -- Start of inner loop for processing each column of the given table
    FOR col_name IN v_table_column_name.FIRST..v_table_column_name.LAST LOOP


      v_column_name := v_table_column_name(col_name);
      v_stmt_number := 60;

      IF(tav_gim_process_migration.is_cyclic_ref_column(v_table_name,v_column_name)>0) THEN
--        v_column_name := 'NULL AS ' || v_column_name;
      GOTO end_loop;
      END IF;

      IF v_insert_column_str IS NULL THEN
        v_insert_column_str := v_column_name || ',';
      ELSE
        v_insert_column_str := v_insert_column_str || v_column_name || ',';
      END IF;



      IF v_select_column_str IS NULL THEN
        v_select_column_str := v_column_name || ',';
      ELSE
        v_select_column_str := v_select_column_str || v_column_name || ',';
      END IF;


   <<end_loop>>
   NULL;

    END LOOP;
    -- End of inner loop for processing each column of the given table

        -- Removing the ',' from the last column
        v_stmt_number := 70;
        v_insert_column_str := RTRIM(v_insert_column_str,',');
        v_select_column_str := RTRIM(v_select_column_str,',');
       -- Framing the Insert statement
       v_stmt_number := 80;
       v_sql_stmt := v_insert_stmt ||  v_synonym_name || v_open_bracket || v_insert_column_str || v_close_bracket ;
       v_sql_stmt := v_sql_stmt || v_space_delimiter || v_select_stmt || v_space_delimiter || v_select_column_str || v_space_delimiter || v_from_stmt || v_space_delimiter || v_stg_table_name;
       v_sql_stmt := v_sql_stmt || v_space_delimiter || v_where_condtn1;
--       dbms_output.put_line('Insert Statement : ' || v_sql_stmt);



       -- Updating query tracker with the framed insert query
       v_stmt_number := 90;
       UPDATE tav_gim_query_tracker
       SET insert_query_60 = v_sql_stmt || ';-->' || systimestamp
       WHERE table_name = v_table_name;


       -- Calling procedure to insert into 6.0 tables
       v_stmt_number := 100;
       tav_gim_process_migration.insert_60_tables(p_in_insert_query => v_sql_stmt,
                           p_in_table_name =>  v_table_name,
                           p_out_error_code => v_error_code,
                           p_out_error_message => v_error_message);
      IF v_error_code <> 0 THEN
        RAISE v_insert_60_tables_exc;
      END IF;
      
      
  END IF; --ending the if for sync tracker lob population
  
  
  
      v_stmt_number := 90;


       EXECUTE IMMEDIATE 'SELECT COUNT(1) FROM ' || v_stg_table_name || v_where_condtn1 INTO v_src_stg_table_cnt;
       
       UPDATE TAV_GIM_VALID_TABLES
       SET stg_table_cnt_y = v_src_stg_table_cnt,load_status = load_status || '-UPLOAD'
       WHERE table_name = v_table_name;

       COMMIT;      

      p_out_error_code := 0;
      p_out_error_message := 'Procedure TAV_GIM_POPULATE_MASTER executed successfully.';

      -- closing log Statistics
      v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );

  END LOOP;
  -- End of Master loop for processing each table
   dbms_output.put_line('procedure end time : ' || to_char( sysdate, 'hh24:mi:ss' ));

EXCEPTION
WHEN v_insert_60_tables_exc THEN
  ROLLBACK;
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'Exception Occured while INSERT INTO 6.0 table. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => v_error_message,
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in INSERT 6.0 TABLE.' );
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
WHEN OTHERS THEN
  ROLLBACK;
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'Exception occured while processing table in Populate Master Procedure.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in TAV_GIM_POPULATE_MASTER' );
  v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
END tav_gim_populate_master;

/*
|| Procedure Name : INSERT_60_TABLES
|| Purpose        : Procedure to insert data into 6.0 tables
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE insert_60_tables(p_in_insert_query IN VARCHAR2,
                           p_in_table_name IN VARCHAR2,
                           p_out_error_code OUT NUMBER,
                           p_out_error_message OUT VARCHAR2)
AS
v_stmt_number NUMBER := 0;
v_row_count NUMBER ;
BEGIN
    v_stmt_number := 10;
    EXECUTE IMMEDIATE p_in_insert_query;

     v_stmt_number := 20;
    v_row_count := SQL%ROWCOUNT;

     v_stmt_number := 30;
    UPDATE tav_gim_valid_tables
    SET dest_table_cnt = v_row_count
    WHERE table_name = p_in_table_name;

    p_out_error_code := 0;
    p_out_error_message := 'Procedure INSERT_60_TABLES executed successfully';
EXCEPTION
WHEN OTHERS THEN
p_out_error_code := SQLCODE;
p_out_error_message := 'Exception occured while inserting into 6.0 tables. Statement Number : ' || v_stmt_number || '. ' || SUBSTR(SQLERRM,1,255);
END insert_60_tables;


FUNCTION get_constraint_key_count(p_in_table_name IN VARCHAR2, p_in_constraint_type IN VARCHAR2) RETURN VARCHAR2
AS
v_constraint_key_count NUMBER;
BEGIN
    SELECT COUNT(a.constraint_type)
    INTO v_constraint_key_count
    from user_constraints a , user_cons_columns b
    where constraint_type = p_in_constraint_type
    and a.constraint_name = b.constraint_name
    and a.table_name = p_in_table_name;

    RETURN v_constraint_key_count;

EXCEPTION
WHEN OTHERS THEN
NULL;
END get_constraint_key_count;

/*
|| Procedure Name : UPDATE_MIGRATE_FLAG
|| Purpose        : Procedure to update migrate flag
|| Author         : Joseph
|| Version        : Initial Write-Up
|| Creation Date  : 19/01/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE update_migrate_flag(p_in_table_name IN VARCHAR2,
                              p_in_stg_table_name IN VARCHAR2,
                              p_out_error_code OUT NUMBER,
                              p_out_error_message OUT VARCHAR2)
AS
  v_pk_cnt NUMBER;
  v_pk_name_1 VARCHAR2(40);
  v_pk_name_2 VARCHAR2(40);
  v_pk_comp_pk VARCHAR2(500);
  v_exec_string VARCHAR2(4000);
  v_stmt_number NUMBER := 0;
  v_synonym_name VARCHAR2(30);
BEGIN

      v_stmt_number := 10;
     SELECT COUNT(B.COLUMN_NAME)
        INTO v_pk_cnt
    FROM user_constraints A ,  user_cons_columns B
      WHERE A.table_name    = p_in_table_name
        AND A.constraint_type = 'P'
        AND A.constraint_name = B.constraint_name;

    -- Selecting the Synonym name for a given table
    SELECT tav_gim_process_migration.get_synonym_name(p_in_table_name)
    INTO v_synonym_name
    FROM DUAL;


    v_stmt_number := 20;
     IF v_pk_cnt = 1 THEN   --gettin pk_name when pk_count is 1
        v_pk_name_1 := tav_gim_process_migration.get_pk_column_name(p_in_table_name);
    END IF;

    v_stmt_number := 30;
    IF (v_pk_cnt = 1 AND tav_gim_process_migration.isforeignkey(p_in_table_name,v_pk_name_1) =1) THEN

        v_stmt_number := 40;
        v_exec_string := 'UPDATE ' || p_in_stg_table_name || ' A SET A.MIGRATE_FLAG = ''N'' WHERE EXISTS
       (SELECT 1 FROM ' || v_synonym_name  || ' B WHERE B.' || v_pk_name_1 || '= A.' || v_pk_name_1 || ')';

    ELSIF v_pk_cnt = 2 THEN

        v_stmt_number := 50;

        SELECT pk_1,pk_2
        INTO  v_pk_name_1,v_pk_name_2
        FROM
        (
        SELECT b.column_name pk_1,
        LEAD(b.column_name,1) OVER (ORDER BY B.column_name) pk_2
        FROM user_constraints A ,
        user_cons_columns B
        WHERE a.table_name    = p_in_table_name
        AND A.constraint_type = 'P'
        AND A.constraint_name = B.constraint_name
        )
        WHERE pk_2 IS NOT NULL;

        v_exec_string := 'UPDATE ' || p_in_stg_table_name || ' A SET A.MIGRATE_FLAG = ''N'' WHERE EXISTS
       (SELECT 1 FROM ' || v_synonym_name || ' B WHERE b.' ||
        v_pk_name_1 || '= a.' || v_pk_name_1 || ' and b.' ||
        v_pk_name_2 || '= a.' || v_pk_name_2 || ')';

    ELSE
        v_stmt_number := 60;
        v_exec_string := null; --no table with 3 pks
    END IF;

    v_stmt_number := 70;
    IF v_exec_string IS NOT NULL THEN
      EXECUTE IMMEDIATE v_exec_string;
    END IF;

    p_out_error_code := 0;
    p_out_error_message := 'Procedure UPDATE_MIGRATE_FLAG executed successfully';
EXCEPTION
WHEN OTHERS THEN
p_out_error_code := SQLCODE;
p_out_error_message := 'Exception occured while updating migrate flag. Statement Number : ' || v_stmt_number || ' ' || SUBSTR(SQLERRM,1,255);
END update_migrate_flag;


/*
|| Procedure Name : UPDATE_CONFIG_VALUE
|| Purpose        : Procedure to update config value table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 18/03/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE update_config_value(p_out_error_code OUT NUMBER,
                              p_out_error_message OUT VARCHAR2)
AS
v_stmt_number NUMBER := 0;
BEGIN
  v_stmt_number := 10;

  UPDATE TG_CONFIG_VALUE A
  SET a.value =
  (SELECT B.ID
      FROM TG_LIST_OF_VALUES B , TG_CONFIG_PARAM C
      WHERE A.VALUE      = B.OLD_43_ID
      AND REGEXP_LIKE (a.VALUE,'[[:digit:]]')
      AND lower(c.name) IN ('defaultaccountabilitycode' ,'defaultacceptancereason' ,'defaultacceptancereasonforcp' ,'defaultrejectionreasonclaimprocessing' ,'defaultrejectionreasonoverdueparts' ,'defaultrejectionreasonforwardedclaim' ,'campaignclassforwarningonehp')
      AND C.ID = A.CONFIG_PARAM)
  WHERE EXISTS
        (SELECT 1
              FROM TG_LIST_OF_VALUES D, TG_CONFIG_PARAM E
              WHERE A.VALUE      = D.OLD_43_ID
              AND REGEXP_LIKE (a.VALUE,'[[:digit:]]')
              AND LOWER(E.NAME) IN ('defaultaccountabilitycode' ,'defaultacceptancereason' ,'defaultacceptancereasonforcp' ,'defaultrejectionreasonclaimprocessing' ,'defaultrejectionreasonoverdueparts' ,'defaultrejectionreasonforwardedclaim' ,'campaignclassforwarningonehp')
              AND E.ID = A.CONFIG_PARAM);

  v_stmt_number := 20;
  UPDATE TG_CONFIG_VALUE A
    SET A.VALUE =
        (SELECT b.id
            FROM TG_COST_CATEGORY B , TG_CONFIG_PARAM C
                WHERE A.VALUE      = B.OLD_43_ID
                AND REGEXP_LIKE (a.VALUE,'[[:digit:]]')
                AND lower(c.name) IN ('configuredcostcategories')
                AND C.ID = A.CONFIG_PARAM)
    WHERE EXISTS
        (SELECT 1
            FROM TG_COST_CATEGORY D, TG_CONFIG_PARAM E
          WHERE A.VALUE      = D.OLD_43_ID
          AND REGEXP_LIKE (a.VALUE,'[[:digit:]]')
          AND LOWER(E.NAME) IN ('configuredcostcategories')
          AND E.ID = A.CONFIG_PARAM);
          
          

commit;
   p_out_error_code := 0;
   p_out_error_message := 'Procedure UPDATE_MIGRATE_FLAG executed successfully';

EXCEPTION
WHEN OTHERS THEN
p_out_error_code := SQLCODE;
p_out_error_message := 'Exception occured while updating config_value. Statement Number : ' || v_stmt_number || ' ' || SUBSTR(SQLERRM,1,255);
END update_config_value;

/*
|| Function Name  : GET_SYNONYM_NAME
|| Purpose        : Function to retrieve the framed synonym name for a given table
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 27/01/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION get_synonym_name(p_in_table_name IN VARCHAR2) RETURN VARCHAR2
AS
v_synonym_name VARCHAR2(30);
BEGIN
    SELECT synonym_name
      INTO v_synonym_name
    FROM tav_gim_valid_tables
      WHERE table_name = p_in_table_name;

    RETURN v_synonym_name;
EXCEPTION
WHEN OTHERS THEN
NULL;
END get_synonym_name;

END tav_gim_process_migration;
/
