-- Patch to insert into config value
-- Author: DEEPAK PATEL
-- Created On : 24-JAN-2014

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
    'Default Due days For Part Return',
    'Default Due days For Part Return',
    'CLAIMS',
    1,
    'defaultDueDaysForPartReturn',
    'textbox',
    1,
    'CLAIM_RETURN_PART',
    1,
    'number',
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='defaultDueDaysForPartReturn'),
	20,
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='defaultDueDaysForPartReturn'),
	20,
	1,
	NULL,
	'AMER'
  )
/
COMMIT
/