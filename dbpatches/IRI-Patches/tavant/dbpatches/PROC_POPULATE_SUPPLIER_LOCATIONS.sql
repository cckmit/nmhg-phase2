--Purpose    : To populate id column of Supplier_Locations table
--Author     : Jhulfikar Ali. A
--Created On : 18-Dec-08

CREATE OR REPLACE PROCEDURE POPULATE_SUPPLIER_LOCATIONS
AS
CURSOR all_rec IS
SELECT *
FROM SUPPLIER_LOCATIONS;

BEGIN

  FOR each_dealer_rec IN all_rec
  LOOP
 update supplier_locations 
 set id = SUPPLIER_LOCATIONS_SEQ.nextval 
 where supplier = each_dealer_rec.supplier 
 and locations =  each_dealer_rec.locations
 and locations_mapkey = each_dealer_rec.locations_mapkey;

 commit;
  END LOOP;
END;
/
BEGIN
POPULATE_SUPPLIER_LOCATIONS();
END;
/
COMMIT
/