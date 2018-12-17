--Purpose    : Patch to modify last_updated_on_date in claim table
--Author     : ParthaSarathy R
--Created On : 05-Mar-2013

alter table claim modify (last_updated_on_date TIMESTAMP (6))
/