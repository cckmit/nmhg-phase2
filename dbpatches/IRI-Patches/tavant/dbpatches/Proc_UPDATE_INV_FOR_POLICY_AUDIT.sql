CREATE OR REPLACE PROCEDURE "UPDATE_INV_FOR_POLICY_AUDIT" (p_policy_id NUMBER ) AS

v_inventory_item_id number;
v_wnty_start_date date;
v_wnty_end_date date;

BEGIN
	select a.id into v_inventory_item_id 
		from inventory_item a, 
              warranty b, 
              policy c
        where b.for_item=a.id and c.warranty=b.id and c.id=p_policy_id;
		
		select min(from_date) into v_wnty_start_date
		from inventory_item a, 
              warranty b, 
              policy c, 
              policy_audit d 
        where b.for_item=a.id 
        and c.warranty=b.id 
        and d.for_policy=c.id 
        and a.id=v_inventory_item_id
        and b.list_index=(select max(list_index) from warranty b1 where b1.for_item=a.id);
		
		select max(till_date) into v_wnty_end_date 
		from inventory_item a, 
              warranty b, 
              policy c, 
              policy_audit d 
        where b.for_item=a.id 
        and c.warranty=b.id 
        and d.for_policy=c.id 
        and a.id=v_inventory_item_id
        and b.list_index=(select max(list_index) from warranty b1 where b1.for_item=a.id);
		
		update inventory_item set wnty_start_date= v_wnty_start_date , 
		wnty_end_date=v_wnty_end_date where id= v_inventory_item_id;

END;
