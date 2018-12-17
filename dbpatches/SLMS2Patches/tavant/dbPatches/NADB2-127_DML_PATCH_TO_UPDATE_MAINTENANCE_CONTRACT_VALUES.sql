-- Patch to Remove Maintenance Contracts that are not needed for AMER
-- Author		: Hari Krishna Y D
-- Created On	: 18-Apr-2014

update maintenance_contract set maintenance_contract='NA' where business_unit_info='AMER' AND maintenance_contract='Full Service Contract'
/
update i18nmaintenance_contract_text set maintenance_contract='NA' where maintenance_contract='Full Service Contract'
/
COMMIT
/