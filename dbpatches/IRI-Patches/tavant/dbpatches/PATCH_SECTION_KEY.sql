--PURPOSE    : PATCH FOR BU BASED NAMES FOR SECTION IN PAYMENTS
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 30-JAN-09

ALTER TABLE SECTION ADD MESSAGE_KEY VARCHAR2(255)
/
UPDATE SECTION SET MESSAGE_KEY='label.section.replacedParts' WHERE NAME='Club Car Parts'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.nonReplacedParts' WHERE NAME='Non Club Car Parts'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.labor' WHERE NAME='Labor'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.travelByDistance' WHERE NAME='Travel By Distance'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.freight' WHERE NAME='Item Freight And Duty'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.meals' WHERE NAME='Meals'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.parking' WHERE NAME='Parking'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.claimAmount' WHERE NAME='Claim Amount'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.travelByTrip' WHERE NAME='Travel By Trip'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.travelByHours' WHERE NAME='Travel by Hours'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.perDiem' WHERE NAME='Per Diem'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.rentalCharges' WHERE NAME='Rental Charges'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.addnTravelHrs' WHERE NAME='Additional Travel Hours'
/
UPDATE SECTION SET MESSAGE_KEY='label.section.miscParts' WHERE NAME='MiscellaneousParts'
/
COMMIT
/