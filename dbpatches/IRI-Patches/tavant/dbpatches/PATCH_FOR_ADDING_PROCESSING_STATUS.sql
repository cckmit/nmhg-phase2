--PURPOSE    : PATCH FOR ADDING PROCESSING_STATUS COLUMN IN SYNC_TRACKER
--AUTHOR     : GYANENDRA BISWANATH MISHRA
--CREATED ON : 21-APR-09

ALTER TABLE sync_tracker ADD (PROCESSING_STATUS VARCHAR2(255))
/
COMMIT
/