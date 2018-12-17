--PURPOSE    : PATCH_TO_ADD_NEW_COLUMN_IN_ORGANIZATION_ADDRESS
--AUTHOR     : Raghavendra
--CREATED ON : 13-MAY-13


alter table organization_address Add (address_active NUMBER(1,0) DEFAULT 1)
/
commit
/