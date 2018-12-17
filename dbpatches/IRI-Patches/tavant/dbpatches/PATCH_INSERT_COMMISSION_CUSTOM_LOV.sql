--Purpose    : Insert Default values for CUSTOM and COMMISSION Report in LOV
--Author     : Jitesh Jain
--Created On : 6-APR-09

Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','COMMISSION',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'Transport Solutions ESA',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'COMMISSION'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','COMMISSION',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'AIR',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'COMMISSION'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','COMMISSION',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'TFM',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'COMMISSION'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','COMMISSION',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'Clubcar ESA',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','COMMISSION Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'COMMISSION'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','CUSTOM',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'Transport Solutions ESA',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Transport Solutions ESA' and code = 'CUSTOM'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','CUSTOM',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'AIR',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='AIR' and code = 'CUSTOM'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','CUSTOM',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'TFM',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='TFM' and code = 'CUSTOM'))
/
Insert into list_of_values
(TYPE,CODE,DESCRIPTION,STATE,VERSION,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,ID,BUSINESS_UNIT_INFO,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
values ('REPORTTYPE','CUSTOM',null,'active',0,to_date('02-APR-09','DD-MON-RR'),null,to_date('02-APR-09','DD-MON-RR'),null,
list_of_values_seq.nextval,'Clubcar ESA',
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('02-APR-09 04.34.39.040000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1)
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_US','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'en_GB','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'de_DE','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'fr_FR','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'it_IT','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'nl_NL','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
Insert into I18NLOV_TEXT (ID,LOCALE,DESCRIPTION,LIST_OF_I18N_VALUES) values (I18N_Lov_Text_SEQ.nextval,'es_ES','CUSTOM Report',(select id from list_of_values where type='REPORTTYPE' and business_unit_info ='Clubcar ESA' and code = 'CUSTOM'))
/
commit
/