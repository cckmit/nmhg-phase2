--Purpose    : Patch for adding column FOR_INVENTORY_ITEM in DIESEL_TIER_WAIVER
--Author     : PARTHASARATHY R
--Created On : 18-Feb-2013

alter table INVENTORY_ITEM drop column WAIVER
/
alter table DIESEL_TIER_WAIVER add (INVENTORY_ITEM NUMBER(19,0))
/
alter table DIESEL_TIER_WAIVER add constraint INVENTORY_ITEM_FK foreign key (INVENTORY_ITEM) references INVENTORY_ITEM (ID)
/