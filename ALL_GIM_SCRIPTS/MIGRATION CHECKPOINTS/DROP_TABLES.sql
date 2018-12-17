DECLARE

CURSOR cur_det_table
IS
SELECT stg_table_name FROM tav_gim_valid_tables a, user_tables b where a.stg_table_name = b.table_name order by exec_order;

BEGIN
	
	FOR cur_det_table_rec IN cur_det_table	LOOP
		begin
		EXECUTE IMMEDIATE 'DROP TABLE ' || cur_det_table_rec.stg_table_name || ' CASCADE CONSTRAINTS';
		EXCEPTION
		when OTHERS then
		null;
		end;
	END LOOP;
	
EXCEPTION
when OTHERS then
DBMS_OUTPUT.PUT_LINE('0' || SQLERRM);
END;
/

