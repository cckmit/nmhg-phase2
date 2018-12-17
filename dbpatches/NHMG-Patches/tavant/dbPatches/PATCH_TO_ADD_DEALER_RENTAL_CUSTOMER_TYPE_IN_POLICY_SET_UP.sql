select cfg_param_optns_mapping_seq.NEXTVAL from dual
/
insert into config_param_option values (config_param_option_seq.nextval,'Dealer Rental','DealerRental')
/
INSERT INTO config_param_options_mapping (id, param_id,option_id) VALUES(cfg_param_optns_mapping_seq.NEXTVAL,(SELECT id FROM config_param WHERE DISPLAY_NAME='Customer Types Displayed in Policy'),(SELECT id FROM config_param_option WHERE DISPLAY_VALUE ='Dealer Rental'))
/
commit
/