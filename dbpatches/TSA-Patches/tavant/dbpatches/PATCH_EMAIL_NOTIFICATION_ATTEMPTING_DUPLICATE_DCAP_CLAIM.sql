--Purpose    : Creating eventstate for email notification for duplicate DCAP Claims
--Author     : Saya Sudha
--Created On : 08-Feb-10

INSERT INTO EVENT_STATE (ID,VERSION,NAME,DISPLAY_NAME,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
VALUES (EVENT_STATE_SEQ.nextval,0,'ATTEMPTING_DUPLICATE_DCAP_CLAIM','Attempting Duplicate Dcap Claim',null,null,null,null,null,null,1)
/
INSERT INTO EVENT_ROLE_MAPPING (EVENT_TYPES, ROLES) VALUES ((SELECT ID FROM EVENT_STATE WHERE NAME = 'ATTEMPTING_DUPLICATE_DCAP_CLAIM'),(SELECT ID FROM ROLE WHERE NAME = 'dcapClaimReviewer'))
/
COMMIT
/

