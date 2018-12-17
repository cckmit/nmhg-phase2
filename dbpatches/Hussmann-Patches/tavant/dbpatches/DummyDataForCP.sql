INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP01','ADDITIONAL HOURS NOT ALLOWED','active','0','Hussmann')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP02','COMMERCIAL POLICY','active','0','Hussmann')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP03','CLAIM REVIEW PENDING','active','0','Hussmann')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP04','CLAIM REVIEW COMPLETED','active','0','Hussmann')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP05','ADDITIONAL HOURS NOT ALLOWED','active','0','Club Car')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP06','COMMERCIAL POLICY','active','0','Club Car')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP07','CLAIM REVIEW PENDING','active','0','Club Car')
/
INSERT INTO ACCEPTANCE_REASON_FOR_CP(code,description,state,version,business_unit_info)
VALUES ('CP08','CLAIM REVIEW COMPLETED','active','0','Club Car')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP01','ADDITIONAL HOURS NOT ALLOWED','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Hussmann')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP02','COMMERCIAL POLICY','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Hussmann')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP03','CLAIM REVIEW PENDING','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Hussmann')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP04','CLAIM REVIEW COMPLETED','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Hussmann')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP05','ADDITIONAL HOURS NOT ALLOWED','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Club Car')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP06','COMMERCIAL POLICY','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Club Car')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP07','CLAIM REVIEW PENDING','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Club Car')
/
INSERT INTO list_of_values (type,code,description,state,version,id,business_unit_info)
VALUES ('ACCEPTANCEREASONFORCP','CP08','CLAIM REVIEW COMPLETED','active','0',LIST_OF_VALUES_SEQ
.NEXTVAL,'Club Car')
/
INSERT INTO CONFIG_PARAM(ID,description,display_name,name,Type)
VALUES (config_param_seq.nextval,'Default Acceptance Reason For CP','default acceptance  reason for CP while processing a claim','defaultAcceptanceReasonForCP','tavant.twms.domain.common.AcceptanceReasonForCP')
/
INSERT INTO CONFIG_VALUE(ID,active,value,config_param,business_unit_info)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'1','CP05',(select id FROM CONFIG_PARAM WHERE name='defaultAcceptanceReasonForCP'),'Club Car')
/
INSERT INTO CONFIG_VALUE(ID,active,value,config_param,business_unit_info)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'1','CP01',(select id FROM CONFIG_PARAM WHERE name='defaultAcceptanceReasonForCP'),'Hussmann')
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','ADDITIONAL HOURS NOT ALLOWED',(Select id from list_of_values where code = 'CP01'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','COMMERCIAL POLICY',(Select id from list_of_values where code = 'CP02'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','CLAIM REVIEW PENDING',(Select id from list_of_values where code = 'CP03'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','CLAIM REVIEW COMPLETED',(Select id from list_of_values where code = 'CP04'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','ADDITIONAL HOURS NOT ALLOWED',(Select id from list_of_values where code = 'CP05'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','COMMERCIAL POLICY',(Select id from list_of_values where code = 'CP06'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','CLAIM REVIEW PENDING',(Select id from list_of_values where code = 'CP07'))
/
INSERT INTO I18NLOV_TEXT(id,locale,description,list_of_i18n_values)
VALUES (HIBERNATE_SEQUENCE.NEXTVAL,'en_US','CLAIM REVIEW COMPLETED',(Select id from list_of_values where code = 'CP08'))
/
commit
/