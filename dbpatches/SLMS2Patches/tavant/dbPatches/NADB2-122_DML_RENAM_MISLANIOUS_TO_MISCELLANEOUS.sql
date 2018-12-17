--Purpose    : Patch for renaming 'Mislanious' to 'Miscellaneous' in 'Is Mislanious Parts Section is visible?' 
--Author     : Priyanka S
--Created On : 13-DEC-2013
update config_param set display_name = 'Is Miscellaneous Parts Section is visible?' where name = 'isMiscPartsSectionVisible'
/
COMMIT
/