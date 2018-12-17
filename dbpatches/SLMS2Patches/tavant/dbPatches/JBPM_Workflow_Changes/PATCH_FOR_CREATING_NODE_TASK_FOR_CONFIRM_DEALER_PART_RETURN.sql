-- Purpose : Patch to create jbpm node and task for Confirm Dealer Part Returns
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
  create_jbpm_node('PartsReturn','ConfirmDealerPartReturns','A',null); 
end;
/
CREATE or replace PROCEDURE create_jbpm_form_nodes(
	def_name varchar2,
	node_name varchar2,
	form_value varchar2,
	form_type varchar2
) AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition pd where pd.name_=def_name;
	
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
	create_jbpm_form_nodes('PartsReturn', 'ConfirmDealerPartReturns', 'supplierPartReceiptFromDealer', 'actionUrl');
end;
/
create or replace procedure create_jbpm_swimlane(
	process_name varchar2,
	swimlane_name varchar2,
	delegation_classname varchar2,
	delegation_configuration varchar2
) AS
	Cursor all_defs IS
	select id_ from jbpm_processdefinition pd where name_ = process_name;
	v_sid number;
	v_delegation_id number;
	v_taskmgmt_id number;
BEGIN
	For def in all_defs
	Loop
		Begin
			select s.id_ into v_sid from jbpm_swimlane s, jbpm_moduledefinition mod_def
			where mod_def.class_ = 'T' and mod_def.processdefinition_ = def.id_ and s.TASKMGMTDEFINITION_ = mod_def.id_
			and s.name_ = swimlane_name;
		Exception
		  When No_Data_Found then
			select hibernate_sequence.nextval-1 into v_delegation_id from dual;
			insert into jbpm_delegation(ID_, CLASSNAME_, CONFIGURATION_, PROCESSDEFINITION_)
			values(v_delegation_id, delegation_classname, delegation_configuration, def.id_);
			
			select mod_def.id_ into v_taskmgmt_id from jbpm_moduledefinition mod_def
			where mod_def.class_ = 'T' and mod_def.processdefinition_ = def.id_;
			
			insert into jbpm_swimlane(ID_, NAME_, ASSIGNMENTDELEGATION_, TASKMGMTDEFINITION_)
			values(hibernate_sequence.nextval-1, swimlane_name, v_delegation_id, v_taskmgmt_id);
			
			commit;
		End;
	End Loop;
END create_jbpm_swimlane;
/
begin
	create_jbpm_swimlane('PartsReturn', 'supplier', 'tavant.twms.jbpm.assignment.ExpressionAssignmentHandler', '
        <expression>actor=ognl{partReturn.oemPartReplaced.userIfRecoveryClaimIsCreated.name}</expression>
    ');
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
	create_jbpm_task('Confirm Dealer Part Returns', 'PartsReturn', 'supplier', 'ConfirmDealerPartReturns');
end;
/
drop procedure create_jbpm_node
/
drop procedure create_jbpm_form_nodes
/
drop procedure create_jbpm_swimlane
/
drop procedure create_jbpm_task
/