-- Purpose : Patch to create jbpm actions for On Hold For Part Return
-- Created On : 11-June_2014
-- Created By : ParthaSarathy R

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
begin
	create_action_with_action('SupplierRecovery', 'On Hold For Part Return', 'task-create', 'C', 'On Hold For Part Return', '20 business hours', 'tavant.twms.jbpm.action.TaskInstanceEndAction', '
                <transition>toAutoDispute</transition>
            ');
end;
/
begin
	create_action_with_action('SupplierRecovery', 'New', 'task-create', 'C', 'New', '20 business hours', 'tavant.twms.jbpm.action.TaskInstanceEndAction', '
                <transition>toAutoDispute</transition>
            ');
end;
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
begin
	create_action_with_delegation('SupplierRecovery', 'On Hold For Part Return', 'timer-create', 'A', 'tavant.twms.jbpm.action.SupplierResponseAction');
end;
/
begin
	create_action_with_delegation('SupplierRecovery', 'New', 'timer-create', 'A', 'tavant.twms.jbpm.action.SupplierDisputeAction');
end;
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
	create_jbpm_action('SupplierRecovery', 'On Hold For Part Return', 'task-end', 'I', 'On Hold For Part Return');
end;
/
begin
	create_jbpm_action('SupplierRecovery', 'New', 'task-end', 'I', 'New');
end;
/
drop procedure create_action_with_delegation
/
drop procedure create_jbpm_action
/
drop procedure create_action_with_action
/