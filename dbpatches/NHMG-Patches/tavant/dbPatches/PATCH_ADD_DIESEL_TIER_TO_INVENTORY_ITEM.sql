--Purpose    : Patch for adding the column diesel_tier in inventory_item table.
--Author     : Pracher Pancholi
--Created On : 20-Sep-2012

alter table inventory_item add (diesel_tier varchar2(20))
/
