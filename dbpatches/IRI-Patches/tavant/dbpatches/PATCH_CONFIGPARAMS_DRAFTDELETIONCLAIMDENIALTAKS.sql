--Purpose    : added threee new business unit configuration parameter for delete draft,forwarded claim and claim for overdue of part return 
--Author     : pratima.rajak
--Created On : 26-July-08

INSERT INTO CONFIG_PARAM (
   ID, DESCRIPTION, DISPLAY_NAME, 
   NAME, TYPE, D_CREATED_ON, 
   D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) 
VALUES (config_param_seq.nextval,'date based on draft to be deleted','dateToBeUsedForDraftClaimDeletion',
    'dateToBeUsedForDraftClaimDeletion','java.lang.String',sysdate,'basedondate draft state becomes denied',NULL,NULL)
/
INSERT INTO CONFIG_PARAM (
   ID, DESCRIPTION, DISPLAY_NAME, 
   NAME, TYPE, D_CREATED_ON, 
   D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) 
VALUES (config_param_seq.nextval,'days for draft auto delete','daysForDraftAutoDelete','daysForDraftAutoDelete','number',
            sysdate,'after these days draft state becomes denied',NULL,NULL)
/
INSERT INTO CONFIG_PARAM (
   ID, DESCRIPTION, DISPLAY_NAME, 
   NAME, TYPE, D_CREATED_ON, 
   D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) 
VALUES (config_param_seq.nextval,'forwarded claims to be denied if not respond','daysForForwardedClaimDenied','daysForForwardedClaimDenied','number',
            sysdate,'forwarded claim becomes denied and closed',NULL,NULL)
/

INSERT INTO CONFIG_PARAM (
   ID, DESCRIPTION, DISPLAY_NAME, 
   NAME, TYPE, D_CREATED_ON, 
   D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY) 
   VALUES (config_param_seq.nextval,'waiting for part returns claims to be denied if not shiped','waitingPartReturnsToBeDeniedIfNotShiped',
  'waitingPartReturnsToBeDeniedIfNotShiped','number',sysdate,NULL,NULL,NULL)
/
INSERT INTO CONFIG_VALUE (
   ID, ACTIVE, VALUE, 
   CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, 
   D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO) 
VALUES (config_value_seq.nextval,1,1,(select id from config_param where name = 'dateToBeUsedForDraftClaimDeletion'),sysdate,'date value for draft deletion',NULL ,NULL,'Club Car')
/
INSERT INTO CONFIG_VALUE (
   ID, ACTIVE, VALUE, 
   CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, 
   D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO) 
VALUES (config_value_seq.nextval,1,1,(select id from config_param where name = 'daysForDraftAutoDelete'),sysdate,'days for draft deletion',NULL,NULL,'Club Car')
/
INSERT INTO CONFIG_VALUE (
   ID, ACTIVE, VALUE, 
   CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, 
   D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO) 
VALUES (config_value_seq.nextval,1,1,(select id from config_param where name = 'daysForForwardedClaimDenied'),sysdate,'days for forwaded draft denied',NULL,NULL,'Club Car')
/
INSERT INTO CONFIG_VALUE (
   ID, ACTIVE, VALUE, 
   CONFIG_PARAM, D_CREATED_ON, D_INTERNAL_COMMENTS, 
   D_UPDATED_ON, D_LAST_UPDATED_BY, BUSINESS_UNIT_INFO) 
VALUES (config_value_seq.nextval,1,1,(select id from config_param where name = 'waitingPartReturnsToBeDeniedIfNotShiped'),sysdate,'days for claim for part return denied',NULL,NULL,'Club Car')
/
commit
/