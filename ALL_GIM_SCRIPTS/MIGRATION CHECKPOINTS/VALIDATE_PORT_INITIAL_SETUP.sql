/*-----------------------------------------------------------------------------
 * File Name	:	VALIDATE_PORT_INITIAL_SETUP.sql
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

PROMPT 

PROMPT '*********** Validation of TAV_GIM_INITIAL_SETUP ***********'

PROMPT 

column object_name format a30
select object_name,object_type,status,timestamp FROM user_objects where object_name = 'TAV_GIM_INITIAL_SETUP';

PROMPT 

PROMPT '*********** End of Validation of TAV_GIM_INITIAL_SETUP ***********'

PROMPT 

PROMPT Message: Refer LOG_2_VALIDATE_PORT_INITIAL_SETUP.log before proceeding.


