--Purpose    : Scripts for adding LOA amount table, changes made as a part of 4.3 upgrade 
--Created On : 11-Oct-2010
--Created By : Kuldeep Patil
--Impact     : None

CREATE TABLE LOA_AMOUNT
	(LOA_LEVEL NUMBER(19,0) NOT NULL, 
	AMOUNT	NUMBER(19,2),
  CURRENCY	VARCHAR2(255 CHAR))
/
ALTER TABLE LOA_AMOUNT 
	ADD CONSTRAINT LOA_AMOUNT_LEVEL_FK 
	FOREIGN KEY (LOA_LEVEL) 
	REFERENCES "LIMIT_OF_AUTHORITY_LEVEL" ("ID")
/
COMMIT
/
  
  
