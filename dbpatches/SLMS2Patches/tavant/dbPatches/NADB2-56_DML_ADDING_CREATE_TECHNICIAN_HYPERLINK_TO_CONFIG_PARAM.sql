-- PURPOSE    : PATCH TO ADD CONFIG PARAM FOR Enable Create Technician Hyperlink
-- AUTHOR     : Priyanka S.
-- CREATED ON : 22-JAN-2014


INSERT 
INTO config_param 
(
		ID,
		DESCRIPTION,
		DISPLAY_NAME,NAME,
		TYPE,D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,
		D_UPDATED_TIME,
		D_ACTIVE,
		PARAM_DISPLAY_TYPE,
		LOGICAL_GROUP,
		LOGICAL_GROUP_ORDER,
		SECTIONS,
		SECTIONS_ORDER,
		PARAM_ORDER
) 
VALUES 
(
		CONFIG_PARAM_SEQ.nextval,
		'Enable Create Technician Hyperlink',
		'Enable Create Technician Hyperlink',
		'enableCreateTechnicianHyperlink',
		'boolean',
		'',
		'',
		'',
		56,
		'',
		'',
		1,
		'radio',
		'CLAIMS',
		1,
		'CLAIM_INPUT_PARAMETERS',
		1,
		1
)
/
INSERT INTO config_value 
(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
) 
VALUES 
(
		CONFIG_VALUE_SEQ.nextval,
		1,
		null,
		(SELECT id FROM config_param cp WHERE cp.name='enableCreateTechnicianHyperlink'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'AMER',
		(select id from config_param_option where value='false')
)
/
INSERT INTO config_value 
(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
) 
VALUES 
(
		CONFIG_VALUE_SEQ.nextval,
		1,
		null,
		(SELECT id FROM config_param cp WHERE cp.name='enableCreateTechnicianHyperlink'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'EMEA',
		(select id from config_param_option where value='true')
)
/
INSERT INTO config_param_options_mapping 
(
		ID,
		PARAM_ID,
		OPTION_ID) 
		VALUES 
		(CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
		(SELECT id FROM config_param cp WHERE cp.name='enableCreateTechnicianHyperlink'),
		(select id from config_param_option where value='true')
)
/
INSERT INTO config_param_options_mapping 
(
		ID,
		PARAM_ID,
		OPTION_ID) 
		VALUES 
		(CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
		(SELECT id FROM config_param cp WHERE cp.name='enableCreateTechnicianHyperlink'),
		(select id from config_param_option where value='false')
)
/
COMMIT
/