--PURPOSE: TO CREATE CREATE TRAVEL COST CATEGORY
--AUTHOR     : AJIT
--CREATED ON : 14-Jan-2014

Insert into COST_CATEGORY
	(
	ID,
	CODE,
	DESCRIPTION,
	NAME,
	VERSION,
	D_CREATED_ON,
	D_CREATED_TIME,
	D_INTERNAL_COMMENTS,
	D_UPDATED_ON,
	D_UPDATED_TIME,
	D_LAST_UPDATED_BY,
	D_ACTIVE
	)
	values
	(
	COST_CATEGORY_SEQ.nextval,
	'TRAVEL',
	'Travel',
	'Travel',
	1,
	null,
	null,
	null,
	null,
	null,
	null,
	1
	)
/
INSERT INTO section 
	(
	id, 
	display_position,
	name, 
	version,
	d_active, 
	message_key
	)
	VALUES
	(
	section_seq.nextval,
	20, 
	'Travel',
	1,
	1, 
	'label.section.travel'
	)
/
Insert into config_value 
	(
	ID,
	ACTIVE,
	VALUE,
	CONFIG_PARAM,
	D_CREATED_ON,
	D_INTERNAL_COMMENTS,
	D_UPDATED_ON,
	D_LAST_UPDATED_BY,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE,
	BUSINESS_UNIT_INFO,
	CONFIG_PARAM_OPTION
	) 
	values 
	(
	CONFIG_VALUE_SEQ.nextval,
	1,
	'20',
	(SELECT id FROM config_param cp WHERE cp.description='Configured Cost Categories'),
	null,
	null,
	null,
	null,
	null,
	null,
	1,
	'AMER',
	null
	)
/