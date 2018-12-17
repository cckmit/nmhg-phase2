--PURPOSE    : Patch to create and insert value for config param supplierRecoveryContractValueConfiguration
--AUTHOR     : Chetan
--CREATED ON : 14-FEB-2014

INSERT
INTO config_param
(
    id,
    description,
    display_name,
    logical_group,
    logical_group_order,
    name,
    param_display_type,
    param_order,
    sections,
    sections_order,
    type,
    d_active,
    d_created_on,
    d_created_time,
    d_internal_comments,
    d_last_updated_by,
    d_updated_on,
    d_updated_time
)
VALUES
(
    config_param_seq.nextval,
    'Supplier Recovery Contract Value Configuration',
    'Supplier Recovery Contract Value Configuration',
    'SUPPLIER_RECOVERY',
    1,
    'supplierRecoveryContractValueConfiguration',
    'select',
    1,
    null,
    1,
    'java.lang.String',
    1,
    sysdate,
    systimestamp,
    'Nacco Configuration',
    1,
    sysdate,
    systimestamp
)
/
INSERT
INTO config_param_options_mapping
(
    id,
    param_id,
    option_id
)
VALUES
(
    cfg_param_optns_mapping_seq.nextval,
    (SELECT id
    FROM config_param cp
    WHERE cp.description='Supplier Recovery Contract Value Configuration'
    ),
    (SELECT id from config_param_option cpo where cpo.value='MaterialCost')
)
/
INSERT
INTO config_param_options_mapping
(
    id,
    param_id,
    option_id
)
VALUES
(
    cfg_param_optns_mapping_seq.nextval,
    (SELECT id
    FROM config_param cp
    WHERE cp.description='Supplier Recovery Contract Value Configuration'
    ),
    (SELECT id from config_param_option cpo where cpo.value='StandardCost')
)
/
INSERT
INTO CONFIG_VALUE
(
    ID,
	ACTIVE,
	CONFIG_PARAM,
	CONFIG_PARAM_OPTION,
	D_ACTIVE,
	D_LAST_UPDATED_BY,
	BUSINESS_UNIT_INFO
)
VALUES
(
	CONFIG_VALUE_SEQ.nextval,
	1,
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='supplierRecoveryContractValueConfiguration'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='MaterialCost'),
	1,
	NULL,
	'AMER'
)
/
INSERT
INTO CONFIG_VALUE
(
    ID,
	ACTIVE,
	CONFIG_PARAM,
	CONFIG_PARAM_OPTION,
	D_ACTIVE,
	D_LAST_UPDATED_BY,
	BUSINESS_UNIT_INFO
)
VALUES
(
	CONFIG_VALUE_SEQ.nextval,
	1,
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='supplierRecoveryContractValueConfiguration'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='StandardCost'),
	1,
	NULL,
	'EMEA'
)
/
COMMIT
/