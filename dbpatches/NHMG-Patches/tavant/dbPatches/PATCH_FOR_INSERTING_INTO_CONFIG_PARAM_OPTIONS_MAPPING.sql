-- Patch to insert into config_param_options_mapping to add Government Account customer type to customerTypesForModifiers and wntyConfigCustomerTypesAllowedinSearchResult
-- Author: PARTHASARATHY R
-- Created On : 30-OCT-2012

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
    (SELECT id FROM CONFIG_PARAM WHERE name = 'customerTypesForModifiers'
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
    (SELECT id FROM CONFIG_PARAM WHERE name = 'wntyConfigCustomerTypesAllowedinSearchResult'
    )
  )
/
COMMIT
/