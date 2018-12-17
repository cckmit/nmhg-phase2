--Purpose    : Added scripts for updating answer type to appropriate value
--Created On : 24-Aug-2011
--Created By : Kuldeep Patil
--Impact     : None

UPDATE REPORT_FORM_QUESTION B SET ANSWER_TYPE = (
SELECT 
	CASE ANSWER_TYPE 
	when 'textbox' THEN 'SMALL_TEXT'
	WHEN 'radio' THEN 'SINGLE_SELECT'
	WHEN 'listbox' THEN 'SINGLE_SELECT_LIST'
	WHEN 'checkbox' THEN 'MULTI_SELECT'
	END
FROM REPORT_FORM_QUESTION A WHERE A.ID = B.ID)
/
COMMIT
/