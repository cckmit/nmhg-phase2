--PURPOSE    : PATCH_TO_ADD_NEW_COLUMN_IN_PAYMENT_MODIFIER
--AUTHOR     : Raghavendra
--CREATED ON : 03-MAY-13


alter table payment_modifier Add (landed_cost NUMBER(1,0) DEFAULT 0)
/
commit
/