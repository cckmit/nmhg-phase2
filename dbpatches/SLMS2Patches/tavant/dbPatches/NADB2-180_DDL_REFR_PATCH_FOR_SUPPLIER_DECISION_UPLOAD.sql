--Purpose    : DML SCRIPTS FOR NMHGSLMS-431 : Supplier Decision Upload columns
--Author     : Arpitha Nadig AR
--Created On : 16-JULY-2014
alter table stg_supplier_decision add CREDIT_MEMO_AMOUNT NUMBER(19,2)
/
alter table stg_supplier_decision add CREDIT_MEMO_CURRENCY VARCHAR2(255)
/