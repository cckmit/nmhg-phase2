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
