--Purpose    : Changes to claim number pattern as requested by TK TSA,changes made as a part of 4.3 upgrade
--Created On : 08-Jun-2010
--Created By : Rahul Katariya
--Impact     : Claim number pattern and credit notification


--UPDATE CLAIM_NUMBER_PATTERN
--SET PATTERN_TYPE = 'W'
--where id = 1
--/
--UPDATE CLAIM_NUMBER_PATTERN
--SET Template = 'D-NNNNNNN'
--where id = 2
--/
--Update claim_number_pattern
--Set Template = 'PREFIX-'||Template, Pattern_Type = 'TKAW'
--where template = 'NNNNNNNN' and sequence_name = 'claim_number_seq' 
--/
--Update claim_number_pattern
--Set Template = 'TKAD-NNNNNNN'
--where template = 'TYP-NNNNNNN' and sequence_name = 'dcap_claim_number_seq'--/
COMMIT
/