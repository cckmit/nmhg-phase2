--Purpose: Many comments are more than 250 and maximum is 1700 currently.
--Author: Data Migration Team
--Created On: Date 29 Aug 2008

alter table I18NPolicy_Terms_Conditions modify (TERMS_AND_CONDITIONS varchar2(2000))
/
commit
/