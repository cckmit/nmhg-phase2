--Purpose    : Creating indexes to improve performance in data migration
--Created On : 26-Apr-2010
--Created By : Rahul Katariya
--Impact     : CUstomer and Address_Book_Address_mapping

CREATE INDEX CUSTOMER_ID_I1 ON CUSTOMER (CUSTOMER_ID)
/
CREATE INDEX ADD_BOOK_ID_ADD_ID_I1 ON ADDRESS_BOOK_ADDRESS_MAPPING (ADDRESS_BOOK_ID, ADDRESS_ID)
/
commit
/