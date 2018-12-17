
--Purpose    : Patch for adding the InstallBase new column in Inventory_Item table
--Author     : Manohar
--Created On : 17-JAN-2014


alter table INVENTORY_ITEM add (EXTD_WNTY_RESRV_AMOUNT NUMBER(19,2))
/
alter table INVENTORY_ITEM  add (NOMENCLATURE varchar2(255))
/
alter table INVENTORY_ITEM add (CURRENCY varchar2(255))
/


