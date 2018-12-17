--Purpose    : CR For Adding Extra Column to Customer Upload Template.
--AUTHOR     : Surendra
--CREATED ON : 18-06-2011

update upload_mgt set columns_to_capture=19 where id=(select id from upload_mgt where name_of_template='customerUpload')
/
alter table CUSTOMER_STAGING add Classification varchar2(4000)
/
Commit
/