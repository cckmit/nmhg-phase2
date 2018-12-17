--Purpose    : Patch for updating WR to DR in Inventory transaction Type 
--Author     : PARTHASARATHY R	
--Created On : 25-Mar-2013

update Inventory_transaction_type set trnx_type_value='DR' where trnx_type_key='DR'
/
commit
/