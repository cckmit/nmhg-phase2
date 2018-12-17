--Purpose    : Scripts for updating preferred currency codes of suppliers - TKTSA-78
--Author     : kuldeep.patil
--Created On : 06/08/2010

UPDATE ORGANIZATION SET PREFERRED_CURRENCY = 'USD' WHERE PREFERRED_CURRENCY IS NULL
/
COMMIT
/