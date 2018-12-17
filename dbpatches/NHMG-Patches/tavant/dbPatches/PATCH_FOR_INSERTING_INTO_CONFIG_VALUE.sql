-- Patch to insert into config value
-- Author: PARTHASARATHY R
-- Created On : 09-NOV-2012

INSERT
INTO CONFIG_VALUE
  (
    ID,
	ACTIVE,
	CONFIG_PARAM,
	CONFIG_PARAM_OPTION,
	D_ACTIVE,
	D_LAST_UPDATED_BY,
	BUSINESS_UNIT_INFO
  )
  VALUES
  (
	CONFIG_VALUE_SEQ.nextval,
	1,
	(SELECT ID FROM CONFIG_PARAM WHERE NAME='displayPdiLinks'),
	(SELECT ID from CONFIG_PARAM_OPTION WHERE VALUE='true'),
	1,
	NULL,
	'EMEA'
  )
/
COMMIT
/