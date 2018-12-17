--PURPOSE    : PATCH FOR CREATING tables I18N_CONTRACT_CODE_TEXT, I18N_INDUSTRY_CODE_TEXT,I18N_MAINTENANCE_TEXT
--AUTHOR     : JYOTI CHAUHAN
--CREATED ON : 10-DEC-12

CREATE SEQUENCE I18NCONTRACT_CODE_TEXT_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1 NOCACHE NOORDER NOCYCLE
/
CREATE TABLE I18NCONTRACT_CODE_TEXT
  (
    ID                  NUMBER(19,0) NOT NULL,
    LOCALE              VARCHAR2(255 CHAR),
    CONTRACT_CODE       VARCHAR2(255 CHAR),
    I18N_CONTRACT_CODE  NUMBER(19,0) NOT NULL ENABLE
   )
/
CREATE SEQUENCE I18NINDUSTRY_CODE_TEXT_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1 NOCACHE NOORDER NOCYCLE
/
CREATE TABLE I18NINDUSTRY_CODE_TEXT
  (
    ID                    NUMBER(19,0) NOT NULL,
    LOCALE                VARCHAR2(255 CHAR),
    INDUSTRY_CODE         VARCHAR2(255 CHAR),
    I18N_INDUSTRY_CODE    NUMBER(19,0) NOT NULL ENABLE
   )
/
CREATE SEQUENCE  I18NMAINTENANCE_CNTRT_TEXT_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1 NOCACHE NOORDER NOCYCLE
/
CREATE TABLE I18NMAINTENANCE_CONTRACT_TEXT
  (
    ID                         NUMBER(19,0) NOT NULL,
    LOCALE                     VARCHAR2(255 CHAR),
    MAINTENANCE_CONTRACT       VARCHAR2(255 CHAR),
    I18N_MAINTENANCE_CONTRACT  NUMBER(19,0) NOT NULL ENABLE
   )
/
ALTER TABLE I18NCONTRACT_CODE_TEXT ADD CONSTRAINT I18N_CONTRACT_CODE_TEXT_PK PRIMARY KEY (ID)
/
ALTER TABLE I18NMAINTENANCE_CONTRACT_TEXT ADD CONSTRAINT I18N_MAINTENANCE_CONTRACT_PK PRIMARY KEY (ID)
/
ALTER TABLE I18NINDUSTRY_CODE_TEXT ADD CONSTRAINT I18N_INDUSTRY_CODE_TEXT_PK PRIMARY KEY (ID)
/
ALTER TABLE I18NCONTRACT_CODE_TEXT ADD CONSTRAINT I18N_CONTRACT_CODE_TEXT_FK FOREIGN KEY (I18N_CONTRACT_CODE) REFERENCES CONTRACT_CODE(ID)
/