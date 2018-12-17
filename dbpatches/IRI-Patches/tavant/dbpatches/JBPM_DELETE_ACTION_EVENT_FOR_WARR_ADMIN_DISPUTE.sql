--Purpose    : To delete Action and Event records from JBPM_ACTION and JBPM_EVENT table to avoid duplicate audits in "DISPUTED" state
--Author     : Jitesh Jain
--Created On : 10-Mar-09

create or replace PROCEDURE DELETE_JBPM_ADMIN_DISPUTE
AS
CURSOR all_rec IS
select je.id_ as event, ja.id_ as action from jbpm_action ja, jbpm_event je, jbpm_transition jt
where ja.event_ = je.id_
and je.transition_ = jt.id_
and jt.name_ = 'toWarrantyAdminDispute';

BEGIN

  FOR each_rec IN all_rec
  LOOP
    delete from jbpm_event where id_  = each_rec.event;

    delete from jbpm_action where id_ = each_rec.action;

 commit;
  END LOOP;
END;
/
BEGIN
DELETE_JBPM_ADMIN_DISPUTE();
END;
/
COMMIT
/