-- Purpose    : TSESA-262 The rules will be evaluated only in ENGLISH after the bug-fix. Hence, "faultFound.name"  needs to be updated to the new value  "faultFound.nameInEnglish" for all existing DOMAIN_RULE and DOMAIN_PREDICATE records which contain "faultFound.name" in the xmls.
-- Author     :  Jairam
-- Created On : 25-Mar-2010

update domain_rule 
set ognl_expression = replace ( ognl_expression, 'faultFound.name', 'faultFound.nameInEnglish'),
d_updated_on = sysdate, d_updated_time = sysdate,
d_internal_comments = d_internal_comments || ', Updated faultFound.name in ognl_expression to faultFound.nameInEnglish - for Bug fix # TSESA-262'
where ognl_expression like '%faultFound.name%'
/
update domain_predicate set predicate_asxml = replace ( predicate_asxml, 'faultFound.name', 'faultFound.nameInEnglish'), 
d_updated_on = sysdate, d_updated_time = sysdate,
d_internal_comments = d_internal_comments || ', Updated faultFound.name in predicate_asxml to faultFound.nameInEnglish - for Bug fix # TSESA-262'
where predicate_asxml like '%faultFound.name%' and context in (
	'ClaimRules','DcapValidationRules','ClaimDuplicacyRules')
/
COMMIT
/