--PURPOSE    : Remapping create & update user to dealer admin.
--AUTHOR     : Chetan
--CREATED ON : 19-MAY-2014
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('internalUserAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsCreateUser')
/
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('internalUserAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsUpdateUser')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsUserManagement'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsUserManagement:update' from role where name in ('dealerWarrantyAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsCreateUser'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsCreateUser:update' from role where name in ('dealerWarrantyAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsUpdateUser'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsUpdateUser:update' from role where name in ('dealerWarrantyAdmin')
/
commit
/