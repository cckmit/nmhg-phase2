--Purpose    : BU Config for Currency to be used for ERP Interactions, changes made as a part of 4.3 upgrade 
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
ADD SERVICE_PART NUMBER(1, 0)
/
UPDATE ITEM SET SERVICE_PART = 1 WHERE SERVICE_PART IS NULL
/
--INSERT INTO DIVISION_BU_MAPPING values (DIVISION_BU_MAPPING_SEQ.nextval,'Thermo King TSA','Thermo King TSA',null,'Thermo King TSA','ACTIVE',sysdate,CAST(sysdate AS TIMESTAMP),'TSA-Migration',sysdate,CAST(sysdate AS TIMESTAMP),56,1)
--/
COMMIT
/
