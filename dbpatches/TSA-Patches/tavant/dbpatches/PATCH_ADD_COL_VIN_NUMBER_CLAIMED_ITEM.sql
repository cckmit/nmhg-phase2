--Purpose    : vin_number is  added to CLAIMED_ITEM table
--Author     : Mayank Vikram
--Created On : 24/03/10
--Impact     : None

ALTER TABLE CLAIMED_ITEM ADD vin_number VARCHAR2(255 CHAR)
/
COMMIT
/