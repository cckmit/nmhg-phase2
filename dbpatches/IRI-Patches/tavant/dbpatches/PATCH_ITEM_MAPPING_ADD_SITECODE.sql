--PURPOSE    : PATCH FOR ADDING SUPPLIER_SITECODE IN ITEM_MAPPING
--AUTHOR     : BHASKARA K
--CREATED ON : 26-APR-09

alter table item_mapping add(supplier_sitecode varchar2(255))
/
COMMIT
/