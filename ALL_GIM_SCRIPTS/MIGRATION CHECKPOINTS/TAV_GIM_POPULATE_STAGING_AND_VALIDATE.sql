/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_POPULATE_STAGING_AND_VALIDATE.sql
 *
 * Purpose	:	This will populate staging tables and validate in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   Code Organization
 *-----------------------------------------------------------------------------
*/


SET SERVEROUTPUT OFF
SET FEEDBACK OFF


SPOOL LOG_6_POPULATE_STAGe_DEST_AND_VALIDATE.log

PROMPT
PROMPT Message: Starting the process to load Staging Tables. Please wait...
PROMPT

@@TAV_GIM_POPULATE_STAGING_AREA.sql

PROMPT
PROMPT Message: Loading staging tables completed !
PROMPT
PROMPT Message: For more information query Master Log and Exception log tables
PROMPT

@@VALIDATE_POPULATE_STAGING_AREA.SQL

PROMPT Message: Next process ==> Updating SELF/CYCLIC references in the staging tables
PROMPT

PROMPT Message: Press 'ENTER' to proceed to update cyclic rerences or close this window to 'ABORT'
PAUSE

PROMPT 'Updating cyclic references in the stage in progress.Please check the master log...'

BEGIN
  TAV_GIM_PROCESS_MIGRATION.TAV_GIM_UPDATE_CYCLIC_REF();
end;
/

PROMPT
PROMPT Message: Updating cyclic references in the staging tables complete!

--------------------------------------------------------------------------------------------------------------------
