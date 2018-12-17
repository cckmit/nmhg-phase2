set echo off
set pages 0 head on emb on newp none

spool .\4.3_Source_Table_Column_Count.txt

SELECT table_name, count(column_name)
FROM user_tab_columns
where table_name not like 'JBPM%'
and table_name not like 'BIN%'
and table_name not like 'MLOG%'
and table_name not like 'RUPD%'
and table_name not like 'STG%'
and table_name not like 'TMP%'
and table_name not like '%TEMP%'
and table_name not like '%BKP%'
and table_name not like '%BACKUP%'
and table_name not like '%TAV_GIM%'
group by table_name
order by table_name;


spool off

exit