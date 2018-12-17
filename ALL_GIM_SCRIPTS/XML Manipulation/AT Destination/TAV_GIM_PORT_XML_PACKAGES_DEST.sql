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


SPOOL LOG_1_TAV_GIM_PORT_XML_PACKAGES_DEST.log



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


------

PROMPT Porting and Compiling TAV_GIM_PROCESS_DOM_PRED_XML.
PROMPT
@@TAV_GIM_PROCESS_DOM_PRED_XML.sql
/

PROMPT Porting and Compiling TAV_GIM_PROCESS_DOM_RULE_XML.
PROMPT
@@TAV_GIM_PROCESS_DOM_RULE_XML.sql
/


PROMPT Porting and Compiling TAV_GIM_PROCESS_FOC_ORDER_XML.
PROMPT
@@TAV_GIM_PROCESS_FOC_ORDER_XML.sql
/






PROMPT 

PROMPT '*********** Validation of XML packages ***********'

PROMPT 

column object_name format a30
select object_name,object_type,status,timestamp from user_objects where object_type like 'PACKAGE%' and object_name in 
('TAV_GIM_INITIAL_SETUP','TAV_GIM_XML_UTILITIES','TAV_GIM_PROCESS_CLAIM_AUD_XML','TAV_GIM_PROCESS_DOM_PRED_XML','TAV_GIM_PROCESS_DOM_RULE_XML','TAV_GIM_PROCESS_FOC_ORDER_XML');

PROMPT 

PROMPT '*********** Execution of XML update procs***********'

PROMPT 

PROMPT Message: Refer LOG_1_TAV_GIM_PORT_XML_PACKAGES_DEST.log before proceeding.
PROMPT
PROMPT Message: Run process 1 ==> Run TAV_GIM_PROCESS_CLAIM_AUD_XML.PROCESS_CLAIM_AUDIT_XML_STR() for XML string updation.
PROMPT
PROMPT Message: Run process 2==> Insert data from the source to populate the tables refered by the ARRAY vefore continuing.This will take time!!!
PROMPT
PROMPT Message: Run process 3==> Run process_claim_audit_job_master() manually providing the number of JOBS!!!
PAUSE

SPOOL OFF


