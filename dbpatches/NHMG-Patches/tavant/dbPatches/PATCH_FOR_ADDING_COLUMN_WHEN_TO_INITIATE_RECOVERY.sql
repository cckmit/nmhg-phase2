-- Purpose    : Patch for adding column initiateRecoveryClaim in contract table
-- Author     : PARTHASARATHY R
-- Created On : 06-Dec-2012

ALTER TABLE CONTRACT ADD ("INITIATE_RECOVERY_CLAIM" VARCHAR2(15))
/