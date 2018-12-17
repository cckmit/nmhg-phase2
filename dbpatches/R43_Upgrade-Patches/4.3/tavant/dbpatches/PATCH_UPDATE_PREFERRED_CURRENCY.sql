--Purpose    : Scripts for updating preferred currency codes of suppliers - TKTSA-78  as a part of 4.3 upgrade 
--Author     : kuldeep.patil
--Created On : 06/08/2010

--UPDATE ORGANIZATION SET PREFERRED_CURRENCY = 'USD' WHERE PREFERRED_CURRENCY IS NULL
--/
COMMIT
/