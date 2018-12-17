--Purpose : Increasing the size of reason_for_additional_hours column of labor_detail table
--Author  : Data Migration Team
--Date    : 29 July 2009

alter table labor_detail modify ( reason_for_additional_hours varchar2(2000))
/
commit
/