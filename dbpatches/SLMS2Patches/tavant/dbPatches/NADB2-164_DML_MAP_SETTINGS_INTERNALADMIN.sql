--PURPOSE    : Map settings for internal admin
--AUTHOR     : Chetan
--CREATED ON : 26-MAY-14

INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsSettingsTab'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsSettingsTab:update' from role where name in ('internalUserAdmin')
/
commit
/