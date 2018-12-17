--Purpose    : Patch for adding the column FAX in ADDRESS_FOR_TRANSFER table.
--Author     : Jyoti Chauhan
--Created On : 24-DEC-2012

ALTER TABLE ADDRESS_FOR_TRANSFER ADD FAX VARCHAR2(255)
/