--Purpose    : Perf fix for claim submission.
--Created On : 14-June-2010
--Created By : Ramalakshmi P
--Impact     : Claim Submission


CREATE INDEX LOA_AUTHORITY_SCHEME_IDX ON LIMIT_OF_AUTHORITY_LEVEL(LOA_SCHEME)
/
COMMIT
/
