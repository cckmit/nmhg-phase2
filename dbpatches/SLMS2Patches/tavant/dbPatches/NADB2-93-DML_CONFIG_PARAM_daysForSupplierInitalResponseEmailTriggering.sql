--PURPOSE    : Patch to create config param daysForSupplierInitalResponseEmailTriggering
--AUTHOR     : Raghu
--CREATED ON : 17-FEB-2014

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
    'Number of Days before the end of the initial response period ',
    'Number of Days before the end of the initial response period',
    'SUPPLIER_RECOVERY',
    1,
    'daysForSupplierInitalResponseEmailTriggering',
    'textbox',
    1,
    'null',
    1,
    'number',
    1,
    sysdate,
    systimestamp,
    'Nacco Configuration',
    1,
    sysdate,
    systimestamp
  )
/
COMMIT
/