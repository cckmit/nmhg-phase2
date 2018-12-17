--Purpose    : Changes to claim number pattern as requested by TK TSA
--Created On : 08-Jun-2010
--Created By : Rahul Katariya
--Impact     : Claim number pattern and credit notification


UPDATE CLAIM_NUMBER_PATTERN
SET PATTERN_TYPE = 'W'
where id = 1
/
UPDATE CLAIM_NUMBER_PATTERN
SET Template = 'D-NNNNNNN'
where id = 2
/
COMMIT
/