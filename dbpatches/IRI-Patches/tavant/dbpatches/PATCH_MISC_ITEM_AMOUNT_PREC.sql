-- Pach for altering precision of amount column
-- Author: Ramalakshmi P
-- Jun 26 2009

ALTER TABLE MISC_ITEM_RATE MODIFY (AMOUNT NUMBER(19,2))
/
COMMIT
/