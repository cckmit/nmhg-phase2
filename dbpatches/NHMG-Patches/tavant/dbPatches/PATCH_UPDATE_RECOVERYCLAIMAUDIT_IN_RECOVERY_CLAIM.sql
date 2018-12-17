--Purpose    : Patch to update active_recovery_claim_audit in recoveryclaim table.
--Author     : Suneetha Nagaboyina
--Created On : 20-NOV-2012

update recovery_claim rc set rc.active_recovery_claim_audit = (select max(id) from rec_claim_audit rca where rca.for_recovery_claim = rc.id)
/
commit
/
