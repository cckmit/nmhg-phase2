--Purpose    : For updating relevance score for existing records
--Author     : prashanth
--Created On : 29-Aug-08

UPDATE LABOR_RATES
SET RELEVANCE_SCORE = 2 * RELEVANCE_SCORE
/

UPDATE TRAVEL_RATES
SET RELEVANCE_SCORE = 2 * RELEVANCE_SCORE
/
COMMIT
/