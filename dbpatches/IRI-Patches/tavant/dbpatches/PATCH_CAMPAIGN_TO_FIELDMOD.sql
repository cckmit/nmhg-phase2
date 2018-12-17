--Purpose: Changed Campaign to Field Modification in the UI
--Author: Raghu Ram
--Created On: Date 07 Apr 2009

ALTER TABLE claim ADD clm_type_name VARCHAR(255)
/
CREATE OR REPLACE TRIGGER set_clmtypename_for_claim
BEFORE INSERT  OR UPDATE
ON claim
REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
    IF (:newRow.TYPE = 'CAMPAIGN') THEN 
        :newRow.clm_type_name := 'Field Modification';
    ELSIF (:newRow.TYPE = 'MACHINE') THEN 
        :newRow.clm_type_name := 'Machine';
    ELSIF (:newRow.TYPE = 'PARTS') THEN 
        :newRow.clm_type_name := 'Parts';
    ELSE	   
        :newRow.clm_type_name := :newRow.TYPE;
    END IF;
END;
/
UPDATE claim SET version=version WHERE clm_type_name IS NULL
/
CREATE OR REPLACE TRIGGER labor_rates_values
BEFORE INSERT  OR UPDATE
ON labor_rates 
REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
    IF (:newRow.WARRANTY_TYPE IS NULL) THEN 
        :newRow.wnty_type_name :=  'All Warranty Types';
    ELSE	   
        :newRow.wnty_type_name := :newRow.WARRANTY_TYPE;
    END IF;
	IF (:newRow.CLAIM_TYPE IS NULL) THEN 
        :newRow.clm_type_name :=  'All Claim Types';		   
    ELSIF (:newRow.CLAIM_TYPE = 'CAMPAIGN') THEN 
        :newRow.clm_type_name := 'FIELD MODIFICATION';
    ELSE	   
        :newRow.clm_type_name := :newRow.CLAIM_TYPE;
    END IF;
    IF (:newRow.FOR_CRITERIA_PRODUCT_TYPE  IS NULL) THEN 
        :newRow.product_name :=  'All Products';
	ELSE
        SELECT NAME  INTO :newRow.product_name
        FROM ITEM_GROUP  itemGroup
        WHERE itemGroup.id = :newRow.FOR_CRITERIA_PRODUCT_TYPE ;	     
    END IF;
    IF (:newRow.DEALER_GROUP IS NULL) THEN 
        IF (:newRow.DEALER IS NULL) THEN 
            :newRow.identifier :=  'All Dealers';
        ELSE	
            SELECT NAME  INTO :newRow.identifier
            FROM PARTY  dealer
            WHERE dealer.id =:newRow.DEALER ;	
        END IF;
    ELSE	
        SELECT NAME  INTO :newRow.identifier
        FROM DEALER_GROUP  dealerGroup
        WHERE dealerGroup.id =:newRow.DEALER_GROUP ;	
    END IF;
END;
/
create or replace TRIGGER POLICY_RATES_VALUES
BEFORE INSERT  OR UPDATE
ON POLICY_RATES 
 REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
   IF (:newRow.WARRANTY_TYPE IS NULL) THEN 
	   	   :newRow.wnty_type_name :=  'All Warranty Types';
	ELSE	   
	      :newRow.wnty_type_name := :newRow.WARRANTY_TYPE;
     END IF;
	IF (:newRow.CLAIM_TYPE IS NULL) THEN 
	   	   :newRow.clm_type_name :=  'All Claim Types';
    ELSIF (:newRow.CLAIM_TYPE = 'CAMPAIGN') THEN
        :newRow.clm_type_name :=  'FIELD MODIFICATION';
	ELSE	   
	      :newRow.clm_type_name := :newRow.CLAIM_TYPE;
     END IF;
	 IF (:newRow.FOR_CRITERIA_PRODUCT_TYPE  IS NULL) THEN 
	   	   :newRow.product_name :=  'All Products';
	ELSE
		 SELECT NAME  INTO :newRow.product_name
		 FROM ITEM_GROUP  itemGroup
   		 WHERE itemGroup.id = :newRow.FOR_CRITERIA_PRODUCT_TYPE ;	     
     END IF;
	 IF (:newRow.DEALER_GROUP IS NULL) THEN 
	 		IF (:newRow.DEALER IS NULL) THEN 
	   	   	   :newRow.identifier :=  'All Dealers';
			ELSE	
					SELECT NAME  INTO :newRow.identifier
					 FROM PARTY  dealer
   					  WHERE dealer.id =:newRow.DEALER ;	
     		END IF;
	ELSE	
			SELECT NAME  INTO :newRow.identifier
			 FROM DEALER_GROUP  dealerGroup
   			  WHERE dealerGroup.id =:newRow.DEALER_GROUP ;	
     END IF;
