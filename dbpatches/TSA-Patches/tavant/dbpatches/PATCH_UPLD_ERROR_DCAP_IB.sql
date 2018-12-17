INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES
(UPLOAD_ERROR_SEQ.NEXTVAL,'IB051','DCAP FLAG')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'installBaseUpload'), (SELECT ID FROM UPLOAD_ERROR where code = 'IB051'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Invalid value for DCAP Flag', (select id from UPLOAD_ERROR where code = 'IB051'))
/
INSERT INTO UPLOAD_ERROR(ID,CODE,UPLOAD_FIELD) VALUES
(UPLOAD_ERROR_SEQ.NEXTVAL,'IB052','DCAP FLAG')
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS VALUES((SELECT ID FROM UPLOAD_MGT WHERE NAME_OF_TEMPLATE = 'installBaseUpload'), (SELECT ID FROM UPLOAD_ERROR where code = 'IB052'))
/
INSERT INTO i18nupload_error_text VALUES(I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','Item should be associated to PRODUCT to insert DCAP accrual details defined for PRODUCTS', (select id from UPLOAD_ERROR where code = 'IB052'))
/
commit
/
