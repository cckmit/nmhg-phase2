--Purpose    : Patch for adding the column BUDGETED_AMOUNT in campaign table.
--Author     : Jyoti Chauhan
--Created On : 28-NOV-2012

alter table campaign add BUDGETED_AMOUNT NUMBER(19,2)
/