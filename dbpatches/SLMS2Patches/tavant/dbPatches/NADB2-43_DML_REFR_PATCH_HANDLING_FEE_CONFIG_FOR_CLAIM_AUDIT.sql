--Purpose    : DDL for NMHGSLMS-425 -Updating older claims' handling fee to Zero
--Author     : Arpitha Nadig AR
--Created On : 17-JAN-2013
update claim_audit set handling_fee_config=0 where handling_fee_config is null
/
commit
/