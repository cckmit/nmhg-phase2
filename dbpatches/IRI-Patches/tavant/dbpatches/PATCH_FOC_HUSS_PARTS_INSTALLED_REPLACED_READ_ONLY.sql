-- Patch for making foc parts read only, creating dummy third party,config param for auto submission days,
-- Date 13th march 2009
-- Author Prashanth konda

ALTER TABLE HUSS_PARTS_REPLACED_INSTALLED ADD (READ_ONLY NUMBER(1) DEFAULT 0)
/
insert into party(id,name,version,address, d_created_on, d_active) values 
(PARTY_SEQ.nextval,'Anonymous ThirdParty',1,null,sysdate,1)
/
insert into organization values ((select max(id) from party ),'USD')
/
insert into service_provider (id,service_provider_number,status)
values ((select max(id) from party ),'99999','ACTIVE');
/
insert into third_party values((select id from service_provider where service_provider_number = '99999'), '99999')
/
Insert into ADDRESS_BOOK (ID, TYPE, VERSION, BELONGS_TO, D_INTERNAL_COMMENTS, D_ACTIVE) Values (address_book_seq.nextval, 'SELF', 0, (select id from third_party where third_party_number = '99999'), 'Anonymous Third Party Address Book', 1)
/
Insert into ADDRESS (ID, ADDRESS_LINE1, CITY, COUNTRY, EMAIL, STATE, VERSION, D_INTERNAL_COMMENTS, D_ACTIVE) Values (address_seq.nextval, 'Anonymous', 'Third Party', 'ES', 'thirdparty@tavant.com', '-', 0, 'Anonymous Third Party', 1)
/
Insert into ADDRESS_BOOK_ADDRESS_MAPPING (ID, IS_PRIMARY, TYPE, VERSION, ADDRESS_ID, ADDRESS_BOOK_ID, D_INTERNAL_COMMENTS, D_ACTIVE) Values  (ADDR_BOOK_ADDR_MAPP_SEQ.nextval, 0, 'SHIPPING', 0, (select id from address where ADDRESS_LINE1 = 'Anonymous'), (select id from address_book where belongs_to in (select id from third_party where third_party_number = '99999') and type = 'SELF'), 'Anonymous TP Address', 1)
/
Insert into ORGANIZATION_ADDRESS (ID, LOCATION, SITE_NUMBER) Values ((select id from address where ADDRESS_LINE1 = 'Anonymous'), 'Anonymous Third Party Servicing Location', '99999')
/
insert into organization_org_addresses values((select id from third_party where third_party_number = '99999'),(select id from address where ADDRESS_LINE1 = 'Anonymous'))
/
Insert into ADDRESS (ID, ADDRESS_LINE1, CITY, COUNTRY, EMAIL, STATE, VERSION, D_INTERNAL_COMMENTS, D_ACTIVE) Values (address_seq.nextval, 'AnonymousThirdParty', 'Third Party', 'ES', 'thirdparty@tavant.com', '-', 0, 'Anonymous Third Party', 1)
/
update party set address = (select id from address where ADDRESS_LINE1 = 'AnonymousThirdParty') where id in (select id from third_party where third_party_number = '99999')
/
insert into role (id,name,version,d_created_on,display_name,d_active)
values ((select max(id)+1 from role),'viewFOCInbox',1,sysdate,'viewFOCInbox',1)
/
insert into config_param 
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE,PARAM_DISPLAY_TYPE)
values (config_param_seq.nextval,'daysForFOCAutoSubmission','Days for FOC Auto Submission','daysForFOCAutoSubmission','number','textbox')
/
--This param is used for only FOC flow.. no other BU will call this param
INSERT INTO CONFIG_VALUE 
(ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION,d_active) 
VALUES 
(config_value_seq.NEXTVAL, 1, NULL, (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'daysForFOCAutoSubmission'),
TO_DATE('03/12/2009 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), '31', TO_DATE('03/12/2009 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), null, 'Hussmann', NULL, NULL,null,1)
/
update jbpm_transition set to_ = (select id_ from jbpm_node where name_ = 'PolicyAndPaymentComputation' and processdefinition_ = (select id_ from jbpm_processdefinition where name_ = 'ClaimSubmission'))
where name_ = 'Submit Claim'
and from_ = (select id_ from jbpm_node where name_ = 'WaitingForLabor' and processdefinition_ = (select id_ from jbpm_processdefinition where name_ = 'ClaimSubmission'))
and to_ = (select id_ from jbpm_node where name_ = 'generateClaimNumber' and processdefinition_ = (select id_ from jbpm_processdefinition where name_ = 'ClaimSubmission'))
/
COMMIT
/
