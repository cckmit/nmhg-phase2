--Purpose    : This Patch is for recovery claim generation once the claim get accepted & closed in case of credit submission 
--Author     : manoj.katare	
--Created On : 19-march-2009

UPDATE jbpm_transition SET TO_=(select ID_ from jbpm_node where NAME_='MailPaymentInfo' 
AND PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'))
WHERE NAME_='goToEndPayment' AND PROCESSDEFINITION_=(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission')
/
COMMIT
/