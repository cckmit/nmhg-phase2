--Purpose    : Patch TO DEACTIVATE BUSINESS RULES
--Author     : HARI KRISHNA Y D
--Created On : 28-APR-2014

update domain_rule set d_active=0 where business_unit_info='AMER' AND predicate in (select id from domain_predicate where business_unit_info='AMER' and system_defined_condition_name in ('claimTravelDuplicacyCondition','jobCodeValidator','campaignCostCategoryValidator'))
/
update domain_predicate set d_active=0 where business_unit_info='AMER' AND system_defined_condition_name in ('claimTravelDuplicacyCondition','jobCodeValidator','campaignCostCategoryValidator')
/
commit
/