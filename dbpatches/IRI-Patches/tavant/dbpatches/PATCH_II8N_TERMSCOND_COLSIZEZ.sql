--Purpose    : Increasing Columns Size to 4000 for Terms & Conditions
--Author     : Ramalakshmi P
--Created On : 25-JUL-09

alter table I18NPOLICY_TERMS_CONDITIONS modify (TERMS_AND_CONDITIONS  varchar2(4000) )
/