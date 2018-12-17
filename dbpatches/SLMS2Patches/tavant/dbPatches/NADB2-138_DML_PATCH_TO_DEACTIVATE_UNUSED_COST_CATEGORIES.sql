--PURPOSE: TO DEACTIVATE UNUSED COST CATEGORIES
--AUTHOR     : AJIT
--CREATED ON : 02-April-14

update cost_category set d_active=0  where name in('Meals','Parking','Miscellaneous Parts','Per Diem','Rental Charges','Other Freight And Duty','Tolls','Local Purchase')
/
update section set d_active=0 where name in('Meals','Parking','Miscellaneous Parts','Per Diem','Rental Charges','Other Freight And Duty','Tolls','Local Purchase')
/
commit
/