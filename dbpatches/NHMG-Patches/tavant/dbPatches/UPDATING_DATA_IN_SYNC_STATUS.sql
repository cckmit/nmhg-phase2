--Purpose    : Patch for updating the status in SyncStatus table.
--Author     : Suneetha Nagaboyina
--Created On : 26-MAR-2013

Insert into sync_status values('Response Sent',SYSDATE,'Data Migration',SYSDATE,'',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'1') 
/
commit
/
