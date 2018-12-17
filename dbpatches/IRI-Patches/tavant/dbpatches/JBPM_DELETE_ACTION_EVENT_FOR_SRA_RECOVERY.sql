--Purpose    : To delete Action and Event records from JBPM_ACTION and JBPM_EVENT table to avoid duplicate audits in "IN RECOVERY" state
--Author     : Jitesh Jain
--Created On : 10-Mar-09

CREATE OR REPLACE PROCEDURE DELETE_JBPM_SRA_RECOVERY
AS
CURSOR all_rec IS
select ja.id_ as action, je.id_ as event from jbpm_action ja,jbpm_event je, jbpm_transition jt, jbpm_node jn1, jbpm_node jn2
where ja.EVENT_ = je.id_
and je.TRANSITION_ = jt.id_
and jt.NAME_='Send To Supplier'
and jt.FROM_ = jn1.ID_
and jn1.NAME_='Supplier Recovery Admin Reject'
and jt.TO_ = jn2.ID_
and jn2.NAME_ = 'SupplierRecoveryAdminReviewFork';

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
DELETE_JBPM_SRA_RECOVERY();
END;
/
COMMIT
/