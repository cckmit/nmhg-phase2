--Purpose: For creating a Foreign Key between entity classes and the BusinessUnit class
--Author: Ramalakshmi P
--Created On: Date 25 Sept 2008

ALTER TABLE USER_CLUSTER  ADD BUSINESS_UNIT_INFO VARCHAR2(255 CHAR)
/
ALTER TABLE USER_CLUSTER ADD CONSTRAINT USER_CLUSTER_Business_Unit_fk
FOREIGN KEY(business_unit_info)
REFERENCES BUSINESS_UNIT(name)
/
