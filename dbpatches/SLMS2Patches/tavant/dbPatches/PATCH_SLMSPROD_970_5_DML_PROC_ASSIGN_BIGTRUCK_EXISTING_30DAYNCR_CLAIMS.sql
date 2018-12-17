--  REFERENCE   : SLMSPOD-970 
--	AUTHOR		: Ravi K Sinha

-- To fix existing claims (which were, by default, BT 30 Day NCR claims)
declare
bt_class_id claim.inv_class_30_day_ncr%TYPE;
cursor cur is 
select c.id, c.claim_number from claim c 
where c.inv_class_30_day_ncr is null
and c.ncr_with_30_days = 1
and c.clm_type_name = 'Machine'
and c.business_unit_info = 'EMEA';
begin 
  select id into bt_class_id from inventory_class where name ='Big Truck' and business_unit_info='EMEA';
 
  for each_rec in cur loop 
    update claim set inv_class_30_day_ncr = bt_class_id where id = each_rec.id;
  end loop;
end; 
/

COMMIT
/
