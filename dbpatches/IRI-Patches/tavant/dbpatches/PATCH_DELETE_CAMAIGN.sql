--PURPOSE    : PATCH FOR updating JBPM to send back campaign claim to pending inbox
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 27-FEB-09

insert into jbpm_node (id_,class_,name_,processdefinition_,isasync_,nodecollectionindex_)
values(hibernate_sequence.nextval,'D','IsCampaignClaim', (select id_ from jbpm_processdefinition where
name_='ClaimSubmission'),'0', (select max(nodecollectionindex_)+1 from jbpm_node where processdefinition_ =(select id_
from jbpm_processdefinition where name_='ClaimSubmission')))
/
update jbpm_transition set to_= (select id_ from jbpm_node where name_='IsCampaignClaim')
/
insert into jbpm_node (id_,class_,name_,processdefinition_,isasync_,decisiondelegation_,nodecollectionindex_)
values(hibernate_sequence.nextval,'X','sendNotificationBack',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'),'0',
(select * from JBPM_DELEGATION where configuration='<beanName>campaignService</beanName>
    <methodName>deleteDraftCampaignClaim</methodName>
    <parameters><variable>claim</variable></parameters>
    <transition name="FlowEnds" to="End"/>')
(select max(nodecollectionindex_)+1 from jbpm_node where processdefinition_
=(select id_ from jbpm_processdefinition where name_='ClaimSubmission')))
/
insert into jbpm_transition 
(id_,name_,processdefinition_,from_,to_,fromindex_)
values 
(hibernate_sequence.nextval,'Yes',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'),
(select id_ from jbpm_node where name_='IsCampaignClaim'),
(select id_ from jbpm_node where name_='sendNotificationBack'),1)
/
insert into jbpm_transition 
(id_,name_,processdefinition_,from_,to_,fromindex_)
values 
(hibernate_sequence.nextval,'No',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'),
(select id_ from jbpm_node where name_='IsCampaignClaim'),
select id_ from jbpm_node where name_='End' and 
processdefinition_=((select id_ from jbpm_processdefinition where name_='ClaimSubmission'))),0)
/
insert into jbpm_decisionconditions 
(decision_,transitionname_,expression_,index_)
values
((select id_ from jbpm_node where name_='IsCampaignClaim'),'Yes','#{claim.type.type=="Campaign"}',0)
/
insert into jbpm_delegation 
(id_,classname_,configuration_,configtype_,processdefinition_)
values
(hibernate_sequence.nextval,tavant.twms.jbpm.infra.ServiceBeanInvoker,
'<beanName>campaignService</beanName>
    <methodName>deleteDraftCampaignClaim</methodName>
    <parameters><variable>claim</variable></parameters>
    <transition name="FlowEnds" to="End"/>',
bean,
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'))
/
insert into jbpm_transition 
(id_,name_,processdefinition_,from_,to_,fromindex_)
values 
(hibernate_sequence.nextval,'FlowEnds',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'),
(select id_ from jbpm_node where name_='sendNotificationBack'),
(select id_ from jbpm_node where name_='End' and 
processdefinition_=(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),0)
/
COMMIT
/