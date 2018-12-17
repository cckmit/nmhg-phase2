--Purpose    : Updated rows for quantity recieved in PRC
--Author     : rakesh.r
--Created On : 07-Sep-08

update part_return_configuration set  QUANTITY_RECEIVED=0 where  QUANTITY_RECEIVED is null
/
commit
/