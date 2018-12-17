--Purpose    : Changes Made to include Certification Status to a Service Provider As a Part of 4.3 Upgrade
--Author     : Lavin Hawes
--Created On : 18-Dec-09

ALTER TABLE SERVICE_PROVIDER ADD CERTIFIED NUMBER(1,0) DEFAULT 0
/
update service_provider set CERTIFIED = 1
/
COMMIT
/