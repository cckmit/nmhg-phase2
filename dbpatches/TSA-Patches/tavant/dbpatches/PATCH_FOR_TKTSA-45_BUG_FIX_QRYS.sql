--Purpose    : 1. Adding SERVICE_PART column in item_staging table    
--             2. Incrementing column value by 1 for number of columns to capture in upload_mgt table
--Author     : devendrababu.n
--Created On : 26/08/2010

alter table item_staging add SERVICE_PART varchar2(4000)
/
update upload_mgt set COLUMNS_TO_CAPTURE = COLUMNS_TO_CAPTURE + 1 where upload_procedure='ITEM_Upload'
/
commit
/