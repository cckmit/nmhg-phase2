--Purpose    : inventory transaction ship to site number
--Author     : Hari Krishna Y D
--Created On : 18-May-09

ALTER TABLE Inventory_Transaction ADD (SHIP_TO_SITE_NUMBER VARCHAR2(255))
/
COMMIT
/