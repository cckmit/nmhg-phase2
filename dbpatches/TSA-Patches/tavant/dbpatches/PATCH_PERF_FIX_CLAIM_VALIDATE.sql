--Purpose    : Perf fix for validate claim from processor review.
--Created On : 02-June-2010
--Created By : Rahul Katariya
--Impact     : Claim Validate


CREATE INDEX INST_PRTS_UPPR_SLNO_IDX ON INSTALLED_PARTS(UPPER(SERIAL_NUMBER))
/
commit
/
