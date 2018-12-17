--PURPOSE    : Delete rule number 15 from failed rule for claim with auth number 'null', and update auth number to null if auth number is 'null'.
--AUTHOR     : Chetan K
--CREATED ON : 04-SEP-2014
DECLARE 
  CURSOR c1 
  IS 
select rf.id as rule_failure_id,fr.rule_detail as failed_rule_id 
from claim c, claim_audit ca, claim_audit_rule_failures carf, rule_failure rf, failed_rule fr 
where (c.active_claim_audit = ca.id or c.id=ca.for_claim) and c.business_unit_info = 'AMER' 
and carf.claim_audit=ca.id and carf.rule_failures=rf.id and rf.id=fr.rule_detail 
and c.auth_number is not null and c.auth_number = 'null' and fr.rule_number='15';
BEGIN 
  FOR each_rec IN c1 
  LOOP 
delete from failed_rule where rule_detail=each_rec.failed_rule_id and rule_number='15';
  END LOOP;
END;
/
update claim set auth_number=null where auth_number is not null and auth_number = 'null'
/
commit
/