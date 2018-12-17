--PURPOSE    : PATCH FOR ADDING ADDITIONAL COLUMN Dealer Pickup Location to Base_part_return table
--AUTHOR     : Suneetha Nagaboyina
--CREATED ON : 18-DEC-2012

alter table base_part_return add (DEALER_PICKUP_LOCATION VARCHAR2(255 CHAR))
/