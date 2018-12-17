--Purpose    : Patch for adding the new status data to sync_status table.
--Author     : Suneetha Nagaboyina
--Created On : 18-MAR-2013

Insert into sync_status values('Response Updated',SYSDATE,'Data Migration',SYSDATE,'',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'1')
/
