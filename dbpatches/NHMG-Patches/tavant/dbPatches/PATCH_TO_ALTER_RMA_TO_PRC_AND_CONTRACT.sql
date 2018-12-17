--Purpose    : Patch for adding the column rmaNumber in part_return_configuration and contract table.
--Author     : Deepak Patel
--Created On : 07-dec-2012
--Note Deleted patches PATCH_TO_ADD_RMANUMBER_IN_PART_RETURN_CONFIGURATION_TABLE.sql
-- PATCH_TO_ADD_RMANUMBER_IN_CONTRACT_TABLE.sql
-- PATCH_TO_ADD_RMA_NUMBER_IN_PART_RETURN_CONFIGURATION_&_CONTRACT_TABLES.sql


ALTER TABLE part_return_configuration MODIFY RMA_NUMBER VARCHAR2(255 CHAR)
/
ALTER TABLE contract MODIFY RMA_NUMBER VARCHAR2(255 CHAR)
/