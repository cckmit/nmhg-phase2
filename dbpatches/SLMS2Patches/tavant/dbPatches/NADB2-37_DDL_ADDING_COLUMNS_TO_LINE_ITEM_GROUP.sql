--Purpose    : Patch TO ADD COLUMN IN LINE_ITEM_GROUP TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 14-JAN-2014

alter table Line_Item_group add
	(	 
    state_mandate_rate NUMBER(19,2),
    state_mandate_rate_curr VARCHAR2(255 CHAR),
	total_cp_curr  VARCHAR2(255 CHAR),  
    total_cp_amt NUMBER(19,2),
    accepted_wnty_curr  VARCHAR2(255 CHAR),  
    accepted_wnty_amt NUMBER(19,2)
  	)
/