--Purpose    : Changes Made to include Certification Status to a Policy Definition
--Author     : Lavin Hawes
--Created On : 05-Jan-10

Alter table Policy_Definition add Certification_Status  VARCHAR2(255) DEFAULT 'NOTCERTIFIED'
/
COMMIT
/