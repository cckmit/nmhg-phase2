--Purpose    : DML for NMHGSLMS-424 -Adding attachments to Inventory.
--Author     : Arpitha Nadig AR
--Created On : 13-DEC-2013
--------------------------------------------------------
--  Constraints for Table INVENTORY_ITEM_ATTACHMENTS
--------------------------------------------------------
  
--------------------------------------------------------
--  Ref Constraints for Table INVENTORY_ITEM_ATTACHMENTS
--------------------------------------------------------

ALTER TABLE INVENTORY_ITEM_ATTACHMENTS MODIFY ("ATTACHMENTS" NOT NULL ENABLE)
/
ALTER TABLE INVENTORY_ITEM_ATTACHMENTS MODIFY ("INVENTORY_ITEM" NOT NULL ENABLE)
/
ALTER TABLE INVENTORY_ITEM_ATTACHMENTS ADD CONSTRAINT "INVENTORY_ITEM_ATTACH_FK" FOREIGN KEY ("INVENTORY_ITEM")
REFERENCES INVENTORY_ITEM ("ID") ENABLE
/
ALTER TABLE INVENTORY_ITEM_ATTACHMENTS ADD CONSTRAINT "INVENTORY_ITEM_ATTACHMENTS_FK" FOREIGN KEY ("ATTACHMENTS")
REFERENCES DOCUMENT ("ID") ENABLE
/