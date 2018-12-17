--PURPOSE    : PATCH FOR setting the to_ for transition no when policy computation is not required
--AUTHOR     : PRADYOT ROUT
--CREATED ON : 16-FEB-09

update jbpm_transition set to_=(
select id_ from jbpm_node where id_ = (select to_ from jbpm_transition where
from_=(select id_ from jbpm_node where name_='ComputePolicy')))
where from_ = (select id_ from jbpm_node where name_='IsPolicyComputationRequired')
and name_='No'