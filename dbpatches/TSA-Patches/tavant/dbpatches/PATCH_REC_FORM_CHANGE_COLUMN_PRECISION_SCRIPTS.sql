--Purpose    : Scripts for correcting the column length for PERCENTAGE_OF_COST, NO_OF_HOURS - TKTSA-138
--Author     : kuldeep.patil
--Created On : 27/08/2010

ALTER TABLE RECOVERY_FORMULA ADD PERCENTAGE_OF_COST_DUMP NUMBER(10,2)
/
UPDATE RECOVERY_FORMULA A SET PERCENTAGE_OF_COST_DUMP = (SELECT PERCENTAGE_OF_COST FROM RECOVERY_FORMULA B WHERE A.ID = B.ID)
/
ALTER TABLE RECOVERY_FORMULA DROP COLUMN PERCENTAGE_OF_COST
/
ALTER TABLE RECOVERY_FORMULA RENAME COLUMN PERCENTAGE_OF_COST_DUMP TO PERCENTAGE_OF_COST
/
ALTER TABLE RECOVERY_FORMULA ADD NO_OF_HOURS_DUMP NUMBER(10,2)
/
UPDATE RECOVERY_FORMULA A SET NO_OF_HOURS_DUMP = (SELECT NO_OF_HOURS FROM RECOVERY_FORMULA B WHERE A.ID = B.ID)
/
ALTER TABLE RECOVERY_FORMULA DROP COLUMN NO_OF_HOURS
/
ALTER TABLE RECOVERY_FORMULA RENAME COLUMN NO_OF_HOURS_DUMP TO NO_OF_HOURS
/
COMMIT
/