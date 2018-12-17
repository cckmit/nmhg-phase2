--Purpose    : Patch for adding the BU config to auto delete this claim from "DSM replies inbox" if the specified days passed.
--Author     : Kuldeep Patil	
--Created On : 14-Aug-2012

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
    'DSM replied claims to be deleted if not responded within specified days',
    'DSM replied claims to be deleted if not responded within specified days',
    'daysForDSMRepliesClaims',
    'number',
    sysdate,
    'Doosan-TWMS-Configuration',
    sysdate,
    NULL,
    NULL,
    NULL,
    'CLAIMS',
    'textbox',
    1,
    'CLAIM_SUBMISSION',
    1,1,1
  )
/
commit
/