-- Patch for Creating Config Param for Internal Install Type
-- Author		: ParthaSarathy R
-- Created On	: 18-DEC-2013

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
    'Display Internal Install Type in Delivery Report Page',
    'Display Internal Install Type',
    'INVENTORY',
    1,
    'displayInternalInstallType',
    'radio',
    1,
    'INVENTORY_DR_ETR',
    1,
    'boolean',
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
    WHERE cp.name='displayInternalInstallType'
    ),
    (SELECT id from config_param_option cpo where cpo.value='true')
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
    WHERE cp.name='displayInternalInstallType'
    ),
    (SELECT id from config_param_option cpo where cpo.value='false')
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='displayInternalInstallType'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='false'),
	1,
	NULL,
	'EMEA'
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='displayInternalInstallType'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='true'),
	1,
	NULL,
	'AMER'
  )
/
COMMIT
/