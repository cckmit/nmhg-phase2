-- Patch for Creating Config Param for Vintage Stock
-- Author		: ParthaSarathy R
-- Created On	: 10-DEC-2013

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
    'Display Vintage Stock Inbox',
    'Display Vintage Stock Inbox',
    'INVENTORY',
    1,
    'displayVintageStock',
    'radio',
    1,
    'INVENTORY_SEARCH',
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
    WHERE cp.name='displayVintageStock'
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
    WHERE cp.name='displayVintageStock'
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='displayVintageStock'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='true'),
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='displayVintageStock'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='false'),
	1,
	NULL,
	'AMER'
  )
/
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
    'Move Inventory Items to Vintage Stock Inbox which are Shipped before the date(Format : DD/MM/YYYY). For Example 1st May, 2013 can be provided in the format as 01/05/2013',
    'Move Inventory Items to Vintage Stock Inbox which are Shipped before the date(Format : DD/MM/YYYY)',
    'INVENTORY',
    1,
    'shipmentDateForVintageStock',
    'textbox',
    1,
    'INVENTORY_SEARCH',
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
INTO CONFIG_VALUE
  (
    ID,
	ACTIVE,
	CONFIG_PARAM,
	VALUE,
	D_ACTIVE,
	D_LAST_UPDATED_BY,
	BUSINESS_UNIT_INFO
  )
  VALUES
  (
	CONFIG_VALUE_SEQ.nextval,
	1,
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='shipmentDateForVintageStock'),
	'01/01/2012',
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
	VALUE,
	D_ACTIVE,
	D_LAST_UPDATED_BY,
	BUSINESS_UNIT_INFO
  )
  VALUES
  (
	CONFIG_VALUE_SEQ.nextval,
	1,
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='shipmentDateForVintageStock'),
	'01/01/2012',
	1,
	NULL,
	'AMER'
  )
/
COMMIT
/