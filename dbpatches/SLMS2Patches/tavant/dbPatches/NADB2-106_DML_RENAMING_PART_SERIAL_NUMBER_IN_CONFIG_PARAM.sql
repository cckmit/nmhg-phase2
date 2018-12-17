-- PURPOSE    : PATCH TO RENAME "Part Serial Number" TO "Component Serial Number"
-- AUTHOR     : Priyanka S.
-- CREATED ON : 24-MARCH-2014
update config_param set display_name='Display Component Serial Number field on Parts Installed/Removed section' where name ='showPartSerialNumber'
/
commit
/