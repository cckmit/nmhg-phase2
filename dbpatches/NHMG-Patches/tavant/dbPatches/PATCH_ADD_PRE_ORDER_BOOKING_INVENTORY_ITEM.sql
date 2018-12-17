--Purpose    : Patch for adding the column pre_order_booking in inventory_item table.
--Author     : Pracher Pancholi
--Created On : 10-Oct-2012

alter table inventory_item add (pre_order_booking NUMBER(1))
/
