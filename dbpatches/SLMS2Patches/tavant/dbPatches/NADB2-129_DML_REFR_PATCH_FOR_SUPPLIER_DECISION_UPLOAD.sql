--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 21-MAR-2014 
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC019','PART RETURN REQUEST')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Part Return Request cannot be set to YES for the decision Reject',(select id from upload_error where code='RC019'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where code='RC019'))
/
commit
/