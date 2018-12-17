--Purpose    : Patch for adding the external comments in Recovery_claim table
--Author     : Priyanka S
--Created On : 13-JAN-2014

alter table RECOVERY_CLAIM add (EXTERNAL_COMMENTS varchar2(4000))
/