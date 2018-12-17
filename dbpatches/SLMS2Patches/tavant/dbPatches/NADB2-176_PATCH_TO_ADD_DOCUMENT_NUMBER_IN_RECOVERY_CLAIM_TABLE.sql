--Purpose    : Patch for adding the Document Number column in recovery_claim  table
--Author     : Pracher 
--Created On : 2-July-2014

alter table recovery_claim add (document_number varchar2(2000))
/
