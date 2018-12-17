--PURPOSE    : PATCH FOR UPDATING DELIVERY_DATE
--AUTHOR     : GHANASHYAM DAS
--MOD ON : 28-JUN-11
update inventory_item set delivery_date=wnty_start_date,d_internal_comments= d_internal_comments || 'QC-ISSUE-90' where delivery_date is null and wnty_start_date is not null and type='RETAIL'
/
COMMIT
/