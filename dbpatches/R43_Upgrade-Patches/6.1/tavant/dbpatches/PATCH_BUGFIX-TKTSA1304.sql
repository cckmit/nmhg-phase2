--Purpose    : Adding error codes for install base upload for DCAP
--Author     : Saya Sudha
--Created On : 11/10/2011
insert into upload_error values(UPLOAD_ERROR_SEQ.nextval,'IB054','PRICE MATRIX')
/
insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='installBaseUpload'), (select id from upload_error where code='IB054'))
/
insert into i18nupload_error_text values(I18N_Upload_Error_SEQ.nextval,'en_US', 'Price Matrix is not set up for this date range and currency',(select id from upload_error where code='IB054'))
/
insert into upload_error values(UPLOAD_ERROR_SEQ.nextval,'IB053','DEALER GROUP')
/
insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='installBaseUpload'), (select id from upload_error where code='IB053'))
/
insert into i18nupload_error_text values(I18N_Upload_Error_SEQ.nextval,'en_US', 'This dealer is currently not mapped to any DCAP Dealer Category',(select id from upload_error where code='IB053'))
/
commit
/