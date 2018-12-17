--PURPOSE    : PATCH_TO_INSERT_DEFINE_SEARCH_RECORD_UNDER_ROLE_PERMISSION_MAPPING_FOR_SUPPLIER
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 09-APR-2014
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='processorRecoveryPreDefinedSearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='processorRecovery'),'processorRecovery:processorRecoveryPreDefinedSearch:update' from role where name in ('supplier')
/
commit
/