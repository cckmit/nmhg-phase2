--PURPOSE    : Update SUBMITTED status ( both manual/auto approval) to ACCEPTED to reduce the SUBMITTED number of records which is need for UI display
--AUTHOR     : Ramalakshmi P
--CREATED ON : 02-MAY-11

UPDATE WARRANTY_TASK_INSTANCE SET STATUS = 'ACCEPTED' WHERE STATUS = 'SUBMITTED' AND ACTIVE = 0
/
COMMIT
/

