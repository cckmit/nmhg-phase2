--Purpose    : Patch for updating jbpm table for part return flow
--Author     : Pradyot Rout
--Created On : 24-Jun-09

UPDATE jbpm_delegation SET configuration_='<beanName>partReturnService</beanName><methodName>updatePartReturnsForClaim</methodName><parameters><variable>claim</variable><variable>null</variable></parameters><transition name="goToClaimAutoAdjudication" to="ClaimAutoAdjudication"/>'
where id_ = (SELECT decisiondelegation FROM jbpm_node WHERE id_=(
select to_ from jbpm_transition  WHERE name_='goToUpdatePartReturnInformation'))
/
COMMIT
/