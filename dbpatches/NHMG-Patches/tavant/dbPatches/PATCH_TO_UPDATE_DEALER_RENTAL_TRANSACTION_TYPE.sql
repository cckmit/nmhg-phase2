--Purpose    : Patch for updating Dealer Rental Transaction Type 
--Author     : ParthaSarathy R	
--Created On : 05-Mar-2013

update Inventory_transaction_type set trnx_type_value='DEALER RENTAL' where trnx_type_key='DR_RENTAL'
/
commit
/