--Purpose    : For updating relevance score for existing records
--Author     : prashanth
--Created On : 12-Sep-08

UPDATE PAYMENT_MODIFIER
SET RELEVANCE_SCORE = 2 * RELEVANCE_SCORE
/
commit
/
