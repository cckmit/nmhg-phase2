/*-----------------------------------------------------------------------------
 * File Name	:	VALIDATE_CREATE_INSERT_MIGRATION_OBJECTS.sql
 *
 * Purpose	:	This will validate migration objects in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   Code Organization
 *-----------------------------------------------------------------------------
*/

PROMPT 

PROMPT '*********** Validation of Migration Objects ***********'

PROMPT 

PROMPT Number of records in TAV_GIM_COMPOSITE_KEY_TABLES:
select count(*) FROM TAV_GIM_COMPOSITE_KEY_TABLES;

PROMPT 

PROMPT Number of records in TAV_GIM_CYCLIC_REF:
select count(*) FROM TAV_GIM_CYCLIC_REF;

PROMPT 

PROMPT Number of records in TAV_GIM_EXCEPTION_LOG:
select count(*) FROM TAV_GIM_EXCEPTION_LOG;

PROMPT 

PROMPT Number of records in TAV_GIM_LOOKUP_60_43_CHAR:
select count(*) FROM TAV_GIM_LOOKUP_60_43_CHAR;

PROMPT 

PROMPT Number of records in TAV_GIM_MASTER_LOG:
select count(*) FROM TAV_GIM_MASTER_LOG;

PROMPT 

PROMPT Number of records in TAV_GIM_VALID_TABLES:
select count(*) FROM TAV_GIM_VALID_TABLES;

PROMPT 

PROMPT '*********** End of Validation of Migration Objects ***********'

PROMPT 

PROMPT Message: Refer LOG_1_VALIDATE_CREATE_MIGRATION_OBJECTS.log before proceeding.

