--PURPOSE    : PATCH to add constraint PK I18NDOMAIN_RULE_TEXT_PK
--AUTHOR     : Joseph Tharakan
--CREATED ON : 03-MAY-11
--IMPACT     : Primary Key added

alter table I18NDOMAIN_RULE_TEXT add constraint I18NDOMAIN_RULE_TEXT_PK primary key(id)
/
COMMIT
/