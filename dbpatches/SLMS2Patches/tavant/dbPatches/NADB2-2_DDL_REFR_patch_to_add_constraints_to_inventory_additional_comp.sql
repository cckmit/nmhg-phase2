--Purpose    :  patch to add constraints to inventory_additional_comp
--Author     : Raghavendra
--Created On : 12-Dec-2013

alter table inventory_additional_comp add CONSTRAINT "INV_ADD_COMP_LST_UPDT_BY_FK" FOREIGN KEY ("D_LAST_UPDATED_BY") REFERENCES "ORG_USER" ("ID") ENABLE
/
alter table inventory_additional_comp add CONSTRAINT "INVADDCOMP_ITEM_FK" FOREIGN KEY ("ITEM") REFERENCES "INVENTORY_ITEM" ("ID") ENABLE
/