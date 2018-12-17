-- Patch to Insert sync_status
-- Author: kalyani
-- Created On : 18-MAR-2013
Insert into sync_status values('Cancelled',SYSDATE,'Data Migration',SYSDATE,'',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,'1')
/
COMMIT
/