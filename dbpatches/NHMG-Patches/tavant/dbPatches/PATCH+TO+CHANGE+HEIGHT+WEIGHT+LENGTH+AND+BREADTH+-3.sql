-- PURPOSE    : PATCH TO CHANGE HEIGHT WEIGHT LENGTH AND BREADTH -3.sql
-- AUTHOR     : PAllavi
-- CREATED ON : 6-May-2013
alter table shipment_load_dimension modify height varchar2(255)
/
alter table shipment_load_dimension modify weight varchar2(255)
/
alter table shipment_load_dimension modify breadth varchar2(255)
/
alter table shipment_load_dimension modify length varchar2(255)
/