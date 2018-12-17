--Purpose    : Updated the trigger to store ALL  for Claim type ALL
--Author     : pradyot.rout
--Created On : 13-Nov-08


Drop Trigger TRAVEL_RATES_VALUES
/
CREATE OR REPLACE TRIGGER TRAVEL_RATES_VALUES
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
commit
/
Drop Trigger POLICY_RATES_VALUES
/
CREATE OR REPLACE TRIGGER POLICY_RATES_VALUES
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
commit
/
Drop Trigger PAYMENT_MODIFIER_VALUES
/
CREATE OR REPLACE TRIGGER PAYMENT_MODIFIER_VALUES
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
commit
/
Drop Trigger PART_RETURN_DEFINTION_VALUES
/
CREATE OR REPLACE TRIGGER PART_RETURN_DEFINTION_VALUES
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
commit
/
Drop Trigger LABOR_RATES_VALUES
/
CREATE OR REPLACE TRIGGER LABOR_RATES_VALUES
BEFORE INSERT  OR UPDATE
ON LABOR_RATES 
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
commit
/
Drop Trigger ITEM_PRICE_CRITERIA_VALUES
/
CREATE OR REPLACE TRIGGER ITEM_PRICE_CRITERIA_VALUES
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
COMMIT
/