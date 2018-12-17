--Purpose: For setting all comments columns to 4000 chars
--Author: Vamshi Gunda
--Created On: Date 24 Jul 2009

create or replace PROCEDURE SET_COMMENTS_COLUMN_SIZE
as
cursor tables_to_modify is
SELECT table_name,column_name 
FROM user_tab_cols 
where data_type='VARCHAR2' 
and column_name like '%COMMENT%' 
and column_name <> 'D_INTERNAL_COMMENTS' 
and char_length < 4000
and table_name<>'BIN$cHXEYRSGR9zgQAB/AQBB0Q==$0';
begin
for each_table_to_modify in tables_to_modify loop
EXECUTE IMMEDIATE 'alter table ' || each_table_to_modify.table_name || ' modify ('||each_table_to_modify.column_name||' varchar2(4000))';
commit;
end loop;
end;
/
begin
SET_COMMENTS_COLUMN_SIZE();
end;
/
commit
/