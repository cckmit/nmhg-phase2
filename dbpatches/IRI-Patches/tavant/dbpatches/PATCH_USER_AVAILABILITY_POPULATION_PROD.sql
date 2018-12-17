--Purpose    : Populate availability to all the users in the system with Default to available as "Yes" (9 secs in Dev DB to complete this patch)
--Author     : Jhulfikar Ali. A
--Created On : 09-Feb-09

DECLARE
CURSOR ALL_ACTIVE_USERS 
IS
select distinct (ou.id), bum.bu, ro.id user_role
from org_user ou, bu_user_mapping bum, role ro, user_roles ur
where 
bum.org_user = ou.id and ur.org_user = ou.id and 
ro.id = ur.roles and ou.d_active = 1 order by ou.id;
BEGIN
FOR EACH_USER IN ALL_ACTIVE_USERS 
LOOP
  INSERT INTO user_bu_availability (ID,ORG_USER, role, business_unit_info, available, default_to_role) 
  values (
  USER_BU_AVAILABILITY_SEQ.nextval, EACH_USER.id, EACH_USER.user_role, EACH_USER.BU, 1, 0);
END LOOP;
END;
/
COMMIT
/