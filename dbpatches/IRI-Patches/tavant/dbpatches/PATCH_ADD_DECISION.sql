-- Purpose    : patch to add decision column in claim, claim_audit tables
-- Author     : BHASKARA.K
-- Created On : 10-JuL-09

alter table claim add  decision varchar2(100)
/
alter table claim_audit add  decision varchar2(100)
/
commit
/