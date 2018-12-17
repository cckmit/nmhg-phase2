--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 06-JULY-2014
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC022','CREDIT MEMO DATE')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Please enter a valid credit Memo Date',(select id from upload_error where code='RC022'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values((select id from upload_mgt where description='Supplier Decision Upload'),(select id from upload_error where code='RC022'))
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC023','CREDIT MEMO NUMBER')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Please enter a credit memo number',(select id from upload_error where code='RC023'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values((select id from upload_mgt where description='Supplier Decision Upload'),(select id from upload_error where code='RC023'))
/
commit
/