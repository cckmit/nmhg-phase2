-- Patch for renaming login banner
-- Author: PARTHASARATHY R
-- Created On : 30-OCT-2012

UPDATE LOGIN_BANNER SET BANNER_MSG='Please note this is the NMHG DEV instance' WHERE BANNER_MSG='Please note this is the R6.2 NMHG DEV instance'
/
UPDATE LOGIN_BANNER SET BANNER_MSG='Please note this is the NMHG QA instance' WHERE BANNER_MSG='Please note this is the R6.2 NMHG QA instance'
/
COMMIT
/