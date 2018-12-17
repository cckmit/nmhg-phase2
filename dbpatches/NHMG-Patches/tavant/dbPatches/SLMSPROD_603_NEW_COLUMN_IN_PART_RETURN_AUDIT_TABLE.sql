--PURPOSE    : Patch to add a new column in part return audit table Table
--AUTHOR     : Deepak
--CREATED ON : 2-Dec-2013

ALTER TABLE part_return_audit ADD PART_RETURN_ACTION3 NUMBER(19)
/
ALTER TABLE part_return_audit ADD(CONSTRAINT "PART_ACT_03" FOREIGN KEY ("PART_RETURN_ACTION3") REFERENCES part_return_action(ID))
/