/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_PROCESS_CLAIM_AUD_XML.
 *
 * Purpose	:	This will port all the xml packages in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   		   Code Organization
 *-----------------------------------------------------------------------------
*/


SPOOL LOG_1_TAV_GIM_PORT_XML_PACKAGES_SOURCE.log



PROMPT Creating migration objects..
PROMPT
@@TAV_GIM_CREATE_XML_MIGRATION_OBJECTS.sql


PROMPT Porting and Compiling TAV_GIM_INITIAL_SETUP.
PROMPT
@@TAV_GIM_INITIAL_SETUP.sql
/
PROMPT Porting and Compiling TAV_GIM_XML_UTILITIES.
PROMPT
@@TAV_GIM_XML_UTILITIES.sql
/


PROMPT Porting and Compiling TAV_GIM_PROCESS_CLAIM_AUD_XML.
PROMPT
@@TAV_GIM_PROCESS_CLAIM_AUD_XML.sql
/

PROMPT 

PROMPT '*********** Validation of XML String Replacement packages in the source ***********'

PROMPT 

column object_name format a30
select object_name,object_type,status,timestamp from user_objects where object_type like 'PACKAGE%' and object_name in 
('TAV_GIM_INITIAL_SETUP','TAV_GIM_XML_UTILITIES','TAV_GIM_PROCESS_CLAIM_AUD_XML');

PROMPT 

PROMPT '*********** Execution XML String Replacement in the source***********'

PROMPT 

PROMPT Message: Refer LOG_1_TAV_GIM_PORT_XML_PACKAGES_SOURCE.log before proceeding.
PROMPT
PROMPT Message: Next process ==> Replace XML's strings in the source for claim Audit.
PROMPT


PROMPT Message: Press 'ENTER' to Replace XML's strings in the source automatically or close this window to 'ABORT' and run the script from SQL Dev.
PAUSE

BEGIN
  TAV_GIM_PROCESS_CLAIM_AUD_XML.PROCESS_CLAIM_AUDIT_XML_STR();
END;
/

SPOOL OFF


