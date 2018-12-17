--Purpose    : Patch TO ADD COLUMN IN LINE ITEM GROUP TABLE
--Author     : AJIT KUMAR SINGH
--Created On : 24-APR-2014

alter table 
	 Line_Item_group
   add
     (     
      SC_TOTAL_CREDIT_CURR  VARCHAR2(255 CHAR),   
      SC_TOTAL_CREDIT_AMT  NUMBER(19,2),       
      MODIFIER_TOTAL_CREDIT_CURR  VARCHAR2(255 CHAR),   
      MODIFIER_TOTAL_CREDIT_AMT  NUMBER(19,2),
      NET_PRICE_TOTAL_CREDIT_CURR  VARCHAR2(255 CHAR),   
      NET_PRICE_TOTAL_CREDIT_AMT  NUMBER(19,2),
      modifier_accepted_amt  NUMBER(19,2),
      modifier_accepted_curr VARCHAR2(255 CHAR)
     )
/