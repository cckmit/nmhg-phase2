-- Author: Pratima Rajak
-- Date : 7th August,2008
-- Reason: Adding Date and days to be considered for removal of  Delete button from Modify page of               DR/ETR

INSERT INTO CONFIG_PARAM (
   ID, DESCRIPTION, DISPLAY_NAME, 
   NAME, TYPE, D_CREATED_ON, 
   D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) 
VALUES (config_param_seq.nextval,'Date to be considered for Delivery Report Deletion','dateToBeUsedForDeliveryReportDeletion',
    'dateToBeUsedForDeliveryReportDeletion','java.lang.String',NULL, NULL, NULL, NULL)
/
	
INSERT INTO CONFIG_VALUE ( ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO ) VALUES ( 
config_value_seq.nextval, 1, 'Delivery Date',(select id from config_param where name = 'dateToBeUsedForDeliveryReportDeletion'),
NULL, NULL, NULL, NULL, 'Club Car') 
/

INSERT INTO CONFIG_PARAM (
   ID, DESCRIPTION, DISPLAY_NAME, 
   NAME, TYPE, D_CREATED_ON, 
   D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) 
VALUES (config_param_seq.nextval,'Days to be considered for Delivery Report Deletion','daysToBeUsedForDeliveryReportDeletion',
    'daysToBeUsedForDeliveryReportDeletion','number',NULL, NULL, NULL, NULL)
/
	
INSERT INTO CONFIG_VALUE ( ID, ACTIVE, VALUE, CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO ) VALUES ( 
config_value_seq.nextval, 1,1,(select id from config_param where name = 'daysToBeUsedForDeliveryReportDeletion'),
NULL, NULL, NULL, NULL, 'Club Car') 
/

commit
/
