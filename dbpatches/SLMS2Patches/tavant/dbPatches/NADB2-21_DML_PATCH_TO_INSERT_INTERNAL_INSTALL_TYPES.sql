-- Patch to Insert Internal Install Types
-- Author		: ParthaSarathy R
-- Created On	: 18-Dec-2013

insert into internal_install_type
(
	ID,
	INTERNAL_INSTALL_TYPE,
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
	'Government Account',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into I18NINTERNAL_INSTALL_TYPE
(
ID,
LOCALE,
INTERNAL_INSTALL_TYPE,
I18NINTERNAL_INSTALL_TYPE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'Government Account',
(select id from internal_install_type where internal_install_type = 'Government Account' and business_unit_info = 'AMER')
)
/
insert into internal_install_type
(
	ID,
	INTERNAL_INSTALL_TYPE,
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
	'National Account',
	1,
	'AMER',
	56,
	'NMHGSLMS-580',
	systimestamp,
	systimestamp,
	1
)
/
insert into I18NINTERNAL_INSTALL_TYPE
(
ID,
LOCALE,
INTERNAL_INSTALL_TYPE,
I18NINTERNAL_INSTALL_TYPE
)
values
(
I18NCONTRACT_CODE_TEXT_SEQ.nextval,
'en_US',
'National Account',
(select id from internal_install_type where internal_install_type = 'National Account' and business_unit_info = 'AMER')
)
/
COMMIT
/