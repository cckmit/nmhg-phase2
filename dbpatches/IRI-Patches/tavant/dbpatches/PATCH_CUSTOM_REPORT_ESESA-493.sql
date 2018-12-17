--PURPOSE    : PATCH FOR ESESA-493
--AUTHOR     : Vamshi Gunda
--CREATED ON : 24-Nov-09
alter table report_form_question add (for_Section number)
/
ALTER TABLE report_form_question ADD CONSTRAINT 
FOR_SECTION_FK FOREIGN KEY (for_Section) REFERENCES report_section(id)
/
update report_form_question a set for_section=(select for_section from questionnaire where questionnaire.questionnaire= a.id)
/
commit
/
create table report_section_questionnaire as (select * from questionnaire)
/
drop table questionnaire
/