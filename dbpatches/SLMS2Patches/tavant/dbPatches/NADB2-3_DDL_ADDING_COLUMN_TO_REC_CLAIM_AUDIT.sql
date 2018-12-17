--Purpose    : Patch for adding the Customer Contact Title column in address table
--Author     : Priyanka S
--Created On : 27-NOV-2013

alter table rec_claim_audit add (EXTERNAL_COMMENTS varchar2(4000))
/