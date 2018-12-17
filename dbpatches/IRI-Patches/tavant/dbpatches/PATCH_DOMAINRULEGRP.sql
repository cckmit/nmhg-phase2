--Purpose: Patch for creating domain rule group and updating existing rules to make them belong to a group
--Author: Ramalakshmi P
--Created On: OCT-15-2008

CREATE TABLE DOMAIN_RULE_GROUP (ID 	NUMBER(20) 	NOT NULL,   	
 PRIORITY NUMBER(20),
 STOP_RULE_PROC_ON_SUCCESS NUMBER(1),
 STOP_RULE_PROC_ON_FIRST_SUCC  NUMBER(1),			
 USE_AS_DEFAULT 		 NUMBER(1)	,
 CONTEXT VARCHAR2(225),
 NAME VARCHAR2(225),
 DESCRIPTION VARCHAR2(225),
 BUSINESS_UNIT_INFO VARCHAR2(255)
   )
/
ALTER TABLE DOMAIN_RULE_GROUP ADD CONSTRAINT DOMAIN_RULE_GROUP_BU_INFO_FK FOREIGN KEY (BUSINESS_UNIT_INFO) REFERENCES BUSINESS_UNIT(NAME)
/
ALTER TABLE DOMAIN_RULE_GROUP ADD CONSTRAINT DOMAIN_RULE_GROUP_PK PRIMARY KEY (ID)
/
CREATE SEQUENCE  DOMAIN_RULE_GROUP_SEQ
  START WITH 1000
  INCREMENT BY 20
  MAXVALUE 999999999999999999999999999
  MINVALUE 1
  NOCYCLE
  CACHE 20
  NOORDER
/
ALTER TABLE DOMAIN_RULE ADD (RULE_GROUP NUMBER(20))
/
ALTER TABLE DOMAIN_RULE ADD (PRIORITY NUMBER(20))
/
ALTER TABLE DOMAIN_RULE ADD CONSTRAINT DOMAIN_RULE_DOMRULE_GRP_FK FOREIGN KEY (RULE_GROUP) REFERENCES DOMAIN_RULE_GROUP
/
INSERT INTO DOMAIN_RULE_GROUP (ID, PRIORITY, STOP_RULE_PROC_ON_SUCCESS,STOP_RULE_PROC_ON_FIRST_SUCC, USE_AS_DEFAULT, CONTEXT, NAME, DESCRIPTION, Business_unit_info) 
VALUES (DOMAIN_RULE_GROUP_SEQ.NEXTVAL ,1 ,0 ,0,0   , 'ClaimRules','Claim Processing Rules' ,'Claim Processing Rules' ,'Club Car')
/
UPDATE DOMAIN_RULE SET RULE_GROUP = (SELECT ID FROM DOMAIN_RULE_GROUP) ,CONTEXT = NULL WHERE CONTEXT = 'ClaimRules'
/
COMMIT
/
CREATE OR REPLACE PROCEDURE UPDATE_RULES_WITHGRP_PRIORITY AS
	V_PRIORITYCTR  NUMBER:= 1;   
   CURSOR RULES_REC IS
      SELECT ID FROM DOMAIN_RULE WHERE CONTEXT = 'ClaimRules';	  
BEGIN
   FOR EACH_REC IN RULES_REC LOOP
      BEGIN
         UPDATE DOMAIN_RULE SET PRIORITY  = V_PRIORITYCTR WHERE ID = EACH_REC.ID;
		 V_PRIORITYCTR := V_PRIORITYCTR + 1;
      END;
   END LOOP;

   COMMIT;
END;
/
BEGIN
UPDATE_RULES_WITHGRP_PRIORITY();
END;
/
																			 

 