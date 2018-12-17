--Purpose    : Changes Made to include Certification Status to a Service Provider
--Author     : Lavin Hawes
--Created On : 18-Dec-09

ALTER TABLE SERVICE_PROVIDER ADD CERTIFIED NUMBER(1,0) DEFAULT 0
/
COMMIT