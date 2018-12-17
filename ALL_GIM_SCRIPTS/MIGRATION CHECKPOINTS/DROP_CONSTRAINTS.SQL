DECLARE

CURSOR cur_det_table
is
select b.constraint_name,a.STG_TABLE_NAME from TAV_GIM_VALID_TABLES  a,user_constraints b
WHERE A.STG_TABLE_NAME = B.TABLE_NAME
and b.constraint_type in ('P','R','U')
order by exec_order desc;

v_query varchar2(1000);

BEGIN

	FOR cur_det_table_rec IN cur_det_table
	LOOP
	begin
	V_QUERY := 'alter table ' || CUR_DET_TABLE_REC.STG_TABLE_NAME || ' drop constraint "' || CUR_DET_TABLE_REC.constraint_name || '"' ; 
	execute immediate v_query;
	EXCEPTION
	WHEN OTHERS THEN
	DBMS_OUTPUT.PUT_LINE(v_query || SQLERRM);
	null;
	end;
	END LOOP;
	
commit;

EXCEPTION
	when OTHERS then
	DBMS_OUTPUT.PUT_LINE('0' || SQLERRM);
END;
/

