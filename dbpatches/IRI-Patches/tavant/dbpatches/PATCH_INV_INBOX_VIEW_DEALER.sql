--Purpose : Disable service provider name and number fields in stock/retail folder views for dealers
--Author : raghuram.d
--Date : 06-Nov-09

update inbox_view set field_names=replace(field_names,',currentOwner.dealerNumber','')
where field_names like '%currentOwner.dealerNumber%'
/
update inbox_view set field_names=replace(field_names,'currentOwner.dealerNumber,','')
where field_names like '%currentOwner.dealerNumber%'
/
update inbox_view set field_names=replace(field_names,'currentOwner.dealerNumber','')
where field_names like '%currentOwner.dealerNumber%'
/
update inbox_view set field_names=replace(field_names,',currentOwner.name','')
where field_names like '%currentOwner.name%'
/
update inbox_view set field_names=replace(field_names,'currentOwner.name,','')
where field_names like '%currentOwner.name%'
/
update inbox_view set field_names=replace(field_names,'currentOwner.name','')
where field_names like '%currentOwner.name%'
/
commit
/