END;
/
create or replace TRIGGER TRAVEL_RATES_VALUES
BEFORE INSERT  OR UPDATE
ON TRAVEL_RATES
 REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
   IF (:newRow.WARRANTY_TYPE IS NULL) THEN 
	   	   :newRow.wnty_type_name :=  'All Warranty Types';
	ELSE	   
	      :newRow.wnty_type_name := :newRow.WARRANTY_TYPE;
     END IF;
	IF (:newRow.CLAIM_TYPE IS NULL) THEN 
	   	   :newRow.clm_type_name :=  'All Claim Types';
    ELSIF (:newRow.CLAIM_TYPE = 'CAMPAIGN') THEN
        :newRow.clm_type_name :=  'FIELD MODIFICATION';
	ELSE	   
	      :newRow.clm_type_name := :newRow.CLAIM_TYPE;
     END IF;
	 IF (:newRow.FOR_CRITERIA_PRODUCT_TYPE  IS NULL) THEN 
	   	   :newRow.product_name :=  'All Products';
	ELSE
		 SELECT NAME  INTO :newRow.product_name
		 FROM ITEM_GROUP  itemGroup
   		 WHERE itemGroup.id = :newRow.FOR_CRITERIA_PRODUCT_TYPE ;	     
     END IF;
	 IF (:newRow.DEALER_GROUP IS NULL) THEN 
	 		IF (:newRow.DEALER IS NULL) THEN 
	   	   	   :newRow.identifier :=  'All Dealers';
			ELSE	
					SELECT NAME  INTO :newRow.identifier
					 FROM PARTY  dealer
   					  WHERE dealer.id =:newRow.DEALER ;	
     		END IF;
	ELSE	
			SELECT NAME  INTO :newRow.identifier
			 FROM DEALER_GROUP  dealerGroup
   			  WHERE dealerGroup.id =:newRow.DEALER_GROUP ;	
     END IF;
END;
/
create or replace TRIGGER ITEM_PRICE_CRITERIA_VALUES 
BEFORE INSERT  OR UPDATE 
ON ITEM_PRICE_CRITERIA 
 REFERENCING NEW AS newRow 
FOR EACH ROW 
BEGIN 
   IF (:newRow.WARRANTY_TYPE IS NULL) THEN 
	   	   :newRow.wnty_type_name :=  'All Warranty Types'; 
	ELSE 
	      :newRow.wnty_type_name := :newRow.WARRANTY_TYPE; 
     END IF; 
	IF (:newRow.CLAIM_TYPE IS NULL) THEN 
	   	   :newRow.clm_type_name :=  'All Claim Types'; 
    ELSIF (:newRow.CLAIM_TYPE = 'CAMPAIGN') THEN 
        :newRow.clm_type_name :=  'FIELD MODIFICATION'; 
	ELSE 
	      :newRow.clm_type_name := :newRow.CLAIM_TYPE; 
     END IF; 
	 IF (:newRow.FOR_CRITERIA_PRODUCT_TYPE  IS NULL) THEN 
	   	   :newRow.product_name :=  'All Products'; 
	ELSE 
		 SELECT NAME  INTO :newRow.product_name 
		 FROM ITEM_GROUP  itemGroup 
   		 WHERE itemGroup.id = :newRow.FOR_CRITERIA_PRODUCT_TYPE ; 
     END IF; 
	 IF (:newRow.DEALER_GROUP IS NULL) THEN 
	 		IF (:newRow.DEALER IS NULL) THEN 
	   	   	   :newRow.identifier :=  'All Dealers'; 
			ELSE 
					SELECT NAME  INTO :newRow.identifier 
					 FROM PARTY  dealer 
   					  WHERE dealer.id =:newRow.DEALER ; 
     		END IF; 
	ELSE 
			SELECT NAME  INTO :newRow.identifier 
			 FROM DEALER_GROUP  dealerGroup 
   			  WHERE dealerGroup.id =:newRow.DEALER_GROUP ; 
     END IF; 
	 IF (:newRow.ITEM_CRITERION_ITEM  IS NOT NULL) THEN 
	 	 SELECT ITEM_NUMBER  INTO :newRow.item_Identifier 
		 FROM ITEM  itemSelected 
   		 WHERE itemSelected.id = :newRow.ITEM_CRITERION_ITEM ; 
	ELSE 
		 SELECT NAME  INTO :newRow.item_Identifier 
		 FROM ITEM_GROUP  itemGroup 
   		 WHERE itemGroup.id = :newRow.ITEM_CRITERION_ITEM_GROUP ; 
     END IF; 
