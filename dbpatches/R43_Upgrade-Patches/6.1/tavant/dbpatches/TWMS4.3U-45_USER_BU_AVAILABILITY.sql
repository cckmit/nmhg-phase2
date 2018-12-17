--Data fix for TWMS4.3U-45
insert into user_bu_availability 
(id,org_user,role,business_unit_info,available,default_to_role)
select User_BU_Availability_SEQ.nextval,u.id,r.id,bum.bu,0,0
from org_user u,user_roles ur,role r,bu_user_mapping bum
where u.id=ur.org_user and ur.roles=r.id and u.id=bum.org_user and u.d_active=1 
  and r.name in ('processor','dsm','dsmAdvisor','recoveryProcessor','cpAdvisor')
  and (select count(*) from user_bu_availability t 
    where t.org_user=u.id and t.role=r.id and t.business_unit_info=bum.bu)=0
/
commit
/