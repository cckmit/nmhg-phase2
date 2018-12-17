--Purpose    : Patch for renaming ''Is Miscellaneous Parts Section is visible?' to 'Is Miscellaneous Expense and Outside Services Section visible?' 
--Author     : Priyanka S
--Created On : 6-MAY-2014
update config_param set display_name = 'Is Miscellaneous Expense and Outside Services Section visible?' where name = 'isMiscPartsSectionVisible'
/
COMMIT
/