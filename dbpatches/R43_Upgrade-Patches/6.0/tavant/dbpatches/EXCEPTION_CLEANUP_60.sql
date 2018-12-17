--Name : Raghu
--Purpose : to clean up records in the R6 instance based on the exception log
--CONFIG_PARAM_OPTION--I18NFAILURE_TYPE_DEFINITION
delete from i18nfailure_type_definition where id in (
  select min(ad.id)
  from i18nfailure_type_definition ad
  group by locale,failure_type_definition,upper(name) having count(*)>1
)
/
commit
/