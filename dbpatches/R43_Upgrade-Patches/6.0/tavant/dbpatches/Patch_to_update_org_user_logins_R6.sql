--Name   : Joseph
--Date   : 24 June 2011
--Impact : Modfies login so that these tktsa logins do not merge with the R4 logins who have the same names
update org_user set login = 'bschroeder'  where lower(login) = 'bob'
/
update org_user set login = 'tktsa_heaneysa'  where lower(login) = 'heaneysa'
/
update org_user set login = 'tktsa_wvdwouw'  where lower(login) = 'wvdwouw'
/
commit
/
update jbpm_taskinstance set actorid_ = 'bschroeder'  where lower(actorid_) = 'bob'
/
update jbpm_taskinstance set actorid_ = 'tktsa_heaneysa'  where lower(actorid_) = 'heaneysa'
/
update jbpm_taskinstance set actorid_ = 'tktsa_wvdwouw'  where lower(actorid_) = 'wvdwouw'
/
commit
/