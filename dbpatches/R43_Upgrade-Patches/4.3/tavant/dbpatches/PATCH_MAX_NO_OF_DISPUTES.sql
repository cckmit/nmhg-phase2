--Purpose    : Upload error message for Supplier decision upload, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 12-Oct-2010

INSERT INTO  UPLOAD_ERROR 
VALUES (UPLOAD_ERROR_SEQ.NEXTVAL,'RC012','DECISION')
/
INSERT INTO  i18nupload_error_text
VALUES (I18N_UPLOAD_ERROR_SEQ.NEXTVAL,'en_US','The action cannot be taken on the claim as the number of times claim can be disputed has reached the maximum limit',(SELECT ID FROM UPLOAD_ERROR WHERE code = 'RC012'))
/
INSERT INTO UPLOAD_MGT_UPLOAD_ERRORS 
VALUES((select ID from UPLOAD_MGT where NAME_OF_TEMPLATE = 'supplierDecisionUpload'),
(select id from  UPLOAD_ERROR WHERE code = 'RC012'))
/
commit
/