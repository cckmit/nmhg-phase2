--Purpose    : BU Config for Currency to be used for ERP Interactions,changes made as a part of 4.3 upgrade
--Created On : 22-Apr-2010
--Created By : Rahul Katariya
--Impact     : None

--Update claim_number_pattern
--Set Template = 'PREFIX-'||Template, Pattern_Type = 'TKAW'
--where template = 'NNNNNNNN' and sequence_name = 'claim_number_seq' 
--/
--Update claim_number_pattern
--Set Template = 'TKAD-NNNNNNN'
--where template = 'TYP-NNNNNNN' and sequence_name = 'dcap_claim_number_seq'
--/
Commit
/

