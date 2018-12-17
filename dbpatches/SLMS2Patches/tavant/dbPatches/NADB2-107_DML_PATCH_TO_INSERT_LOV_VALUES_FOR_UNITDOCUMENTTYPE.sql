-- Patch to insert LOV values for Unit Document Type
-- Created On : 15-Mar-14
-- Created by : ParthaSarathy R

insert into list_of_values
(
type,
code,
description,
state,
version,
d_created_on,
d_updated_on,
d_last_updated_by,
id,
business_unit_info,
d_created_time,
d_updated_time,
d_active
)
values
(
'UNITDOCUMENTTYPE',
'ITDR',
'ITDR',
'active',
0,
sysdate,
sysdate,
56,
List_Of_Values_SEQ.nextval,
'EMEA',
systimestamp,
systimestamp,
1
)
/
insert into i18nlov_text
(id,
locale,
description,
list_of_i18n_values
)
values (
I18N_Lov_Text_SEQ.nextval,
'en_US',
'ITDR',
(select id from list_of_values where code = 'ITDR')
)
/
insert into list_of_values
(
type,
code,
description,
state,
version,
d_created_on,
d_updated_on,
d_last_updated_by,
id,
business_unit_info,
d_created_time,
d_updated_time,
d_active
)
values
(
'UNITDOCUMENTTYPE',
'Others_EMEA',
'Others',
'active',
0,
sysdate,
sysdate,
56,
List_Of_Values_SEQ.nextval,
'EMEA',
systimestamp,
systimestamp,
1
)
/
insert into i18nlov_text
(id,
locale,
description,
list_of_i18n_values
)
values (
I18N_Lov_Text_SEQ.nextval,
'en_US',
'Others',
(select id from list_of_values where code = 'Others_EMEA')
)
/
insert into list_of_values
(
type,
code,
description,
state,
version,
d_created_on,
d_updated_on,
d_last_updated_by,
id,
business_unit_info,
d_created_time,
d_updated_time,
d_active
)
values
(
'UNITDOCUMENTTYPE',
'PDI',
'PDI',
'active',
0,
sysdate,
sysdate,
56,
List_Of_Values_SEQ.nextval,
'AMER',
systimestamp,
systimestamp,
1
)
/
insert into i18nlov_text
(id,
locale,
description,
list_of_i18n_values
)
values (
I18N_Lov_Text_SEQ.nextval,
'en_US',
'PDI',
(select id from list_of_values where code = 'PDI')
)
/
insert into list_of_values
(
type,
code,
description,
state,
version,
d_created_on,
d_updated_on,
d_last_updated_by,
id,
business_unit_info,
d_created_time,
d_updated_time,
d_active
)
values
(
'UNITDOCUMENTTYPE',
'Authorization',
'Authorization',
'active',
0,
sysdate,
sysdate,
56,
List_Of_Values_SEQ.nextval,
'AMER',
systimestamp,
systimestamp,
1
)
/
insert into i18nlov_text
(id,
locale,
description,
list_of_i18n_values
)
values (
I18N_Lov_Text_SEQ.nextval,
'en_US',
'Authorization',
(select id from list_of_values where code = 'Authorization')
)
/
insert into list_of_values
(
type,
code,
description,
state,
version,
d_created_on,
d_updated_on,
d_last_updated_by,
id,
business_unit_info,
d_created_time,
d_updated_time,
d_active
)
values
(
'UNITDOCUMENTTYPE',
'Others_AMER',
'Others',
'active',
0,
sysdate,
sysdate,
56,
List_Of_Values_SEQ.nextval,
'AMER',
systimestamp,
systimestamp,
1
)
/
insert into i18nlov_text
(id,
locale,
description,
list_of_i18n_values
)
values (
I18N_Lov_Text_SEQ.nextval,
'en_US',
'Others',
(select id from list_of_values where code = 'Others_AMER')
)
/
commit
/