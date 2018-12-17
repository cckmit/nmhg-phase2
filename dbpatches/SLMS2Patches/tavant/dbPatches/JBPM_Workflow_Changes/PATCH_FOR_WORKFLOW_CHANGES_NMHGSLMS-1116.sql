-- Patch for JBPM Workflow changes for NMHGSLMS-1116
-- Created By : ParthaSarathy R
-- Created On : 16-08-2014

create or replace procedure create_jbpm_node (
	p_process varchar2,
	p_name varchar2,
	p_class varchar2,
	p_isasync number,
	p_signal number,
	p_createtasks number,
	p_endtasks number
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
		values (v_nid,p_class,p_name,def.id_,p_isasync,p_signal,p_createtasks,p_endtasks,v_index);	
	
	update jbpm_node n set nodecollectionindex_=v_index+1
	where class_='E' and name_='End' and processdefinition_ = def.id_;
	commit;
	
end;
end loop;
END create_jbpm_node;
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
create or replace procedure create_jbpm_decision(
	p_process_name varchar2,
	p_node_name varchar2,
	p_transition_name varchar2,
	p_expression varchar2,
	p_index number
)
AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition where name_ = p_process_name;
	v_d_id number;
	v_node_id number;
BEGIN
	FOR def in all_defs
	LOOP
		BEGIN
			select decision.decision_ into v_d_id
			from jbpm_processdefinition pd, jbpm_node n, jbpm_decisionconditions decision
			where pd.id_ = def.id_ and n.name_ = p_node_name and n.processdefinition_ = pd.id_
			and decision.decision_ = n.id_ and decision.transitionname_ = p_transition_name;
			
		Exception
		  When No_Data_Found THEN
			
			select n.id_ into v_node_id from jbpm_node n where n.name_ = p_node_name and n.processdefinition_ = def.id_;
			
			insert into jbpm_decisionconditions(DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
			values(v_node_id, p_transition_name, p_expression, p_index);
			
			commit;
			
		END;
	END LOOP;
END create_jbpm_decision;
/
create or replace procedure update_jbpm_transition_to_node(
	p_process_name varchar2,
	p_from_node_name varchar2,
	p_to_node_name varchar2,
	p_transition_name varchar2
)
AS
	cursor all_defs IS
	select id_ from jbpm_processdefinition where name_ = p_process_name;
	v_to_node_id number;
	v_transition_id number;
	v_id number;
begin
	for def in all_defs
	loop
		begin
			select t.id_ into v_id from jbpm_transition t, jbpm_node from_node, jbpm_node to_node
			where from_node.processdefinition_ = def.id_ and from_node.name_ = p_from_node_name and to_node.name_ = p_to_node_name
			and t.from_ = from_node.id_ and t.to_ = to_node.id_ and t.name_ = p_transition_name;
			
		exception
		  when No_Data_Found then
			
			select id_ into v_to_node_id from jbpm_node where processdefinition_ = def.id_ and name_ = p_to_node_name;
			
			select t.id_ into v_transition_id from jbpm_node from_node, jbpm_transition t
			where from_node.processdefinition_ = def.id_ and t.from_ = from_node.id_ and from_node.name_ = p_from_node_name
			and t.name_ = p_transition_name;
		
			update jbpm_transition set to_ = v_to_node_id where id_ = v_transition_id;
			
			commit;
		end;
	end loop;
end update_jbpm_transition_to_node;
/
create or replace procedure update_jbpm_delegation(
	p_process_name varchar2,
	p_node_name varchar2,
	p_event_type varchar2,
	p_delegation_class varchar2
) AS
	cursor all_defs IS
	select id_ from jbpm_processdefinition where name_ = p_process_name;
	v_id number;
	v_delegation_id number;
begin
	for def in all_defs
	loop
		begin
			select d.id_ into v_id from jbpm_node n, jbpm_task t, jbpm_event e, jbpm_action a, jbpm_delegation d
			where n.name_ = p_node_name and n.processdefinition_ = def.id_ and t.tasknode_ = n.id_
			and e.task_ = t.id_ and a.event_ = e.id_
			and d.id_ = a.actiondelegation_ and d.classname_ = p_delegation_class;
			
			Exception
			  When No_Data_Found then
				select d.id_ into v_delegation_id from jbpm_node n, jbpm_task t, jbpm_event e, jbpm_action a, jbpm_delegation d
				where n.name_ = p_node_name and n.processdefinition_ = def.id_ and t.tasknode_ = n.id_
				and e.task_ = t.id_ and a.event_ = e.id_
				and d.id_ = a.actiondelegation_;
				
				update jbpm_delegation set classname_ = p_delegation_class where id_ = v_delegation_id;
				
				commit;
		end;
	end loop;
end update_jbpm_delegation;
/
CREATE OR REPLACE PROCEDURE create_jbpm_event(
	p_name varchar2,
	event_type varchar2,
	type_ varchar2,
	task_name varchar2
) AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition where name_ = p_name;
	v_event_id number;
	v_task_id number;
BEGIN
	FOR def in all_defs
	LOOP
		BEGIN
			select e.id_ into v_event_id from jbpm_event e, jbpm_task t
			where t.name_ = task_name and e.task_ = t.id_
			and t.processdefinition_ = def.id_ and e.eventtype_ = event_type;
		Exception
			When No_Data_Found THEN
			select t.id_ into v_task_id from jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_;
			
			insert into jbpm_event(ID_, EVENTTYPE_, TYPE_, GRAPHELEMENT_, TASK_)
			values(hibernate_sequence.nextval-1, event_type, type_, v_task_id, v_task_id);
			commit;
		END;
	END LOOP;
END create_jbpm_event;
/
create or replace procedure create_action_with_action(
	process_name varchar2,
	task_name varchar2,
	event_type varchar2,
	class_ varchar2,
	timer_name varchar2,
	due_days varchar2,
	delegation_classname varchar2,
	delegation_configuration varchar2
) AS 
	cursor all_defs is
	select id_ from jbpm_processdefinition where name_ = process_name;
	v_action_id number;
	v_timer_action_id number;
	v_delegation_id number;
	v_event_id number;
BEGIN
	FOR def in all_defs
	LOOP
		BEGIN
			select a.id_ into v_action_id
			from jbpm_event e, jbpm_action a, jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and a.event_ = e.id_
			and e.eventtype_ = event_type;
		EXCEPTION
		  WHEN No_Data_Found THEN
			select hibernate_sequence.nextval-1 into v_delegation_id from dual;
			insert into jbpm_delegation(ID_, CLASSNAME_, CONFIGURATION_, PROCESSDEFINITION_)
			values(v_delegation_id, delegation_classname, delegation_configuration, def.id_);
			
			select hibernate_sequence.nextval-1 into v_timer_action_id from dual;
			insert into jbpm_action(ID_, CLASS, ISPROPAGATIONALLOWED_, ISASYNC_, ACTIONDELEGATION_)
			values(v_timer_action_id, 'A', 1, 0, v_delegation_id);
			
			select e.id_ into v_event_id from jbpm_event e, jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and e.eventtype_ = event_type;
			
			insert into jbpm_action(ID_, CLASS, ISPROPAGATIONALLOWED_, ISASYNC_, EVENT_, TIMERNAME_, DUEDATE_, TIMERACTION_, EVENTINDEX_)
			values(hibernate_sequence.nextval-1, class_, 1, 0, v_event_id, timer_name, due_days, v_timer_action_id, 0);
			commit;
		END;
	END LOOP;
END create_action_with_action;
/
create or replace procedure create_action_with_delegation(
	process_name varchar2,
	task_name varchar2,
	event_type varchar2,
	class_ varchar2,
	delegation_classname varchar2
) AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition where name_ = process_name;
	v_delegation_id number;
	v_event_id number;
	v_action_id number;
BEGIN
	FOR def in all_defs
	LOOP
		BEGIN
			select a.id_ into v_action_id
			from jbpm_event e, jbpm_action a, jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and a.event_ = e.id_
			and e.eventtype_ = event_type;
		Exception
		  When No_Data_Found THEN
			select hibernate_sequence.nextval-1 into v_delegation_id from dual;
			
			insert into jbpm_delegation(ID_, CLASSNAME_, PROCESSDEFINITION_)
			values(v_delegation_id, delegation_classname, def.id_);
			
			select e.id_ into v_event_id from jbpm_event e, jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and e.eventtype_ = event_type;
			
			insert into jbpm_action(ID_, CLASS, ISPROPAGATIONALLOWED_, ISASYNC_, ACTIONDELEGATION_, EVENT_, EVENTINDEX_)
			values(hibernate_sequence.nextval-1, class_, 1, 0, v_delegation_id, v_event_id , 0);
			
			commit;
		END;
	END LOOP;
END create_action_with_delegation;
/
create or replace procedure create_jbpm_action(
	process_name varchar2,
	task_name varchar2,
	event_type varchar2,
	class_ varchar2,
	timer_name varchar2
) AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition where name_ = process_name;
	v_action_id number;
	v_event_id number;
BEGIN
	FOR def in all_defs
	LOOP
		BEGIN
			select a.id_ into v_action_id
			from jbpm_event e, jbpm_action a, jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and a.event_ = e.id_
			and e.eventtype_ = event_type;
			
		Exception
		  When No_Data_Found THEN
			select e.id_ into v_event_id from jbpm_event e, jbpm_task t
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and e.eventtype_ = event_type;
			
			insert into jbpm_action(ID_, CLASS, ISPROPAGATIONALLOWED_, ISASYNC_, EVENT_, TIMERNAME_, EVENTINDEX_)
			values(hibernate_sequence.nextval-1, class_, 1, 0, v_event_id, timer_name, 0);
			
			commit;
		END;
	END LOOP;
END create_jbpm_action;
/
begin
  create_jbpm_node('SupplierRecovery','initialOrFinalResponsePeriod','D',0,null,null,null); 
end;
/
begin
  create_jbpm_transition('SupplierRecovery','toAutoDisputeFinal','initialOrFinalResponsePeriod','SuppplierRejectEndFork','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.AUTO_DISPUTED_FINAL_RESPONSE)');
end;
/
begin
  create_jbpm_transition('SupplierRecovery','toAutoDisputeInitial','initialOrFinalResponsePeriod','SuppplierRejectEndFork',' recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.AUTO_DISPUTED_INITIAL_RESPONSE)');
end;
/
begin
	create_jbpm_decision('SupplierRecovery', 'initialOrFinalResponsePeriod', 'toAutoDisputeFinal', '#{recoveryClaim.partShippedByDealer == true}', 0);
end;
/
begin
	create_jbpm_decision('SupplierRecovery', 'initialOrFinalResponsePeriod', 'toAutoDisputeInitial', '#{recoveryClaim.partShippedByDealer == false}', 1);
end;
/
begin
	update_jbpm_transition_to_node('SupplierRecovery', 'Supplier Contract', 'initialOrFinalResponsePeriod', 'toAutoDispute');
end;
/
begin
	update_jbpm_delegation('SupplierRecovery', 'On Hold For Part Return', 'timer-create', 'tavant.twms.jbpm.action.SupplierDisputeAction');
end;
/
begin
	update_jbpm_transition_to_node('SupplierRecovery', 'On Hold For Part Return', 'initialOrFinalResponsePeriod', 'toAutoDispute');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-create', 'A', 'Reopened Claims');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'timer-create', 'A', 'Reopened Claims');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-end', 'A', 'Reopened Claims');
end;
/
begin
	create_action_with_action('SupplierRecovery', 'Reopened Claims', 'task-create', 'C', 'Reopened Claims', '20 business hours', 'tavant.twms.jbpm.action.TaskInstanceEndAction', '
                <transition>toAutoDispute</transition>
            ');
end;
/
begin
	create_action_with_delegation('SupplierRecovery', 'Reopened Claims', 'timer-create', 'A', 'tavant.twms.jbpm.action.SupplierDisputeAction');
end;
/
begin
	create_jbpm_action('SupplierRecovery', 'Reopened Claims', 'task-end', 'I', 'Reopened Claims');
end;
/
begin
  create_jbpm_transition('SupplierRecovery','toAutoDispute','Reopened Claims','initialOrFinalResponsePeriod',null);
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-create', 'A', 'Disputed');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'timer-create', 'A', 'Disputed');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-end', 'A', 'Disputed');
end;
/
begin
	create_action_with_action('SupplierRecovery', 'Disputed', 'task-create', 'C', 'Disputed', '20 business hours', 'tavant.twms.jbpm.action.TaskInstanceEndAction', '
                <transition>toAutoDispute</transition>
            ');
end;
/
begin
	create_action_with_delegation('SupplierRecovery', 'Disputed', 'timer-create', 'A', 'tavant.twms.jbpm.action.SupplierDisputeAction');
end;
/
begin
	create_jbpm_action('SupplierRecovery', 'Disputed', 'task-end', 'I', 'Disputed');
end;
/
begin
  create_jbpm_transition('SupplierRecovery','toAutoDispute','Disputed Claims','initialOrFinalResponsePeriod',null);
end;
/
update jbpm_event set transition_=null where transition_ in (select id_ from jbpm_transition where name_='toAutoDispute'))
/
commit
/
