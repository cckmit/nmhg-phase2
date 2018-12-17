--added by kalayani for adding the BU config parameter for allowing processor to take actiona on 
--date:10-102013
update config_param set display_name='Is dealer allowed take action on claim during EPO is down?',
description='Is dealer allowed take action on claim during EPO is down?' where display_name='Is dealer is allowed take action on claim during EPO is down?'
/
update config_param set display_name='Is processor allowed take action on claim during EPO is down?',
description='Is processor allowed take action on claim during EPO is down?' where display_name='Is processor is allowed take action on claim during EPO is down?'
/
commit
/