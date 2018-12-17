--Purpose    : Update Campaign Class with the LOV ID
--Author     : Jitesh Jain
--Created On : 30-June-09

CREATE OR REPLACE
PROCEDURE UPDATE_CAMPAIGN_CLASS AS
CURSOR C1 IS
select campaign_class_string, business_unit_info,id from campaign where campaign_class_string is not null and business_unit_info is not null;

BEGIN
  FOR c1_rec IN C1 LOOP 
    UPDATE campaign set campaign_class = (select id from list_of_values where business_unit_info = c1_rec.business_unit_info 
          and code = c1_rec.campaign_class_string and type ='CAMPAIGNCLASS')
    where id = c1_rec.id;
    commit;
  END LOOP;
END UPDATE_CAMPAIGN_CLASS;
/
BEGIN
UPDATE_CAMPAIGN_CLASS();
END;
/

