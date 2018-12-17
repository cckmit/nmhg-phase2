INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES(UPLOAD_ERROR_SEQ.NEXTVAL,'DC_99','Servicing Location Id')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'draftWarrantyClaims'), (SELECT ID FROM UPLOAD_ERROR where code = 'DC_99'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Invalid Servicing Location', (select id from UPLOAD_ERROR where code = 'DC_99'))
/
commit
/
