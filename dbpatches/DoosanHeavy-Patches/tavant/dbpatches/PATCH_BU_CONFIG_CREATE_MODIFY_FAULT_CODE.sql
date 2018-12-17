--Purpose    : Patch for config param to allow the warranty admin to Create/Modify Fault code and Fault found and caused by.
--Author     : Kuldeep Patil	
--Created On : 24-July-2012

INSERT
INTO CONFIG_PARAM
  (
    ID,
    DESCRIPTION,
    DISPLAY_NAME,
    NAME,
    TYPE,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    LOGICAL_GROUP,
    PARAM_DISPLAY_TYPE,
    LOGICAL_GROUP_ORDER,
    SECTIONS,
    SECTIONS_ORDER,
    PARAM_ORDER,
    d_active
  )
  VALUES
  (
    config_param_seq.NEXTVAL,
    'Allow the warranty admin to Create/Modify Fault code',
    'Allow the warranty admin to Create/Modify Fault code',
    'allowCreateModifyFaultCode',
    'boolean',
    sysdate,
    'Doosan-Heavy-Configuration',
    sysdate,
    NULL,
    systimestamp,
    systimestamp,
    'CLAIMS',
    'radio',
    1,
    'CLAIM_INPUT_PARAMETERS',
    1,1,1
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    ID,
    PARAM_ID,
    OPTION_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
    (SELECT id FROM config_param WHERE name='allowCreateModifyFaultCode'
    ),
    (SELECT id FROM config_param_option WHERE value='true'
    )
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    ID,
    PARAM_ID,
    OPTION_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
    (SELECT id FROM config_param WHERE name='allowCreateModifyFaultCode'
    ),
    (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE='false'
    )
  )
/
INSERT
INTO CONFIG_PARAM
  (
    ID,
    DESCRIPTION,
    DISPLAY_NAME,
    NAME,
    TYPE,
    D_CREATED_ON,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_LAST_UPDATED_BY,
    D_CREATED_TIME,
    D_UPDATED_TIME,
    LOGICAL_GROUP,
    PARAM_DISPLAY_TYPE,
    LOGICAL_GROUP_ORDER,
    SECTIONS,
    SECTIONS_ORDER,
    PARAM_ORDER,
    d_active
  )
  VALUES
  (
    config_param_seq.NEXTVAL,
    'Allow the warranty admin to Create/Modify Fault found/Caused by',
    'Allow the warranty admin to Create/Modify Fault found/Caused by',
    'allowCreateModifyFaultFoundCausedBy',
    'boolean',
    sysdate,
    'Doosan-Heavy-Configuration',
    sysdate,
    NULL,
    systimestamp,
    systimestamp,
    'CLAIMS',
    'radio',
    1,
    'CLAIM_INPUT_PARAMETERS',
    1,1,1
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    ID,
    PARAM_ID,
    OPTION_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
    (SELECT id FROM config_param WHERE name='allowCreateModifyFaultFoundCausedBy'
    ),
    (SELECT id FROM config_param_option WHERE value='true'
    )
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    ID,
    PARAM_ID,
    OPTION_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
    (SELECT id FROM config_param WHERE name='allowCreateModifyFaultFoundCausedBy'
    ),
    (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE='false'
    )
  )
/
commit
/