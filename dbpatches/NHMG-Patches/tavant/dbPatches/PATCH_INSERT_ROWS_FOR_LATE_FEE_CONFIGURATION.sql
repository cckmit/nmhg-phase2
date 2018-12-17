--PURPOSE   :LATE FEE CONFIGURATION,ROWS TO BE INSERTED INTO CONFIG_PARAM AND CONFIG_PARAM_OPTIONS_MAPPING
--AUTHOR    :SANTISWAROOP.K
--CREATED ON:19-OCT-2012
DELETE
FROM config_param_options_mapping
WHERE param_id in  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
  )
/
DELETE
FROM config_param_options_mapping
WHERE param_id in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
  )
/
DELETE
FROM config_param_options_mapping
WHERE param_id in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 61-90 days'
  )
/
DELETE
FROM config_param_options_mapping
WHERE param_id in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 91-120 days'
  )
/
DELETE
FROM config_value
WHERE config_param in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
  )
/
DELETE
FROM config_value
WHERE config_param in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
  )
/
DELETE
FROM config_value
WHERE config_param in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 61-90 days'
  )
/
DELETE
FROM config_value
WHERE config_param in
  (SELECT id
  FROM config_param cp
  WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 91-120 days'
  )
/
DELETE
FROM config_param cp
WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
/
DELETE
FROM config_param cp
WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
/
DELETE
FROM config_param cp
WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 61-90 days'
/
DELETE
FROM config_param cp
WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 91-120 days'
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
    'Late fee for claims when filed later than repair date by 61-90 days',
    'Late fee for filing the claims later than repair date by 61-90 days :',
    'CLAIMS',
    1,
    'lateFeeSetupFor61-90Days',
    'radio',
    1,
    'CLAIM_INPUT_PARAMETERS',
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
    'Late fee percentage value for claims when filed later than repair date by 61-90 days',
    'Percentage deducted from total amount(%):',
    'CLAIMS',
    1,
    'lateFeeSetupPercentageValueFor61-90Days',
    'textbox',
    1,
    'CLAIM_INPUT_PARAMETERS',
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
    'Late fee for claims when filed later than repair date by 91-120 days',
    'Late fee for filing the claims later than repair date by 91-120 days:',
    'CLAIMS',
    1,
    'lateFeeSetupFor91-120Days',
    'radio',
    1,
    'CLAIM_INPUT_PARAMETERS',
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
    'Late fee percentage value for claims when filed later than repair date by 91-120 days',
    'Percentage deducted from total amount(%):',
    'CLAIMS',
    1,
    'lateFeeSetupPercentageValueFor91-120Days',
    'textbox',
    1,
    'CLAIM_INPUT_PARAMETERS',
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
    WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
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
    WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
    ),
    (SELECT id FROM config_param_option cpo WHERE cpo.value='false')
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
    WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
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
    WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
    ),
    (SELECT id FROM config_param_option cpo WHERE cpo.value='false')
  )
/
CREATE OR REPLACE
PROCEDURE DEFAULTVALUESFORLATEFEECHARGES
IS
  CURSOR C1 IS SELECT name FROM business_unit;
  buName VARCHAR2(255);
  falseValueId NUMBER(19,0);
  configParam1Id NUMBER(19,0);
  configParam2Id NUMBER(19,0);
  configParam3Id NUMBER(19,0);
  configParam4Id NUMBER(19,0);
BEGIN
   SELECT id INTO falseValueId FROM config_param_option WHERE value='false';
   SELECT id INTO configParam1Id  FROM config_param cp WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days';
   SELECT id INTO configParam2Id  FROM config_param cp WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days';
   SELECT id INTO configParam3Id  FROM config_param cp WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 61-90 days';
   SELECT id INTO configParam4Id  FROM config_param cp WHERE cp.description='Late fee percentage value for claims when filed later than repair date by 91-120 days';
  FOR businessUnitName IN C1 LOOP
    buName := businessUnitName.name;
    INSERT
    INTO CONFIG_VALUE
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
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        NULL,
        configParam1Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        falseValueId
      );
	INSERT
    INTO CONFIG_VALUE
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
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        NULL,
        configParam2Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        falseValueId
      );
    INSERT
    INTO CONFIG_VALUE
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
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        '15',
        configParam3Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        NULL
      );
    INSERT
    INTO CONFIG_VALUE
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
      VALUES
      (
        CONFIG_VALUE_SEQ.nextval,
        1,
        '30',
        configParam4Id,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        NULL
      );	  
  END LOOP;
  COMMIT;
  EXCEPTION
  WHEN OTHERS
  THEN
  Rollback;
END;
/
BEGIN
  DEFAULTVALUESFORLATEFEECHARGES();
END;
/
commit
/