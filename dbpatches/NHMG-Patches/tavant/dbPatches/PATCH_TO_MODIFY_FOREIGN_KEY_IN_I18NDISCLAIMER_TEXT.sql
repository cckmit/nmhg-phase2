--Purpose    : Patch to DROP COLUMN IN I18NDISCLAIMER_TEXT
--Author     : PARTHASARATHY R
--Created On : 18-Feb-2013

BEGIN
  BEGIN
  EXECUTE immediate 'alter table I18NDISCLAIMER_TEXT drop constraint I18NWAIVER_TEXT_WAIVER_FK';
  EXECUTE immediate 'ALTER TABLE I18NDISCLAIMER_TEXT RENAME COLUMN DIESEL_TIER_WAIVER TO DIESEL_TIER_COUNTRY_MAPPING';
EXCEPTION
WHEN OTHERS THEN  NULL;
END;
END;
/
alter table I18NDISCLAIMER_TEXT add constraint WAVIER_DISCLAIMER_FK foreign key (DIESEL_TIER_COUNTRY_MAPPING) references  DIESEL_TIER_COUNTRY_MAPPING(ID)
/