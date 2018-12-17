--PURPOSE    : patch_to_drop_constraint on CAMPAIGN_SNO_COVERAGE,campaign_range_coverage
--AUTHOR     : Raghavendra
--CREATED ON : 01-OCT-13

alter table CAMPAIGN_SNO_COVERAGE drop constraint CAMPAIGNSNOCOVERAGE_ID_FK
/
alter table campaign_range_coverage drop constraint CAMPAIGNRANGECOVERAGE_ID_FK
/
commit
/