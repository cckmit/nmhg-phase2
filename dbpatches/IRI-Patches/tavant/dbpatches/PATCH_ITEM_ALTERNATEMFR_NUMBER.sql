--Purpose    : PATCH FOR ADDING A COLUMN IN ITEM TABLE THAT SAYS IF ITEM NUMBER IS DUPLICATED
--Author     : Ramalakshmi P
--Created On : 25-Jan-09

alter table item add (duplicate_Alternate_Number NUMBER(1))
/
