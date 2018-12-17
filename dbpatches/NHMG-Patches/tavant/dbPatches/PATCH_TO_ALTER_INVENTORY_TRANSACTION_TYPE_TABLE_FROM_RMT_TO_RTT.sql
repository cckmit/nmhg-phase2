--PURPOSE    : PATCH TO ALTER INVENTORY_TRANSACTION_TYPE TABLE FROM RMT TO RTT
--AUTHOR     : PALLAVI
--CREATED ON : 15-MARCH-13

update inventory_transaction_type set trnx_type_value='RTT' Where trnx_type_key='RMT'
/
COMMIT
/