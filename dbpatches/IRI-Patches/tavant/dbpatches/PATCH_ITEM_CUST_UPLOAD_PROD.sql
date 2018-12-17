--Purpose    : Used to Item and Customer upload
--Author     : Priyank Gupta
--Created On : 14-Mar-09

CREATE TABLE ITEM_STAGING
(
	ID				   		NUMBER,
	FILE_UPLOAD_MGT_ID				NUMBER,
	ITEM_NUMBER				VARCHAR2(4000),
	OWNER					VARCHAR2(4000),
	ITEM_DESC				VARCHAR2(4000),
	ITEM_GROUP_CODE			VARCHAR2(4000),
	PART_MANUFACTURING_CODE	VARCHAR2(4000),
	IS_SERIALIZED			VARCHAR2(4000),
	HAS_HOUR_METER			VARCHAR2(4000),
	TREAD_TYPE				VARCHAR2(4000),
	TREAD_FUEL_SYSTEM			VARCHAR2(4000),
	TREAD_BRAKE_SYSTEM			VARCHAR2(4000),
	ITEM_STATUS				VARCHAR2(4000),
	UNIT_OF_MEASURE			VARCHAR2(4000),
	BUSINESS_UNIT			VARCHAR2(4000),
	UPDATES					VARCHAR2(4000),
	ERROR_STATUS			VARCHAR2(4000),
	ERROR_CODE				VARCHAR2(4000),
	UPLOAD_STATUS			VARCHAR2(4000),
	UPLOAD_ERROR			VARCHAR2(4000),			
	UPLOAD_DATE				VARCHAR2(4000),
	STAGING_DATE			VARCHAR2(4000)
)
/
CREATE TABLE CUSTOMER_STAGING
(
	ID		NUMBER,
	FILE_UPLOAD_MGT_ID NUMBER,
	CUSTOMER_NUMBER	VARCHAR2(4000),
	CUSTOMER_NAME	VARCHAR2(4000),
	CUSTOMER_TYPE	VARCHAR2(4000),
	CURRENCY	VARCHAR2(4000),
	CONTACT_PERSON  VARCHAR2(4000),
	ADDRESS1	VARCHAR2(4000),
	ADDRESS2	VARCHAR2(4000),
	CITY		VARCHAR2(4000),
	STATE		VARCHAR2(4000),
	POSTAL_CODE	VARCHAR2(4000),
	COUNTRY		VARCHAR2(4000),
	PHONE		VARCHAR2(4000),
	EMAIL		VARCHAR2(4000),
	STATUS		VARCHAR2(4000),
	BUSINESS_Unit	VARCHAR2(4000),
	UPDATES		VARCHAR2(4000),
	STAGING_DATE	DATE,
	ERROR_STATUS	VARCHAR2(100),
	ERROR_CODE	VARCHAR2(4000),
	UPLOAD_STATUS	VARCHAR2(100),
	UPLOAD_ERROR	VARCHAR2(4000),
	UPLOAD_DATE	DATE
)
/
COMMIT
/