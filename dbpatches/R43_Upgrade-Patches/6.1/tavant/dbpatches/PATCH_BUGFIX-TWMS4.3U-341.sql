/* A new column has been added in criteria_evaluation_precedence which will hold the business unit info value. Because of this the following scripts have to be run to populate the information in the table with respect to the business unit .

1. Alter table and create sequnce which was not present . 
2. Script to replicate the precedence of properties for criterias common among all business unit sections (Part return configuration ,Labor rate price list etc) 
3. Scrip to populate the business unit info for criteria related to payment variables.
4. Delete duplicate criteria

*/

alter table criteria_evaluation_precedence add  business_unit_info VARCHAR2(255 CHAR)
/
--CREATE SEQUENCE CRITERIA_EVALUATION_PREC_SEQ
 --START WITH 1100000000592
 --INCREMENT BY   1
 --NOCACHE
 --NOCYCLE
--/

DECLARE
TYPE V_ARR IS VARRAY(25) OF VARCHAR(50);
V_PAY_VAR V_ARR := V_ARR('Parts Price List','Dealer Unit Rate List','Claim Bonus','OEM Parts Discount','Part Return Configuration','Labor Rate Price List',
'Travel Rate Price List','LABOR RATE WEIGHTS','TRAVEL RATE WEIGHTS','PAYMENT MODIFIER WEIGHTS');
V_BUS_UNITS V_ARR :=V_ARR('AIR','Hussmann','Clubcar ESA','TFM','Transport Solutions ESA','Thermo King TSA' );
BEGIN
FOR  EACH_BUSUNIT IN 1 .. V_BUS_UNITS.COUNT 
LOOP
  BEGIN
  FOR EACH_PAY_VAR IN 1 .. V_PAY_VAR.COUNT 
     LOOP 
       BEGIN
          INSERT INTO CRITERIA_EVALUATION_PRECEDENCE VALUES
          (CRITERIA_EVALUATION_PREC_SEQ.nextval,V_PAY_VAR(EACH_PAY_VAR),0,sysdate,'UPGRADE-MIGRATION',sysdate,null,current_timestamp,current_timestamp,1,V_BUS_UNITS(EACH_BUSUNIT));
          FOR EACH_REC IN (SELECT EVAL.* FROM EVAL_PRECEDENCE_PROPERTIES EVAL,CRITERIA_EVALUATION_PRECEDENCE CRITERIA
           WHERE EVAL.FOR_CRITERIA=CRITERIA.ID AND CRITERIA.BUSINESS_UNIT_INFO is null AND CRITERIA.FOR_DATA =V_PAY_VAR(EACH_PAY_VAR) )
             LOOP
               BEGIN 
                INSERT INTO EVAL_PRECEDENCE_PROPERTIES(FOR_CRITERIA,PROPERTIES_ELEMENT_DOMAIN_NAME,PROP_EXPR,PRECEDENCE)
                VALUES(CRITERIA_EVALUATION_PREC_SEQ.currval,EACH_REC.PROPERTIES_ELEMENT_DOMAIN_NAME,EACH_REC.PROP_EXPR,EACH_REC.PRECEDENCE);
               END;
            END LOOP;
          END;
     END LOOP;
   END;
END LOOP ;
END;
/

DECLARE 
BEGIN 
FOR EACH_REC IN (
SELECT PV.BUSINESS_UNIT_INFO as businessunit,CEP.ID EACHID,PV.NAME FROM PAYMENT_VARIABLE PV, CRITERIA_EVALUATION_PRECEDENCE CEP 
WHERE PV.NAME = CEP.FOR_DATA and cep.d_active = 1 and pv.d_active =1
and cep.business_unit_info is null)	
LOOP 
BEGIN 
 INSERT INTO CRITERIA_EVALUATION_PRECEDENCE VALUES
 (CRITERIA_EVALUATION_PREC_SEQ.NEXTVAL,EACH_REC.NAME,0,SYSDATE,'UPGRADE - MIGRATION',SYSDATE,NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,1,EACH_REC.BUSINESSUNIT);
          FOR EACH_SUB_REC IN (SELECT EVAL.* FROM EVAL_PRECEDENCE_PROPERTIES EVAL,CRITERIA_EVALUATION_PRECEDENCE CRITERIA
           WHERE EVAL.FOR_CRITERIA=each_rec.eachID AND CRITERIA.BUSINESS_UNIT_INFO is null AND CRITERIA.FOR_DATA =each_rec.name )
             LOOP
               BEGIN 
                INSERT INTO EVAL_PRECEDENCE_PROPERTIES(FOR_CRITERIA,PROPERTIES_ELEMENT_DOMAIN_NAME,PROP_EXPR,PRECEDENCE)
                VALUES(CRITERIA_EVALUATION_PREC_SEQ.currval,EACH_SUB_REC.PROPERTIES_ELEMENT_DOMAIN_NAME,EACH_SUB_REC.PROP_EXPR,EACH_SUB_REC.PRECEDENCE);
               END;
            END LOOP;
END;
END LOOP;
END;

/
DECLARE 
BEGIN 
FOR EACH_REC IN (
SELECT cep.id FROM  CRITERIA_EVALUATION_PRECEDENCE CEP 
WHERE  cep.business_unit_info is null)	
LOOP 
BEGIN 
DELETE FROM EVAL_PRECEDENCE_PROPERTIES WHERE FOR_CRITERiA = EACH_REC.ID ;
delete  FROM  CRITERIA_EVALUATION_PRECEDENCE CEP where cep.id = each_rec.id;
END;
END LOOP;
END;
/
COMMIT
/