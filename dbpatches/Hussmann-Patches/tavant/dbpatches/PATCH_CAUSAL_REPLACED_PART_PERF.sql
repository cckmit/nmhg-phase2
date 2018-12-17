--First of all singh is king.
--Created on: 18 Mar 09
--Priyank Gupta

--This is a patch to update the values of Parts kits and options to part kit and option, as I need to substitue these values directly in my query to fetch causal and rpelaced items
--and parts kits and options represent product type while i need model names which are part kit and option. :) This saves me a one whole dirty SQL my friend, so treat this
--update with some respect.

INSERT INTO CONFIG_PARAM_OPTION (ID, DISPLAY_VALUE, VALUE) VALUES (CONFIG_PARAM_OPTION_SEQ.NEXTVAL, 'Part', 'Part')
/
UPDATE CONFIG_VALUE SET CONFIG_PARAM_OPTION = (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Part') WHERE CONFIG_PARAM  IN (SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('causalItemsOnClaimConfiguration','replacedItemsOnClaimConfiguration'))
/
UPDATE CONFIG_PARAM_OPTION SET DISPLAY_VALUE = SUBSTR(DISPLAY_VALUE,0,LENGTH(DISPLAY_VALUE)-1), VALUE = SUBSTR(VALUE,0,LENGTH(VALUE)-1) WHERE UPPER(DISPLAY_VALUE) IN ('KITS','OPTIONS')
/
delete from  CONFIG_PARAM_OPTIONs_mapping WHERE param_id in (SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('causalItemsOnClaimConfiguration','replacedItemsOnClaimConfiguration')) and option_id in (SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Parts')
/
insert into config_param_options_mapping (id, param_id, option_id) values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,(SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('replacedItemsOnClaimConfiguration')),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Part'))
/
insert into config_param_options_mapping (id, param_id, option_id) values (CFG_PARAM_OPTNS_MAPPING_SEQ.nextval,(SELECT ID FROM CONFIG_PARAM WHERE NAME IN ('causalItemsOnClaimConfiguration')),(SELECT ID FROM CONFIG_PARAM_OPTION WHERE VALUE = 'Part'))
/
COMMIT
/