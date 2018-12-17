-- Patch for adding customer type Government Account
-- Author: PARTHASARATHY R
-- Created On : 30-OCT-2012

INSERT
INTO CONFIG_PARAM_OPTION
  (
    id,
    DISPLAY_VALUE,
    value
  )
  VALUES
  (
    CONFIG_PARAM_OPTION_SEQ.NEXTVAL,
    'GovernmentAccount',
    'GovernmentAccount'
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    id,
    OPTION_ID,
    PARAM_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'GovernmentAccount'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'customersFilingDR'
    )
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    id,
    OPTION_ID,
    PARAM_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'GovernmentAccount'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'customersFilingETR'
    )
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    id,
    OPTION_ID,
    PARAM_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'GovernmentAccount'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'customersFilingClaim'
    )
  )
/
INSERT
INTO CONFIG_PARAM_OPTIONS_MAPPING
  (
    id,
    OPTION_ID,
    PARAM_ID
  )
  VALUES
  (
    CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'GovernmentAccount'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'customersForPolicy'
    )
  )
/
COMMIT
/