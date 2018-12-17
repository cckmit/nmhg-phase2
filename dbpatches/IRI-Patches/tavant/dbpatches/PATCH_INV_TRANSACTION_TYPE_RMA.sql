-- Purpose    : Added Missing Incidental Cost Categories in the Campaign service detail
-- Author     : Jitesh Jain
-- Created On : 18-Jun-09

delete from INVENTORY_TYPE where type = 'RMA'
/
INSERT INTO INVENTORY_TRANSACTION_TYPE (ID, TRNX_TYPE_KEY, TRNX_TYPE_VALUE, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_ACTIVE)
VALUES ((SELECT MAX(ID)+1 FROM INVENTORY_TRANSACTION_TYPE), 'RMA', 'RMA', 0, SYSDATE, 'IRI-Migration', SYSDATE, 1)
/
commit
/