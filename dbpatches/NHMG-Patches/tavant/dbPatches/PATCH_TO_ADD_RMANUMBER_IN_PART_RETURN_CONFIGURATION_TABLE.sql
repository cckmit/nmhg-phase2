--Purpose    : Patch for adding the column rmaNumber in part_return_configuration table.
--Author     : Suneetha Nagaboyina
--Created On : 04-dec-2012

alter table part_return_configuration add (rmaNumber NUMBER)
/