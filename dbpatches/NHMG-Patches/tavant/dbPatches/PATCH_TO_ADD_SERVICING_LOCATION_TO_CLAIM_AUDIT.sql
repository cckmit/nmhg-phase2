-- Purpose    : Patch for adding column SERVICING_LOCATION in CLAIM_AUDIT table
-- Author     : PARTHASARATHY R
-- Created On : 05-Dec-2013

ALTER TABLE CLAIM_AUDIT ADD ("SERVICING_LOCATION" NUMBER(19,0))
/