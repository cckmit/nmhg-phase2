--Purpose    : To have more columns in staging uploads
--Author     : Jhulfikar Ali. A
--Created On : 10-Feb-09

alter table STG_JOB_CODE add system_code varchar2(4000)
/
alter table STG_JOB_CODE add sub_system_code varchar2(4000)
/
alter table STG_JOB_CODE add component_code varchar2(4000)
/
alter table STG_JOB_CODE add sub_component_code varchar2(4000)
/
alter table STG_JOB_CODE add immediate_parent_code varchar2(4000)
/
commit
/