--PURPOSE    : PATCH_TO_REMOVE_SUPPLIER_RETURNED_FROM_WAREHOUSE_LIST
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 23-JLY-2014
UPDATE warehouse SET d_active=0 WHERE business_name='Supplier Returned'
/
commit
/