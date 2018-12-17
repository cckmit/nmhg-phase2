--PURPOSE    : patch_to_change_campaign_description_data_type_length
--AUTHOR     : Raghavendra
--CREATED ON : 13-MAY-14


alter table CAMPAIGN modify (description varchar2(4000 CHAR))
/
alter table i18ncampaign_text modify (description varchar2(4000 CHAR))
/
commit
/