--Purpose    : Patch for SupplierRecovery(usecase1) As a Part of 4.3 Upgrade
--Author     : saya.sudha	
--Created On : 24-nov-2008
ALTER TABLE CONTRACT ADD RECOVERY_BASED_ON_CAUSAL_PART NUMBER(1,0)
/
update contract set recovery_based_on_causal_part = 1
/
COMMIT
/