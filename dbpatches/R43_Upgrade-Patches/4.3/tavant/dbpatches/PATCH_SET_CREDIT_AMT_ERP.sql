update credit_memo 
set paid_amount_erp_curr=paid_amount_curr
where paid_amount_erp_curr is null
and paid_amount_curr is not null
/
update credit_memo 
set paid_amount_erp_amt=paid_amount_amt
where paid_amount_erp_amt is null
and paid_amount_amt is not null
/
update credit_memo 
set tax_amount_erp_amt=tax_amount_amt
where tax_amount_erp_amt is null 
and tax_amount_amt is not null
/
update credit_memo 
set tax_amount_erp_curr=tax_amount_curr
where tax_amount_erp_curr is null 
and tax_amount_curr is not null
/
commit
/