-- PURPOSE    : Patch to hide Create Technician hyperlink in claim pages for Processor for AMER
-- AUTHOR     : Priyanka S.
-- CREATED ON : 13-JAN-2014

Insert into bu_settings 
(ID,
KEY_NAME,
KEY_VALUE,
VERSION,
D_CREATED_ON,
D_INTERNAL_COMMENTS,
D_UPDATED_ON,
D_LAST_UPDATED_BY,
D_CREATED_TIME,
D_UPDATED_TIME,D_ACTIVE,
BUSINESS_UNIT_INFO
) values 
(BU_SETTINGS_SEQ.nextval,
'create.technician.hyperlink.AMER',
'false',
1,
null,
'To hide Create Technician hyperlink in claim pages for Processor for AMER',
null,
56,
null,
null,
1,
'AMER'
)
/
Insert into bu_settings 
(ID,
KEY_NAME,
KEY_VALUE,
VERSION,
D_CREATED_ON,
D_INTERNAL_COMMENTS,
D_UPDATED_ON,
D_LAST_UPDATED_BY,
D_CREATED_TIME,
D_UPDATED_TIME,D_ACTIVE,
BUSINESS_UNIT_INFO
) values 
(BU_SETTINGS_SEQ.nextval,
'create.technician.hyperlink.EMEA',
'true',
1,
null,
'To hide Create Technician hyperlink in claim pages for Processor for EMEA',
null,
56,
null,
null,
1,
'EMEA'
)
/
commit
/