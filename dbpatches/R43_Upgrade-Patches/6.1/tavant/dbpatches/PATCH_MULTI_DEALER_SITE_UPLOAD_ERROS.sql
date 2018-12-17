--Purpose    : Adding error codes for validation of duplicate dealer site for the same customers
--Author     : Kuldeep Patil
--Created On : 02-Aug-2011
--Impact     : None

Insert into UPLOAD_ERROR (ID,CODE,UPLOAD_FIELD) values (UPLOAD_ERROR_SEQ.NEXTVAL,'CU025','DEALER SITE')
/
Insert into i18nupload_error_text (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values (I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','This Dealer Site is already exists for the customer',(select id from upload_error where code = 'CU025'))
/
insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'multipleSiteNumbers'),(select id from upload_error where code = 'CU025'))
/
Insert into UPLOAD_ERROR (ID,CODE,UPLOAD_FIELD) values (UPLOAD_ERROR_SEQ.NEXTVAL,'CU026','CUSTOMER NUMBER')
/
Insert into i18nupload_error_text (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values (I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Customer Number is not valid',(select id from upload_error where code = 'CU026'))
/
insert into upload_mgt_upload_errors values((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'multipleSiteNumbers'),(select id from upload_error where code = 'CU026'))
/
commit
/