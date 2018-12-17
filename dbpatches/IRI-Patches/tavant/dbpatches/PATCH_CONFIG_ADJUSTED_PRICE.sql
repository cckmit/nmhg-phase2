--PURPOSE    : PATCH FOR ADDING CONFIG_PARAM FOR ADJUSTED PRICE
--AUTHOR     : GYANENDRA BISWANATH MISHRA
--CREATED ON : 11-MAR-09

insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE)
values (config_param_seq.nextval,'useAdjustedPriceOnClaim','Use adjusted price from ERP remote system for claim calculation','useAdjustedPriceOnClaim','boolean','radio')
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='useAdjustedPriceOnClaim'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'))
/
insert into config_param_options_mapping 
values ( CFG_PARAM_OPTNS_MAPPING_SEQ.nextval, (select id from  config_param cp where name='useAdjustedPriceOnClaim'),
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='No')) 
/
insert into config_value  (ID, ACTIVE, CONFIG_PARAM, BUSINESS_UNIT_INFO, CONFIG_PARAM_OPTION)
values (config_value_seq.nextval,1,(select id from  config_param cp where name='useAdjustedPriceOnClaim'),'AIR',
(select id from  config_param_option cpo where cpo.DISPLAY_VALUE ='Yes'))
/
COMMIT
/