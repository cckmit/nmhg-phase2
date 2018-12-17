--BU CONFIGS for Ticket # HUSS-332---

--select * from config_param where name in ('isAllAuditsShownToDealer')

--update config_param set description = 'Show dealer full claim audit history', sections = 'CLAIM_DISPLAY',display_name = 'Show dealer full claim audit history'where name  = 'isAllAuditsShownToDealer' and id  =  1100000002000 ;

--insert into config_param values (config_param_seq.nextval, 'Show dealer Actual Status of claim' ,'Show dealer Actual Status of claim' , 'isActualClaimStatusShownToDealer','boolean', sysdate, 'Created for Hussmann # HUSS-332', sysdate, null, sysdate, sysdate,1, 'radio', 'CLAIMS', 1, 'CLAIM_DISPLAY', 1, 1) ;

--insert into config_param values (config_param_seq.nextval, 'Show dealer who claim is currently with' ,'Show dealer who claim is currently with' , 'isClaimAsigneeShownToDealer','boolean', sysdate, 'Created for Hussmann # HUSS-332', sysdate, null, sysdate, sysdate,1, 'radio', 'CLAIMS', 1, 'CLAIM_DISPLAY', 1, 1) ;

--select * from config_param where name in ('isAllAuditsShownToDealer', 'isActualClaimStatusShownToDealer', 'isClaimAsigneeShownToDealer')

--select min(id) from config_param_options_mapping --value was 1170 

--select * from config_param_option where display_value in ('No', 'Yes') 

--insert into CONFIG_PARAM_OPTIONS_MAPPING values (1160, 1100000002400 , 1160 )

--insert into CONFIG_PARAM_OPTIONS_MAPPING values (1150, 1100000002400 , 1180 )

--insert into CONFIG_PARAM_OPTIONS_MAPPING values (1140, 1100000002420 , 1160 )

--insert into CONFIG_PARAM_OPTIONS_MAPPING values (1130, 1100000002420 , 1180 ) 

--To verify--

--select * from CONFIG_PARAM_OPTIONS_MAPPING where param_id in (select id from config_param where name in ('isAllAuditsShownToDealer', 'isActualClaimStatusShownToDealer', 'isClaimAsigneeShownToDealer')) 
	
--select * from config_param where name = 'isDefaultProcessorShownOnAutoReplies';

--update config_param set d_active = 0 where name = 'isDefaultProcessorShownOnAutoReplies'and id = 1100000002020;

-------------Actual Patch used by TSA -------------------


Insert into CONFIG_PARAM (ID,DESCRIPTION,DISPLAY_NAME,NAME,TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,
D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,PARAM_DISPLAY_TYPE,LOGICAL_GROUP,LOGICAL_GROUP_ORDER,
SECTIONS,SECTIONS_ORDER,PARAM_ORDER) values (config_param_seq.nextval,'Show dealer Actual Status of claim',
'Show dealer Actual Status of claim','isActualClaimStatusShownToDealer','boolean',
to_timestamp('17-DEC-09','DD-MON-RR HH.MI.SS.FF AM'),'Created for Hussmann # HUSS-332',
to_timestamp('17-DEC-09','DD-MON-RR HH.MI.SS.FF AM'),null,to_timestamp('17-DEC-09 12.45.55.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('17-DEC-09 12.45.55.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1,'radio','CLAIMS',1,'CLAIM_DISPLAY',1,1)
/

Insert into CONFIG_PARAM (ID,DESCRIPTION,DISPLAY_NAME,NAME,TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,
D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE,PARAM_DISPLAY_TYPE,LOGICAL_GROUP,LOGICAL_GROUP_ORDER,SECTIONS,SECTIONS_ORDER,PARAM_ORDER) 
values (config_param_seq.nextval,'Show dealer who claim is currently with','Show dealer who claim is currently with',
'isClaimAsigneeShownToDealer','boolean',to_timestamp('17-DEC-09','DD-MON-RR HH.MI.SS.FF AM'),'Created for Hussmann # HUSS-332',
to_timestamp('17-DEC-09','DD-MON-RR HH.MI.SS.FF AM'),null,to_timestamp('17-DEC-09 12.45.56.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),
to_timestamp('17-DEC-09 12.45.56.000000000 PM','DD-MON-RR HH.MI.SS.FF AM'),1,'radio','CLAIMS',1,'CLAIM_DISPLAY',1,1)
/

INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='isActualClaimStatusShownToDealer'),
(select id from config_param_option where value='true'))

/

INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='isActualClaimStatusShownToDealer'),
(select id from config_param_option where value='false'))
/

INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='isClaimAsigneeShownToDealer'),
(select id from config_param_option where value='true'))

/

INSERT INTO CONFIG_PARAM_OPTIONS_MAPPING (ID, PARAM_ID, OPTION_ID) 
values
(CFG_PARAM_OPTNS_MAPPING_SEQ.NEXTVAL,
(select id from config_param where name='isClaimAsigneeShownToDealer'),
(select id from config_param_option where value='false'))
/
commit
/


