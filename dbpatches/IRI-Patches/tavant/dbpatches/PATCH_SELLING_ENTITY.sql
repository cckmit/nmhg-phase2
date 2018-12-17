--PURPOSE    : PATCH FOR ADDING NEW LOV SELLING ENTITY
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 19-JAN-09

--below insert statements are commented coz these are inserting dummy data

--insert into list_of_values (ID,TYPE, CODE, DESCRIPTION, STATE, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,  BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, D_ACTIVE) values 
--(List_Of_Values_SEQ.nextval,'SELLINGENTITY','SE01-CC','Selling Entity 1','active',1,sysdate,'dummy',sysdate,117247,'Club Car',sysdate,sysdate,1)
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'en_US','Selling Entity 1 US',(select id from list_of_values where code='SE01' and business_unit_info='Club Car'))
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'fr_FR','Selling Entity 1 FR',(select id from list_of_values where code='SE01' and business_unit_info='Club Car'))
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'en_EN','Selling Entity 1 EN',(select id from list_of_values where code='SE01' and business_unit_info='Club Car'))
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'de_DE','Selling Entity 1 DE',(select id from list_of_values where code='SE01' and business_unit_info='Club Car'))
--/
--insert into list_of_values (ID,TYPE, CODE, DESCRIPTION, STATE, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, D_ACTIVE) values 
--(List_Of_Values_SEQ.nextval,'SELLINGENTITY','SE01-AIR','Selling Entity 1','active',1,sysdate,'dummy',sysdate,117247,'AIR',sysdate,sysdate,1)
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'en_US','Selling Entity 1 US',(select id from list_of_values where code='SE01' and business_unit_info='ASG'))
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'fr_FR','Selling Entity 1 FR',(select id from list_of_values where code='SE01' and business_unit_info='ASG'))
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'en_EN','Selling Entity 1 EN',(select id from list_of_values where code='SE01' and business_unit_info='ASG'))
--/
--insert into i18nlov_text (ID, LOCALE, DESCRIPTION, LIST_OF_I18N_VALUES) values
--(I18N_Lov_Text_SEQ.nextval,'de_DE','Selling Entity 1 DE',(select id from list_of_values where code='SE01' and business_unit_info='ASG'))
--/
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER,d_active)
VALUES
(config_param_seq.NEXTVAL, 'Capture Selling Entity', 'Capture Selling Entity',
    'CaptureSellingEntity','boolean', 
    TO_DATE('11/01/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', TO_DATE('12/24/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 117247, NULL, 
    NULL, NULL, 'radio', NULL, NULL, 
    NULL, NULL,1)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='CaptureSellingEntity'),
(select id from config_param_option where value='true'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='CaptureSellingEntity'),
(select id from config_param_option where value='false'))
/
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'CaptureSellingEntity'),
TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 117247, 'Club Car', NULL, NULL,(select id from config_param_option where value='true'),1)
/
ALTER TABLE CLAIM ADD SELLING_ENTITY NUMBER(19,0)
/
ALTER TABLE CLAIM  ADD CONSTRAINT CLAIM_SELL_ENTITY_FK FOREIGN KEY (SELLING_ENTITY) REFERENCES LIST_OF_VALUES
/
COMMIT
/
