--Purpose    : Patch for Policy to be shown for warranty registration
--Author     : manoj.katare	
--Created On : 2-apr-2009
alter table policy_definition
add IS_POLICY_APPLICABLE_FOR_WR number(1)
/
commit
/