--Purpose    : Patch TO ADD COLUMN IN travel_detail
--Author     : AJIT KUMAR SINGH
--Created On : 19-APR-2014

alter table travel_detail
add
(
BASE_DISTANCE  NUMBER(19,2),
BASE_HOURS NUMBER(19,2)
)
/