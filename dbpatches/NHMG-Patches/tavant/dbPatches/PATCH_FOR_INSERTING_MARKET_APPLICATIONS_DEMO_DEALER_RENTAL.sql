-- Patch to insert market type, market application values Dealer Rental and Demo
-- Author: PARTHASARATHY R
-- Created On : 07-NOV-2012

INSERT INTO BUSINESS_UNIT 
(
NAME,
DESCRIPTION,
DISPLAY_NAME
)
values ('NMHG US','NMHG US','NMHG US')
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
	'DRUS',
	'NMHG US',
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
	'DMUS',
	'NMHG US',
	'Demo',
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
	'Market Application',
    MARKET_SEQ.nextval,
	0,
	'DRA',
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
	'Market Application',
    MARKET_SEQ.nextval,
	0,
	'DMA',
	'NMHG EMEA',
	'Demo',
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
	'Market Application',
    MARKET_SEQ.nextval,
	0,
	'DRAUS',
	'NMHG US',
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
	'Market Application',
    MARKET_SEQ.nextval,
	0,
	'DMAUS',
	'NMHG US',
	'Demo',
	1
  )
/
COMMIT
/