--Purpose    : Updating config_param to add the BU Flag 'isAlarmCodesSectionVisible' in the 'CLAIM_INPUT_PARAMETERS' section
--Author     : ajitkumar.singh
--Created On : 18/05/2011
update config_param set param_display_type='radio',logical_group='CLAIMS',sections='CLAIM_INPUT_PARAMETERS' where name='isAlarmCodesSectionVisible'
/
commit
/