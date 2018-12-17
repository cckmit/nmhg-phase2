--Purpose    : To populate the id column and assigning Primary key constraint to the id column
--Author     : Jhulfikar Ali. A
--Created On : 18-Dec-08

BEGIN
	POPULATE_SUPPLIER_LOCATIONS;
END;
/
ALTER TABLE supplier_locations
add CONSTRAINT supplier_locations_pk PRIMARY KEY (id)
/
COMMIT
/