--Purpose    : BU Config for Currency to be used for ERP Interactions
--Created On : 17-Feb-2010
--Created By : Rahul Katariya
--Impact     : None

ALTER TABLE ITEM  
MODIFY (CLASS_CODE NUMBER)
/
ALTER TABLE ITEM ADD (
 CONSTRAINT ITEM_PARTCLASS_FK 
 FOREIGN KEY (CLASS_CODE) 
 REFERENCES ITEM_GROUP (ID))
/
ALTER TABLE ITEM 
ADD (SERVICE_PART NUMBER(1, 0))
/
COMMIT
/
INSERT INTO DIVISION_BU_MAPPING values (DIVISION_BU_MAPPING_SEQ.nextval,'Thermo King TSA','Thermo King TSA',null,'Thermo King TSA','ACTIVE',CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),'TSA-Migration',CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),56,1)
/
COMMIT
/
