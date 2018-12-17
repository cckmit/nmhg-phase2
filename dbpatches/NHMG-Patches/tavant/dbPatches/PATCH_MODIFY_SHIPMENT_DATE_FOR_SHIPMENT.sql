--Purpose    : Patch to modify shipment_date in shipment table.
--Author     : Suneetha Nagaboyina
--Created On : 08-oct-2012

alter table shipment modify (SHIPMENT_DATE TIMESTAMP (6))
/
commit
/
