--PURPOSE    : PATCH FOR CHANGING DATATYPE OF COLUMN FOR ALLOWING DECIMAL VALUES ALSO, changed as a part of 4.3 upgrade
--AUTHOR     : Kuldeep Patil
--CREATED ON : 12-Oct-2010

ALTER TABLE campaign_travel_detail ADD c_Hours NUMBER(9,2)
/
UPDATE campaign_travel_detail
SET    c_Hours=hours
/
ALTER TABLE campaign_travel_detail DROP COLUMN hours
/
ALTER TABLE campaign_travel_detail RENAME COLUMN c_hours to hours
/
COMMIT
/