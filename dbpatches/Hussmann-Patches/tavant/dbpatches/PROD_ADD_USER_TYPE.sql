--Patch for adding column in ORG_USER table which will capture the USER type whether its INTERNAL, OR EXTERNAL.

ALTER TABLE ORG_USER ADD USER_TYPE VARCHAR2(255)
/