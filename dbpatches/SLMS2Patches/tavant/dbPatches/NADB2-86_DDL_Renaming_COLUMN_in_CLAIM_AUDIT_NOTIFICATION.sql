--Purpose    : Patch TO Rename column in claim_audit_notifications  TABLE
--Author     : RAGHU
--Created On : 07-MAR-2014

alter table claim_audit_notifications rename column NOTIFICATION to notifications
/