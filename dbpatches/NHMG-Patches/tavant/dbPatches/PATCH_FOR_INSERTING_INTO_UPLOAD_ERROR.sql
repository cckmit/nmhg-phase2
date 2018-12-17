-- Patch to Insert and Update values in UPLOAD_ERROR, UPLOAD_MGT
-- Author: Vamsi Krishna Jolla
-- Created On : 14-MAR-2013

INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC108',
	'REPAIR START DATE'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC109',
	'REPAIR END DATE'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC110',
	'HOURS ON TRUCK DURING INSTALLATION'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC111',
	'HOURS ON TRUCK DURING INSTALLATION'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC112',
	'BRAND'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC113',
	'AUTHORIZATION NUMBER'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC114',
	'REPLACED OEM PART NUMBER'
  )
/
INSERT
INTO UPLOAD_ERROR
  (
   	ID,
	CODE,
	UPLOAD_FIELD
  )
  VALUES
  (
   	UPLOAD_ERROR_SEQ.nextval,
	'DC115',
	'PART NUMBER/PART SERIAL NUMBER'
  )
/
UPDATE UPLOAD_MGT SET COLUMNS_TO_CAPTURE=57 WHERE NAME_OF_TEMPLATE='draftWarrantyClaims'
/
UPDATE UPLOAD_MGT SET CONSUME_ROWS_FROM=10 WHERE NAME_OF_TEMPLATE='draftWarrantyClaims'
/
COMMIT
/