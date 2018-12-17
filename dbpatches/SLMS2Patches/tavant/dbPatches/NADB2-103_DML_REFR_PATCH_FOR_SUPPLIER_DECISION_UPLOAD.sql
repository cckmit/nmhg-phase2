--Purpose    : INSERT SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload Errors
--Author     : Arpitha Nadig AR
--Created On : 19-MAR-2014 
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC013','SUPPLIER CONTRACT CODE')
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC014','PART RETURN REQUEST')
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC015','RETURN LOCATION CODE')
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC016','CLAIM AMOUNT BEING ACCEPTED')
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC017','SUPPLIER CONTRACT CODE')
/
insert into upload_error (id,code,upload_field) values(upload_error_seq.nextval,'RC018','RETURN LOCATION CODE')
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Supplier Contract Code is mandatory',(select id from upload_error where code='RC013'))
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Supplier Contract Code does not match with Recovery claim contract code',(select id from upload_error where code='RC017'))
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Part Return Request is mandatory for Part Return Requested decision.Please set Part Return Request field to Yes',(SELECT id FROM upload_error WHERE code='RC014'))
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Return Location Code is mandatory if Part Return Request is Yes',(select id from upload_error where upload_field='RETURN LOCATION CODE' and code='RC015'))
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Return Location Code is mandatory if Part Return Request is Yes',(select id from upload_error where upload_field='RETURN LOCATION CODE' and code='RC018'))
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Claim Amount Being Accepted cannot be Zero',(select id from upload_error where upload_field='CLAIM AMOUNT BEING ACCEPTED'))
/
insert into I18NUPLOAD_ERROR_TEXT (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) values(I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Invalid Return Location Code',(select id from upload_error where code='RC018'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where upload_field='SUPPLIER CONTRACT CODE' AND CODE='RC013'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where upload_field='SUPPLIER CONTRACT CODE' AND CODE='RC017'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where code='RC017'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where upload_field='PART RETURN REQUEST' AND ROWNUM=1))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where upload_field='RETURN LOCATION CODE' AND code='RC015'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where upload_field='RETURN LOCATION CODE' AND code='RC018'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where upload_field='CLAIM AMOUNT BEING ACCEPTED'))
/
insert into upload_mgt_upload_errors(upload_mgt,upload_errors) values(9,(select id from upload_error where code='RC018'))
/
update upload_mgt set columns_to_capture=12 where description='Supplier Decision Upload'
/
update upload_error set upload_field ='SUPPLIER COMMENTS' where upload_field='DECISION COMMENTS'
/
UPDATE i18nupload_error_text SET description ='Supplier Comments are mandatory for the Disputed Decision' WHERE upload_error IN (SELECT id FROM upload_error WHERE upload_field='SUPPLIER COMMENTS')
/
update i18nupload_error_text set description ='Invalid decision. Please choose either of [Accepted / Disputed / Part Return Requested ] ' where upload_error in (select id from upload_error where code='RC006')
/
update upload_mgt set consume_rows_from=6 where description='Supplier Decision Upload'
/
commit
/