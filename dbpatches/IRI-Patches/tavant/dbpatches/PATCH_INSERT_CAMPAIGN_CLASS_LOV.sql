--Purpose    : Update Campaign Class with the LOV ID
--Author     : Jitesh Jain
--Created On : 30-June-09

ALTER TABLE CAMPAIGN RENAME COLUMN "CAMPAIGN_CLASS" TO "CAMPAIGN_CLASS_STRING"
/
alter table CAMPAIGN add(CAMPAIGN_CLASS NUMBER(19))
/
alter table CAMPAIGN add constraint CAMPAIGN_LOV_FK foreign key("CAMPAIGN_CLASS") references "LIST_OF_VALUES"("ID")
/
Commit
/
