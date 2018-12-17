--Purpose    : Patch for SupplierRecovery(usecase1)
--Author     : saya.sudha	
--Created On : 24-nov-2008
ALTER TABLE CONTRACT ADD RECOVERY_BASED_ON_CAUSAL_PART NUMBER(1,0)
/
COMMIT
/
update contract set recovery_based_on_causal_part = 1 where recovery_based_on_causal_part is NULL
/
COMMIT
/