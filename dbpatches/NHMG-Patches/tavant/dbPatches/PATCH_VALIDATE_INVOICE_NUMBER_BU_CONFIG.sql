--PURPOSE   :TO VALIDATE INVOICE NUMBER
--AUTHOR    :JYOTI CHAUHAN
--CREATED ON:19-OCT-2012
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
    'Validate Invoice Number',
    'Validate Invoice Number',
    'CLAIMS',
    1,
    'validateInvoiceNumber',
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
    WHERE cp.description='Validate Invoice Number'
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
    WHERE cp.description='Validate Invoice Number'
    ),
    (SELECT id FROM config_param_option cpo WHERE cpo.value='false')
  )
/
CREATE OR REPLACE
PROCEDURE DEFAULTVALUESVALIDATEINVOICENO
IS
  CURSOR C1 IS SELECT name FROM business_unit;
  buName VARCHAR2(255);
  trueValueId NUMBER(19,0);
  configParamId NUMBER(19,0);
BEGIN
   SELECT id INTO trueValueId FROM config_param_option WHERE value='true';
   SELECT id INTO configParamId  FROM config_param cp WHERE cp.description='Validate Invoice Number';
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
        configParamId,
        sysdate,
        'Nacco Configuration',
        sysdate,
        1,
        systimestamp,
        systimestamp,
        1,
        buName,
        trueValueId
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
  DEFAULTVALUESVALIDATEINVOICENO();
END;
/
commit
/