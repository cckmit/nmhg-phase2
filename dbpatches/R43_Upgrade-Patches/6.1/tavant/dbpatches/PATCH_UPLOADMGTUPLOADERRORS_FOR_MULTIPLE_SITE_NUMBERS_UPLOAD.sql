--Purpose    : Adding upload_mgt_upload_errors for Multiple Site Numbers
--Author     : Kuldeep Patil
--Created On : 04-Aug-2011

Insert into UPLOAD_ERROR (ID,CODE,UPLOAD_FIELD) values (UPLOAD_ERROR_SEQ.NEXTVAL,'CU0020','DEALER SITE')
/
Insert into i18nupload_error_text values(i18n_upload_error_seq.nextval,'en_US','Dealer Site Number is not specified',(select id from upload_error where code='CU0020'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU003'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU006'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU007'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU008'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU009'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU010'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU011'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU012'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU013'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU014'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU019'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU020'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU021'))
/
Insert into upload_mgt_upload_errors values((select id from upload_mgt where name_of_template='multipleSiteNumbers'),(select id from upload_error where code='CU0020'))
/
COMMIT
/