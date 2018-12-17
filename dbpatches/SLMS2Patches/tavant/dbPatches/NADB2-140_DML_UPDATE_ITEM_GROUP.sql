--PURPOSE    : update item_group group_code='MACHINE'
--AUTHOR     : Chetan
--CREATED ON : 05-MAY-2014
update item_group set group_code='MACHINE' where 
name = 'Machine-MACHINE - US' and 
ITEM_GROUP_TYPE='PRODUCT TYPE' and BUSINESS_UNIT_INFO='AMER'
/
commit
/