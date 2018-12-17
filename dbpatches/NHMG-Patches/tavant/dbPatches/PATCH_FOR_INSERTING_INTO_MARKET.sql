-- Patch to insert market type, values Dealer Rental and Demo
-- Author: PARTHASARATHY R
-- Created On : 06-NOV-2012

INSERT
INTO MARKET
  (
    TYPE,
    ID,
	VERSION,
	CODE,
	BUSINESS_UNIT_INFO,
	TITLE,
	D_ACTIVE
  )
  VALUES
  (
	'Market Type',
    MARKET_SEQ.nextval,
	0,
	'DR',
	'NMHG EMEA',
	'Rental',
	1
  )
/
INSERT
INTO MARKET
  (
    TYPE,
    ID,
	VERSION,
	CODE,
	BUSINESS_UNIT_INFO,
	TITLE,
	D_ACTIVE
  )
  VALUES
  (
	'Market Type',
    MARKET_SEQ.nextval,
	0,
	'DM',
	'NMHG EMEA',
	'Demo',
	1
  )
/
COMMIT
/