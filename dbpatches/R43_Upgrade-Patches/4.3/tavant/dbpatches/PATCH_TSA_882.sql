--Purpose : ESESA-888 as a part of 4.3 upgrade 
--Author : raghuram.d
--Date : 04/Apr/2010

update domain_predicate set predicate_asxml=replace(predicate_asxml,'<lesserBy>','<atleastLesserBy>')
where predicate_asxml like '%<lesserBy>%'
/
update domain_predicate set predicate_asxml=replace(predicate_asxml,'</lesserBy>','</atleastLesserBy>')
where predicate_asxml like '%</lesserBy>%'
/
update domain_predicate set predicate_asxml=replace(predicate_asxml,'<greaterBy>','<atleastGreaterBy>')
where predicate_asxml like '%<greaterBy>%'
/
update domain_predicate set predicate_asxml=replace(predicate_asxml,'</greaterBy>','</atleastGreaterBy>')
where predicate_asxml like '%</greaterBy>%'
/
commit
/