--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 16-JULY-2014
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC024','CREDIT MEMO AMOUNT')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Please enter Credit Memo Amount',(select id from upload_error where code='RC024'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values((select id from upload_mgt where description='Supplier Decision Upload'),(select id from upload_error where code='RC024'))
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC025','CREDIT MEMO CURRENCY')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Please enter Credit Memo currency',(select id from upload_error where code='RC025'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values((select id from upload_mgt where description='Supplier Decision Upload'),(select id from upload_error where code='RC025'))
/
update upload_mgt set columns_to_capture=14 where name_of_template='supplierDecisionUpload'
/
commit;