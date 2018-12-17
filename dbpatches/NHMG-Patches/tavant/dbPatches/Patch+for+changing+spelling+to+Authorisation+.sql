--Purpose    : Patch for changing spelling to Authorisation 
--Author     : pallavi kawware	
--Created On : 22-april-2013

update config_param set description='Show Authorisation Number',display_name='Show Authorisation Number' where description='Show Authorization Number' 
/
update config_param set description='Show Authorisation Received',display_name='Show Authorisation Received' where description='Show Authorization Received'
/
commit
/