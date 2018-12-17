-- PURPOSE    : PATCH TO ADD Transportation Rate per Loaded Mile FOR CONFIG PARAM
-- AUTHOR     : Priyanka S.
-- CREATED ON : 9-DEC-2013

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
'TRANSPORTATION',
'Transportation',
'Transportation',
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
 18,
 'Transportation',
 1, 
 1, 
 'label.section.transportation'
 )
/
commit
/