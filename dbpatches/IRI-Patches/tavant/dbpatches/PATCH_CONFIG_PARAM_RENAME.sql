update config_param
set display_name = 'Days to be Used for Warranty Registration Modification/Deletion'
where display_name like 'Days to be Used for Warranty Registration Deletion%'
/
update config_param
set display_name = 'Date to be Used for Warranty Registration Modification/Deletion'
where display_name like 'Date to be Used for Warranty Registration Deletion%'
/
commit
/