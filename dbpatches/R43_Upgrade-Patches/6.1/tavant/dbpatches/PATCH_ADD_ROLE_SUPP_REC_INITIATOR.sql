--Purpose    : Patch for Adding supplierRecoveryInitiator role to BJ Smith
--Author     : Kuldeep Patil
--Created On : 04-July-2011

INSERT INTO USER_ROLES VALUES ( (SELECT ID FROM ORG_USER WHERE LOGIN = 'bjsmith'), (SELECT ID FROM ROLE WHERE NAME='supplierRecoveryInitiator'))
/
COMMIT
/