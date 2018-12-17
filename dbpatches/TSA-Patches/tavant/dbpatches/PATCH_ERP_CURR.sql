
alter table credit_memo add 
      paid_amount_erp_curr   VARCHAR2(255 CHAR)
/
alter table credit_memo add 
      TAX_AMOUNT_ERP_AMT  NUMBER(19,2)
/
alter table credit_memo add 
      paid_amount_erp_amt  NUMBER(19,2)
/
alter table credit_memo add 
      TAX_AMOUNT_ERP_CURR   VARCHAR2(255 CHAR)
/
commit
/