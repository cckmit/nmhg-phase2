--Purpose    : Adding upload_mgt_upload_errors for Multiple Site Numbers
--Author     : surendra varma
--Created On : 04-Aug-2011

Insert into UPLOAD_ERROR values (UPLOAD_ERROR_SEQ.NEXTVAL,'CU0026','DEALER SITE')
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='customerUpload'),(select id from upload_error where code='CU0026'))
/
Insert into i18nupload_error_text values(i18n_upload_error_seq.nextval,'en_US','Dealer Site Number is not specified',(select id from upload_error where code='CU0026'))
/
COMMIT
/