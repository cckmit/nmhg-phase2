--PURPOSE    : PATCH_TO_DELETE_MANAGE_ROLES_OPTION_FOR_DEALER
--AUTHOR     : Sumesh kumar.R
--CREATED ON : 09-APR-2014
delete from ROLE_PERMISSION_MAPPING where subject_area in(select id from MST_ADMIN_SUBJECT_AREA where name='settings') and role_def_id in(select id from role where name in('dealerSalesAdministration','dealerWarrantyAdmin')) and functional_area in(select id from MST_ADMIN_FNC_AREA where name='settingsManageRoles')
/
commit
/