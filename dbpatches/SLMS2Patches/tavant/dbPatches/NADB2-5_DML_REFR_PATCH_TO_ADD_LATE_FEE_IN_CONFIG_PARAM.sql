--Purpose    : Patch for adding Late Fee in config_param table as part of Business Unit Configuration
--Author     : Arpitha Nadig AR
--Created On : 13-DEC-2013
INSERT
INTO config_param_options_mapping
  (
    id,
    param_id,
    option_id
  )
  VALUES
  (
    cfg_param_optns_mapping_seq.nextval,
    (SELECT id
    FROM config_param cp
    WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
    ),
    (SELECT id from config_param_option cpo where cpo.value='true')
  )
/
INSERT
INTO config_param_options_mapping
  (
    id,
    param_id,
    option_id
  )
  VALUES
  (
    cfg_param_optns_mapping_seq.nextval,
    (SELECT id
    FROM config_param cp
    WHERE cp.description='Late fee for claims when filed later than repair date by 61-90 days'
    ),
    (SELECT id FROM config_param_option cpo WHERE cpo.value='false')
  )
/
INSERT
INTO config_param_options_mapping
  (
    id,
    param_id,
    option_id
  )
  VALUES
  (
    cfg_param_optns_mapping_seq.nextval,
    (SELECT id
    FROM config_param cp
    WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
    ),
    (SELECT id from config_param_option cpo where cpo.value='true')
  )
/
INSERT
INTO config_param_options_mapping
  (
    id,
    param_id,
    option_id
  )
  VALUES
  (
    cfg_param_optns_mapping_seq.nextval,
    (SELECT id
    FROM config_param cp
    WHERE cp.description='Late fee for claims when filed later than repair date by 91-120 days'
    ),
    (SELECT id FROM config_param_option cpo WHERE cpo.value='false')
  )
/