--PURPOSE    : PATCH_TO_UPDATE_MANAGE_ROLES_ACCESS_FOR_USERS
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 14-MAY-2014
delete from ROLE_PERMISSION_MAPPING where subject_area in(select id from MST_ADMIN_SUBJECT_AREA where name='settings') and functional_area in(select id from MST_ADMIN_FNC_AREA where name='settingsManageRoles')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsManageRoles'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsManageRoles:update' from role where name in ('internalUserAdmin')
/
commit
/