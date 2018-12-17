--Patch to fix the claim HUS-1014315 which was partially deactivated(HUSS-772)
insert into claim_audit
select CLAIM_AUDIT_SEQ.nextval,'Deactivated : HUSS-772',0,'Deactivated : HUSS-772',
  replace(prev_claim_snapshot_string,'<state>TRANSFERRED</state>','<state>ON_HOLD</state>'),
  'DEACTIVATED',(updated_on +10),version,for_claim,updated_by,
  list_index+1,d_created_on,'Deactivated : HUSS-772',d_updated_on,d_last_updated_by,
  to_date('14-MAR-2011','DD-MON-YYYY'),decision,multi_claim_maintenance,payment
from claim_audit 
where for_claim=(select id from claim where claim_number='HUS-1014315_deactivated')
and previous_state='ON_HOLD'
/
update claim set state='DEACTIVATED',claim_number='HUS-1014315' 
where claim_number='HUS-1014315_deactivated'
/
COMMIT
/