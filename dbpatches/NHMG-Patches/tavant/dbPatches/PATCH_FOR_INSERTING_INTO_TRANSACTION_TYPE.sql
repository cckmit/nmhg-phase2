-- Patch to insert transaction type, values Dealer Rental and Demo
-- Author: PARTHASARATHY R
-- Created On : 06-NOV-2012


INSERT
INTO TRANSACTION_TYPE
  (
    TYPE,
    VERSION,
    ID,
	BUSINESS_UNIT_INFO
  )
  VALUES
  (
	'DealerRental',
	1,
    TRANSACTION_TYPE_SEQ.nextval,
    'NMHG EMEA'
  )
/
INSERT
INTO TRANSACTION_TYPE
  (
    TYPE,
    VERSION,
    ID,
	BUSINESS_UNIT_INFO
  )
  VALUES
  (
	'Demo',
	1,
    TRANSACTION_TYPE_SEQ.nextval,
    'NMHG EMEA'
  )
/
COMMIT
/