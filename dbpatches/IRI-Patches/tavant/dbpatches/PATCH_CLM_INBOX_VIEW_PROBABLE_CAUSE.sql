--Purpose : Remove Probable Cause from all the folder views
--Author : raghuram.d
--Date : 10-Nov-09

update inbox_view set field_names=replace(field_names,',claim.probableCause','')
where field_names like '%claim.probableCause%'
/
update inbox_view set field_names=replace(field_names,'claim.probableCause,','')
where field_names like '%claim.probableCause%'
/
update inbox_view set field_names=replace(field_names,'claim.probableCause','')
where field_names like '%claim.probableCause%'
/
update inbox_view set field_names=replace(field_names,',probableCause','')
where field_names like '%probableCause%'
/
update inbox_view set field_names=replace(field_names,'probableCause,','')
where field_names like '%probableCause%'
/
update inbox_view set field_names=replace(field_names,'probableCause','')
where field_names like '%probableCause%'
/
commit
/
