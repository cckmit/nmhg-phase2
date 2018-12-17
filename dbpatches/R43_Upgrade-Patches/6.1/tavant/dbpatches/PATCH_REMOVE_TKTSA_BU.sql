--PURPOSE    : PATCH FOR REMOVING TKTSA BU FROM gpresley login
--AUTHOR     : DEVENDRA BABU N
--CREATED ON : 12-MAY-2011
delete bu_user_mapping where org_user = (select id from org_user where login = 'gpresley') and bu = 'Thermo King TSA'
/
commit
/