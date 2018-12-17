/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_CREATE_STAGING_AREA.sql
 *
 * Purpose	:	This will create staging area with staging table structure and constraints in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   		   Code Organization
 *-----------------------------------------------------------------------------
*/

SPOOL LOG_4_VALIDATE_CREATE_STAGING_AREA_PHASE1.log

PROMPT Message : Starting creation of Staging Area...
PROMPT
PROMPT Message : Dropping CONSTRAINTS for STAGING TABLES.Please wait...
PROMPT
PROMPT Message : Phase 1 ==> Drop Constraints
PROMPT
@DROP_CONSTRAINTS.sql;
PROMPT
PROMPT Message : Phase 2 ==> Drop Constraints
PROMPT
@DROP_CONSTRAINTS.sql;
PROMPT
PROMPT Message : Dropping Constraints completed!
PROMPT

PROMPT ********** VALIDATION OF DROP CONSTRAINTS **********
PROMPT
PROMPT List of CONSTRAINTS not dropped:
PROMPT
select b.constraint_name constraint_name,a.STG_TABLE_NAME stage_table from TAV_GIM_VALID_TABLES  a,user_constraints b
where a.STG_TABLE_NAME = B.TABLE_NAME
and b.constraint_type in ('P','R','U');
PROMPT
PROMPT ********** END OF VALIDATION OF DROP CONSTRAINTS **********
PROMPT
PROMPT Message : Dropping existing STAGING TABLES. Please wait...
PROMPT
@DROP_TABLES.SQL;
PROMPT
PROMPT Message : Dropping Staging Tables completed!
PROMPT
PROMPT ********** VALIDATION OF DROP STAGING TABLES **********
PROMPT
PROMPT List of STAGING TABLES not dropped:
PROMPT
select a.STG_TABLE_NAME from TAV_GIM_VALID_TABLES  a,user_tables B
WHERE A.STG_TABLE_NAME = B.TABLE_NAME;
PROMPT
PROMPT ********** END OF VALIDATION OF DROP STAGING TABLES **********
PROMPT
PROMPT Message: Next Step ==> Create Staging Tables and Constraints
PROMPT
PROMPT Message: Please refer LOG_4_VALIDATE_CREATE_STAGING_AREA_PHASE1.log before proceeding
PROMPT

SPOOL OFF

PROMPT User Action: Press 'Enter' to continue (or) Close this window to 'Abort'
PAUSE

SPOOL LOG_5_VALIDATE_CREATE_STAGING_AREA_PHASE2.log

PROMPT Message: Creating STAGING TABLES.Please provide the source schema name 
PROMPT
declare 
P_IN_60_SCHEMA_OWNER varchar(100);
P_IN_TABLE_CONS_BOTH varchar(10);
BEGIN
  P_IN_60_SCHEMA_OWNER := '&&SOURCE_SCHEMA_NAME';
  P_IN_TABLE_CONS_BOTH := 'B';
  
  TAV_GIM_INITIAL_SETUP.PROC_CREATE_ALTER_STG_TABLES(
    P_IN_60_SCHEMA_OWNER => P_IN_60_SCHEMA_OWNER,
    P_IN_TABLE_CONS_BOTH => P_IN_TABLE_CONS_BOTH
  );
END;
/
PROMPT Message: Creating STAGING TABLES completed!
PROMPT
PROMPT ********** VALIDATION OF CREATE STAGING TABLES **********
PROMPT
PROMPT Number of staging tables created:
select count(1) TABLE_COUNT from user_tables a ,TAV_GIM_VALID_TABLES B where a.TABLE_NAME = B.stg_TABLE_NAME ;

PROMPT

PROMPT List of Valid staging tables not created:
SELECT table_name, stg_table_name  FROM TAV_GIM_VALID_TABLES where stg_table_name NOT IN (SELECT TABLE_NAME FROM USER_TABLES);

PROMPT

PROMPT Number of constraints existing in the SOURCE for this owner:

PROMPT
select 
count(a.CONSTRAINT_NAME) SOURCE_CONSTRAINT_COUNT
from ALL_CONSTRAINTS a , TAV_GIM_VALID_TABLES B
where a.TABLE_NAME = B.TABLE_NAME 
AND A.OWNER = '&&SOURCE_SCHEMA_NAME'
AND CONSTRAINT_TYPE IN ('P','R','U')
and b.table_name not in ('ADD_PAYMENT_INFO','LINE_ITEM_GROUPS','ITEMS_IN_GROUP','I18NITEM_TEXT','MODIFIERS');

PROMPT

PROMPT Number of constraints existing in the STAGING for this owner:

PROMPT
select 
count(a.CONSTRAINT_NAME) STAGE_CONSTRAINT_COUNT
from ALL_CONSTRAINTS a , TAV_GIM_VALID_TABLES B
where a.TABLE_NAME = B.stg_TABLE_NAME 
AND A.OWNER = '&&SOURCE_SCHEMA_NAME'
AND CONSTRAINT_TYPE IN ('P','R','U');

PROMPT

PROMPT List of MISSING CONSTRAINTS in staging 
select 
case
when length('TG_' || a.CONSTRAINT_NAME)>30 then ('TG_' || SUBSTR(a.CONSTRAINT_NAME,4,30))
else ('TG_' || a.CONSTRAINT_NAME)
end  CONSTRAINT_NAME
from ALL_CONSTRAINTS a , TAV_GIM_VALID_TABLES B
where a.TABLE_NAME = B.TABLE_NAME 
and b.table_name not in ('ADD_PAYMENT_INFO','LINE_ITEM_GROUPS','ITEMS_IN_GROUP','I18NITEM_TEXT','MODIFIERS')
AND A.OWNER = '&&SOURCE_SCHEMA_NAME'
AND CONSTRAINT_TYPE IN ('P','R','U')
minus
select a.CONSTRAINT_NAME from USER_CONSTRAINTS a , TAV_GIM_VALID_TABLES B 
where a.TABLE_NAME = B.STG_TABLE_NAME 
and A.constraint_type in ('P','R','U');

PROMPT
PROMPT ********** END OF VALIDATION OF CREATE STAGING TABLES **********
PROMPT
PROMPT Message: Please refer LOG_5_VALIDATE_CREATE_STAGING_AREA_PHASE2.log before proceeding

PROMPT Message: Next process ==> Porting and Compiling Migration Packages
PROMPT

SPOOL OFF

PROMPT User Action: Press 'Enter' to continue (or) Close this window to 'Abort'.
PAUSE

@@TAV_GIM_PORT_OTHER_MIGRATION_PACKAGES.SQL














