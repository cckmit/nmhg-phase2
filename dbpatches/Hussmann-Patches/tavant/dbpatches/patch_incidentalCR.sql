ALTER TABLE
   campaign_service_detail
ADD
   (
   PER_DIEM_AMT         NUMBER(19,2),
   PER_DIEM_CURR        VARCHAR2(255 CHAR),
   RENTAL_CHARGES_AMT   NUMBER(19,2),
   RENTAL_CHARGES_CURR  VARCHAR2(255 CHAR)
)
/
ALTER TABLE
   campaign_travel_detail
ADD
   (
    ADDITIONAL_HOURS     NUMBER(19,2)  
)
/
commit
