ALTER TABLE
   SERVICE
ADD
   (
   PER_DIEM_AMT         NUMBER(19,2),
   PER_DIEM_CURR        VARCHAR2(255 CHAR),
   RENTAL_CHARGES_AMT   NUMBER(19,2),
   RENTAL_CHARGES_CURR  VARCHAR2(255 CHAR)
)
/
ALTER TABLE
   CLAIM
ADD
   (
  PER_DIEM_CONFIG                    NUMBER(1),
  RENTAL_CHARGES_CONFIG              NUMBER(1),
  ADDITIONAL_TRAVEL_HOURS_CONFIG    NUMBER(1)
)
/
ALTER TABLE
   TRAVEL_DETAIL
ADD
   (
    ADDITIONAL_HOURS     NUMBER(19,2)  
)
/
insert into cost_category (id,code,description,name,version)
values(COST_CATEGORY_SEQ.nextval,'PER_DIEM','Per Diem','Per Diem',1)
/
insert into cost_category (id,code,description,name,version)
values(COST_CATEGORY_SEQ.nextval,'RENTAL_CHARGES','Rental Charges','Rental Charges',1)
/
insert into cost_category (id,code,description,name,version)
values(COST_CATEGORY_SEQ.nextval,'ADDITIONAL_TRAVEL_HOURS','Additional Travel Hours','Additional Travel Hours',1)
/
insert into config_value (id,active, value, config_param, business_unit_info) 
values(COST_CATEGORY_SEQ.nextval,1, (select id from cost_category where code = 'PER_DIEM'),
(select id from config_param where name = 'configuredCostCategories'), 'Club Car')
/
insert into config_value (id,active, value, config_param, business_unit_info) 
values(COST_CATEGORY_SEQ.nextval,1, (select id from cost_category where code = 'RENTAL_CHARGES'),
(select id from config_param where name = 'configuredCostCategories'), 'Club Car')
/
insert into config_value (id,active, value, config_param, business_unit_info) 
values(COST_CATEGORY_SEQ.nextval,1, (select id from cost_category where code = 'ADDITIONAL_TRAVEL_HOURS'),
(select id from config_param where name = 'configuredCostCategories'), 'Club Car')
/
insert into section (id,display_position,name,version)
values(SECTION_SEQ.nextval,(select max(DISPLAY_POSITION) + 1  from section),'Per Diem',1)
/
insert into section (id,display_position,name,version)
values(SECTION_SEQ.nextval,(select max(DISPLAY_POSITION) + 1  from section),'Rental Charges',1)
/
insert into section (id,display_position,name,version)
values(SECTION_SEQ.nextval,(select max(DISPLAY_POSITION) + 1  from section),'Additional Travel Hours',1)
/
insert into I18NSECTION_TEXT values (I18N_Section_Text_SEQ.nextval, 'en_US', 'Per Diem',(select id  from section where name = 'Per Diem'))
/
insert into I18NSECTION_TEXT values (I18N_Section_Text_SEQ.nextval, 'en_UK', 'Per Diem',(select id  from section where name = 'Per Diem'))
/
insert into I18NSECTION_TEXT values (I18N_Section_Text_SEQ.nextval, 'fr_FR', 'Per Diem',(select id  from section where name = 'Per Diem'))
/
insert into I18NSECTION_TEXT values (I18N_Section_Text_SEQ.nextval, 'de_DE', 'Per Diem',(select id  from section where name = 'Per Diem'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'en_US', 'Rental Charges',(select id  from section where name = 'Rental Charges'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'en_UK', 'Rental Charges',(select id  from section where name = 'Rental Charges'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'fr_FR', 'Rental Charges',(select id  from section where name = 'Rental Charges'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'de_DE', 'Rental Charges',(select id  from section where name = 'Rental Charges'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'en_US', 'Additional Travel Hours',(select id  from section where name = 'Additional Travel Hours'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'en_UK', 'Additional Travel Hours',(select id  from section where name = 'Additional Travel Hours'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'fr_FR', 'Additional Travel Hours',(select id  from section where name = 'Additional Travel Hours'))
/
insert into I18NSECTION_TEXT values(I18N_Section_Text_SEQ.nextval, 'de_DE', 'Additional Travel Hours',(select id  from section where name = 'Additional Travel Hours'))
/
commit
/