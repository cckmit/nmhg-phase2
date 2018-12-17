-- Patch to insert into config_param_options_mapping to add Dealer Rental, Demo customer types for wntyConfigCustomerTypesAllowedinSearchResult
-- Author: PARTHASARATHY R
-- Created On : 06-NOV-2012


INSERT INTO CONFIG_PARAM_OPTION Values(110000000003240,'Dealer Rental','Dealer Rental')
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
    110000000006400,
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Dealer Rental'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'wntyConfigCustomerTypesAllowedinSearchResult'
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
    110000000006420,
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Demo'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'wntyConfigCustomerTypesAllowedinSearchResult'
    )
  )
/
COMMIT
/