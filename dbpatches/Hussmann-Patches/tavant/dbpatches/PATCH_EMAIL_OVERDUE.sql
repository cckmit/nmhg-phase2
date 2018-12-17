--PURPOSE    : Patch generating emails for parts moving to overdue
--AUTHOR     : Smita Kadle
--CREATED ON : 20-MAR-09
insert into jbpm_delegation 
(id_,classname_,configuration_,configtype_,processdefinition_)
values
(hibernate_sequence.nextval,'tavant.twms.jbpm.infra.ServiceBeanInvoker',
'<beanName>sendEmailService</beanName><methodName>createEmailEventForOverdue</methodName><parameters><variable>claim</variable><variable>partReturn</variable></parameters><transition name="goToOverdueParts" to="Overdue Parts for Shipment"/>',
'bean',
(select id_ from jbpm_processdefinition where name_='PartsReturn'))
/
insert into jbpm_node (id_,class_,name_,processdefinition_,isasync_,decisiondelegation,nodecollectionindex_)
values(hibernate_sequence.nextval,'X','GenerateEmailNotifications',
(select id_ from jbpm_processdefinition where name_='PartsReturn'),'0',
(select id_ from JBPM_DELEGATION where configuration_ like '%createEmailEventForOverdue%'),
(select max(nodecollectionindex_)+1 from jbpm_node where processdefinition_
=(select id_ from jbpm_processdefinition where name_='PartsReturn')))
/
update jbpm_transition
set to_ = (select id_ from jbpm_node where name_ = 'GenerateEmailNotifications')
where name_ = 'Part Overdue'
/
insert into jbpm_transition 
(id_,name_,processdefinition_,from_,to_,fromindex_)
values 
(hibernate_sequence.nextval,'goToOverdueParts',
(select id_ from jbpm_processdefinition where name_='PartsReturn'),
(select id_ from jbpm_node where name_='GenerateEmailNotifications'),
(select id_ from jbpm_node where name_='Overdue Parts for Shipment' and 
processdefinition_=(select id_ from jbpm_processdefinition where name_='PartsReturn')),0)
/
COMMIT
/