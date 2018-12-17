-- Patch to Remove Maintenance Contracts that are not needed for AMER
-- Author		: ParthaSarathy R
-- Created On	: 23-Dec-2013

delete from i18nmaintenance_contract_text where i18n_maintenance_contract in
(
select id from maintenance_contract where maintenance_contract <> 'Full Service Contract' and business_unit_info = 'AMER'
)
/
delete from maintenance_contract where maintenance_contract <> 'Full Service Contract' and business_unit_info = 'AMER'
/
COMMIT
/