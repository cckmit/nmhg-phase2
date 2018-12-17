--Purpose    : Added tables I18NPolicy_Terms_Conditions
--Author     : rakesh.r
--Created On : 17-SEP-08

CREATE TABLE I18NPolicy_Terms_Conditions (id NUMBER(19,0) NOT NULL, locale VARCHAR2(255 CHAR), terms_And_Conditions VARCHAR2(255 CHAR), policy_definition NUMBER(19,0) NOT NULL)
/
ALTER TABLE I18NPolicy_Terms_Conditions  ADD CONSTRAINT i18nPolicy_Terms_PK	PRIMARY KEY(ID)
/
ALTER TABLE I18NPolicy_Terms_Conditions ADD CONSTRAINT i18nPolicy_Terms_POLICY_DEF_FK FOREIGN KEY (policy_definition) REFERENCES policy_definition(ID)
/
CREATE SEQUENCE I18N_POLICY_TERMS_SEQ START WITH 1000 INCREMENT BY 20
/
