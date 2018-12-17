--Patch for configuring customer types to be shown in modifiers list
-- Author Prashanth Konda
-- monday Nov 24th 2008

INSERT INTO CONFIG_PARAM(ID,   description,   display_name,   NAME,   TYPE, param_display_type,   d_created_on,   d_internal_comments,   d_updated_on,   d_last_updated_by,   d_created_time,   d_updated_time,   LOGICAL_GROUP)
VALUES(config_param_seq.NEXTVAL,   'List the customer types displayed in the drop down menu on Modifiers page.',   'Customer Types Displayed In Modifiers',   'customerTypesForModifiers',   'java.lang.String','multiselect',   SYSDATE,   'Configuration',   SYSDATE,   NULL,   NULL,   NULL,   NULL)
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customerTypesForModifiers' and cpo.value = 'DirectCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customerTypesForModifiers' and cpo.value = 'EndCustomer')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customerTypesForModifiers' and cpo.value = 'Dealer')
/ 
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customerTypesForModifiers' and cpo.value = 'InterCompany')
/
insert into config_param_options_mapping ( select CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL, cp.id, cpo.id from config_param cp, config_param_option cpo where cp.name = 'customerTypesForModifiers' and cpo.value = 'NationalAccount')
/
commit
/