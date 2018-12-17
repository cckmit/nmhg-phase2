--  PURPOSE		: To create mappings for existing dealers who are allowed 30 DAY NCR for Big Truck class
--  REFERENCE   : SLMSPOD-970 
--	AUTHOR		: Ravi K Sinha

declare

  bt_class_id 		number(19,0);
  sys_user_id	  number(19,0);

  cursor spCursor is
    select sp.id from service_provider sp where sp.allowed_ncr_with_30_days = 1;
    
  
begin
    
  for eachSP in spCursor
  loop
    -- Get the system user id
	select id into sys_user_id from org_user where login='system';
		
    -- Get the BT class id
	select id into bt_class_id from inventory_class where name = 'Big Truck' and business_unit_info='EMEA'; -- 1000000000	
    
    -- Create the mapping
    insert into inv_class_dealer_mapping values (
		inv_class_dealer_mapping_seq.nextVal, 
		bt_class_id, 
		eachSP.id, 
		sysdate, 
		'Migrated existing allowed user for SLMSPROD-970', 
		sysdate,
		56, 
		systimestamp, 
		systimestamp, 
		1);
    
  end loop;
end;
/

COMMIT;
/