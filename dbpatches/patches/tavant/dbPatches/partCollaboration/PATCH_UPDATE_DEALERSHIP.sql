--Purpose    : Dropping Business Unit Info Column to avoid log issues and which is not required
--Author     : Jhulfikar Ali. A
--Created On : 12-Nov-2008
--Created By : Jhulfikar Ali. A

ALTER TABLE dealership 
DROP COLUMN business_unit_info
/
COMMIT
/