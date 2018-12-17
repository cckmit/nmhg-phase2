--PURPOSE    : Remapping user management screen from 'dealerAdministrator','SSDataAdmin' roles to 'internalUserAdmin'
--AUTHOR     : Chetan
--CREATED ON : 09-MAY-2014
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('dealerAdministrator','SSDataAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsUserManagement')
/
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('dealerAdministrator','SSDataAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsCreateUser')
/
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('dealerAdministrator','SSDataAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsUpdateUser')
/
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('dealerAdministrator','SSDataAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsSeriesReftoCertification')
/
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('dealerAdministrator','SSDataAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsCreateInternalUser')
/
delete from ROLE_PERMISSION_MAPPING where subject_area=(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings') and role_def_id in (select id from role where name in ('dealerAdministrator','SSDataAdmin')) and functional_area=(select id from MST_ADMIN_FNC_AREA where name='settingsUpdateInternalUser')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsUserManagement'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsUserManagement:update' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsCreateUser'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsCreateUser:update' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsUpdateUser'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsUpdateUser:update' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsSeriesReftoCertification'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsSeriesReftoCertification:update' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsCreateInternalUser'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsCreateInternalUser:update' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsUpdateInternalUser'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsUpdateInternalUser:update' from role where name in ('internalUserAdmin')
/
commit
/