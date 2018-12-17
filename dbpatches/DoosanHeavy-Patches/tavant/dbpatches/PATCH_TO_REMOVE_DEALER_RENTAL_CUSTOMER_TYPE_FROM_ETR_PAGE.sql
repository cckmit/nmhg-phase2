--PURPOSE    : Patch to remove "Dealer Rental" customer type from ETR page. 
--AUTHOR     : Amrita Rout
--CREATED ON : 20-August-2012
delete from config_param_options_mapping where param_id in(select id from config_param where display_name ='Customer Types Displayed in ETR') 
and option_id in (select  id from config_param_option where value = 'Dealer Rental')
/
delete from config_value where config_param in(select id from config_param where display_name ='Customer Types Displayed in ETR') and config_param_option in(select  id from config_param_option where value = 'Dealer Rental')
/
commit
/