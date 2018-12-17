--Purpose    : Patch to ALTER FOERIGN KEY IN I18NDISCLAIMER_TEXT
--Author     : PARTHASARATHY R
--Created On : 25-Mar-2013

delete FROM I18NDISCLAIMER_TEXT
/
commit
/
alter table I18NDISCLAIMER_TEXT drop constraint WAVIER_DISCLAIMER_FK
/
ALTER TABLE I18NDISCLAIMER_TEXT RENAME COLUMN DIESEL_TIER_COUNTRY_MAPPING TO TIER_TIER_MAPPING
/
alter table I18NDISCLAIMER_TEXT add constraint WAVIER_DISCLAIMER_FK foreign key (TIER_TIER_MAPPING) references  TIER_TIER_MAPPING(ID)
/