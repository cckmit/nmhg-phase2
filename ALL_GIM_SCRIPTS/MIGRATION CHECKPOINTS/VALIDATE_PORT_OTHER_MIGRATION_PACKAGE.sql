/*-----------------------------------------------------------------------------
 * File Name	:	VALIDATE_PORT_OTHER_MIGRATION_PACKAGE.sql
 *
 * Purpose	:	This will validate whether the package is valid
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   Code Organization
 *-----------------------------------------------------------------------------
*/

SPOOL LOG_7_VALIDATE_PORT_OTHER_MIGRATION_PACKAGES.log

PROMPT 

PROMPT '*********** Validation of Migration Packages ***********'

PROMPT 

column object_name format a30
select object_name,object_type,status,timestamp from user_objects where object_type like 'PACKAGE%' and object_name like 'TAV_GIM%';

PROMPT 

PROMPT '*********** End of Validation of Migration Packages ***********'

PROMPT 

PROMPT Message: Refer LOG_7_VALIDATE_PORT_OTHER_MIGRATION_PACKAGES.log.log before proceeding.
PROMPT
PROMPT Message: Next process ==> Populate Staging Area
PROMPT
PROMPT Message: Please execute TAV_GIM_POPULATE_STAGING_AREA.sql to populate staging area. 
PROMPT

SPOOL OFF
PROMPT Message: Press 'ENTER' to populate staging area automatically or close this window to 'ABORT'
PAUSE

@@TAV_GIM_POPULATE_STAGING_AND_VALIDATE.sql
