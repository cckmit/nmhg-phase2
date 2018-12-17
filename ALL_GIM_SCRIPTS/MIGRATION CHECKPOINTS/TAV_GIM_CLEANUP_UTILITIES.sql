CREATE OR REPLACE PACKAGE                                           tav_gim_cleanup_utilities
AS
PROCEDURE reset_valid_tables( p_in_load_status IN VARCHAR2 DEFAULT NULL,
                              p_out_error_code OUT NUMBER,
                              p_out_error_message OUT VARCHAR2);

PROCEDURE cleanup_staging_tables( p_in_load_status IN VARCHAR2 DEFAULT NULL,
                                  p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2);

PROCEDURE cleanup_synonyms(p_out_error_code OUT NUMBER,
                           p_out_error_message OUT VARCHAR2);

END tav_gim_cleanup_utilities;
/


CREATE OR REPLACE PACKAGE BODY                                                                       tav_gim_cleanup_utilities
AS
PROCEDURE reset_valid_tables(
    p_in_load_status IN VARCHAR2 DEFAULT NULL,
    p_out_error_code OUT NUMBER,
    p_out_error_message OUT VARCHAR2)
AS
BEGIN
  IF p_in_load_status IS NULL THEN

  UPDATE tav_gim_valid_tables
    SET load_status = 'NOT PROCESSED',
        run_seq_id = 0,
        src_table_cnt = 0,
        stg_table_cnt = 0;
  END IF;

  COMMIT;
EXCEPTION
WHEN OTHERS THEN
  NULL;
END reset_valid_tables;

PROCEDURE cleanup_staging_tables( p_in_load_status IN VARCHAR2 DEFAULT NULL,
                                  p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2)
AS
BEGIN
  IF p_in_load_status IS NULL THEN
    FOR cur_rec IN (SELECT stg_table_name FROM tav_gim_valid_tables
                    WHERE (load_status IS NOT NULL and load_status != 'NOT PROCESSED')
                    ORDER BY exec_order desc) LOOP
            EXECUTE IMMEDIATE 'DELETE FROM ' || cur_rec.stg_table_name;
            COMMIT;
   END LOOP;
  END IF;
EXCEPTION
WHEN OTHERS THEN
NULL;
END cleanup_staging_tables;

PROCEDURE cleanup_synonyms(p_out_error_code OUT NUMBER,
                           p_out_error_message OUT VARCHAR2)
AS

v_ddl_stmt   VARCHAR2(32767);
v_stmt_number NUMBER ;

CURSOR cur_valid_tables IS
  SELECT table_name,synonym_name
    FROM tav_gim_valid_tables;

BEGIN

  v_stmt_number := 10;
  FOR cur_valid_tables_rec IN cur_valid_tables LOOP

      v_stmt_number := 20;
      v_ddl_stmt := 'DROP SYNONYM ' || cur_valid_tables_rec.synonym_name;

      v_stmt_number := 30;
      EXECUTE IMMEDIATE v_ddl_stmt;

  END LOOP;

  p_out_error_code := 0;
  p_out_error_message := 'Procedure CLEANUP_SYNONYMS executed successfully';
  dbms_output.put_line(p_out_error_message);
EXCEPTION
WHEN OTHERS THEN
p_out_error_code := SQLCODE;
p_out_error_message := SUBSTR(SQLERRM,1,255);
dbms_output.put_line('Exception Occured while dropping synonyms. Statement Number : ' || v_stmt_number || '. ' || p_out_error_code || ' ' || p_out_error_message);
END cleanup_synonyms;
END tav_gim_cleanup_utilities;
/
