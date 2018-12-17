--Purpose    : Updated Trigger to remove the Schema name(TWMS_OWNER) from the trigger name
--Author     : shraddha.nanda
--Created On : 22-Aug-08


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
	IF (:newRow.CLAIM_TYPE ='ALL') THEN 
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



