ALTER TABLE service_provider
ADD (SUBMIT_CREDIT  VARCHAR2(10))
/
update service_provider set submit_credit='N' where id in(
select a.id from dealership a, organization b, bu_org_mapping c
where a.id = b.id and
c.org = b.id and
c.bu = 'Hussmann')
/
update JBPM_DECISIONCONDITIONS set expression_='#{claim.forDealer.submitCreditBooleanVal and claim.payment.paymentToBeMade == true}' where transitionname_='proceedWithNotification'
/

INSERT INTO JBPM_DELEGATION (ID_, CLASSNAME_, CONFIGURATION_, CONFIGTYPE_, PROCESSDEFINITION_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL,'tavant.twms.jbpm.infra.ServiceBeanInvoker',
'<transition name="goToEndPayment" to="End">
 	<script>claim.setState(tavant.twms.domain.claim.ClaimState.ACCEPTED_AND_CLOSED);</script>
 </transition>
 <beanName>paymentAsyncService</beanName>
 <methodName>startCreditMemoPayment</methodName>
 <parameters>
   <variable>claim</variable>
 </parameters>','bean',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'))
/
INSERT INTO JBPM_NODE (ID_, CLASS_, NAME_, PROCESSDEFINITION_, ISASYNC_, ACTION_, SUPERSTATE_, SIGNAL_, CREATETASKS_, 
ENDTASKS_, TASK_NAMES_TO_END, DECISIONEXPRESSION_, DECISIONDELEGATION, SUBPROCESSDEFINITION_, END_TRANSITION, 
NORMAL_TRANSITION, NODECOLLECTIONINDEX_) 
VALUES 
(HIBERNATE_SEQUENCE.NEXTVAL, 'X','PaidNClosed',
(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),0,NULL,NULL,NULL,NULL,NULL,NULL,NULL,(select id_ from JBPM_DELEGATION 
where CONFIGURATION_ like '%startCreditMemoPayment%'),NULL,NULL,NULL,
(select max(nodecollectionindex_)+1 from jbpm_node))
/

INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'goToEndPayment',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'PaidNClosed' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'Close' and 
processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),'0')
/

INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'goToPaidAndClose',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'IsPaymentNotificationNeeded' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'PaidNClosed' and 
processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),
(select max(fromindex_)+1 from jbpm_transition where from_ =(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'IsPaymentNotificationNeeded' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission'))))
/

INSERT 	INTO JBPM_DECISIONCONDITIONS(DECISiON_,TRANSITIONNAME_,EXPRESSION_,INDEX_)
VALUES ((select DECISION_ from JBPM_DECISIONCONDITIONS where transitionname_='proceedWithNotification'),'goToPaidAndClose','#{!claim.forDealer.submitCreditBooleanVal}',3)
/

INSERT INTO JBPM_EVENT (ID_,EVENTTYPE_,TYPE_,GRAPHELEMENT_,TRANSITION_,TASK_,NODE_,PROCESSDEFINITION_) 
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'transition','T',(SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'goToEndPayment'),
(SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'goToEndPayment'),NULL,NULL,NULL)
/

INSERT INTO JBPM_ACTION (ID_, CLASS, NAME_, ISPROPAGATIONALLOWED_, ACTIONEXPRESSION_, ISASYNC_, REFERENCEDACTION_, ACTIONDELEGATION_, EVENT_, PROCESSDEFINITION_, TIMERNAME_, EXPRESSION_, DUEDATE_, REPEAT_, TRANSITIONNAME_, TIMERACTION_, EVENTINDEX_, EXCEPTIONHANDLER_, EXCEPTIONHANDLERINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'S',NULL,1,null,0,null,null,
(SELECT ID_ FROM JBPM_EVENT WHERE TRANSITION_ = (SELECT ID_ FROM JBPM_TRANSITION WHERE NAME_ = 'goToEndPayment')),null,null,'claim.setState(tavant.twms.domain.claim.ClaimState.ACCEPTED_AND_CLOSED);',null,null,null,null,0,null,null)
/
COMMIT
/
