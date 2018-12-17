--PURPOSE    : Search permission for dealer role
--AUTHOR     : Chetan
--CREATED ON : 15-MAY-2014
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='partReturnsDefineSearchQuery'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='partReturns'),'partReturns:partReturnsDefineSearchQuery:update' from role where name in ('dealer')
/
INSERT INTO ROLE_PERMISSION_MAPPING select ROLE_PERMISSION_MAPPING_SEQ.nextval,id,(select id from MST_ADMIN_FNC_AREA where name='partReturnsPredefinedSearch'),(select id from MST_ADMIN_ACTION where action='update'),(select id from MST_ADMIN_SUBJECT_AREA WHERE name='partReturns'),'partReturns:partReturnsPredefinedSearch:update' from role where name in ('dealer')
/
commit
/