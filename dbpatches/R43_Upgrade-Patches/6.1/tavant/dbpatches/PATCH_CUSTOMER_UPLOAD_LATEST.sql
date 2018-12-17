--Purpose    : Adding upload mgt entries for Multiple Site Numbers
--Author     : Surendra
--Created On : 10-July-2011

Insert into UPLOAD_MGT values (UPLOAD_MGT_SEQ.NEXTVAL,'multipleSiteNumbers','Multiple Site Numbers','Multiple Site Numbers','./pages/secure/admin/upload/templates/Template-MultipleSiteNumbers.xls','CUSTOMER_STAGING',null,'MULTIPLE_CUSTOMER_VALIDATION','MULTIPLE_CUSTOMER_UPLOAD',10,null,6,1,null)
/
COMMIT
/