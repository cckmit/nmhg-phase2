/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_CREATE_MIGRATION_OBJECTS.sql
 *
 * Purpose	:	This will create migration objects in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   Code Organization
 *-----------------------------------------------------------------------------
*/

Prompt Message: Dropping objects if already availbale...

DROP SEQUENCE TAV_GIM_JOB_SEQ;
DROP TABLE TAV_GIM_MASTER_LOG CASCADE CONSTRAINTS;
DROP TABLE TAV_GIM_EXCEPTION_LOG;
DROP TABLE TAV_GIM_LOOKUP_60_43_char; 
DROP TABLE TAV_GIM_VALID_TABLES;
DROP TABLE TAV_GIM_COMPOSITE_KEY_TABLES;
DROP TABLE TAV_GIM_CYCLIC_REF;


Prompt Message: Creating Composite key table...

CREATE TABLE TAV_GIM_COMPOSITE_KEY_TABLES
  (
    TABLE_NAME      VARCHAR2(30 BYTE),
    CONSTRAINT_TYPE VARCHAR2(10 BYTE),
    PK_COUNT        NUMBER
  );


Prompt Message: Creating Self/Cyclic references table...

CREATE TABLE TAV_GIM_CYCLIC_REF
  (
    CYC_REF_TABLE  VARCHAR2(100 BYTE),
    CYC_REF_COLUMN VARCHAR2(100 BYTE),
    IGNORE         VARCHAR2(20 BYTE)
  );


Prompt Message: Creating TAV_GIM_VALID_TABLES table...

CREATE TABLE TAV_GIM_VALID_TABLES
  (
    EXEC_ORDER       NUMBER,
    TABLE_NAME       VARCHAR2(30 BYTE),
    STG_TABLE_NAME   VARCHAR2(30 BYTE),
    STG_CREATED_FLAG VARCHAR2(10 BYTE),
    LOAD_STATUS      VARCHAR2(20 BYTE),
    RUN_SEQ_ID       NUMBER,
    LOOKUP_FUNCTION  VARCHAR2(500 BYTE),
    SRC_TABLE_CNT    NUMBER,
    STG_TABLE_CNT    NUMBER,
    STG_TABLE_CNT_Y  NUMBER,
    DEST_TABLE_CNT   NUMBER,
    PROCEDURE_NAME   VARCHAR2(500 BYTE),
    SYNONYM_NAME     VARCHAR2(30 BYTE),
    HIBERNATE_ENTITY varchar2(1000 byte)
  );


Prompt Message: Creating Query Tracker table...

CREATE TABLE TAV_GIM_QUERY_TRACKER
  (
    TABLE_NAME VARCHAR2(500 BYTE),
    INSERT_QUERY CLOB,
    SELECT_QUERY CLOB,
    INSERT_QUERY_60 CLOB
  );
  
Prompt Message: Creating temperory lookup table table...

CREATE TABLE TAV_GIM_LOOKUP_60_43_CHAR
(
	PK_60_ID varchar2(4000),
	PK_43_ID varchar2(4000),
	TABLE_NAME varchar(50)
);



Prompt Message: Creating Master log and Exception Log tables

-- Create Sequence for Job Sequence Id for Master Log and Exception Log tables
CREATE SEQUENCE TAV_GIM_JOB_SEQ  
  MINVALUE 1 
  MAXVALUE 9999999999999999999999999999 
 INCREMENT BY 1 
     START WITH 1
     CACHE 20 
   NOORDER NOCYCLE;
 
-- Create Master Log table
CREATE TABLE TAV_GIM_MASTER_LOG 
   (	JOB_SEQ_ID NUMBER NOT NULL ENABLE, 
		EXECUTION_START_DATE TIMESTAMP, 
		EXECUTION_ENDED_DATE TIMESTAMP, 
        TIME_TAKEN VARCHAR2(100 BYTE),  
		JOB_NAME VARCHAR2(100 BYTE), 
		CURRENT_STATUS VARCHAR2(50 BYTE)
);   

-- Create Indexes for Master Log table
CREATE INDEX CTL_MIG_MLENTRY_STATUS ON TAV_GIM_MASTER_LOG (CURRENT_STATUS, JOB_NAME); 

CREATE UNIQUE INDEX PK_HANDLE ON TAV_GIM_MASTER_LOG (JOB_SEQ_ID); 

-- Create Constraints for Master log table
ALTER TABLE TAV_GIM_MASTER_LOG  ADD (
  CONSTRAINT CHECK_STATUS CHECK (CURRENT_STATUS IN ('IN-PROG-OK','COMPLETE-OK','COMPLETE-LOGGING-ERROR','COMPLETE-PROC-ERROR')));
  
ALTER TABLE TAV_GIM_MASTER_LOG  ADD (
  CONSTRAINT PK_HANDLE PRIMARY KEY (job_seq_id));

-- Create Exception Log table
CREATE TABLE TAV_GIM_EXCEPTION_LOG 
(
JOB_SEQ_ID NUMBER,
ISSUE_ID varchar2(10), 
TABLE_NAME varchar2(100 byte),
ISSUE_COL_NAME  VARCHAR2(1000 BYTE),
ISSUE_COL_VALUE  VARCHAR2(4000 BYTE),
ISSUE_TYPE  VARCHAR2(2000 BYTE),
KEY_COL_NAME_1  VARCHAR2(200 BYTE),
KEY_COL_VALUE_1 VARCHAR2(4000 BYTE),
KEY_COL_NAME_2 VARCHAR2(200 BYTE),
KEY_COL_VALUE_2 VARCHAR2(4000 BYTE),
KEY_COL_NAME_3 VARCHAR2(200 BYTE),
KEY_COL_VALUE_3 VARCHAR2(4000 BYTE),
KEY_COL_NAME_4 VARCHAR2(200 BYTE),
KEY_COL_VALUE_4 VARCHAR2(4000 BYTE),
KEY_COL_NAME_5 VARCHAR2(200 BYTE),
KEY_COL_VALUE_5 VARCHAR2(200 BYTE),
KEY_COL_NAME_6 VARCHAR2(200 BYTE),
KEY_COL_VALUE_6 VARCHAR2(200 BYTE),
KEY_COL_NAME_7 VARCHAR2(200 BYTE),
KEY_COL_VALUE_7 VARCHAR2(200 BYTE),
KEY_COL_NAME_8 VARCHAR2(200 BYTE),
KEY_COL_VALUE_8 VARCHAR2(200 BYTE),
KEY_COL_NAME_9 VARCHAR2(200 BYTE),
KEY_COL_VALUE_9 VARCHAR2(200 BYTE),
KEY_COL_NAME_10 VARCHAR2(200 BYTE),
KEY_COL_VALUE_10 VARCHAR2(200 BYTE),
KEY_COL_NAME_11 VARCHAR2(200 BYTE),
KEY_COL_VALUE_11 VARCHAR2(200 BYTE),
KEY_COL_NAME_12 VARCHAR2(200 BYTE),
KEY_COL_VALUE_12 VARCHAR2(200 BYTE),
KEY_COL_NAME_13 VARCHAR2(200 BYTE),
KEY_COL_VALUE_13 VARCHAR2(200 BYTE),
KEY_COL_NAME_14 VARCHAR2(200 BYTE),
KEY_COL_VALUE_14 VARCHAR2(200 BYTE),
KEY_COL_NAME_15 VARCHAR2(200 BYTE),
KEY_COL_VALUE_15 VARCHAR2(200 BYTE),
ORA_ERROR_MESSAGE VARCHAR2(4000 BYTE), 
RUN_DATE timestamp,
BLOCK_NAME varchar2(2000 byte)
);


