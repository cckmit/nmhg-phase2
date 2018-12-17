-- Purpose: Patch to capture URL against Carrier
-- Author: Ramalakshmi P
-- Created On: 19 June 2009

ALTER TABLE CARRIER ADD (URL VARCHAR2(255))
/
COMMIT
/