--Purpose    : Added a new column ANSWER_DATE in REPORT_FORM_ANSWER table to store the dates
--Author     : Pradyot.rout
--Created On : 01-Apr-10

alter table report_form_answer add  answer_date date
/
COMMIT
/