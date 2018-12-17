--PURPOSE   :TO DISPLAY PDI LINKS IN WR
--AUTHOR    :SANTISWAROOP.K
--CREATED ON:21-SEPT-2012

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
    'Display PDI links',
    'Display PDI links',
    'INVENTORY',
    1,
    'displayPdiLinks',
    'radio',
    1,
    'INVENTORY_DR',
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
    WHERE cp.name='displayPdiLinks'
    ),
    (select id from config_param_option where value='true')
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
    WHERE cp.name='displayPdiLinks'
    ),
    (select id from config_param_option where value='false')
  )
/
commit
/