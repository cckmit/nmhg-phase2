--Purpose    : Adding error codes for warranty claim upload
--Author     : Bharath kumar
--Created On : 01/06/2010
--Impact     : None

insert into upload_error values(1119,'DC045_ADD_INP','INSTALLED IR PARTS')
/
insert into upload_mgt_upload_errors values(4,1119)
/
insert into i18nupload_error_text values(1119,'en_US','Please add Installed parts or remove Replaced parts',1119)
/
commit
/


