-- Patch to insert transaction types Dealer, Demo for NMHG US, i18n transaction type values for Dealer Rental, Demo
-- Author: PARTHASARATHY R
-- Created On : 08-NOV-2012

INSERT
INTO TRANSACTION_TYPE
  (
    TYPE,
    VERSION,
    ID,
	BUSINESS_UNIT_INFO,
	D_ACTIVE
  )
  VALUES
  (
	'DealerRental',
	1,
    TRANSACTION_TYPE_SEQ.nextval,
    'US',
	1
  )
/
INSERT
INTO TRANSACTION_TYPE
  (
    TYPE,
    VERSION,
    ID,
	BUSINESS_UNIT_INFO,
	D_ACTIVE
  )
  VALUES
  (
	'Demo',
	1,
    TRANSACTION_TYPE_SEQ.nextval,
    'US',
	1
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_EN',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_EN',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_EN',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_EN',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'fr_FR',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'fr_FR',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'fr_FR',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'fr_FR',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'de_DE',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'de_DE',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'de_DE',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'de_DE',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_GB',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_GB',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_GB',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_GB',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'DealerRental',
	(select id from transaction_type where type='DealerRental' and business_unit_info='NMHG US')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG EMEA')
  )
/
INSERT
INTO I18NTRANSACTION_TYPE_TEXT
  (
    ID,
	LOCALE,
	TYPE,
	I18N_TRANSACTION_TYPE
  )
  VALUES
  (
	I18N_Transaction_Type_Text.nextval,
	'en_US',
	'Demo',
	(select id from transaction_type where type='Demo' and business_unit_info='NMHG US')
  )
/
COMMIT
/