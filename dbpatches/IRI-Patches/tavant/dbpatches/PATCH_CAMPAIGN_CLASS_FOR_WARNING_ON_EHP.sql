--patch for Field mod warning on EHP
--Vamshi.Gunda
DECLARE

v_config_param_id number;
v_config_option_1 number;

cursor codes_for_bu is
select business_unit_info,code from list_of_values where type='CAMPAIGNCLASS';

BEGIN
    select config_param_seq.nextval into v_config_param_id from dual;

    Insert into CONFIG_PARAM
    (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_INTERNAL_COMMENTS, PARAM_DISPLAY_TYPE, D_ACTIVE)
    values
    (v_config_param_id, 'Field Modification Campaign Classes For Warning',
    'Field Modification Campaign Classes For Warning', 'campaignClassForWarningOnEHP',
    'tavant.twms.domain.campaign.CampaignClass', 'IRI-MIGRATION', 'multiselect', 1);
	
	update config_param set logical_group='INVENTORY', 
	logical_group_order=1, sections='INVENTORY_SEARCH', 
	sections_order=1, param_order=1 where id =v_config_param_id;

END;
/
