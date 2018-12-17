--Purpose    : Patch for adding the Flag for Manual DR column in warranty table
--Author     : Priyanka S
--Created On : 27-NOV-2013

alter table warranty add (MANUAL_FLAG_DR NUMBER(1,0))
/