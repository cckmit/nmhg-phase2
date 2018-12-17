--PURPOSE    : Rules which needs code fix
--AUTHOR     : Vamshi Gunda
--CREATED ON : 18-Nov-09

update domain_rule set ognl_expression='(isACRPublished(claim) &&(claim.claimedItems.{ ? itemReference.referredInventoryItem.type.type.toLowerCase().equals("STOCK".toLowerCase()) &&(itemReference.referredInventoryItem.reportAnswers == null || itemReference.referredInventoryItem.reportAnswers.isEmpty()) }.size > 0 || !(claim.claimedItems.{ ? itemReference.referredInventoryItem.type.type.toLowerCase().equals("STOCK".toLowerCase()) &&  itemReference.referredInventoryItem.reportAnswers.{ ? customReport.reportType.code.toLowerCase().startsWith("ACR".toLowerCase()) }.size > 0 }.size > 0)))' where rule_number='999'
/
COMMIT
/
update domain_rule set ognl_expression='executeQuery("select count(*) from Claim claim where claim.id != ${claim.id}$ and claim.claimNumber is not null  and ( claim.id in( select distinct claim144538630.id from Claim claim144538630 join claim144538630.claimedItems claimedItems144538630, Claim claim join claim.claimedItems claimedItems where claimedItems144538630.hoursInService  =  claimedItems.hoursInService and claimedItems144538630.itemReference.referredInventoryItem.id  =  claimedItems.itemReference.referredInventoryItem.id and 1=1 and claim.id <> claim144538630.id and claim.id = ${claim.id}$)) and claim.repairDate = to_date(''${claim.repairDate}$'',''YY-mm-dd'') and lower(claim.workOrderNumber) = ''${claim.workOrderNumber.toLowerCase()}$'' and  claim.id in( select distinct claim144538630.id from Claim claim144538630 join claim144538630.serviceInformation si0 join si0.serviceDetail sd1 left outer join sd1.laborPerformed laborPerformed144538630, Claim claim join claim.serviceInformation si1 join si1.serviceDetail sd1 left outer join sd1.laborPerformed laborPerformed where nvl(laborPerformed144538630.serviceProcedure.id,1)  =  nvl(laborPerformed.serviceProcedure.id,1) and 1=1 and claim.id <> claim144538630.id and claim.id = ${claim.id}$)))")' where rule_number = 49 and business_unit_info='Transport Solutions ESA';
/
UPDATE domain_rule
SET   ognl_expression     ='executeQuery("select count(*) from Claim claim where claim.id != ${claim.id}$ and claim.claimNumber is not null  and ( claim.id in( select distinct claim24953360.id from Claim claim24953360 join claim24953360.claimedItems claimedItems24953360, Claim claim join claim.claimedItems claimedItems where claimedItems24953360.itemReference.referredInventoryItem.id  =  claimedItems.itemReference.referredInventoryItem.id and 1=1 and nvl(claimedItems24953360.hoursInService,0)>=nvl(claimedItems.hoursInService,0) and claim.id <> claim24953360.id and claim.id = ${claim.id}$ and to_date((claim24953360.failureDate),''YY-mm-dd'') <= claim.failureDate))")'
WHERE rule_number     =44
AND business_unit_info='Transport Solutions ESA';
/
UPDATE domain_rule
SET   ognl_expression     ='executeQuery("select count(*) from Claim claim where claim.id != ${claim.id}$ and claim.claimNumber is not null  and ( claim.id in( select distinct claim64696610.id from Claim claim64696610 join claim64696610.claimedItems claimedItems64696610, Claim claim join claim.claimedItems claimedItems where claimedItems64696610.itemReference.referredInventoryItem.id  =  claimedItems.itemReference.referredInventoryItem.id and 1=1 and claim.id <> claim64696610.id and claim.id = ${claim.id}$ and claim.failureDate = to_date(''${claim.failureDate}$'',''YY-mm-dd'') and claim.failureDate= claim64696610.failureDate and abs(claimedItems.hoursInService-claimedItems64696610.hoursInService)>=8))")'
WHERE rule_number     =47
AND business_unit_info='Transport Solutions ESA';
/
UPDATE domain_rule
SET   ognl_expression     ='executeQuery("select count(*) from Claim claim where claim.id != ${claim.id}$ and claim.claimNumber is not null  and ( claim.id in( select distinct claim24953360.id from Claim claim24953360 join claim24953360.claimedItems claimedItems24953360, Claim claim join claim.claimedItems claimedItems where claimedItems24953360.itemReference.referredInventoryItem.id  =  claimedItems.itemReference.referredInventoryItem.id and 1=1 and nvl(claimedItems24953360.hoursInService,0)>=nvl(claimedItems.hoursInService,0) and claim.id <> claim24953360.id and claim.id = ${claim.id}$ and to_date((claim24953360.repairDate),''YY-mm-dd'') <= claim.repairDate))")'
WHERE rule_number     =67
AND business_unit_info='Transport Solutions ESA';
/
UPDATE domain_rule
SET   ognl_expression     ='executeQuery("select count(*) from Claim claim where claim.id != ${claim.id}$ and claim.claimNumber is not null  and ( claim.id in( select distinct claim24953360.id from Claim claim24953360 join claim24953360.claimedItems claimedItems24953360, Claim claim join claim.claimedItems claimedItems where claimedItems24953360.itemReference.referredInventoryItem.id  =  claimedItems.itemReference.referredInventoryItem.id and 1=1 and nvl(claimedItems24953360.hoursInService,0)<=nvl(claimedItems.hoursInService,0) and claim.id <> claim24953360.id and claim.id = ${claim.id}$ and to_date((claim24953360.repairDate),''YY-mm-dd'') >= claim.repairDate))")'
WHERE rule_number     =68
AND business_unit_info='Transport Solutions ESA';
/
UPDATE domain_rule
SET   ognl_expression     ='executeQuery("select count(*) from Claim claim where claim.id != ${claim.id}$ and claim.claimNumber is not null  and ( claim.id in( select distinct claim64696610.id from Claim claim64696610 join claim64696610.claimedItems claimedItems64696610, Claim claim join claim.claimedItems claimedItems where claimedItems64696610.itemReference.referredInventoryItem.id  =  claimedItems.itemReference.referredInventoryItem.id and 1=1 and claim.id <> claim64696610.id and claim.id = ${claim.id}$ and claim.repairDate = to_date(''${claim.repairDate}$'',''YY-mm-dd'') and claim.repairDate= claim64696610.repairDate and abs(claimedItems.hoursInService-claimedItems64696610.hoursInService)>=8))")'
WHERE rule_number     =70
AND business_unit_info='Transport Solutions ESA';