--Purpose    : Patch for adding the column Authorization_Received in claim table.
--Author     : Jyoti Chauhan
--Created On : 17-Oct-2012

alter table claim add Authorization_Received NUMBER(1,0)
/