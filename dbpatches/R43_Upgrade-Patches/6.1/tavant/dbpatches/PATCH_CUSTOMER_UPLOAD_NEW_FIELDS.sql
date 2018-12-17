--Purpose    : Adding upload mgt entries for Multiple Site Numbers
--Author     : Surendra
--Created On : 05-Sep-2011

alter table  customer_staging add ( first_name varchar2(4000),last_name varchar2(4000), addressline1 varchar2(4000),addressline2 varchar2(4000), addressline3 varchar2(4000),addressline4 varchar2(4000),secondary_phone varchar2(4000), zip_code_extension varchar2(4000), fax varchar2(4000) )
/
update  upload_mgt set columns_to_capture=26 where name_of_template='customerUpload'
/
update  upload_mgt set columns_to_capture=20 where name_of_template='multipleSiteNumbers'
/
COMMIT
/