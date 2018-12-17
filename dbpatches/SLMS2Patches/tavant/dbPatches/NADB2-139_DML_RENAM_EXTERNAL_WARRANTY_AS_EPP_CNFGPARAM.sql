--Purpose    : Patch for renaming 'Extended Warranty' to 'EPP' 
--Author     : Priyanka S
--Created On : 13-DEC-2013
update config_param set display_name = 'Enable EPP Purchase' where name = 'canExternalUserPurchaseExtendedWarranty'
/
update config_param set display_name = 'Date from which EPP Purchase will start' where name = 'dateConsideredForEWP'
/
update config_param set display_name = 'EPP Purchase Logic Driven by' where name = 'dateConsideredForExtendedPlanAvailability'
/
update config_param set display_name = 'Is EPP Price Check Enabled' where name = 'extWarrantyPriceCheckEnabled'
/
COMMIT
/