-- Patch to insert into config_param_options_mapping to add Dealer Rental in Customer Types Displayed in Policy
-- Author: RAGHAVENDRA
-- Created On : 04-SEP-2012

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
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Dealer Rental'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE display_name = 'Customer Types Displayed in Policy'
    )
  )
/
COMMIT
/