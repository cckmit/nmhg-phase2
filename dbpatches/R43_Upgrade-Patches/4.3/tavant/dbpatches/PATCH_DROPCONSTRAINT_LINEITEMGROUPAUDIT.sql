--Purpose    : Patch for dropping constraints, changed as a part of 4.3 upgrade
--Author     : Kuldeep Patil
--Created On : 11-Oct-10

--alter table line_item_group_audit drop constraint SYS_C0011917
--/
--alter table line_item_group_audit drop constraint SYS_C0011918
--/
COMMIT
/