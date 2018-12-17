--PURPOSE    : PATCH_TO_INSERT_STOLEN_AS_AN_ITEM_CONDITION
--AUTHOR     : Raghavendra
--CREATED ON : 03-MAY-13


insert into inventory_item_condition(item_condition,version,d_active) values('STOLEN',1,1)
/
commit
/