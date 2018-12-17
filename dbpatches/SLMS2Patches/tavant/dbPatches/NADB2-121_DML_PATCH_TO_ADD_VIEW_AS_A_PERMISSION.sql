--Chetan
insert into mst_admin_action(id,action,description) values((select max(id) + 1 from MST_ADMIN_ACTION),'view','View')
/
commit
/