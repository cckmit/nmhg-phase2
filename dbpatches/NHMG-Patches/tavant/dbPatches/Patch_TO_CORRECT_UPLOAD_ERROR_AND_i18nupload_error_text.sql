--Purpose    : Patch TO CORRECT UPLOAD FIELD VALUE IN UPLOAD ERROR TABLE and to Insert error msg in i18nupload_error_text
--Author     : ROHIT MEHROTRA
--Created On : 26-JUNE-2013

update upload_error set upload_field='TRUCK SERIAL NUMBER' where code='DC_SN_MCHNE'
/
INSERT INTO i18nupload_error_text (select UPLOAD_ERROR_SEQ.nextVal ,'en_US','Invalid Serial Number for the given Claim Type' ,id  from upload_error 
where code = 'DC_SN_MCHNE')
/
commit
/