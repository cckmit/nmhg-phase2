--Purpose    : Increasing the decimal scale from 2 to 6
--Author     : Kethan
--Created On : 11/11/2011

alter table line_item_group add ( PERCENTAGE_ACCEPTANCE_NEW number(19,6))
/
create Index TEMP_PER_ACCEP_NEW_IDX on line_item_group(PERCENTAGE_ACCEPTANCE)
/
update line_item_group set PERCENTAGE_ACCEPTANCE_NEW = PERCENTAGE_ACCEPTANCE
/
commit
/
drop index TEMP_PER_ACCEP_NEW_IDX
/
alter table line_item_group rename column PERCENTAGE_ACCEPTANCE to PERCENTAGE_ACCEPTANCE_BACKUP
/
alter table line_item_group rename column PERCENTAGE_ACCEPTANCE_NEW to PERCENTAGE_ACCEPTANCE
/
commit
/