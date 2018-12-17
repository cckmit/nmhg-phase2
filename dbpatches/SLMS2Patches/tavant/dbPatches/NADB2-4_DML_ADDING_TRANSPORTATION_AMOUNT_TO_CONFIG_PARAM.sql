-- PURPOSE    : PATCH TO ADD Transportation Rate per Loaded Mile FOR CONFIG PARAM
-- AUTHOR     : Priyanka S.
-- CREATED ON : 9-DEC-2013


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
		'Transportation Rate per Loaded Mile',
		'Transportation Rate per Loaded Mile',
		'transportationRatePerLoadedMile',
		'number',
		'',
		'',
		'',
		56,
		'',
		'',
		1,
		'textbox',
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
		0,
		(SELECT id FROM config_param cp WHERE cp.description='Transportation Rate per Loaded Mile'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'AMER',
		null
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
		0,
		(SELECT id FROM config_param cp WHERE cp.description='Transportation Rate per Loaded Mile'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'EMEA',
		null
)
/
commit 
/