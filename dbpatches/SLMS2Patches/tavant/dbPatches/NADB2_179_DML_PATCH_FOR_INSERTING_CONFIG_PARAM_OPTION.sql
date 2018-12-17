-- Patch to insert config value
-- Author : ParthaSarathy R
-- Created On : 05-July-2014

DECLARE
  v_id number;
BEGIN
	select id into v_id from config_param_option where value = 'Do Not Take Action';
	
	EXCEPTION WHEN NO_DATA_FOUND THEN
		insert into config_param_option(
			id,
			display_value,
			value
		)
		values
		(
			CONFIG_PARAM_OPTION_SEQ.nextval,
			'Do Not Take Action',
			'Do Not Take Action'
		);
	
		insert into config_param_options_mapping
		(
			id,
			param_id,
			option_id
		)
		values
		(
			CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
			(select id from config_param where name='actionForPartsShippedNotReceived'),
			(select id from config_param_option where value='Do Not Take Action')
		);
    commit;
END;
/