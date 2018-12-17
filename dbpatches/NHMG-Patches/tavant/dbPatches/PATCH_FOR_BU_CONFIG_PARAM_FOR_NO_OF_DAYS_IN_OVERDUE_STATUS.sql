--Purpose    : Patch to create config param for Days pending in overdue status after which mail is send to dealer.
--Author     : PRACHER PANCHOLI	
--Created On : 15-JAN-2013

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
    'Days pending in overdue status after which mail is send to dealer',
    'Days pending in overdue status after which mail is send to dealer',
    'daysPendingInOverDueStatusForEmailTriggering ',
    'NUMBER',
    sysdate,
    'NMHG-Configuration',
    sysdate,
    NULL,
    systimestamp,
    systimestamp,
    'CLAIMS',
    'textbox',
    1,
    'CLAIM_RETURN_PART',
    1,1,1
  )
/
commit
/