-- Purpose    : Migrate Incidental Cost Categories in the Campaign service detail
-- Author     : Jitesh Jain
-- Created On : 12-Jul-09


CREATE OR REPLACE
PROCEDURE MIGRATE_CAMPAIGN_SECTIONS AS
CURSOR C1 IS
select FREIGHT_DUTY_AMT,MEALS_AMT,PARKING_AND_TOLL_EXPENSE_AMT,PER_DIEM_AMT,RENTAL_CHARGES_AMT,id from campaign_service_detail;

BEGIN
  FOR c1_rec IN C1 LOOP 
    insert into campaign_section_price values (campaign_section_price_seq.nextval,c1_rec.FREIGHT_DUTY_AMT,'USD',null,'FREIGHT_DUTY',c1_rec.id);
    insert into campaign_section_price values (campaign_section_price_seq.nextval,c1_rec.MEALS_AMT,'USD',null,'MEALS',c1_rec.id);
    insert into campaign_section_price values (campaign_section_price_seq.nextval,c1_rec.PARKING_AND_TOLL_EXPENSE_AMT,'USD',null,'PARKING',c1_rec.id);
    insert into campaign_section_price values (campaign_section_price_seq.nextval,c1_rec.PER_DIEM_AMT,'USD',null,'PER_DIEM',c1_rec.id);
    insert into campaign_section_price values (campaign_section_price_seq.nextval,c1_rec.RENTAL_CHARGES_AMT,'USD',null,'RENTAL_CHARGES',c1_rec.id);
  END LOOP;
  COMMIT;
END MIGRATE_CAMPAIGN_SECTIONS;
/
BEGIN
MIGRATE_CAMPAIGN_SECTIONS();
END;
/