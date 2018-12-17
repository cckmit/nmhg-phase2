--Purpose    : Patch for updating jbpm table for part return flow when a prc payment condition is pay on part return
--Author     : Pradyot Rout
--Created On : 1-Jul-09

update jbpm_node set end_transition=null, normal_transition=null, class_='D'
where name_='Clone'
/
update jbpm_transition set fromindex_=1 where name_='Notify Payment On Receipt'
/
update jbpm_transition set fromindex_=0 where id_=(select id_ from jbpm_transition where from_=(select id_ from jbpm_node where name_='Clone')
and name_='Send for Inspection') and name_='Send for Inspection'
/
COMMIT
/