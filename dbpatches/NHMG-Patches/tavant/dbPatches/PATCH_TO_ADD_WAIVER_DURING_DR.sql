--Purpose    : Patch for adding column WAIVER_DURING_DR in INVENTORY_ITEM
--Author     : PARTHASARATHY R
--Created On : 19-Feb-2013

alter table INVENTORY_ITEM add (WAIVER_DURING_DR NUMBER(19,0))
/
alter table INVENTORY_ITEM add constraint WAIVER_DURING_DR_FK foreign key (WAIVER_DURING_DR) references DIESEL_TIER_WAIVER (ID)
/