--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 24-MAY-2014
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC020','CREDIT MEMO DATE')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Incorrect Date Format',(select id from upload_error where code='RC020'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values((select id from upload_mgt where description='Supplier Decision Upload'),(select id from upload_error where code='RC020'))
/
commit
/