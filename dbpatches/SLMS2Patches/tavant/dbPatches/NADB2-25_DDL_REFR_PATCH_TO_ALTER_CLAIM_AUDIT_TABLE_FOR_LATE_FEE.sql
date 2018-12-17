--Purpose    : DDL for NMHGSLMS-425 -Adding column for late fee in ClaimAudit table.
--Author     : Arpitha Nadig AR
--Created On : 10-JAN-2013
ALTER TABLE CLAIM_AUDIT ADD IS_LATE_FEE_APPROVAL_REQUIRED NUMBER(1,0)
/