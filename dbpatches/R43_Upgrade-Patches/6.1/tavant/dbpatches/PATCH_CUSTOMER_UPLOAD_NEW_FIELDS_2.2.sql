--Purpose    : Adding upload mgt entries for Multiple Site Numbers
--Author     : Surendra
--Created On : 05-Sep-2011

alter table  customer_staging add (zip_code varchar2(4000))
/
COMMIT
/