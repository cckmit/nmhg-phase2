--Purpose    : Creating eventstate for Email notification for DCAP claims manual review, changes made as a part of 4.3 upgrade 
--Author     : Saya Sudha
--Created On : 15-Jan-10

--INSERT INTO EVENT_STATE (ID,VERSION,NAME,DISPLAY_NAME,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) 
--VALUES (EVENT_STATE_SEQ.nextval,0,'DCAP_CLAIM_MANUAL_REVIEW','Dcap Claim Manual Review',sysdate,null,sysdate,null,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1)
--/
--INSERT INTO EVENT_ROLE_MAPPING (EVENT_TYPES, ROLES) VALUES ((SELECT ID FROM EVENT_STATE WHERE NAME = 'DCAP_CLAIM_MANUAL_REVIEW'),(SELECT ID FROM ROLE WHERE NAME = 'dcapClaimReviewer'))
--/
--Manish - Moved this part to DB patch PATCH_EMAIL_NOTIFICATION_ATTEMPTING_DUPLICATE_DCAP_CLAIM.sql, this patch not needed.
COMMIT
/