--Purpose    : Patch for inserting maintenance_contract, contract_code, industry_code 
--Author     : PARTHASARATHY R	
--Created On : 10-DEC-2012

INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'No Service Contract', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Periodic Maintenance Contract', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Full Service Contract', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Hourmeter Reading', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Customer Representative', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Dealer Representative', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Sale', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Short Term Rental less than 12 months', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Long Term Rental greater than 12 months', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Lease', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO INDUSTRY_CODE (ID, INDUSTRY_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (INDUSTRY_CODE_SEQ.nextval, 'Sales', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO INDUSTRY_CODE (ID, INDUSTRY_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (INDUSTRY_CODE_SEQ.nextval, 'Marketing', 1, 'NMHG EMEA', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'No Service Contract', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Periodic Maintenance Contract', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Full Service Contract', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Hourmeter Reading', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Customer Representative', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO MAINTENANCE_CONTRACT (ID, MAINTENANCE_CONTRACT, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (MAINTENANCE_CONTRACT_SEQ.nextval, 'Dealer Representative', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Sale', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Short Term Rental less than 12 months', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Long Term Rental greater than 12 months', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO CONTRACT_CODE (ID, CONTRACT_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (CONTRACT_CODE_SEQ.nextval, 'Lease', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO INDUSTRY_CODE (ID, INDUSTRY_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (INDUSTRY_CODE_SEQ.nextval, 'Sales', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
INSERT INTO INDUSTRY_CODE (ID, INDUSTRY_CODE, VERSION, BUSINESS_UNIT_INFO, D_CREATED_BY, D_CREATED_TIME, D_ACTIVE) VALUES (INDUSTRY_CODE_SEQ.nextval, 'Marketing', 1, 'NMHG US', (SELECT ID FROM ORG_USER WHERE LOGIN='towlejs'), SYSTIMESTAMP, 1)
/
COMMIT
/