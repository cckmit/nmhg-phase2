--Purpose    : Updating name to 'Miscellaneous Parts' in line_item_group table where name is showing 'MiscellaneousParts' that has no space in between  
--Author     : ajitkumar.singh
--Created On : 28/04/2011

update line_item_group set name='Miscellaneous Parts' where name='MiscellaneousParts'
/
commit
/
