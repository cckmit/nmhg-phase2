--PURPOSE    : PATCH_TO_create_table_deliverychecklist
--AUTHOR     : Raghavendra
--CREATED ON : 11-JAN-14

create table delivery_check_list
(
 id Number(19,0) not null enable,
 delivery_check_list varchar2(2000)
)
/