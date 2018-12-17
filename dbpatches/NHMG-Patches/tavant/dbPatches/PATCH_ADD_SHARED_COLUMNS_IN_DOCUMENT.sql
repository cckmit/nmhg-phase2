--Purpose    : Patch for adding the columns in document table.
--Author     : Suneetha Nagaboyina
--Created On : 15-oct-2012

alter table document add (IS_SHARED_WITH_SUPPLIER NUMBER(1,0),
IS_SHARED_WITH_DEALER NUMBER(1,0))
/