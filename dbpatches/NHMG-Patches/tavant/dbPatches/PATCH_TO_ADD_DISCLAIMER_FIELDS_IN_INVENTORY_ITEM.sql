--Purpose    : Patch for adding the column disclaimer_for and disclaimer_info in inventory_item table.
--Author     : Suneetha Nagaboyina
--Created On : 27-nov-2012

alter table inventory_item add (disclaimer_for number(1,0),disclaimer_info varchar2(255))
/