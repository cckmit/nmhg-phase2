--PURPOSE    : ADDED NEW COLUMN IN INVENTORY_ITEM TABLE TO DETERMINE IF WARRANTY IS PENDING ON THAT INVENTORY
--AUTHOR     : PRADYOT.ROUT
--CREATED ON : 19-SEP-08

ALTER TABLE INVENTORY_ITEM ADD PENDING_WARRANTY NUMBER(1,0)
/
UPDATE INVENTORY_ITEM SET PENDING_WARRANTY=0
/
UPDATE INVENTORY_ITEM SET PENDING_WARRANTY = 1 WHERE ID IN (SELECT FOR_ITEM FROM WARRANTY WHERE STATUS = 'DRAFT')
/
DECLARE
CURSOR GET_WARRANTY_TASK  IS
SELECT INV_ITEM,WARRANTY_TASK FROM WARRANTY_TASK_INCLUDED_ITEMS;
WARRANTY_TASK NUMBER:=0;
WARRANTY_TASK_STATUS VARCHAR(255);
BEGIN
	FOR GET_REC IN GET_WARRANTY_TASK LOOP
	 	SELECT ID,STATUS INTO WARRANTY_TASK,WARRANTY_TASK_STATUS  FROM WARRANTY_TASK_INSTANCE WHERE ID=GET_REC.WARRANTY_TASK;
	 	IF(WARRANTY_TASK_STATUS='ACCEPTED' OR WARRANTY_TASK_STATUS='DELETED') THEN
	 	 UPDATE INVENTORY_ITEM SET PENDING_WARRANTY=0 WHERE ID=GET_REC.INV_ITEM;
	 	ELSE
	 	 UPDATE INVENTORY_ITEM SET PENDING_WARRANTY=1 WHERE ID=GET_REC.INV_ITEM;
 		END IF;
	END LOOP;
END;
/
UPDATE WARRANTY T1 SET TRANSACTION_TYPE = (SELECT INV_TRANSACTION_TYPE FROM INVENTORY_TRANSACTION WHERE ID = T1.FOR_TRANSACTION)
WHERE TRANSACTION_TYPE IS NULL AND FOR_TRANSACTION IS NOT NULL
/
COMMIT
/