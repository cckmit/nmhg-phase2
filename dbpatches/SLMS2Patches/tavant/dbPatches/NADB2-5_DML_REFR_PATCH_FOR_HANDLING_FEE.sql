-- PURPOSE    : PATCH TO ADD Handling Fee in Cost Category and Section table
-- AUTHOR     : Arpitha Nadig AR.
-- CREATED ON : 13-DEC-2013

Insert into cost_category (
ID,
CODE,
DESCRIPTION,
NAME,
VERSION,
D_CREATED_ON,
D_CREATED_TIME,
D_INTERNAL_COMMENTS,
D_UPDATED_ON,
D_UPDATED_TIME,
D_LAST_UPDATED_BY,
D_ACTIVE
) 
values (
COST_CATEGORY_SEQ.nextval,
'HANDLING_FEE',
'Handling Fee',
'Handling Fee',
1,
null,
null,
null,
null,
null,
null,
1)
/
INSERT INTO section 
(
id,
display_position,
name,
version, 
d_active, 
message_key
)
VALUES( 
section_seq.nextval,
 19,
 'Handling Fee',
 1, 
 1, 
 'label.section.handlingFee'
 )
/
INSERT INTO config_value 
	(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
	) 
VALUES 
	(
		CONFIG_VALUE_SEQ.nextval,
		1,
		null,
		(SELECT id FROM config_param cp WHERE cp.description='Configured Cost Categories'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'AMER',
		null
	)
/
ALTER TABLE CLAIM_AUDIT ADD HANDLING_FEE_CONFIG NUMBER(1,0)
/
ALTER TABLE SERVICE ADD HANDLING_FEE NUMBER(19,2)
/
ALTER TABLE SERVICE ADD HANDLING_FEE_CURR VARCHAR2(255 CHAR)
/
ALTER TABLE SERVICE ADD HANDLING_FEE_INVOICE NUMBER(19,0)
/
ALTER TABLE SERVICE ADD 
CONSTRAINT "HANDLING_FEE_INVOICE_FK" FOREIGN KEY ("HANDLING_FEE_INVOICE")
REFERENCES "DOCUMENT" ("ID")
/