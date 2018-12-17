--Purpose    : Patch for adding credit date to claim_audit
--Author     : Nandakumar Devi
--Created On : 06-June-2013

alter table claim_audit add (credit_date date)
/