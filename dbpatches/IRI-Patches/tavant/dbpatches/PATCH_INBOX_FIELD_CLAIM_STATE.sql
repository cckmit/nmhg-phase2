--Purpose : Inbox field name for claim state is updated, to enable filtering on claim state
--Author : raghuram.d
--Date : 28/Aug/09

update inbox_view set sort_by_field = 'enum:ClaimState:claim.state' 
where sort_by_field='claim.state' and type='ClaimSearches'
/
update inbox_view set field_names=replace(field_names,'claim.state','enum:ClaimState:claim.state') 
where type='ClaimSearches'
    and ( field_names like 'claim.state'
        or field_names like 'claim.state,%'
        or field_names like '%,claim.state'
        or field_names like '%,claim.state,%')

/
commit
/