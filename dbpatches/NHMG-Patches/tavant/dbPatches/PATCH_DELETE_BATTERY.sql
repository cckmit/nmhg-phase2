--purpose : PATCH FOR deleting "Is battery labor details applicable"

--Created On: Date 05-04-2012

delete from config_param_options_mapping where  param_id =(select ID from config_param where display_name='Is battery labor details applicable')
/
delete from config_value where config_param =(select ID from config_param where display_name='Is battery labor details applicable')
/
delete  from config_param where id =(select ID from config_param where display_name='Is battery labor details applicable')
/
commit
/