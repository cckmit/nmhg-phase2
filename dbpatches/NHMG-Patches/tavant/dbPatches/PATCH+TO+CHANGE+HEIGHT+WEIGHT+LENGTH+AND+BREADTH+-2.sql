-- PURPOSE    : PATCH TO CHANGE HEIGHT WEIGHT LENGTH AND BREADTH -2.sql
-- AUTHOR     : PAllavi
-- CREATED ON : 6-May-2013
update shipment_load_dimension set h1=height
/
update shipment_load_dimension set height=''
/
update shipment_load_dimension set w1=weight
/
update shipment_load_dimension set weight=''
/
update shipment_load_dimension set b1=breadth
/
update shipment_load_dimension set breadth=''
/
update shipment_load_dimension set l1=length
/
update shipment_load_dimension set length=''
/
commit
/