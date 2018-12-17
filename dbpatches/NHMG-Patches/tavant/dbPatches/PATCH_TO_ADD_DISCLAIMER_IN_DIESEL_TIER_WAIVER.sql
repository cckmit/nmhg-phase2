--Purpose    : Patch for adding column DISCLAIMER IN DIESEL_TIER_WAIVER
--Author     : PARTHASARATHY R
--Created On : 18-Feb-2013

alter table DIESEL_TIER_WAIVER add (DISCLAIMER VARCHAR2(4000 CHAR))
/