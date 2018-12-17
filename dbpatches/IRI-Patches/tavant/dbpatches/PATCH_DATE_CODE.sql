--Purpose    : Created a new BU Config to Data Code for TFM
--Author     : Jhulfikar Ali. A
--Created On : 05-Feb-09

alter table claim add date_code varchar2(255)
/
insert into config_param(
ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, 
D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, 
PARAM_DISPLAY_TYPE, LOGICAL_GROUP, LOGICAL_GROUP_ORDER, SECTIONS, 
SECTIONS_ORDER, PARAM_ORDER, D_ACTIVE)
values (config_param_seq.nextval, 'This configuration species whether Date Code applicable or not', 
'Is Date Code enabled', 'isDateCodeEnabled', 'boolean', sysdate, 
'System: Created for Date Code Applicability', sysdate, 
'', 'radio', 'Claim Inputs', 1, 'Claim Inputs Parameter', 1, 1, 1)
/
insert into config_param_options_mapping values 
((select max(id)+1 from config_param_options_mapping), 
(select id from config_param where name = 'isDateCodeEnabled'), 
(select id from config_param_option where value = 'true')
)
/
insert into config_param_options_mapping values 
((select max(id)+1 from config_param_options_mapping), 
(select id from config_param where name = 'isDateCodeEnabled'), 
(select id from config_param_option where value = 'false')
)
/
COMMIT
/