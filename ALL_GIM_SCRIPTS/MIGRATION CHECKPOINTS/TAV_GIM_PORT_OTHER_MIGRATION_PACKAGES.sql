/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_PORT_MIGRATION_PACKAGES.sql
 *
 * Purpose	:	This will port all the migration packages in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   		   Code Organization
 *-----------------------------------------------------------------------------
*/


SPOOL LOG_6_PORT_OTHER_MIGRATION_PACKAGES.log

PROMPT Porting and Compiling TAV_GIM_UTILITIES.
PROMPT
@@TAV_GIM_UTILITIES.sql
/
PROMPT Porting and Compiling TAV_GIM_PROCESS_MIGRATION.
PROMPT
@@TAV_GIM_PROCESS_MIGRATION.sql
/
PROMPT Porting and Compiling TAV_GIM_COMP_UNIQUE_SCENARIOS.
PROMPT
@@TAV_GIM_COMP_UNIQUE_SCENARIOS.sql
/
PROMPT Porting and Compiling TAV_GIM_CLEANUP_UTILITIES.
PROMPT
@@TAV_GIM_CLEANUP_UTILITIES.sql
/

SPOOL OFF
PROMPT
@@VALIDATE_PORT_OTHER_MIGRATION_PACKAGE.SQL

