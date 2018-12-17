-- Patch to insert into config_param_options_mapping to add config param for Days pending in overdue status after which mail is send to dealer
-- Author: PRACHER PANCHOLI
-- Created On : 15-JAN-2013


UPDATE CONFIG_PARAM SET NAME='daysPendingInOverDueStatusForEmailTriggering' WHERE NAME='daysPendingInOverDueStatusForEmailTriggering '
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
    (SELECT id FROM CONFIG_PARAM WHERE name = 'daysPendingInOverDueStatusForEmailTriggering'
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
    (SELECT id FROM CONFIG_PARAM WHERE name = 'daysPendingInOverDueStatusForEmailTriggering' 
    )
  )
/
COMMIT
/