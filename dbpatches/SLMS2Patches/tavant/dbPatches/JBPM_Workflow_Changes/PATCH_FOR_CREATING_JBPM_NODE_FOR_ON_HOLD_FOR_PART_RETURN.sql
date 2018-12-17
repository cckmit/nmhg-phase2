-- Purpose : Patch to create jbpm node On Hold For Part Return
-- Created On : 09-June_2014
-- Created By : ParthaSarathy R

create or replace procedure create_jbpm_node (
	p_process varchar2,
	p_name varchar2,
	p_class varchar2,
	p_end_tasks varchar2
) AS
	cursor all_defs is
	select id_ from jbpm_processdefinition where name_=p_process;
	
	v_nid number;
    v_did number;
	v_index number;
	
BEGIN
for def in all_defs loop
begin
	select n.id_ into v_nid from jbpm_node n
	where n.processdefinition_=def.id_ and n.name_=p_name;
	
exception
	when no_data_found then
	
	select max(nodecollectionindex_) into v_index
		from jbpm_node where processdefinition_=def.id_;
		
	select hibernate_sequence.nextval-1 into v_nid from dual;
	
		insert into jbpm_node 
		(id_,class_,name_,processdefinition_,isasync_,signal_,createtasks_,endtasks_,nodecollectionindex_)
		values (v_nid,p_class,p_name,def.id_,0,4,1,0,v_index);	
	
	update jbpm_node n set nodecollectionindex_=v_index+1
	where class_='E' and name_='End' and processdefinition_ = def.id_;
	commit;
	
end;
end loop;
END create_jbpm_node;
/
begin
  create_jbpm_node('SupplierRecovery','On Hold For Part Return','A',null); 
end;
/
CREATE PROCEDURE create_jbpm_form_nodes(
	def_name varchar2,
	node_name varchar2,
	form_value varchar2,
	form_type varchar2
) AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition where name_=def_name;
	
	v_nid number;
	v_node_id number;
BEGIN
	For def in all_defs
	LOOP
		BEGIN
			select form_nodes.FORM_TASK_NODE_FORM_ID into v_nid
			from jbpm_processdefinition defn, jbpm_node node, jbpm_form_nodes form_nodes
			where defn.id_ = def.id_
			and node.name_ = node_name and node.processdefinition_ = defn.id_
			and form_nodes.form_task_node_form_id = node.id_;
		
		Exception When No_Data_Found Then
			select n.id_ into v_node_id from jbpm_node n
			where n.processdefinition_ = def.id_ and n.name_ = node_name;
			
			insert into JBPM_FORM_NODES(FORM_TASK_NODE_FORM_ID, FORM_VALUE, FORM_TYPE)
			values (v_node_id, form_value, form_type);
			commit;
		
		END;
	END LOOP;
END create_jbpm_form_nodes;
/
begin
	create_jbpm_form_nodes('SupplierRecovery', 'On Hold For Part Return', 'onHoldForPartReturn', 'actionUrl');
end;
/
commit
/
drop procedure create_jbpm_node
/
drop procedure create_jbpm_form_nodes
/