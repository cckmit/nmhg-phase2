-- Patch to Insert Labor Rate Types
-- Author		: Arpitha Nadig AR
-- Created On	: 17-JAN-2013
insert into list_of_values values('LABORRATETYPE','LRT1','CUSTOMER','active',0,sysdate,null,sysdate,null,List_Of_Values_SEQ.nextval,'AMER',current_timestamp,current_timestamp,1)
/
insert into list_of_values values('LABORRATETYPE','LRT2','CERTIFIED','active',0,sysdate,null,sysdate,null,List_Of_Values_SEQ.nextval,'AMER',current_timestamp,current_timestamp,1)
/
insert into list_of_values values('LABORRATETYPE','LRT3','WARRANTY','active',0,sysdate,null,sysdate,null,List_Of_Values_SEQ.nextval,'AMER',current_timestamp,current_timestamp,1)
/
insert into i18nlov_text values(I18N_Lov_Text_SEQ.nextval,'en_US','CUSTOMER',(select id from list_of_values where code='LRT1'))
/
insert into i18nlov_text values(I18N_Lov_Text_SEQ.nextval,'en_US','CERTIFIED',(select id from list_of_values where code='LRT2'))
/
insert into i18nlov_text values(I18N_Lov_Text_SEQ.nextval,'en_US','WARRANTY',(select id from list_of_values where code='LRT3'))
/