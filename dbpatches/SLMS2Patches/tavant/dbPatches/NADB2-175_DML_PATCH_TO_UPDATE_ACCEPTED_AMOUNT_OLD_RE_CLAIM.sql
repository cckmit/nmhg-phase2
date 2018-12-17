-- Purpose    : Patch for updating rec_claim_audit accepted_cost_amt for older re-claims
-- Courtesy   : Pracher
-- Created On : 23-Jun-2014

DECLARE
    CURSOR accepted_amount is
   SELECT sum(cli.recovered_cost_amt) as SUM_ACCEPTED_AMT,rca.id,r.recovery_claim_number
    from rec_claim_audit rca , recovery_claim r, cost_line_item cli , rec_clm_cost_line_items rcc  
   where rca.for_recovery_claim=r.id and  rca.recovery_claim_state='DEBITTED_AND_CLOSED'
   and rca.accepted_cost_amt is null
   and rcc.recovery_claim=r.id and cli.id in (rcc.cost_line_items) and r.active_recovery_claim_audit=rca.id
   group by rca.id,r.recovery_claim_number;
BEGIN
     FOR i IN accepted_amount
  LOOP
      UPDATE rec_claim_audit  SET  accepted_cost_amt=i.SUM_ACCEPTED_AMT WHERE id=i.id;
      
  END LOOP;
  
END;
