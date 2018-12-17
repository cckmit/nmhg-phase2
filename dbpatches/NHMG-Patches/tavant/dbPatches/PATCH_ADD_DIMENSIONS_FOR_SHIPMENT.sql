--Purpose    : Patch for adding the columns for dimensions in shipment table.
--Author     : Suneetha Nagaboyina
--Created On : 08-oct-2012

alter table shipment add (height number(19,2),weight number(19,2),breadth number(19,2),length number(19,2))
/
commit
/