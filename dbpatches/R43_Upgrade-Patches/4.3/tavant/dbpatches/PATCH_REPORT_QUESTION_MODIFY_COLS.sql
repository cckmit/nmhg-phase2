--Purpose    : Increase the size of text fields for report question and i18n text as a part of 4.3 upgrade 
--Created On : 16-April-2010
--Created By : Rashmi Malik
--Impact     : Report Form Question

ALTER TABLE REPORT_FORM_QUESTION MODIFY NAME VARCHAR2(4000)
/
ALTER TABLE REPORTI18NTEXT MODIFY DESCRIPTION VARCHAR2(4000)
/
ALTER TABLE CUSTOM_REPORT_INSTRUCTIONS MODIFY INSTRUCTIONS VARCHAR2(4000)
/