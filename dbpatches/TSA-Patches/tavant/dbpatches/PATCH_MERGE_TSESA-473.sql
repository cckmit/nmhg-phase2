--Purpose: Merge TSESA-473 from R4 to R6
--Author: raghuram.d
--Date: 27/Apr/2011

update applicable_policy set d_updated_on=sysdate,d_updated_time=systimestamp, 
  d_internal_comments=d_internal_comments||':TSESA-473', 
  policy_definition=(select policy_definition from policy p where p.id=registered_policy) 
where id in ( 
    select ap.id from applicable_policy ap,claimed_item ci,claim c 
    where ap.registered_policy is not null and ap.policy_definition is null 
    and ap.d_active=1 and ap.id=ci.applicable_policy and ci.claim=c.id 
  )
/
commit
/