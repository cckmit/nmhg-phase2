--INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'DC_98','Replaced IR Parts Serial Num')
--/
--INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'), (SELECT ID FROM UPLOAD_ERROR where code = 'DC_98'))
--/
--INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Please remove duplicate serialized part', (select id from UPLOAD_ERROR where code = 'DC_98'))
--/
--Kuldeep - Merged with PATCH_ADD_ROWS_UPLOAD_ERROR.sql DB patch
commit
/