--PURPOSE    : PATCH_TO_DISP_PREDEF_DEF_SEARCH_OPT_VRPROCESSOR
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 11-JUN-2014
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='partReturnsDefineSearchQuery'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='partReturns'),'partReturns:partReturnsDefineSearchQuery:update' from role where name in ('warrantySupervisor')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='partReturnsPredefinedSearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='partReturns'),'partReturns:partReturnsPredefinedSearch:update' from role where name in ('warrantySupervisor')
/
commit
/