--Purpose : TSESA-579
--Author : raghuram.d
--Date : 09/Jul/2011

update domain_predicate set predicate_asxml=replace(predicate_asxml,
  '<fieldName>itemReference.model.isPartOf.name</fieldName>',
  '<fieldName>itemReference.model.getProductNameForUnserializedItem()</fieldName>'
  )
where predicate_asxml like '%<fieldName>itemReference.model.isPartOf.name</fieldName>%'
/ 
update domain_predicate set predicate_asxml=replace(predicate_asxml,
  '<fieldName>pricePerUnit</fieldName>',
  '<fieldName>pricePerUnit.breachEncapsulationOfAmount()</fieldName>'
  )
where predicate_asxml like '%<fieldName>pricePerUnit</fieldName>%'
/
update domain_rule set ognl_expression=replace(ognl_expression,
  'itemReference.model.isPartOf.name',
  'itemReference.model.getProductNameForUnserializedItem()')
where ognl_expression like '%itemReference.model.isPartOf.name%'
/
update domain_rule set ognl_expression=replace(ognl_expression,
  'pricePerUnit',
  'pricePerUnit.breachEncapsulationOfAmount()')
where ognl_expression like '%pricePerUnit%'
  and ognl_expression not like '%pricePerUnit.breachEncapsulationOfAmount()%'
/
COMMIT
/
