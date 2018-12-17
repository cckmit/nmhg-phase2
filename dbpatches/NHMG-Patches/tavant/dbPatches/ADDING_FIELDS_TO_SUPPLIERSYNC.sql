--Purpose    : Patch for adding the column in supplier table.
--Author     : Suneetha Nagaboyina
--Created On : 13-FEB-2013

alter table supplier add (FIRST_NAME VARCHAR2(255))
/
alter table supplier add (LAST_NAME VARCHAR2(255))
/
alter table supplier add (status VARCHAR2(10))
/