-- Purpose    : Patch to create table i18NDisclaimer
-- Author     : PARTHASARATHY R
-- Created On : 15-mAR-2013

CREATE TABLE I18NDISCLAIMER
   (	
    ID NUMBER(19,0) NOT NULL ENABLE, 
	LOCALE VARCHAR2(255 CHAR), 
	DESCRIPTION VARCHAR2(4000 CHAR), 
	DIESEL_TIER_WAIVER NUMBER(19,0) NOT NULL ENABLE, 
	CONSTRAINT I18NDISCLAIMER_PK PRIMARY KEY (ID) ENABLE, 
	CONSTRAINT I18NDISCLAIMER_INVENTORY_FK FOREIGN KEY (DIESEL_TIER_WAIVER) REFERENCES DIESEL_TIER_WAIVER (ID) ENABLE
   ) 
/
CREATE SEQUENCE  I18nDisclaimer_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 1000 CACHE 20 NOORDER  NOCYCLE
/