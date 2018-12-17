-- PURPOSE    : Patch To Add BU CONFIGs for Supplier Part Return Routing
-- AUTHOR     : ParthaSarathy R
-- CREATED ON : 05-Mar-2014

INSERT 
INTO config_param 
	(
		ID,
		DESCRIPTION,
		DISPLAY_NAME,NAME,
		TYPE,D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,
		D_UPDATED_TIME,
		D_ACTIVE,
		PARAM_DISPLAY_TYPE,
		LOGICAL_GROUP,
		LOGICAL_GROUP_ORDER,
		SECTIONS,
		SECTIONS_ORDER,
		PARAM_ORDER
	) 
VALUES (
		CONFIG_PARAM_SEQ.nextval,
		'Route Supplier Part Return Requests to VR Admin',
		'Route Supplier Part Return Requests to VR Admin',
		'supplierPartReturnRoutingRequired',
		'boolean',
		'',
		'',
		'',
		56,
		'',
		'',
		1,
		'radio',
		'SUPPLIER_RECOVERY',
		1,
		null,
		1,
		1
)
/
INSERT INTO config_param_options_mapping 
	(
		ID,
		PARAM_ID,
		OPTION_ID) 
		VALUES 
		(CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
		(SELECT id FROM config_param cp WHERE cp.name='supplierPartReturnRoutingRequired'),
		(select id from config_param_option where value='false')
	)
/
INSERT INTO config_param_options_mapping 
(
		ID,
		PARAM_ID,
		OPTION_ID) 
		VALUES 
		(CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,
		(SELECT id FROM config_param cp WHERE cp.name='supplierPartReturnRoutingRequired'),
		(select id from config_param_option where value='true')
)
/
INSERT INTO config_value 
	(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
	) 
VALUES 
	(
		CONFIG_VALUE_SEQ.nextval,
		1,
		null,
		(SELECT id FROM config_param cp WHERE cp.name='supplierPartReturnRoutingRequired'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'AMER',
		(select id from config_param_option where value='true')
	)
/
INSERT INTO config_value 
	(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
	) 
VALUES 
	(
		CONFIG_VALUE_SEQ.nextval,
		1,
		null,
		(SELECT id FROM config_param cp WHERE cp.name='supplierPartReturnRoutingRequired'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'EMEA',
		(select id from config_param_option where value='false')
	)
/
INSERT 
INTO config_param 
	(
		ID,
		DESCRIPTION,
		DISPLAY_NAME,NAME,
		TYPE,D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,
		D_UPDATED_TIME,
		D_ACTIVE,
		PARAM_DISPLAY_TYPE,
		LOGICAL_GROUP,
		LOGICAL_GROUP_ORDER,
		SECTIONS,
		SECTIONS_ORDER,
		PARAM_ORDER
	) 
VALUES (
		CONFIG_PARAM_SEQ.nextval,
		'Threshold for Recovery Claim Total Amount',
		'Threshold for Recovery Claim Total Amount',
		'recClaimThresholdAmount',
		'number',
		'',
		'',
		'',
		56,
		'',
		'',
		1,
		'textbox',
		'SUPPLIER_RECOVERY',
		1,
		null,
		1,
		1
)
/
INSERT INTO config_value 
	(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
	) 
VALUES 
	(
		CONFIG_VALUE_SEQ.nextval,
		1,
		50,
		(SELECT id FROM config_param cp WHERE cp.name='recClaimThresholdAmount'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'AMER',
		null
	)
/
INSERT INTO config_value 
	(
		ID,
		ACTIVE,
		VALUE,
		CONFIG_PARAM,
		D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,D_UPDATED_TIME,
		D_ACTIVE,
		BUSINESS_UNIT_INFO,
		CONFIG_PARAM_OPTION
	) 
VALUES 
	(
		CONFIG_VALUE_SEQ.nextval,
		1,
		0,
		(SELECT id FROM config_param cp WHERE cp.name='recClaimThresholdAmount'),
		'',
		null,
		'',
		56,
		'',
		'',
		1,
		'EMEA',
		null
	)
/
INSERT 
INTO config_param 
	(
		ID,
		DESCRIPTION,
		DISPLAY_NAME,NAME,
		TYPE,D_CREATED_ON,
		D_INTERNAL_COMMENTS,
		D_UPDATED_ON,
		D_LAST_UPDATED_BY,
		D_CREATED_TIME,
		D_UPDATED_TIME,
		D_ACTIVE,
		PARAM_DISPLAY_TYPE,
		LOGICAL_GROUP,
		LOGICAL_GROUP_ORDER,
		SECTIONS,
		SECTIONS_ORDER,
		PARAM_ORDER
	) 
VALUES (
		CONFIG_PARAM_SEQ.nextval,
		'Dealer Country',
		'Dealer Country',
		'recClaimRoutingDealerCountries',
		'java.lang.String',
		'',
		'',
		'',
		56,
		'',
		'',
		1,
		'multiselect',
		'SUPPLIER_RECOVERY',
		1,
		null,
		1,
		1
)
/
commit
/
DECLARE
CURSOR country_cursor IS
SELECT name country_name, code country_code from country order by name;
option_seq NUMBER;
mapping_seq NUMBER;
param_id NUMBER;
BEGIN
  SELECT ID INTO param_id from config_param where name='recClaimRoutingDealerCountries';
  FOR country_index in country_cursor
  LOOP
    BEGIN
      SELECT CONFIG_PARAM_OPTION_SEQ.NEXTVAL INTO option_seq FROM dual;
      SELECT CFG_PARAM_OPTNS_MAPPING_SEQ.nextval INTO mapping_seq FROM dual;
      insert into config_param_option
      values
        (
          option_seq,
		  country_index.country_name,
          country_index.country_code
        );
      insert into config_param_options_mapping
      values
        (
          mapping_seq,
		  param_id,
          option_seq
        );
    END;
  END LOOP;
END;
/
COMMIT
/