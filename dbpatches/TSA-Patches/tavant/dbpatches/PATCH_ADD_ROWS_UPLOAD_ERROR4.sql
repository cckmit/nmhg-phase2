--Purpose    : Adding error codes for warranty claim upload
--Author     : Bharath kumar
--Created On : 17/05/2010
--Impact     : None

insert into upload_error values(1112,'DC045_IN','INSTALLED IR PARTS')
/
insert into upload_error values(1113,'DC075','INSTALLED IR PARTS')
/
insert into upload_error values(1114,'DC046_IN','INSTALLED IR PARTS')
/
insert into upload_error values(1115,'DC027_IN','INSTALLED IR PARTS QUANTITY')
/
insert into upload_error values(1116,'DC047_IN','INSTALLED IR PARTS QUANTITY')
/
insert into upload_error values(1117,'DC048_IN','INSTALLED IR PARTS QUANTITY')
/
insert into upload_error values(1118,'DC054_IN','INSTALLED IR PARTS QUANTITY')
/
insert into upload_mgt_upload_errors values(4,1112)
/
insert into upload_mgt_upload_errors values(4,1113)
/
insert into upload_mgt_upload_errors values(4,1114)
/
insert into upload_mgt_upload_errors values(4,1115)
/
insert into upload_mgt_upload_errors values(4,1116)
/
insert into upload_mgt_upload_errors values(4,1117)
/
insert into upload_mgt_upload_errors values(4,1118)
/
insert into i18nupload_error_text values(1112,'en_US','Invalid format for Installed IR Parts',1112)
/
insert into i18nupload_error_text values(1113,'en_US','Replaced IR Parts or Number of quantity values do not match the Installed IR Parts or number of Installed IR Parts',1113)
/
insert into i18nupload_error_text values(1114,'en_US','One or more Installed IR Parts are invalid',1114)
/
insert into i18nupload_error_text values(1115,'en_US','Empty Installed IR Parts Quantity',1115)
/
insert into i18nupload_error_text values(1116,'en_US','Invalid format for Installed IR Parts Quantity',1116)
/
insert into i18nupload_error_text values(1117,'en_US','Number of quantity values do not match the number of Installed IR Parts',1117)
/
insert into i18nupload_error_text values(1118,'en_US','One or more Installed IR Parts Quantity is not valid',1118)
/
commit
/


