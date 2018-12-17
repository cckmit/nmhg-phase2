--PURPOSE    : patch to insert values to bu_item_mapping table
--AUTHOR     : Raghu
--CREATED ON : 09-APR-2014


Declare
cursor c1 is
	select id,item_type from item;
begin
	for c1_rec in c1
	  loop
		begin
			insert into bu_item_mapping values(c1_rec.id,'EMEA');
			IF  c1_rec.item_type = 'PART' THEN
			insert into bu_item_mapping values(c1_rec.id,'AMER');
			END IF;
		END;
		COMMIT;
	END LOOP;
END;