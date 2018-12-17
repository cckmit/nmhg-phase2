--PURPOSE    : Patch for ending the part return flow when the claim is denied.
--AUTHOR     : Smita Kadle
--CREATED ON : 28-FEB-09
insert into jbpm_delegation 
(id_,classname_,configuration_,configtype_,processdefinition_)
values
(hibernate_sequence.nextval,'tavant.twms.jbpm.infra.ServiceBeanInvoker',
'<beanName>partReturnProcessingService</beanName>
    <methodName>endAllPartTasksForClaim</methodName>
    <parameters><variable>claim</variable></parameters>
    <transition name="goToIsDealerNotificationNeeded" to="IsDealerNotificationNeeded"/>',
'bean',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'))
/
insert into jbpm_node (id_,class_,name_,processdefinition_,isasync_,decisiondelegation,nodecollectionindex_)
values(hibernate_sequence.nextval,'X','EndAllPartTasksForClaim',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'),'0',
(select id_ from JBPM_DELEGATION where configuration_ like '%endAllPartTasksForClaim%'),
(select max(nodecollectionindex_)+1 from jbpm_node where processdefinition_
=(select id_ from jbpm_processdefinition where name_='ClaimSubmission')))
/
update jbpm_delegation
set configuration_ = '<beanName>paymentService</beanName><methodName>calculatePaymentForDeniedClaim</methodName><parameters><variable>claim</variable></parameters><postProcess>claim.setPayment(result)</postProcess><transition name="goToEndAllPartTasksForClaim" to="EndAllPartTasksForClaim"/>'
where configuration_ like '%calculatePaymentForDeniedClaim%'
/
update jbpm_transition
set from_ = (select id_ from jbpm_node where name_ = 'EndAllPartTasksForClaim')
where name_ = 'goToIsDealerNotificationNeeded'
/
insert into jbpm_transition 
(id_,name_,processdefinition_,from_,to_,fromindex_)
values 
(hibernate_sequence.nextval,'goToEndAllPartTasksForClaim',
(select id_ from jbpm_processdefinition where name_='ClaimSubmission'),
(select id_ from jbpm_node where name_='DebitPaymentOnDenial'),
(select id_ from jbpm_node where name_='EndAllPartTasksForClaim' and 
processdefinition_=(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),0)
/
COMMIT
/