--Purpose    : Patch for adding the column marketing_group_code in inventory_item table.
--Author     : Pracher Pancholi
--Created On : 10-Oct-2012

alter table inventory_item add (marketing_group_code varchar2(255))
/