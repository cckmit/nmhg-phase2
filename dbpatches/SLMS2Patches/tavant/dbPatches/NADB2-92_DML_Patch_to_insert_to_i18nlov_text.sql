--PURPOSE    : PATCH TO insert lov values in i18nlov_text  
--AUTHOR     : Raghavendra
--CREATED ON : 10-MAR-13


insert into i18nlov_text values(I18N_LOV_TEXT_SEQ.nextVal,'en_US','Fork', (select id from list_of_values where type='ADDITIONALCOMPONENTTYPE' and description='Fork'))
/
insert into i18nlov_text values(I18N_LOV_TEXT_SEQ.nextVal,'en_US','Attachment', (select id from list_of_values where type='ADDITIONALCOMPONENTTYPE' and description='Attachment'))
/
insert into i18nlov_text values(I18N_LOV_TEXT_SEQ.nextVal,'en_US','Carriage', (select id from list_of_values where type='ADDITIONALCOMPONENTTYPE' and description='Carriage'))
/
insert into i18nlov_text values(I18N_LOV_TEXT_SEQ.nextVal,'en_US','Hook', (select id from list_of_values where type='ADDITIONALCOMPONENTSUBTYPE' and description='Hook'))
/
insert into i18nlov_text values(I18N_LOV_TEXT_SEQ.nextVal,'en_US','Pin', (select id from list_of_values where type='ADDITIONALCOMPONENTSUBTYPE' and description='Pin'))
/
insert into i18nlov_text values(I18N_LOV_TEXT_SEQ.nextVal,'en_US','None', (select id from list_of_values where type='ADDITIONALCOMPONENTSUBTYPE' and description='None'))
/
commit
/