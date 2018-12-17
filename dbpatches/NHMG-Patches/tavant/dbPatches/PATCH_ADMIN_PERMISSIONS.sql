-- Patch for implementing permission based admin authorization framework
-- Author Tanveer Chowdary
-- Date August 7, 2012

CREATE SEQUENCE MST_ADMIN_FNC_AREA_SEQ
  MINVALUE 1
  MAXVALUE 999999999999999999999999999
  INCREMENT BY 1
  NOCYCLE
  NOORDER
  CACHE 20
/
  CREATE SEQUENCE MST_ADMIN_SUBJECT_AREA_SEQ
  MINVALUE 1
  MAXVALUE 999999999999999999999999999
  INCREMENT BY 1
  NOCYCLE
  NOORDER
  CACHE 20
/
CREATE TABLE MST_ADMIN_FNC_AREA (ID NUMBER(19),NAME VARCHAR2(50), DESCRIPTION VARCHAR2(200),CONSTRAINT  MST_ADMIN_FNC_AREA_PK  PRIMARY KEY(ID))
/
CREATE TABLE MST_ADMIN_SUBJECT_AREA (ID NUMBER(19),NAME VARCHAR2(50), DESCRIPTION VARCHAR2(200),CONSTRAINT  MST_ADMIN_SUBJECT_AREA_PK  PRIMARY KEY(ID))
/
CREATE TABLE MST_ADMIN_ACTION (ID NUMBER(19),ACTION VARCHAR2(50), DESCRIPTION VARCHAR2(200),CONSTRAINT  MST_ADMIN_ACTION_PK  PRIMARY KEY(ID))
/
CREATE TABLE SUBJECT_FUNC_AREA_MAPPING (FUNCTIONAL_AREA NUMBER(19) NOT NULL,SUBJECT_AREA NUMBER(19) NOT NULL , CONSTRAINT SUBJECT_FUNC_AREA_MAPPING_PK PRIMARY KEY (FUNCTIONAL_AREA,SUBJECT_AREA) ENABLE )
/
ALTER TABLE SUBJECT_FUNC_AREA_MAPPING ADD CONSTRAINT SUBJ_FUNC_MAP_FUNC_ID FOREIGN KEY(FUNCTIONAL_AREA) REFERENCES MST_ADMIN_FNC_AREA(ID)
/
ALTER TABLE SUBJECT_FUNC_AREA_MAPPING ADD CONSTRAINT SUBJ_FUNC_MAP_SUBJ_ID FOREIGN KEY(SUBJECT_AREA) REFERENCES MST_ADMIN_SUBJECT_AREA(ID)
/
CREATE SEQUENCE  "ROLE_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1000 NOCACHE  NOORDER  NOCYCLE 
/
ALTER TABLE ROLE ADD (DESCRIPTION VARCHAR2(50))
/
CREATE TABLE ROLE_PERMISSION_MAPPING (ID NUMBER(19),ROLE_DEF_ID NUMBER(19) NOT NULL,FUNCTIONAL_AREA NUMBER(19) NOT NULL,
ACTION NUMBER(19) NOT NULL,CONSTRAINT  ROLE_PERMISSION_MAPPING_PK  PRIMARY KEY(ID))
/
CREATE SEQUENCE  "ROLE_PERMISSION_MAPPING_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1000 NOCACHE  NOORDER  NOCYCLE
/
ALTER TABLE ROLE_PERMISSION_MAPPING ADD CONSTRAINT ROLE_PERM_MAP_ROLE_FK FOREIGN KEY(ROLE_DEF_ID) REFERENCES ROLE(ID)
/
ALTER TABLE ROLE_PERMISSION_MAPPING ADD CONSTRAINT ROLE_PERM_MAP_FNC_AREA_FK FOREIGN KEY(FUNCTIONAL_AREA) REFERENCES MST_ADMIN_FNC_AREA(ID)
/
ALTER TABLE ROLE_PERMISSION_MAPPING ADD CONSTRAINT ROLE_PERM_MAP_ACTION_FK FOREIGN KEY(ACTION) REFERENCES MST_ADMIN_ACTION(ID)
/
ALTER TABLE ROLE_PERMISSION_MAPPING ADD (SUBJECT_AREA NUMBER(19))
/
ALTER TABLE ROLE_PERMISSION_MAPPING ADD PERMISSION_STRING VARCHAR2(255)
/