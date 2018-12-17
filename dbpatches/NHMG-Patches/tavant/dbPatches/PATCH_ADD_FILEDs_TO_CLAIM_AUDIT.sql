--Purpose    : Patch for adding fileds to claim_audit
--Author     : Kalyani
--Created On : 01-OCT-2013

alter table Claim_Audit add (IS_PRICE_FETCH_DOWN NUMBER(1,0))
/
alter table Claim_Audit add (IS_PRICE_FETCH_RETURN_ZERO NUMBER(1,0))
/
alter table Claim_Audit add (PRICE_FETCH_ERROR_MESSAGE VARCHAR2(4000 CHAR))
/