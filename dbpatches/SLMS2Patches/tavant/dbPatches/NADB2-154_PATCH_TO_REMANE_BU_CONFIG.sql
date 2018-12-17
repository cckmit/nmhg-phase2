--Purpose    : Patch for renaming BU Config
--Author     : ParthaSarathy R
--Created On : 19-May-2014

update config_param set display_name = 'Is Miscellaneous Parts Section visible?' where name = 'isMiscPartsSectionVisible'
/
commit
/