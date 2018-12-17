-- Patch to insert BU Settings Values for Manual Approval for Demo
-- Author		: ParthaSarathy R
-- Created on	: 30-12-2013

insert into bu_settings
(id,
key_name,
key_value,
version,
d_created_on,
d_updated_on,
d_last_updated_by,
d_created_time,
d_updated_time,
d_active,
business_unit_info)
values
(
BU_SETTINGS_SEQ.nextval,
'DR.demo.manualApproval.AMER',
'true',
1,
sysdate,
sysdate,
56,
systimestamp,
systimestamp,
1,
'AMER'
)
/
commit
/