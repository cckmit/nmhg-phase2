--Purpose    : To add Supplier cost amount and currency for the recovery claim in COST_LINE_ITEM table
--Author     : Jitesh Jain
--Created On : 06-Mar-2009

alter table COST_LINE_ITEM add(SUPPLIER_COST_AMT NUMBER(19, 2))
/
alter table COST_LINE_ITEM add(SUPPLIER_COST_CURR VARCHAR2(255 char))
/
commit
/