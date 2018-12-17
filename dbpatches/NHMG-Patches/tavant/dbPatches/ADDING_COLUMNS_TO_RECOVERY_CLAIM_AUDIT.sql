--Purpose    : Patch for Adding accepted_cost_curr and  accepted_cost_amt IN RECOVERY_CLAIM_AUDIT TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 11-JUNE-2013

alter table
   rec_claim_audit
add
   (
   accepted_cost_curr  VARCHAR2(255 CHAR),  
   accepted_cost_amt NUMBER(19,2)
  
   )
/

