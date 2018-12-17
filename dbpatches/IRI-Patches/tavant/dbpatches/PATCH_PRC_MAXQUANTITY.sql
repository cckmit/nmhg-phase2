--Purpose    : added new columns "maxQuantity" and "quantityRecevied" in PartReturnConfiguration
--Author     : rakesh.r
--Created On : 11-Aug-08


alter table part_return_configuration add max_quantity number(10,0)
/
alter table part_return_configuration add quantity_received number(10,0)
/
