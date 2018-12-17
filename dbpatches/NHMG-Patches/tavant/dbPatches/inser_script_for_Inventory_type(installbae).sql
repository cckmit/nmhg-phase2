-- Patch to insert into config_param_options_mapping to add Government Account customer type to customerTypesForModifiers and wntyConfigCustomerTypesAllowedinSearchResult
-- Author: PARTHASARATHY R
-- Created On : 30-OCT-2012

INSERT
INTO INVENTORY_TYPE
  (TYPE,
  VERSION,
  D_CREATED_ON,
	D_INTERNAL_COMMENTS,
	D_UPDATED_ON,
	D_LAST_UPDATED_BY,
	D_CREATED_TIME,
	D_UPDATED_TIME,
	D_ACTIVE
  )
  VALUES
  (
  'OEM_STOCK',
	1,
	CURRENT_TIMESTAMP,
	'Migration',
	CURRENT_TIMESTAMP,
	1,
	CURRENT_TIMESTAMP,
	CURRENT_TIMESTAMP,
	1
  )
/
COMMIT
/