/*-----------------------------------------------------------------------------
 * File Name	:	VALIDATE_POPULATE_STAGING_AREA.sql
 *
 * Purpose	:	This will validate the staging tables created
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
PROMPT '*********** Validation of Staging tables ***********'
PROMPT 
PROMPT Total Number of tables to be processed:
select count(table_name) from tav_gim_valid_tables ; 
PROMPT 
PROMPT Number of tables not processed:
select count(table_name)  from tav_gim_valid_tables where load_status = 'NOT PROCESSED';
PROMPT 
PROMPT '*********** End of Validation of staging tables ***********'
PROMPT 




