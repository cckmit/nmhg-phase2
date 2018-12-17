--Purpose    : Adding columns to stg_draft_claim
--Author     : Bharath kumar
--Created On : 30/04/2010
--Impact     : None

alter table stg_draft_claim add Is_Serialized VARCHAR2(255 CHAR)
/
commit
/



