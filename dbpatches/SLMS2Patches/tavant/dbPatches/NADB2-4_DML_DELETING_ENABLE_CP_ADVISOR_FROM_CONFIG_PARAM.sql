--Purpose    : Patch for removing "Enable Cp Review" from BU Configuration
--Author     : Priyanka S
--Created On : 13-DEC-2013

DELETE FROM config_param_options_mapping WHERE param_id IN (SELECT ID FROM config_param WHERE display_name = 'Enable CP Advisor')
/
DELETE FROM config_value WHERE config_param IN (SELECT ID FROM config_param WHERE display_name = 'Enable CP Advisor')
/
DELETE FROM config_param WHERE display_name ='Enable CP Advisor'
/
commit
/