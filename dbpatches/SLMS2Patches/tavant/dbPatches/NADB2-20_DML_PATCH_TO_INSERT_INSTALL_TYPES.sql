-- Patch to Insert Install Types
-- Author		: ParthaSarathy R
-- Created On	: 17-Dec-2013 

insert into contract_code
(
	ID,
	CONTRACT_CODE,
	VERSION,
	BUSINESS_UNIT_INFO,
	D_CREATED_BY,
	D_INTERNAL_COMMENTS,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
)
values
(
	CONTRACT_CODE_SEQ.nextVal,
	'Sale',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into i18ncontract_code_text
(
ID,
LOCALE,
CONTRACT_CODE,
I18N_CONTRACT_CODE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Sale',
(select id from contract_code where contract_code = 'Sale' and business_unit_info = 'AMER')
)
/
insert into contract_code
(
	ID,
	CONTRACT_CODE,
	VERSION,
	BUSINESS_UNIT_INFO,
	D_CREATED_BY,
	D_INTERNAL_COMMENTS,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
)
values
(
	CONTRACT_CODE_SEQ.nextVal,
	'Rental Short Term',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into i18ncontract_code_text
(
ID,
LOCALE,
CONTRACT_CODE,
I18N_CONTRACT_CODE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Rental Short Term',
(select id from contract_code where contract_code = 'Rental Short Term' and business_unit_info = 'AMER')
)
/
insert into contract_code
(
	ID,
	CONTRACT_CODE,
	VERSION,
	BUSINESS_UNIT_INFO,
	D_CREATED_BY,
	D_INTERNAL_COMMENTS,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
)
values
(
	CONTRACT_CODE_SEQ.nextVal,
	'Rental Long Term',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into i18ncontract_code_text
(
ID,
LOCALE,
CONTRACT_CODE,
I18N_CONTRACT_CODE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Rental Long Term',
(select id from contract_code where contract_code = 'Rental Long Term' and business_unit_info = 'AMER')
)
/
insert into contract_code
(
	ID,
	CONTRACT_CODE,
	VERSION,
	BUSINESS_UNIT_INFO,
	D_CREATED_BY,
	D_INTERNAL_COMMENTS,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
)
values
(
	CONTRACT_CODE_SEQ.nextVal,
	'Demo',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into i18ncontract_code_text
(
ID,
LOCALE,
CONTRACT_CODE,
I18N_CONTRACT_CODE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Demo',
(select id from contract_code where contract_code = 'Demo' and business_unit_info = 'AMER')
)
/
insert into contract_code
(
	ID,
	CONTRACT_CODE,
	VERSION,
	BUSINESS_UNIT_INFO,
	D_CREATED_BY,
	D_INTERNAL_COMMENTS,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
)
values
(
	CONTRACT_CODE_SEQ.nextVal,
	'Refurbished',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into i18ncontract_code_text
(
ID,
LOCALE,
CONTRACT_CODE,
I18N_CONTRACT_CODE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Refurbished',
(select id from contract_code where contract_code = 'Refurbished' and business_unit_info = 'AMER')
)
/
insert into contract_code
(
	ID,
	CONTRACT_CODE,
	VERSION,
	BUSINESS_UNIT_INFO,
	D_CREATED_BY,
	D_INTERNAL_COMMENTS,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
)
values
(
	CONTRACT_CODE_SEQ.nextVal,
	'Pre-owned',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into i18ncontract_code_text
(
ID,
LOCALE,
CONTRACT_CODE,
I18N_CONTRACT_CODE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Pre-owned',
(select id from contract_code where contract_code = 'Pre-owned' and business_unit_info = 'AMER')
)
/
COMMIT
/