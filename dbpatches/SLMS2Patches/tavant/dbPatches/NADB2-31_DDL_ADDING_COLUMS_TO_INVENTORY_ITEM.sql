
--Purpose    : Patch for adding the InstallBase new column in Inventory_Item table
--Author     : Manohar
--Created On : 13-JAN-2014

alter table address_for_transfer add (customer_contact_title varchar2(2000))
/
alter table INVENTORY_ITEM add (STDW_RESERVE_AMOUNT_YEAR1 NUMBER(19,2))
/
alter table INVENTORY_ITEM add (STDW_RESERVE_AMOUNT_YEAR2 NUMBER(19,2))
/
alter table INVENTORY_ITEM add (AOP_Rate1 NUMBER(19,2))
/
alter table INVENTORY_ITEM add (AOP_Rate2 NUMBER(19,2))
/
alter table INVENTORY_ITEM add (AOP_Target_Rate1 NUMBER(19,2))
/
alter table INVENTORY_ITEM add (AOP_Target_Rate2 NUMBER(19,2))
/
alter table INVENTORY_ITEM add (Order_Gross_Value NUMBER(19,2))
/
alter table INVENTORY_ITEM add (Order_Net_Value NUMBER(19,2))
/