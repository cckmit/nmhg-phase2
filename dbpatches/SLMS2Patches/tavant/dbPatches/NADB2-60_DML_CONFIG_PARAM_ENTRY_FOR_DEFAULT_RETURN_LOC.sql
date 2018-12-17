--Db patch for default return location
-- Author Deepak 
--date 28th Jan 2014

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
    'Default Part Return Location',
    'Default Part Return Location',
    'SUPPLIER_RECOVERY',
    1,
    'defaultReturnLocationCode',
    'textbox',
    1,
    'null',
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='defaultReturnLocationCode'),
	'501037-GREENVILLE',
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
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='defaultReturnLocationCode'),
	'501037-GREENVILLE',
	1,
	NULL,
	'AMER'
)
/
COMMIT
/