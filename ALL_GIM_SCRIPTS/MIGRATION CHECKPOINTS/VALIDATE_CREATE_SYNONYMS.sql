/*-----------------------------------------------------------------------------
 * File Name	:	VALIDATE_CREATED_SYNONYMS.sql
 *
 * Purpose	:	This will validate whether the SYNONYMS are created
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
PROMPT '*********** Validation of Synonyms created for Schema Name or DBLink ***********'
PROMPT 
PROMPT Number of valid tables:

select count(synonym_name) from tav_gim_valid_tables;

PROMPT 

PROMPT Number of Synonyms created for valid tables:

set pagesize 100
column COUNT_SYNONYMS FORMAT 9999
column DB_LINK  FORMAT A10

select count(*) as COUNT_SYNONYMS,DB_LINK from user_synonyms where synonym_name in (select synonym_name from tav_gim_valid_tables) GROUP BY db_link;

PROMPT

PROMPT Synonyms are not created for the below tables:

select table_name, synonym_name from tav_gim_valid_tables 
where synonym_name not in (select synonym_name from user_synonyms);

PROMPT

PROMPT '*********** End of Validation of Synonyms created for Schema Name or DBLink ***********'

PROMPT 




