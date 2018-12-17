--Purpose : Fix for TSESA-259. Updated the input form to use the same as processor review. 
--Author  : smita.kadle
--Date    : 09/Feb/2010

update jbpm_form_nodes
set form_value = 'processor_review'
where form_value = 'part_shipped_not_received'
/
commit
/