--Purpose    : Adding error codes for warranty claim upload,changes made as a part of 4.3 upgrade
--Author     : Bharath kumar
--Created On : 01/06/2010
--Impact     : None

--insert into upload_error values(upload_error_seq.nextval,'DC045_ADD_INP','INSTALLED IR PARTS')
--/
--insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'),(select id from upload_error where code = 'DC045_ADD_INP'))
--/
--insert into i18nupload_error_text values(i18nupload_error_text_seq.nextval,'en_US','Please add Installed parts or remove Replaced parts',(select id from upload_error where code = 'DC045_ADD_INP'))
--/
----Kuldeep - Merged with PATCH_ADD_ROWS_UPLOAD_ERROR.sql DB patch
commit
/