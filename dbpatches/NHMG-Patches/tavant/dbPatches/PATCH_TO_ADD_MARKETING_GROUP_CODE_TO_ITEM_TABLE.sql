--Purpose    : Patch for adding the column marketing_group_code in item table.
--Author     : Pracher Pancholi
--Created On : 3-Dec-2012

ALTER TABLE ITEM ADD (MARKETING_GROUP_CODE VARCHAR2(400))
/