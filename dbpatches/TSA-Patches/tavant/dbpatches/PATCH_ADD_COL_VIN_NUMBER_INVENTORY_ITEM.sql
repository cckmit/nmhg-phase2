--Purpose    : vin_number is  added to inventory_item table
--Author     : Mayank Vikram
--Created On : 18/03/10
--Impact     : None

ALTER TABLE inventory_item ADD vin_number VARCHAR2(255 CHAR)
/
COMMIT
/