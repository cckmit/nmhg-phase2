-- Patch to insert into config param Show Dealer Job Number
-- Author: PRACHER PANCHOLI
-- Created On : 27-MAR-2013


INSERT
INTO config_param
  (
    id,
    description,
    display_name,
    logical_group,
    logical_group_order,
    name,
    param_display_type,
    param_order,
    sections,
    sections_order,
    type,
    d_active,
    d_created_on,
    d_created_time,
    d_internal_comments,
    d_last_updated_by,
    d_updated_on,
    d_updated_time
  )
  VALUES
  (
    config_param_seq.nextval,
    'Show Dealer Job Number',
    'Show Dealer Job Number',
    'CLAIMS',
    1,
    'showDealerJobNumber',
    'radio',
    1,
    'CLAIM_INPUT_PARAMETERS',
    1,
    'boolean',
    1,
    sysdate,
    systimestamp,
    'Nacco Configuration',
    1,
    sysdate,
    systimestamp
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
    WHERE cp.description='Show Dealer Job Number'
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
    WHERE cp.description='Show Dealer Job Number'
    ),
    (SELECT id from config_param_option cpo where cpo.value='false')
  )
/
commit
/