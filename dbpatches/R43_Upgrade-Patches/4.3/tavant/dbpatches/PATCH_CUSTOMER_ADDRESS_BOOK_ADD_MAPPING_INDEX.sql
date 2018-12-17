--Purpose    : Creating indexes to improve performance in data migration, changes done as part of 4.3 upgrade
--Created On : 12-Oct-2010
--Created By : Kuldeep Patil
--Impact     : CUstomer and Address_Book_Address_mapping

CREATE INDEX CUSTOMER_ID_I1 ON CUSTOMER (CUSTOMER_ID)
/
CREATE INDEX ADD_BOOK_ID_ADD_ID_I1 ON ADDRESS_BOOK_ADDRESS_MAPPING (ADDRESS_BOOK_ID, ADDRESS_ID)
/
commit
/