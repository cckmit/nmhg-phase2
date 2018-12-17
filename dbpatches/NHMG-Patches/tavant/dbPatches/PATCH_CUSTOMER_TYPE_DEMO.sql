--Purpose    : Patch for adding the customer types for filing Warranty registration.
--Author     : Kuldeep Patil
--Created On : 05-Sep-2012

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
    'Demo',
    'Demo'
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
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Demo'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'customersFilingDR'
    )
  )
/
commit
/