--PURPOSE    : Remapping series certificate screen.
--AUTHOR     : Chetan
--CREATED ON : 21-MAY-2014
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsSeriesReftoCertification'),(select id from MST_ADMIN_ACTION where action='view'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsSeriesReftoCertification:view' from role where name in ('internalUserAdmin')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='settingsSeriesReftoCertification'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='settings'),'settings:settingsSeriesReftoCertification:update' from role where name in ('admin')
/
commit
/