INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'RejectedPartReturn' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(select max(fromindex_)+1) from jbpm_transition where from_ = (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'RejectedPartReturn' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')))
/

INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ProcessAppeal' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(select max(fromindex_)+1) from jbpm_transition where from_ = (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ProcessAppeal' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission'))))
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'OnHold' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(select max(fromindex_)+1 from jbpm_transition where from_ = (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'OnHold' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')))/
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'OnHoldForPartReturn' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(select max(fromindex_)+1 from jbpm_transition where from_ = (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'OnHoldForPartReturn' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission'))))
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'Transferred' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(select max(fromindex_)+1 from jbpm_transition where from_ = (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'Transferred' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission'))))
/
INSERT INTO JBPM_TRANSITION (ID_,NAME_,PROCESSDEFINITION_,FROM_,TO_,FROMINDEX_)
VALUES
(HIBERNATE_SEQUENCE.NEXTVAL,'Seek Review',(SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_='ClaimSubmission'),
(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'MoveClaimToPartsShippedNotreceivedInbox' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'ForkForCPreview' and processdefinition_ = 
(select id_ from jbpm_processdefinition where name_='ClaimSubmission')),(select max(fromindex_)+1 from jbpm_transition where from_ = (SELECT ID_ FROM JBPM_NODE WHERE NAME_ = 'MoveClaimToPartsShippedNotreceivedInbox' and processdefinition_ = (select id_ from jbpm_processdefinition where name_='ClaimSubmission'))))
/
UPDATE JBPM_TRANSITION SET TO_= (SELECT ID_ FROM JBPM_NODE WHERE NAME_ ='MailPaymentInfo')
WHERE NAME_='goToEndPayment'
/
commit
/
