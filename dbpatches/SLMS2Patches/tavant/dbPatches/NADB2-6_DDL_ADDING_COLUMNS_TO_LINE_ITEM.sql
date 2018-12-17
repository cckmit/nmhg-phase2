--Purpose    : Patch TO ADD COLUMN IN LINE_ITEM TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 13-DEC-2013

alter table Line_Item  add
     (
   accepted_curr  VARCHAR2(255 CHAR),  
   accepted_amt NUMBER(19,2),
   PERCENTAGE_ACCEPTANCE NUMBER(19,6),
   state_mandate_curr  VARCHAR2(255 CHAR),  
   state_mandate_amt NUMBER(19,2)
	)
/
