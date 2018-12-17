--PURPOSE    : Patch to add a new Upload Errors for Draft Claim Upload CR.
--AUTHOR     : Roopa Kariyappa
--CREATED ON : 09-August-2012

Insert into upload_error (ID,CODE,UPLOAD_FIELD) values (upload_error_seq.nextval,'DC_SN_MCHNE','SERIAL NUMBER')
/
Insert into upload_error (ID,CODE,UPLOAD_FIELD) values (upload_error_seq.nextval,'DC_SN_ATTCHMNT','SERIAL NUMBER')
/
Insert into i18nupload_error_text (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) 
values (I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Claim type not matching with Machine Serial Number',(SELECT id FROM upload_error WHERE code ='DC_SN_MCHNE'))
/
Insert into i18nupload_error_text (ID,LOCALE,DESCRIPTION,UPLOAD_ERROR) 
values (I18N_UPLOAD_ERROR_SEQ.nextval,'en_US','Claim type not matching with Attachment Serial Number',(SELECT id FROM upload_error WHERE code ='DC_SN_ATTCHMNT'))
/
commit
/