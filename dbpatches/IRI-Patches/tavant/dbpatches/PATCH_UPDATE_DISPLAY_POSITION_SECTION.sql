-- Purpose    : Changing the display position of the payment Section
-- Author     : Jitesh Jain
-- Created On : 19-Jun-09

update section set display_position=0 where name='Club Car Parts'
/
update section set display_position=1 where name='Non Club Car Parts'
/
update section set display_position=2 where name='MiscellaneousParts'
/
update section set display_position=3 where name='Labor'
/
update section set display_position=4 where name='Travel By Distance'
/
update section set display_position=5 where name='Travel by Hours'
/
update section set display_position=6 where name='Travel By Trip'
/
update section set display_position=7 where name='Per Diem'
/
update section set display_position=8 where name='Rental Charges'
/
update section set display_position=9 where name='Item Freight And Duty'
/
update section set display_position=10 where name='Meals'
/
update section set display_position=11 where name='Parking'
/
update section set display_position=12 where name='Additional Travel Hours'
/
update section set display_position=13 where name='Local Purchase'
/
update section set display_position=14 where name='Tolls'
/
update section set display_position=15 where name='Other Freight And Duty'
/
update section set display_position=16 where name='Others'
/
update section set display_position=17 where name='Claim Amount'
/
commit
/
