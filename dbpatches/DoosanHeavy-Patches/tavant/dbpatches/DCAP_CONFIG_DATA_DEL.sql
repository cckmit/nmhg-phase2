delete from config_value where config_param_option  in (select id from config_param_option where value = 'dcapDealer')
/
delete from config_param_options_mapping where option_id  in (select id from config_param_option where value = 'dcapDealer')
/
delete from config_param_option where id  in (select id from config_param_option where value = 'dcapDealer')
/
delete from dealer_scheme_purposes  where purposes in  (select id from purpose where name = 'DCAP')
/
delete from USER_SCHEME_PURPOSES where PURPOSES  in  (select id from purpose where name = 'DCAP')
/
delete from purpose where id in  (select id from purpose where name = 'DCAP')
/
delete from dealer_scheme_purposes  where purposes in  (select id from purpose where name = 'SEA')
/
delete from USER_SCHEME_PURPOSES where PURPOSES  in  (select id from purpose where name = 'SEA')
/
delete from purpose where id in  (select id from purpose where name = 'SEA')
/
delete from dealer_scheme_purposes  where purposes in  (select id from purpose where name = 'SCA')
/
delete from USER_SCHEME_PURPOSES where PURPOSES  in  (select id from purpose where name = 'SCA')
/
delete from purpose where id in  (select id from purpose where name = 'SCA')
/
delete from dealers_in_group where dealer_group in (select id from DEALER_GROUP where scheme in (select id from dealer_scheme where name = 'DCAP'))
/
delete from DEALER_GROUP where scheme in (select id from dealer_scheme where name = 'DCAP')
/
delete from dealer_scheme_purposes  where purposes in  (select id from purpose where name = 'DCAP')
/
delete from dealer_scheme_purposes  where dealer_scheme in  (select id from dealer_scheme where name = 'DCAP')
/
delete from dealer_scheme  where name = 'DCAP'
/
commit
/