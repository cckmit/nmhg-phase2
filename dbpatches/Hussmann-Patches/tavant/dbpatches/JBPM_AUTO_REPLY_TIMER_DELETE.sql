delete FROM jbpm_action WHERE event_ IN 
(SELECT id_ FROM jbpm_event WHERE transition_ IN (SELECT id_ FROM jbpm_transition WHERE name_ = 'TimeoutAfterForwardedInternally'))
/
delete FROM jbpm_event WHERE transition_ IN (SELECT id_ FROM jbpm_transition WHERE name_ = 'TimeoutAfterForwardedInternally')
/
delete FROM jbpm_transition WHERE name_ = 'TimeoutAfterForwardedInternally'
/
COMMIT
/
update jbpm_timer set exception_ = 'Should not be run - No BU filter set - HUSS-275'
where name_ = 'Forwarded Internally'
and exception_ is null
/
commit
/