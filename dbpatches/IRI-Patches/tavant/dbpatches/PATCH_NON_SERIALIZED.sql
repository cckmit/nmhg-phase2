--Purpose    : Patch to implement non serialzed claims and related configurations for it
--Author     : Pradyot Rout
--Created On : 28-Nov-08

-- Added a new parameter to check if filing of non serialzed claim is allowed--
INSERT INTO CONFIG_PARAM
(ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, 
 D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, 
 LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
 VALUES
(config_param_seq.NEXTVAL, 'Determines if filing of non serialized claim is allowed.', 
'Is filing of non serialized claim is allowed',
'nonSerializedClaimAllowed', 'boolean', 
 TO_DATE('11/28/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', 
 TO_DATE('11/28/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
 NULL, NULL, 'radio', NULL, NULL,NULL, NULL)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  
VALUES
(config_param_option_seq.NEXTVAL,
 (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'nonSerializedClaimAllowed' ),
 (select id from config_param_option WHERE VALUE ='false'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  
VALUES
(config_param_option_seq.NEXTVAL,
 (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'nonSerializedClaimAllowed' ),
 (select id from config_param_option WHERE VALUE ='true'))
/
INSERT INTO CONFIG_VALUE
(ID, ACTIVE, VALUE, CONFIG_PARAM, 
 D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON,D_LAST_UPDATED_BY, 
 BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION)
 VALUES
(config_value_seq.NEXTVAL, 1, 0, 
(SELECT ID FROM CONFIG_PARAM WHERE NAME = 'nonSerializedClaimAllowed' ), 
 TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
 NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 117247, 
 'Club Car', NULL, NULL,(select id from config_param_option WHERE VALUE ='true'))
/
INSERT INTO CONFIG_PARAM
 (ID, DESCRIPTION, DISPLAY_NAME, NAME, TYPE, 
  D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY, D_CREATED_TIME, D_UPDATED_TIME, 
  LOGICAL_GROUP, PARAM_DISPLAY_TYPE, LOGICAL_GROUP_ORDER, SECTIONS, SECTIONS_ORDER, PARAM_ORDER)
  VALUES
 (config_param_seq.NEXTVAL, 'Determines if invoice number to be captured.', 
 'Is invoice number to be captured',
 'invoiceNumberApplicable', 'boolean', 
  TO_DATE('11/28/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), 'Configuration', 
  TO_DATE('11/28/2008 15:38:14', 'MM/DD/YYYY HH24:MI:SS'), NULL, NULL, 
  NULL, NULL, 'radio', NULL, NULL,NULL, NULL)
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  
 VALUES
 (config_param_option_seq.NEXTVAL,
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'invoiceNumberApplicable' ),
  (select id from config_param_option WHERE VALUE ='false'))
/
INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING  
 VALUES
 (config_param_option_seq.NEXTVAL,
  (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'invoiceNumberApplicable' ),
  (select id from config_param_option WHERE VALUE ='true'))
/
INSERT INTO CONFIG_VALUE
 (ID, ACTIVE, VALUE, CONFIG_PARAM, 
  D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON,D_LAST_UPDATED_BY, 
  BUSINESS_UNIT_INFO, D_CREATED_TIME, D_UPDATED_TIME, CONFIG_PARAM_OPTION)
  VALUES
 (config_value_seq.NEXTVAL, 1, 0, 
 (SELECT ID FROM CONFIG_PARAM WHERE NAME = 'invoiceNumberApplicable' ), 
  TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 
  NULL, TO_DATE('11/12/2008 00:00:00', 'MM/DD/YYYY HH24:MI:SS'), 117247, 
 'Club Car', NULL, NULL,(select id from config_param_option WHERE VALUE ='true'))
/
alter table claim add invoice_number varchar2(50)
/
update JBPM_DECISIONCONDITIONS set transitionname_='goToEnd' 
  where expression_='#{claim.type.type=="Parts" || claim.itemReference.serialized==false}'
/
update jbpm_transition set name_='goToEnd',to_=( select id_ from jbpm_node where name_='End' and
processdefinition_=( select id_ from JBPM_PROCESSDEFINITION  where name_='PolicyAndPaymentComputationProcess'))  where name_='No' and from_ = ( select id_ from jbpm_node where name_='IsPolicyComputationRequired') 
/
COMMIT
/
