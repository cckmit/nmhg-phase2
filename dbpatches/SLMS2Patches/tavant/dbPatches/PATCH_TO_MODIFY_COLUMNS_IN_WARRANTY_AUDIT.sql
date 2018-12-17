-- PURPOSE    : PATCH To alter d_updated_time column in Warranty_audit
-- AUTHOR     : Siva Kalyani
-- CREATED ON : 08-AUGUST-2014
alter table warranty_audit modify (D_UPDATED_TIME TIMESTAMP(6))
/
