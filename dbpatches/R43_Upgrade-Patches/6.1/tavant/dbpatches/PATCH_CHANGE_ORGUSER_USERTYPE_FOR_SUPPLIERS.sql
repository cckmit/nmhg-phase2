--PURPOSE    : PATCH FOR CHANGING SUPPLIER'S LOGIN USERTYPE TO "SUPPLIER USER" IF USER TYPE IS NOT "SUPPLIER USER"
--AUTHOR     : DEVENDRA BABU N
--CREATED ON : 10-MAY-2011
--Impact     : ORG_USER

update org_user 
set user_type = 'SUPPLIER USER' 
where id in (select ou.id
    from role r, user_roles ur, org_user ou 
    where r.name = 'supplier'
    and r.id = ur.roles
    and ur.org_user = ou.id
    and ou.user_type <> 'SUPPLIER USER')
/
commit
/