/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_CREATE_SYNONYMS_AND_VALIDATE.sql
 *
 * Purpose	:	This will create synonyms and validate in the given source database
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


SPOOL LOG_3_CREATE_AND_VALIDATE_SYNONYMS.log

PROMPT
PROMPT Message: Please provide SCHEMA name or DBLINK name for creating synonyms
PROMPT

@@TAV_GIM_CREATE_SYNONYMS.SQL

@@VALIDATE_CREATE_SYNONYMS.SQL

PROMPT Message: Refer LOG_3_CREATE_AND_VALIDATE_SYNONYMS.log before proceeding.
PROMPT
PROMPT Message: Next process ==> Creating Staging Area with Staging Table and Constraints
PROMPT
PROMPT Message: Please Execute TAV_GIM_CREATE_STAGING_AREA.bat to create staging area. 
PROMPT

SPOOL OFF

PROMPT User Action: Press 'Enter' to create staging area (or) Close this window to 'Abort'

PAUSE

SPOOL OFF

@@TAV_GIM_CREATE_STAGING_AREA.SQL