END;
/
create or replace TRIGGER PART_RETURN_DEFINTION_VALUES
BEFORE INSERT  OR UPDATE
ON PART_RETURN_DEFINITION 
 REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
   IF (:newRow.WARRANTY_TYPE IS NULL) THEN 
	   	   :newRow.wnty_type_name :=  'All Warranty Types';
	ELSE	   
	      :newRow.wnty_type_name := :newRow.WARRANTY_TYPE;
     END IF;
	IF (:newRow.CLAIM_TYPE IS NULL) THEN 
	   	   :newRow.clm_type_name :=  'All Claim Types';
    ELSIF (:newRow.CLAIM_TYPE = 'CAMPAIGN') THEN 
        :newRow.clm_type_name :=  'FIELD MODIFICATION'; 
	ELSE	   
	      :newRow.clm_type_name := :newRow.CLAIM_TYPE;
     END IF;
	 IF (:newRow.FOR_CRITERIA_PRODUCT_TYPE  IS NULL) THEN 
	   	   :newRow.product_name :=  'All Products';
	ELSE
		 SELECT NAME  INTO :newRow.product_name
		 FROM ITEM_GROUP  itemGroup
   		 WHERE itemGroup.id = :newRow.FOR_CRITERIA_PRODUCT_TYPE ;	     
     END IF;
	 IF (:newRow.DEALER_GROUP IS NULL) THEN 
	 		IF (:newRow.DEALER IS NULL) THEN 
	   	   	   :newRow.identifier :=  'All Dealers';
			ELSE	
					SELECT NAME  INTO :newRow.identifier
					 FROM PARTY  dealer
   					  WHERE dealer.id =:newRow.DEALER ;	
     		END IF;
	ELSE	
			SELECT NAME  INTO :newRow.identifier
			 FROM DEALER_GROUP  dealerGroup
   			  WHERE dealerGroup.id =:newRow.DEALER_GROUP ;	
     END IF;
	 IF (:newRow.ITEM_CRITERION_ITEM  IS NOT NULL) THEN 
	 	 SELECT ITEM_NUMBER  INTO :newRow.item_Identifier
		 FROM ITEM  itemSelected
   		 WHERE itemSelected.id = :newRow.ITEM_CRITERION_ITEM ;
	ELSE
		 SELECT NAME  INTO :newRow.item_Identifier
		 FROM ITEM_GROUP  itemGroup
   		 WHERE itemGroup.id = :newRow.ITEM_CRITERION_ITEM_GROUP ;	     
     END IF;
END;
/
create or replace TRIGGER PAYMENT_MODIFIER_VALUES
BEFORE INSERT  OR UPDATE
ON PAYMENT_MODIFIER 
 REFERENCING NEW AS newRow
FOR EACH ROW
BEGIN
   IF (:newRow.WARRANTY_TYPE IS NULL) THEN 
	   	   :newRow.wnty_type_name :=  'All Warranty Types';
	ELSE	   
	      :newRow.wnty_type_name := :newRow.WARRANTY_TYPE;
     END IF;
	IF (:newRow.CLAIM_TYPE IS NULL) THEN 
	   	   :newRow.clm_type_name :=  'All Claim Types';
    ELSIF (:newRow.CLAIM_TYPE = 'CAMPAIGN') THEN 
        :newRow.clm_type_name :=  'FIELD MODIFICATION'; 
	ELSE	   
	      :newRow.clm_type_name := :newRow.CLAIM_TYPE;
     END IF;
	 IF (:newRow.FOR_CRITERIA_PRODUCT_TYPE  IS NULL) THEN 
	   	   :newRow.product_name :=  'All Products';
	ELSE
		 SELECT NAME  INTO :newRow.product_name
		 FROM ITEM_GROUP  itemGroup
   		 WHERE itemGroup.id = :newRow.FOR_CRITERIA_PRODUCT_TYPE ;	     
     END IF;
	 IF (:newRow.DEALER_GROUP IS NULL) THEN 
	 		IF (:newRow.DEALER IS NULL) THEN 
	   	   	   :newRow.identifier :=  'All Dealers';
			ELSE	
					SELECT NAME  INTO :newRow.identifier
					 FROM PARTY  dealer
   					  WHERE dealer.id =:newRow.DEALER ;	
     		END IF;
	ELSE	
			SELECT NAME  INTO :newRow.identifier
			 FROM DEALER_GROUP  dealerGroup
   			  WHERE dealerGroup.id =:newRow.DEALER_GROUP ;	
     END IF;
END;
/
UPDATE travel_rates SET version=version WHERE claim_type='CAMPAIGN'
/
UPDATE labor_rates SET version=version WHERE claim_type='CAMPAIGN'
/
UPDATE policy_rates SET version=version WHERE claim_type='CAMPAIGN'
/
UPDATE item_price_criteria SET claim_type=claim_type WHERE claim_type='CAMPAIGN'
/
UPDATE part_return_definition SET version=version WHERE claim_type='CAMPAIGN'
/
UPDATE payment_modifier SET version=version WHERE claim_type='CAMPAIGN'
/
UPDATE domain_predicate 
SET predicate_asxml=replace(predicate_asxml,'claim.type.type','claim.type.displayType')
WHERE predicate_asxml LIKE '%claim.type.type%'
/
UPDATE inbox_view
SET field_names=replace(field_names,'claim.type','claim.clmTypeName')
WHERE field_names LIKE '%claim.type%'
/
commit
/