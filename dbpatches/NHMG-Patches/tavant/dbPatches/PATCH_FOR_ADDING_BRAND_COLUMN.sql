--Purpose    : Patch for adding brand column to service_information,oem_part_replaced,installed_parts,claim table.
--Author     : ROHIT MEHROTRA
--Created On : 26-NOV-2012

ALTER TABLE CLAIM ADD BRAND VARCHAR2(255)
/
ALTER TABLE SERVICE_INFORMATION ADD CAUSAL_PART_BRAND VARCHAR2(255)
/
ALTER TABLE oem_part_replaced ADD BRAND VARCHAR2(255)
/
ALTER TABLE installed_parts ADD BRAND VARCHAR2(255)
/