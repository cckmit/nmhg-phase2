--PURPOSE    : PATCH TO DROP UNIQUE CONSTRAINT ON COUNTY_NAME IN COUNTY_CODE_MAPPING TABLE
--AUTHOR     : Arpitha Nadig AR
--CREATED ON : 13-MAY-2014
declare
v_consname varchar2(4000);
v_sql varchar2(4000);
begin  
  select constraint_name into v_consname 
  from user_constraints where table_name='COUNTY_CODE_MAPPING' and constraint_type='U';
  v_sql := 'alter table COUNTY_CODE_MAPPING drop constraint '|| v_consname;
  execute immediate v_sql;
  v_sql := 'drop index '|| v_consname;
  execute immediate v_sql;  
exception when others then
  dbms_output.put_line ('*** Exception when dropping index ***');
end;
/
commit
/