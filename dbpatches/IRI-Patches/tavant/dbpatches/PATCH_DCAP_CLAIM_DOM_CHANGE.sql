--Purpose    : Alter tables due to model change
--Author     : rashmi.malik
--Created On : 26-Aug-08

alter table dcap_Claim_audit drop column inventory_dcap_detail
/

alter table dcap_Claim_audit drop column inventory_item
/

alter table dcap_Claim_audit drop column claim_number
/

alter table dcap_Claim_audit drop column claim_type
/

alter table dcap_claim_audit add external_comments varchar2(255)
/

 
alter table dcap_claim drop column updated
/

alter table dcap_claim add claimed_amount number(19,2)
/

alter table dcap_claim add claimed_amount_curr varchar2(255)
/
