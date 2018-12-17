/*-----------------------------------------------------------------------------
 * File Name	:	TAV_GIM_CREATE_MIGRATION_OBJECTS.sql
 *
 * Purpose	:	This will create migration objects in the given source database
 *                           
 * Revision History:
 *
 *  Date           Programmer              Description
 *  ------------   ---------------------   ------------------------------------
 *  Jan 15, 2011   Joseph                  Initial Write-Up
 *  Mar 28,2011	   Prabhu R		   Code Organization
 *-----------------------------------------------------------------------------
*/

Prompt Message: Dropping objects if already availbale...

DROP TABLE PARTY_ARRAY_TABLE;
DROP TABLE ORG_USER_ARRAY_TABLE;
DROP TABLE COST_CATEGORY_ARRAY_TABLE;

PROMPT Creating temp tables to compile TAV_GIM_PROCESS_CLAIM_AUD_XML package...
PROMPT


------------Creating temp tables------------

CREATE TABLE PARTY_ARRAY_TABLE(ID NUMBER, OLD_43_ID NUMBER);

CREATE TABLE ORG_USER_ARRAY_TABLE(ID NUMBER, OLD_43_ID NUMBER);

CREATE TABLE COST_CATEGORY_ARRAY_TABLE(ID NUMBER, OLD_43_ID NUMBER);

/*------------Inserting Data------------ */


PROMPT Inserting data nito temp tables to for tables with lookup values
PROMPT

INSERT INTO party_array_table(ID,OLD_43_ID)
SELECT id,old_43_id FROM TG_PARTY@&&LINK_R4;

INSERT INTO org_user_array_table(ID,OLD_43_ID)
SELECT ID,OLD_43_ID FROM TG_ORG_USER@&&LINK_R4;

INSERT INTO cost_category_array_table(ID,OLD_43_ID)
SELECT ID,OLD_43_ID FROM TG_COST_CATEGORY@&&LINK_R4;

COMMIT;


/*------------Creating indexes------------*/

CREATE INDEX PARTY_ARRAY_TABLE_ID ON PARTY_ARRAY_TABLE(ID);
CREATE INDEX PARTY_ARRAY_TABLE_43_ID ON PARTY_ARRAY_TABLE(OLD_43_ID);


CREATE INDEX org_user_array_IDx ON org_user_array_table(ID);
CREATE INDEX org_user_array_43_IDX ON org_user_array_table(OLD_43_ID);

CREATE INDEX COST_CATEGORY_ARRAY_TABLE_ID ON COST_CATEGORY_ARRAY_TABLE(ID);
CREATE INDEX COST_CAT_ARRAY_TABLE_43_ID ON COST_CATEGORY_ARRAY_TABLE(OLD_43_ID);

/*-------------------END---------------------*/