--PURPOSE    : patch to add constraint for claim notification table
--AUTHOR     : Raghu
--CREATED ON : 10-FEB-2014

alter table claim_audit_notifications add CONSTRAINT "CLAIMAUDNOTIF_CLAIMAUD_FK" FOREIGN KEY ("CLAIM_AUDIT")
REFERENCES "CLAIM_AUDIT" ("ID") ENABLE
/