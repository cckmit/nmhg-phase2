-- Patch to insert LOV values for Discount Type
-- Created On : 09-Sep-14
-- Created by : Chetan K
insert into list_of_values(type,code,description,state,version,d_created_on,d_updated_on,d_last_updated_by,id,business_unit_info,d_created_time,d_updated_time,d_active)
values('DISCOUNTTYPE','SPR','SPR','active',0,sysdate,sysdate,56,List_Of_Values_SEQ.nextval,'EMEA',systimestamp,systimestamp,1)
/
insert into i18nlov_text(id,locale,description,list_of_i18n_values)
values (I18N_Lov_Text_SEQ.nextval,'en_US','SPR',(select id from list_of_values where code = 'SPR' and business_unit_info='EMEA'))
/
insert into list_of_values(type,code,description,state,version,d_created_on,d_updated_on,d_last_updated_by,id,business_unit_info,d_created_time,d_updated_time,d_active)
values('DISCOUNTTYPE','SPR','SPR','active',0,sysdate,sysdate,56,List_Of_Values_SEQ.nextval,'AMER',systimestamp,systimestamp,1)
/
insert into i18nlov_text(id,locale,description,list_of_i18n_values)
values (I18N_Lov_Text_SEQ.nextval,'en_US','SPR',(select id from list_of_values where code = 'SPR' and business_unit_info='AMER'))
/
insert into list_of_values(type,code,description,state,version,d_created_on,d_updated_on,d_last_updated_by,id,business_unit_info,d_created_time,d_updated_time,d_active)
values('DISCOUNTTYPE','MATRIX','MATRIX','active',0,sysdate,sysdate,56,List_Of_Values_SEQ.nextval,'EMEA',systimestamp,systimestamp,1)
/
insert into i18nlov_text(id,locale,description,list_of_i18n_values)
values (I18N_Lov_Text_SEQ.nextval,'en_US','MATRIX',(select id from list_of_values where code = 'MATRIX' and business_unit_info='EMEA'))
/
insert into list_of_values(type,code,description,state,version,d_created_on,d_updated_on,d_last_updated_by,id,business_unit_info,d_created_time,d_updated_time,d_active)
values('DISCOUNTTYPE','MATRIX','MATRIX','active',0,sysdate,sysdate,56,List_Of_Values_SEQ.nextval,'AMER',systimestamp,systimestamp,1)
/
insert into i18nlov_text(id,locale,description,list_of_i18n_values)
values (I18N_Lov_Text_SEQ.nextval,'en_US','MATRIX',(select id from list_of_values where code = 'MATRIX' and business_unit_info='AMER'))
/
insert into list_of_values(type,code,description,state,version,d_created_on,d_updated_on,d_last_updated_by,id,business_unit_info,d_created_time,d_updated_time,d_active)
values('DISCOUNTTYPE','PROGRAM','PROGRAM','active',0,sysdate,sysdate,56,List_Of_Values_SEQ.nextval,'EMEA',systimestamp,systimestamp,1)
/
insert into i18nlov_text(id,locale,description,list_of_i18n_values)
values (I18N_Lov_Text_SEQ.nextval,'en_US','PROGRAM',(select id from list_of_values where code = 'PROGRAM' and business_unit_info='EMEA'))
/
insert into list_of_values(type,code,description,state,version,d_created_on,d_updated_on,d_last_updated_by,id,business_unit_info,d_created_time,d_updated_time,d_active)
values('DISCOUNTTYPE','PROGRAM','PROGRAM','active',0,sysdate,sysdate,56,List_Of_Values_SEQ.nextval,'AMER',systimestamp,systimestamp,1)
/
insert into i18nlov_text(id,locale,description,list_of_i18n_values)
values (I18N_Lov_Text_SEQ.nextval,'en_US','PROGRAM',(select id from list_of_values where code = 'PROGRAM' and business_unit_info='AMER'))
/