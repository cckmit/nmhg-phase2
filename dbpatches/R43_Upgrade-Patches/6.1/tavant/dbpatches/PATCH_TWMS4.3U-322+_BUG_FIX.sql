--Purpose    : Updating domain_predicate and domain rule to change the business condition for particular BU whoose predicate_asml contain oemPartReplaced' to replacedParts
--Author     : ajitkumar.singh
--Created On : 07/05/2011
--1

update domain_rule
set ognl_expression=replace(ognl_expression,'claim.serviceInformation.serviceDetail.oemPartsReplaced','claim.serviceInformation.serviceDetail.replacedParts') 
where predicate in(
select id from domain_predicate where business_unit_info in('Clubcar ESA','Transport Solutions ESA','TFM','AIR')
and predicate_asxml like '%claim.serviceInformation.serviceDetail.oemPartsReplaced%')
/
commit
/
--2
update domain_predicate 
set predicate_asxml=replace(predicate_asxml,'claim.serviceInformation.serviceDetail.oemPartsReplaced','claim.serviceInformation.serviceDetail.replacedParts')
where business_unit_info in('Clubcar ESA','Transport Solutions ESA','TFM','AIR')
and predicate_asxml like '%claim.serviceInformation.serviceDetail.oemPartsReplaced%'
/
commit
/