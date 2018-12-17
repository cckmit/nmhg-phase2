--Purpose    : Updated Trigger to remove the Schema name(TWMS_OWNER) from the trigger name
--Author     : shraddha.nanda
--Created On : 22-Aug-08


Drop Trigger UPDATE_CUSTOMER
/
CREATE OR REPLACE TRIGGER UPDATE_CUSTOMER
BEFORE  INSERT OR UPDATE
ON CUSTOMER
REFERENCING NEW AS NEW OLD AS OLD
FOR EACH ROW
BEGIN
    IF (:NEW.CUSTOMER_ID IS NULL) THEN
	   	   :NEW.CUSTOMER_ID := 'TAV' || TO_CHAR(:NEW.id)	;
	END IF ;
END;
/
commit
/
