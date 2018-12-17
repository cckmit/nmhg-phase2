--PURPOSE    : PATCH TO CHANGE TRANSACTION TYPE FROM ETR TO TTR IN INVENTORY TRANSACTION TYPE
--AUTHOR     : Raghu
--CREATED ON : 21-Feb-13

update inventory_transaction_type set trnx_type_value = 'TTR_MODIFY' Where trnx_type_key ='ETR_MODIFY'
/
update inventory_transaction_type set trnx_type_value = 'TTR_DELETE' Where trnx_type_key ='ETR_DELETE'
/
update inventory_transaction_type set trnx_type_value = 'TTR_REJECT' Where trnx_type_key ='ETR_REJECT'
/
COMMIT
/