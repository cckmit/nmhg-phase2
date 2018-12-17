--Purpose    : Creating new role dcapClaimReviewer for service allowance, changes done as per 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 11-Oct-2010

INSERT INTO ROLE (ID, NAME, VERSION, D_CREATED_ON, D_INTERNAL_COMMENTS, D_UPDATED_ON, D_LAST_UPDATED_BY,
D_CREATED_TIME, D_UPDATED_TIME, DISPLAY_NAME, D_ACTIVE)VALUES((SELECT MAX(ID) FROM ROLE)+1,'dcapClaimReviewer',0,SYSDATE,NULL,NULL,NULL,NULL,
NULL,NULL,1)
/
INSERT INTO ROLE values ((SELECT MAX(ID) FROM ROLE)+1,'dcapRegionalDirector',1,sysdate,'DCAP Regional Director|Internal',sysdate,NULL,CAST(sysdate AS TIMESTAMP),CAST(sysdate AS TIMESTAMP),1,'DCAP Regional Director')
/
COMMIT
/
