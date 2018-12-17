--Purpose    : Adding error codes for warranty claim upload,changes made as a part of 4.3 upgrade
--Author     : Bharath kumar
--Created On : 21/06/2010
--Impact     : None

--insert into upload_error values(UPLOAD_ERROR_SEQ.NEXTVAL,'DC045_SE','REPLACED IR PARTS SERIAL NUM')
--/
--insert into upload_mgt_upload_errors values(4,(select ID from upload_error where CODE='DC045_SE'))
--/
--insert into i18nupload_error_text values((select ID from upload_error where CODE='DC045_SE'),'en_US','Invalid delimiter',(select ID from upload_error where CODE='DC045_SE'))
--/
--Kuldeep - Merged with PATCH_ADD_ROWS_UPLOAD_ERROR.sql DB patch
commit
/
