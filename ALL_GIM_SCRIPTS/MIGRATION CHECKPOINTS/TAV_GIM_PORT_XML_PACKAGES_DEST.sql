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


SPOOL LOG_10_TAV_GIM_PORT_XML_PACKAGES_DEST.log


PROMPT Please provide DB LINK name when prompted..Enter to continue..
PAUSE

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
PROMPT Message: Run process 1==> Check for inserted data from the source to populate the tables refered by the ARRAY before continuing.
PROMPT
PROMPT Message: Run process 3==> Hit enter to run claim audit string and id replacement
PAUSE
PROMPT Running claim audit string and id replacement procedure(TAV_GIM_PROCESS_CLAIM_AUD_XML.PROCESS_CLAIM_AUDIT_JOB_CHILD)..

DECLARE
  P_IN_ID_START_RANGE NUMBER;
  P_IN_ID_END_RANGE NUMBER;
BEGIN
  P_IN_ID_START_RANGE := 1;
  P_IN_ID_END_RANGE := 1;

  TAV_GIM_PROCESS_CLAIM_AUD_XML.PROCESS_CLAIM_AUDIT_JOB_CHILD(
    P_IN_ID_START_RANGE => P_IN_ID_START_RANGE,
    P_IN_ID_END_RANGE => P_IN_ID_END_RANGE
  );
END;
/
SPOOL OFF