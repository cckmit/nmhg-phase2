--PURPOSE    : PATCH FOR DELETING A ROLE FOR USER jgarcia
--AUTHOR     : ROHIT MEHROTRA
--CREATED ON : 21-MAY-11
--IMPACT     : DUE PARTS INSPECTION

delete from user_roles where org_user = (select id from org_user where login = 'jgarcia') and roles = (select id from role where name='inspector')
/
COMMIT
/