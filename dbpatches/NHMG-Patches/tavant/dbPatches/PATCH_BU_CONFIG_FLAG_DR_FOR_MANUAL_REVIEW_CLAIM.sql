--Purpose    : Patch to create config param Flag for Manual Review which will allow system to mark every xth claim for manual review.
--Author     : ROHIT MEHROTRA	
--Created On : 22-MARCH-2012

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
    'Every X Claim should go for manual review',
    'Flag for Claim Manual Review ',
    'flagForManualReviewClaim',
    'NUMBER',
    sysdate,
    'NMHG-Configuration',
    sysdate,
    NULL,
    systimestamp,
    systimestamp,
    'CLAIMS',
    'textbox',
    1,
    'CLAIM_PROCESS',
    1,1,1
  )
/
commit
/