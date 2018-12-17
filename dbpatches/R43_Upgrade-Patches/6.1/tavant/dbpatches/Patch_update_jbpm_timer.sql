update jbpm_timer set repeat_ = '6 hours' where exception_ is null and repeat_ = '5 minutes' and name_ = 'PartsReturnScheduler'
/
commit
/

