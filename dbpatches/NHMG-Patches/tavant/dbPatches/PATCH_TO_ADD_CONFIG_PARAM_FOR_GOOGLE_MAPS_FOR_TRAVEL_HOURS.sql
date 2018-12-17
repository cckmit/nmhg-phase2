--Purpose    : Patch to create config param for Days pending in overdue status after which mail is send to dealer.
--Author     : ParthaSarathy R	
--Created On : 07-Mar-2013

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
    'Enable Google Maps to calculate Travel Hours, Travel Location, Travel Distance',
    'Enable Goodle Maps for Travel Hours',
    'googleMapsForTravelHours',
    'boolean',
    sysdate,
    'NMHG-Configuration',
    sysdate,
    NULL,
    systimestamp,
    systimestamp,
    'CLAIMS',
    'radio',
    1,
    'CLAIM_INPUT_PARAMETERS',
    1,
	1,
	1
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
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'true'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'googleMapsForTravelHours'
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
    (SELECT id FROM CONFIG_PARAM_OPTION WHERE VALUE = 'false'
    ),
    (SELECT id FROM CONFIG_PARAM WHERE name = 'googleMapsForTravelHours' 
    )
  )
/
commit
/