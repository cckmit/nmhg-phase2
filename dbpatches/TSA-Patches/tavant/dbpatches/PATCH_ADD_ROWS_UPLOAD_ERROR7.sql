--Purpose    : Adding error codes for warranty claim upload
--Author     : Bharath kumar
--Created On : 21/06/2010
--Impact     : None

insert into upload_error values(1121,'DC045_SE','REPLACED IR PARTS SERIAL NUM')
/
insert into upload_mgt_upload_errors values(4,1121)
/
insert into i18nupload_error_text values(1121,'en_US','Invalid delimiter',1121)
/
commit
/


