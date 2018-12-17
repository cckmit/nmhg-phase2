--Purpose: For including customer type in  evaluating relevance  score
--Author: Prashanth 
--Created On: Date 29 Aug 2008

ALTER TABLE LABOR_RATES ADD (CUSTOMER_TYPE VARCHAR2(50))
/
ALTER TABLE TRAVEL_RATES ADD (CUSTOMER_TYPE VARCHAR2(50))
/
UPDATE LABOR_RATES SET CUSTOMER_TYPE = 'ALL'
/
UPDATE TRAVEL_RATES SET CUSTOMER_TYPE = 'ALL'
/
INSERT INTO CRITERIA_EVALUATION_PRECEDENCE(ID, FOR_DATA, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) VALUES ( (SELECT MAX(ID) + 1 FROM CRITERIA_EVALUATION_PRECEDENCE),'LABOR RATE WEIGHTS',0,NULL,NULL,NULL,NULL)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.dealerCriterion.dealer',0)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.dealerCriterion.dealerGroup',1)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.claimType',2)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.warrantyType',3)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.productType',4)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'customerType',5)
/
INSERT INTO CRITERIA_EVALUATION_PRECEDENCE (ID, FOR_DATA, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY)  VALUES ( (SELECT MAX(ID) + 1 FROM CRITERIA_EVALUATION_PRECEDENCE),'TRAVEL RATE WEIGHTS',0,NULL,NULL,NULL,NULL)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.dealerCriterion.dealer',0)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.dealerCriterion.dealerGroup',1)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.claimType',2)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.warrantyType',3)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'forCriteria.productType',4)
/
INSERT INTO EVAL_PRECEDENCE_PROPERTIES  VALUES ((SELECT MAX(ID) FROM CRITERIA_EVALUATION_PRECEDENCE),NULL,'customerType',5)
/
COMMIT
/