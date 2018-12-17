--PURPOSE    : PATCH FOR PURCHASE DATE LOGIC
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 21-JUL-09

alter table policy add (purchase_date date)
/
update policy set purchase_date = d_created_on 
where policy_definition in (select id from policy_definition
where warranty_type = 'EXTENDED' ) and d_created_on is not null
and purchase_date is null
/
COMMIT
/