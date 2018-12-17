--Purpose    : Patch for adding the column AUTH_NUMBER and alter CMS_NUMBER datatype from number to varchar2 in claim table.
--Author     : Jyoti Chauhan
--Created On : 08-OCT-2012

ALTER TABLE CLAIM ADD AUTH_NUMBER VARCHAR2(255)
/
ALTER TABLE CLAIM ADD CMS_NUMBER VARCHAR2(255)
/
commit
/