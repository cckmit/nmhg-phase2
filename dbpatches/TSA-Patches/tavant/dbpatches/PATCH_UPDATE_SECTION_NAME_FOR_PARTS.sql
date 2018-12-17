update section  set name='Oem Parts' where name='Club Car Parts'
/
update section set name='Non Oem Parts' where name='Non Club Car Parts'
/
update line_item_group set name='Oem Parts' where name ='Club Car Parts'
/
update line_item_group set name='Non Oem Parts' where name ='Non Club Car Parts'
/
Update Cost_Category 
Set Name = 'Non Oem Parts', Description = 'Non Oem Parts'
where code = 'NON_OEM_PARTS'
/
Update Cost_Category 
Set Name = 'Oem Parts', Description = 'Oem Parts'
where code = 'OEM_PARTS'
/
commit
/