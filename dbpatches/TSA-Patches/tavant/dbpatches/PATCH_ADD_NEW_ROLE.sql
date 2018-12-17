--Purpose    : ADDING NEW ROLE FOR INTERNAL USER CREATION
--Author     : Lavin Hawes
--Created On : 17/12/10
--Impact     : None

INSERT INTO ROLE (id, name, version, d_created_on, d_internal_comments, d_updated_on, d_last_updated_by,
d_created_time, d_updated_time, display_name, d_active, role_type)values((select max(id) from role)+1,'internalUserAdmin',0,sysdate,'Internal User Admin|Internal',null,null,null,
null,'Internal User Admin',1,'INTERNAL');
/
COMMIT
/


