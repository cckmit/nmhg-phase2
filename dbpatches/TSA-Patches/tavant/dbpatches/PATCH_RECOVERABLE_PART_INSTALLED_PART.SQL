--Purpose    : domain change on recoverable part
--Created On : 09-APR-2010
--Created By : Sudaksh Chohan

ALTER TABLE RECOVERABLE_PART DROP COLUMN INSTALLED_PART
/
DROP TABLE REC_PARTS_SUP_PART_RETURNS
/
COMMIT
