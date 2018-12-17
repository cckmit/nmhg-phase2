--Purpose    : Patch created for index on claimnumber on claim
--Author     : Saya Sudha
--Created On : 24-Feb-11

CREATE UNIQUE INDEX CLAIM_UPPER_CLAIMNUMBER_UNQ ON CLAIM( UPPER(CLAIM_NUMBER) )
/
COMMIT
/