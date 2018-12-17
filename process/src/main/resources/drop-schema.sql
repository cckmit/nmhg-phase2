SET NEWPAGE 0
SET SPACE 0
SET LINESIZE 80
SET PAGESIZE 0
SET ECHO OFF
SET FEEDBACK OFF
SET HEADING OFF
SET MARKUP HTML OFF
SET ESCAPE \
SPOOL DELETEME.SQL
select 'drop table ', table_name, ' cascade constraints \;' from user_tables;
SPOOL OFF
@DELETEME
/
BEGIN 
FOR I IN ( SELECT SEQUENCE_NAME FROM all_sequences WHERE SEQUENCE_OWNER='TWMS_OWNER')
LOOP
EXECUTE IMMEDIATE 'DROP SEQUENCE TWMS_OWNER.' || I.SEQUENCE_NAME;
END LOOP ;
END ;
/