--Purpose    : INSERT SCRIPTS FOR VALIDATING STANDARD WARRANTY ON PARTS CLAIMS
--Author     : Arpitha Nadig AR
--Created On : 24-MAY-2014
INSERT
INTO config_param
  (
    id,
    description,
    display_name,
    name,
    type,
    d_created_on,
    d_internal_comments,
    d_updated_on,
    d_last_updated_by,
    d_created_time,
    d_updated_time,
    d_active,
    param_display_type,
    logical_group,
    logical_group_order,
    sections,
    sections_order,
    param_order
  )
  VALUES
  (
    CONFIG_PARAM_SEQ.nextval,
    'Validate Standard Warranty on Parts Claims',
    'Validate Standard Warranty on Parts Claims',
    'validateStdWarranty',
    'boolean',
    sysdate,
    NULL,
    sysdate,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1,
    'radio',
    'CLAIMS',
    '1',
    'CLAIM_INPUT_PARAMETERS',
    1,1
  )
/
INSERT
INTO config_value
  (
    id,
    active,
    value,
    config_param,
    d_created_on,
    d_internal_comments,
    d_updated_on,
    d_last_updated_by,
    d_created_time,
    d_updated_time,
    d_active,
    business_unit_info,
    config_param_option
  )
  VALUES
  (
    CONFIG_VALUE_SEQ.nextval,
    1,
    NULL,
    (SELECT id
    FROM config_param
    WHERE description='Validate Standard Warranty on Parts Claims'
    ),
    sysdate,
    NULL,
    sysdate,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1,
    'EMEA',
    12
  )
/
INSERT
INTO config_value
  (
    id,
    active,
    value,
    config_param,
    d_created_on,
    d_internal_comments,
    d_updated_on,
    d_last_updated_by,
    d_created_time,
    d_updated_time,
    d_active,
    business_unit_info,
    config_param_option
  )
  VALUES
  (
    CONFIG_VALUE_SEQ.nextval,
    1,
    NULL,
    (SELECT id
    FROM config_param
    WHERE description='Validate Standard Warranty on Parts Claims'
    ),
    sysdate,
    NULL,
    sysdate,
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    1,
    'AMER',
    11
  )
/
INSERT
INTO config_param_options_mapping
  (
    id,
    option_id,
    param_id
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
    11,
    (SELECT id
    FROM config_param
    WHERE description='Validate Standard Warranty on Parts Claims'
    )
  )
/
INSERT
INTO config_param_options_mapping
  (
    id,
    option_id,
    param_id
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
    12,
    (SELECT id
    FROM config_param
    WHERE description='Validate Standard Warranty on Parts Claims'
    )
  )
/
COMMIT
/