--Purpose:    Increasing the answer value column size since R3 has more than this size
--Author:     Jhulfikar Ali. A
--Created On: Date 29 Aug 2008

alter table REPORT_FORM_ANSWER modify (ANSWER_VALUE varchar2(512))
/
commit
/