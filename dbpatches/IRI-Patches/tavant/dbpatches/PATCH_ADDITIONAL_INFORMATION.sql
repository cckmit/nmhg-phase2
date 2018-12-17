--Purpose    : Patch for creating Market table
--Author     : Pradyot Rout
--Created On : 20-Jun-09

alter table marketing_information drop column market_type
/
alter table marketing_information drop column COMPETITION_TYPE
/
alter table marketing_information add  market_type number(19,0)
/
ALTER TABLE marketing_information ADD (
  CONSTRAINT MARKETINFO_MARKET_TYPE_FK 
 FOREIGN KEY (market_type) 
 REFERENCES MARKET_TYPE (ID))
/
alter table marketing_information add COMPETITION_TYPE number(19,0)
/
ALTER TABLE marketing_information ADD (
  CONSTRAINT MARKETINFO_COMP_TYPE_FK 
 FOREIGN KEY (COMPETITION_TYPE) 
 REFERENCES COMPETITION_TYPE (ID))
/
COMMIT
/