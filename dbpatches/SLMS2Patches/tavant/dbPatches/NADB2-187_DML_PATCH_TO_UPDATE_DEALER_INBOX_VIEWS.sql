--Purpose    : Procedure for updating brand part number in inboxview for dealers, SLMSPROD-1324
--Author     : Raghavendra Raju P
--Created On : 23-July-2014

declare 
cursor c1 is  
select v.id inboxId,v.name,u.login,v.folder_name from inbox_view v, org_user u
where  v.created_by = u.id and v.field_names like '%claim.activeClaimAudit.serviceInformation.causalPart.number%' and upper(u.user_type)<>'INTERNAL' and v.folder_name <> 'Search';
begin
for each_rec in c1 loop
update inbox_view set field_names = replace(field_names,'claim.activeClaimAudit.serviceInformation.causalPart.number','claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber') where id=each_rec.inboxId;
end loop;
end;
/
commit
/