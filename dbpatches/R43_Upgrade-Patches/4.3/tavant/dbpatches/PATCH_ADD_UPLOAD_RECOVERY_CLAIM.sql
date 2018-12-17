--Purpose    : Scripts for uploading supplier decision, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

INSERT INTO UPLOAD_MGT 
VALUES(UPLOAD_MGT_SEQ.NEXTVAL,'supplierDecisionUpload','Supplier Decision Upload','Supplier Decision Upload','./pages/secure/admin/upload/templates/Template-SupplierDecisionUpload.xls','STG_SUPPLIER_DECISION',
null,'SUPPLIER_DECISION_VALIDATION',null,5,null,5,0,null)
/
INSERT INTO UPLOAD_ROLES
VALUES((SELECT id FROM UPLOAD_MGT WHERE name_of_template = 'supplierDecisionUpload'),(SELECT id FROM ROLE WHERE name = 'supplier'))
/
INSERT INTO UPLOAD_ROLES
VALUES((SELECT id FROM UPLOAD_MGT WHERE name_of_template = 'supplierDecisionUpload'),(SELECT id FROM ROLE WHERE name = 'recoveryProcessor'))
/
CREATE TABLE STG_SUPPLIER_DECISION
  (
     ID   NUMBER NOT NULL PRIMARY KEY,
     FILE_UPLOAD_MGT_ID NUMBER,
     RECOVERY_CLAIM_NUMBER  VARCHAR2(255 BYTE),
     DECISION     VARCHAR2(255 BYTE),
     DECISION_REASON     VARCHAR2(255 BYTE),
     DECISION_COMMENTS    VARCHAR2(255 BYTE),
     ERROR_CODE VARCHAR2(255  BYTE),
     ERROR_STATUS VARCHAR2(255  BYTE),
     UPLOAD_STATUS VARCHAR2(255  BYTE),
     UPLOAD_ERROR VARCHAR2(255  BYTE)
     )
/
COMMIT
/