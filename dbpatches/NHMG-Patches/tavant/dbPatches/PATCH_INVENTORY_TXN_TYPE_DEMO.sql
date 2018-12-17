--PURPOSE    : Patch for adding demo inventory transaction type
--AUTHOR     : Kuldeep Patil
--CREATED ON : 13-Sep-2012

INSERT
INTO INVENTORY_TRANSACTION_TYPE
  (
    id,
    TRNX_TYPE_VALUE,
    TRNX_TYPE_KEY,
    version,
    D_CREATED_ON,
    D_CREATED_TIME,
    D_INTERNAL_COMMENTS,
    D_UPDATED_ON,
    D_UPDATED_TIME,
    D_ACTIVE,
    D_LAST_UPDATED_BY
  )
  VALUES
  (
    INV_TXN_TYPE_SEQ.nextval,
    'DEMO',
    'DEMO',
    1,
    sysdate,
    systimestamp,
    'NMHG-Configuration',
    sysdate,
    systimestamp,
    1,
    1
  )
/
commit
/