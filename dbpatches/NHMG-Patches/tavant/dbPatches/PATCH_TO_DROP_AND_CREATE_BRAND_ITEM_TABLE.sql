--Purpose    : Drop and Create table for brand item
--Created On : 12-Nov-2012
--Created By : PRACHER PANCHOLI

DROP TABLE BRAND_ITEM
/

CREATE TABLE BRAND_ITEM
(
  ID      NUMBER(19,0)  NOT NULL ENABLE,
  CONSTRAINT BRAND_IITEM_ID_PK PRIMARY KEY (ID) ENABLE,
  BRAND    VARCHAR2(255 CHAR) NOT NULL ENABLE,
  ITEM_NUMBER VARCHAR2(255 CHAR) NOT NULL ENABLE,
  ITEM NUMBER(19,0) NOT NULL ENABLE,
  CONSTRAINT BRD_ITM_FK FOREIGN KEY (ITEM) REFERENCES ITEM (ID) ENABLE
)
/


CREATE SEQUENCE BRAND_ITEM_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 20 START WITH 110000000000440 CACHE 20 NOORDER NOCYCLE
/
