--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 28-MAY-2014
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC021','DECISION')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Part Return Requested Decision cannot be taken from this Inbox',(select id from upload_error where code='RC021'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values((select id from upload_mgt where description='Supplier Decision Upload'),(select id from upload_error where code='RC021'))
/
commit
/