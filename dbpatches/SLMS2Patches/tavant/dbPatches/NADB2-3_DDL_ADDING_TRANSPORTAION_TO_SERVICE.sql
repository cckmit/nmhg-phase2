--Purpose    : Patch for adding new Cost Category "Transporatation"
--Author     : Priyanka S
--Created On : 27-NOV-2013

alter table claim_audit add (TRANSPORTATION NUMBER(1,0))
/

alter table service add (TRANSPORTATION_AMT NUMBER(19,2))
/

alter table service add (TRANSPORTATION_INVOICE NUMBER(19,0))
/

alter table service add (TRANSPORTATION_CURR VARCHAR2(255 CHAR))
/

alter table service add (NO_INVOICE_AVAILABLE NUMBER(1,0))
/