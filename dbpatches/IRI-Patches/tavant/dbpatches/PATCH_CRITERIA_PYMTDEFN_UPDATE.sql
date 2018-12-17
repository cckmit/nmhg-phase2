--PURPOSE    :update policy criteria to set the clmtypename and identifier to make filtering work
--AUTHOR     : PRADYOT.ROUT
--CREATED ON : 14-MAY-09

DECLARE  
  CURSOR GET_POLICYCRITERIA  IS  
  SELECT ID,CLAIM_TYPE,LABEL  
  FROM POLICY_CRITERIA WHERE ID IN (SELECT CRITERIA FROM PAYMENT_DEFINITION);  
  CLAIMTYPE varchar(255);  
  POLICYTYPE varchar(255);
  V_COMMIT_CTR NUMBER := 0;   
  BEGIN  
  FOR GET_REC IN GET_POLICYCRITERIA LOOP 
  	IF (GET_REC.CLAIM_TYPE IS NULL) THEN   
	      CLAIMTYPE :=  'All Claim Types';  
	ELSE
	      IF (GET_REC.CLAIM_TYPE ='CAMPAIGN') THEN   
	      	      CLAIMTYPE :=  'Field Modification';  
	      	ELSE         
	      	      CLAIMTYPE := GET_REC.CLAIM_TYPE;  
	      END IF;	        
	END IF; 
	IF (GET_REC.LABEL IS NULL) THEN   
	      POLICYTYPE :=  'All Policy Types'; 
    ELSE
		  POLICYTYPE :=  GET_REC.LABEL;  
	END IF; 
	UPDATE POLICY_CRITERIA SET CLM_TYPE_NAME = CLAIMTYPE , IDENTIFIER=POLICYTYPE where id = GET_REC.id;
           V_COMMIT_CTR := V_COMMIT_CTR + 1;  
        IF(V_COMMIT_CTR  = 1000) THEN  
             COMMIT;  
             V_COMMIT_CTR := 0;  
          END IF;  
  END LOOP;  
  END;