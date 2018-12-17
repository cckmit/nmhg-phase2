/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_CREATE_INSERT_MIGRATION_OBJECTS.sql
 *
 * Purpose	:	This will create and insert migration objects in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   		   Code Organization
 *-----------------------------------------------------------------------------
*/


SET SERVEROUTPUT ON
SET FEEDBACK ON
SET ECHO OFF
SET VERIFY ON
SET feedback   ON

/*
set ECHO OFF
set SERVEROUTPUT on
SET PAUSE ON --commented
SET termout    ON
SET verify     OFF
SET feedback   ON
*/

SPOOL LOG_1_VALIDATE_CREATE_MIGRATION_OBJECTS.log

PAUSE 'Start creation of migration objects?'
PROMPT
PROMPT Message: Starting CREATE/INSERT migration objects...
PROMPT



@@TAV_GIM_CREATE_MIGRATION_OBJECTS.SQL

PROMPT

@@TAV_GIM_INSERT_MIGRATION_OBJECTS.SQL

PROMPT

@@VALIDATE_CREATE_INSERT_MIGRATION_OBJECTS.SQL



PROMPT
PROMPT Message: Next process ==> Port package code for TAV_GIM_INITIAL_SETUP

SPOOL OFF

PROMPT
PROMPT User Action: Press 'Enter' to continue (or) Close this window to 'Abort'

PAUSE

SPOOL LOG_2_VALIDATE_PORT_INITIAL_SETUP.log

@@TAV_GIM_PORT_INITIAL_SETUP_PACKAGE.SQL

@@VALIDATE_PORT_INITIAL_SETUP.SQL

PROMPT

PROMPT Message: Migration objects created and Initial setup compiled as per logs.

PAUSE