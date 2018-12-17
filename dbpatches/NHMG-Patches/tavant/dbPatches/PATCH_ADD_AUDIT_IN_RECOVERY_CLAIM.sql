--Purpose    : Patch for adding the column ACTIVE_CLAIM_AUDIT in recovery claim table.
--Author     : Suneetha Nagaboyina
--Created On : 17-Oct-2012

alter table recovery_claim add ("ACTIVE_RECOVERY_CLAIM_AUDIT" NUMBER(19,0))
/