--PURPOSE    :ADDED TWO NEW SYSTEM DEFINED ROLES
--AUTHOR     : PRADYOT.ROUT
--CREATED ON : 27-APR-09


insert into domain_predicate
(ID, CONTEXT, NAME, VERSION, PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS,D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Cost Categories claimed are not covered under Payment Definiton',1,
null,null,null,sysdate,null,sysdate,sysdate,'AIR',1,'claimCostCategoryValidator')
/
insert into domain_predicate
(ID, CONTEXT, NAME, VERSION, PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS,D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Cost Categories claimed are not covered under Payment Definiton',1,
null,null,null,sysdate,null,sysdate,sysdate,'TFM',1,'claimCostCategoryValidator')
/
insert into domain_predicate
(ID, CONTEXT, NAME, VERSION, PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS,D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Cost Categories claimed are not covered under Payment Definiton',1,
null,null,null,sysdate,null,sysdate,sysdate,'Hussmann',1,'claimCostCategoryValidator')
/
insert into domain_predicate
(ID, CONTEXT, NAME, VERSION, PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS,D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Cost Categories claimed are not covered under Payment Definiton',1,
null,null,null,sysdate,null,sysdate,sysdate,'Clubcar ESA',1,'claimCostCategoryValidator')
/
insert into domain_predicate
(ID, CONTEXT, NAME, VERSION, PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS,D_UPDATED_ON, D_LAST_UPDATED_BY, 
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Cost Categories claimed are not covered under Payment Definiton',1,
null,null,null,sysdate,null,sysdate,sysdate,'Transport Solutions ESA',1,'claimCostCategoryValidator')
/
insert into domain_predicate
(ID, CONTEXT,NAME, VERSION,PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Fault Code and Job Code does not have an exact match',1,
null,null,null,sysdate,null,sysdate,sysdate,'AIR',1,'faultCodeJobCodeValidator')
/
insert into domain_predicate
(ID, CONTEXT,NAME, VERSION,PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Fault Code and Job Code does not have an exact match',1,
null,null,null,sysdate,null,sysdate,sysdate,'TFM',1,'faultCodeJobCodeValidator')
/
insert into domain_predicate
(ID, CONTEXT,NAME, VERSION,PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Fault Code and Job Code does not have an exact match',1,
null,null,null,sysdate,null,sysdate,sysdate,'Hussmann',1,'faultCodeJobCodeValidator')
/
insert into domain_predicate
(ID, CONTEXT,NAME, VERSION,PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Fault Code and Job Code does not have an exact match',1,
null,null,null,sysdate,null,sysdate,sysdate,'Clubcar ESA',1,'faultCodeJobCodeValidator')
/
insert into domain_predicate
(ID, CONTEXT,NAME, VERSION,PREDICATE_ASXML,D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,
D_CREATED_TIME, D_UPDATED_TIME,BUSINESS_UNIT_INFO, D_ACTIVE, SYSTEM_DEFINED_CONDITION_NAME)
values
(DOMAIN_PREDICATE_SEQ.nextval ,'ClaimRules','Fault Code and Job Code does not have an exact match',1,
null,null,null,sysdate,null,sysdate,sysdate,'Transport Solutions ESA',1,'faultCodeJobCodeValidator')
/
commit
/
