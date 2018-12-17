--Purpose    : Scripts for changing the reason column size in labor_split table, changes made as a part of request sent by Single Instance DEV Team
--Created On : 16-Mar-2011
--Created By : Kuldeep Patil
--Impact     : None

alter table labor_split modify reason varchar(4000)
/
commit
/
