-- Purpose : Patch to create jbpm workflow Routed to NMHG
-- Created On : 12-June_2014
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
  create_jbpm_node('SupplierPartReturn','Routed to NMHG','A',null); 
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
	create_jbpm_form_nodes('SupplierPartReturn', 'Routed to NMHG', 'routedToNMHG', 'actionUrl');
end;
/
create or replace procedure create_jbpm_task(
	task_name varchar2,
	def_name varchar2,
	swimlane varchar2,
	node varchar2
) AS
	CURSOR all_def IS
	select id_ from jbpm_processdefinition where name_ = def_name;
	v_task_id number;
	v_taskmgmt_defn number;
	v_swimlane_id number;
	v_node_id number;
BEGIN
	FOR def in all_def
	LOOP
		BEGIN
			select task.id_ into v_task_id
			from jbpm_task task	where task.processdefinition_ = def.id_ and task.name_ = task_name;
		EXCEPTION
			WHEN NO_DATA_FOUND THEN
			select module_def.id_ into v_taskmgmt_defn from jbpm_moduledefinition module_def where processdefinition_ = def.id_ and class_ = 'T';
			select swimlane.id_ into v_swimlane_id from jbpm_swimlane swimlane where swimlane.taskmgmtdefinition_ = v_taskmgmt_defn and swimlane.name_ = swimlane;
			select n.id_ into v_node_id from jbpm_node n where n.name_ = node and n.processdefinition_ = def.id_;
			
			insert into jbpm_task(id_, name_, processdefinition_, isblocking_, issignalling_, TASKMGMTDEFINITION_, TASKNODE_, SWIMLANE_)
			values(hibernate_sequence.nextval-1, task_name, def.id_, 0, 1, v_taskmgmt_defn, v_node_id, v_swimlane_id);
			commit;
		END;
	END LOOP;
END create_jbpm_task;
/
begin
	create_jbpm_task('Routed to NMHG', 'SupplierPartReturn', 'sra', 'Routed to NMHG');
end;
/
create or replace procedure create_jbpm_transition (
  p_process varchar2,
	p_tname varchar2,
	p_fromnode varchar2,
	p_tonode varchar2,
	p_script VARCHAR2
) AS
  cursor all_defs is
	select id_ from jbpm_processdefinition where name_=p_process;
	v_id NUMBER;
	v_event NUMBER;
	v_index NUMBER;
BEGIN
for def in all_defs loop
BEGIN
	select t.id_ into v_id
	from jbpm_transition t,jbpm_node n1,jbpm_node n2
	where t.processdefinition_=def.id_ and t.from_=n1.id_ and t.to_=n2.id_
		and t.name_=p_tname	and n1.name_=p_fromnode and n2.name_=p_tonode;
EXCEPTION
	WHEN NO_DATA_FOUND THEN
	
	select count(*) into v_index
	from jbpm_transition t,jbpm_node n1
	where t.processdefinition_=def.id_ and t.from_=n1.id_
		and n1.processdefinition_=def.id_ and n1.name_=p_fromnode ;
	  
	select hibernate_sequence.nextval-1 into v_id from dual;
	insert into jbpm_transition (id_,name_,processdefinition_,from_,to_,fromindex_)
	select v_id,p_tname,def.id_,n.id_,
	  (select id_ from jbpm_node n where name_=p_tonode and processdefinition_=def.id_),
    v_index
	from jbpm_node n where name_=p_fromnode and processdefinition_=def.id_;
end;
if p_script is not null then
  begin
    select e.id_ into v_event
    from jbpm_event e where e.transition_=v_id and eventtype_='transition' and type_='T';
  exception when no_data_found then
		select hibernate_sequence.nextval-1 into v_event from dual;
		insert into jbpm_event (id_,eventtype_,type_,graphelement_,transition_)
		values (v_event,'transition','T',v_id,v_id);
    
		insert into jbpm_action (id_,class,ispropagationallowed_,isasync_,event_,expression_,eventindex_)
		values (hibernate_sequence.nextval-1,'S',1,0,v_event,p_script,0);
  end;
end if;
	
commit;
end loop;
END create_jbpm_transition;
/
begin
  create_jbpm_transition('SupplierPartReturn','Routed to NMHG','Start','Routed to NMHG','supplierPartReturn.setStatus(tavant.twms.domain.partreturn.PartReturnStatus.SUPPLIER_PART_RETURN_REQUESTED)');   
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Generate Shipment','Routed to NMHG','SupplierPartReturnFork',null);   
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Awaiting Shipment to Warehouse','Routed to NMHG','AwaitngShipmentToWarehouseFork',null);   
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Routed to NMHG','End',null);   
end;
/
commit
/
drop procedure create_jbpm_node
/
drop procedure create_jbpm_form_nodes
/
drop procedure create_jbpm_task
/
drop procedure create_jbpm_transition
/