--Purpose    : alter staging table
--Author     : Rahul
--Created On : 12/07/2010


alter table STG_WARRANTY_REGISTRATIONS drop column REQUEST_FOR_EXTENSION
/
alter table STG_WARRANTY_REGISTRATIONS add(REQUEST_FOR_EXTENSION VARCHAR2(255))
/
commit
/

