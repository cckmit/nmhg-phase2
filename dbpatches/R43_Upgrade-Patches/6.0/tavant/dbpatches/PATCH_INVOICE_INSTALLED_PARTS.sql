--Purpose    : Scripts for adding INVOICE column into INSTALLED_PARTS table, changes made as a part of request sent by Single Instance DEV Team
--Created On : 16-Mar-2011
--Created By : Kuldeep Patil
--Impact     : None

ALTER TABLE INSTALLED_PARTS ADD INVOICE NUMBER(19)
/
COMMIT
/