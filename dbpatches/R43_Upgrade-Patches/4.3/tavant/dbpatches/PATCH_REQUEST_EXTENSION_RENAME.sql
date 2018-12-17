--Purpose    : Correcting staging table name
--Author     : Rahul
--Created On : 30/07/2010

--alter table STG_REQUEST_EXTENSION rename to STG_REQUESTS_FOR_EXTENSION
--/
-- Kuldeep - Moved this part to PATCH_REQUEST_EXTENSION_STG_TABLE.sql DB patch so this patch is not needed.

--UPDATE UPLOAD_MGT SET STAGING_TABLE = 'STG_REQUEST_FOR_EXTENSION', validation_procedure = 'UPLOAD_REQ_FOR_EXTN_VALIDATION', upload_procedure = 'UPLOAD_REQ_FOR_EXTN_UPLOAD' where --name_of_template = 'requestForExtension'
--/
COMMIT
/