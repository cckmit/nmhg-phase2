--Purpose    : Update Campaign Class with the LOV ID
--Author     : Jitesh Jain
--Created On : 30-June-09

alter table CAMPAIGN drop column "CAMPAIGN_CLASS_STRING"
/
commit
/