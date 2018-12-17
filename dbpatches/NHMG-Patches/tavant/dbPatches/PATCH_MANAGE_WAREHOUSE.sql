--PURPOSE    : Patch for adding Contact Person Name column to Warehouse table, as part of Doosan Heavy TWMS implementation
--AUTHOR     : Kuldeep Patil
--CREATED ON : 28-June-2012

ALTER TABLE WAREHOUSE ADD CONTACT_PERSON_NAME VARCHAR2(255)
/
CREATE SEQUENCE WAREHOUSE_SHIPPERS_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999999 START WITH 1 INCREMENT BY 20 NOCACHE NOORDER NOCYCLE
/
CREATE TABLE WAREHOUSE_SHIPPERS
  (
	ID NUMBER(19,0) ,
    WAREHOUSE      NUMBER(19,0) ,
    CARRIER        NUMBER(19,0) ,
    ACCOUNT_NUMBER VARCHAR2(255)
  )
/
ALTER TABLE CARRIER ADD CONSTRAINT CARRIER_PK PRIMARY KEY (ID)
/
ALTER TABLE WAREHOUSE_SHIPPERS ADD CONSTRAINT WRHSE_SHIPPERS_PK PRIMARY KEY (ID)
/
ALTER TABLE WAREHOUSE_SHIPPERS ADD CONSTRAINT WRHSE_SHIPPERS_WAREHOUSE_FK FOREIGN KEY (WAREHOUSE) REFERENCES WAREHOUSE(ID)
/
ALTER TABLE WAREHOUSE_SHIPPERS ADD CONSTRAINT WRHSE_SHIPPERS_CARRIER_FK FOREIGN KEY (CARRIER) REFERENCES CARRIER(ID)
/