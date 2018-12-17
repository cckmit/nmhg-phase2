--Purpose    : Patch for updating DR to WR in Inventory transaction Type 
--Author     : Jyoti Chauhan	
--Created On : 19-DEC-2012

update Inventory_transaction_type set trnx_type_value='WR' where trnx_type_value='DR'
/
commit
/