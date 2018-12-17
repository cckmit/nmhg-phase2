-- Purpose : Patch to create jbpm actions for Confirm Dealer Part Returns
-- Created On : 12-June_2014
-- Created By : ParthaSarathy R

create or replace procedure create_jbpm_event_and_action(
	process_name varchar2,
	task_name varchar2,
	event_type varchar2,
	type_ varchar2,
	delegation_classname varchar2,
	class_ varchar2
) AS
	Cursor all_defs IS
	select id_ from jbpm_processdefinition where name_ = process_name;
	v_eid number;
	v_event_id number;
	v_task_id number;
	v_delegation_id number;
BEGIN
	FOR def in all_defs
	Loop
		Begin
			select e.id_ into v_eid from jbpm_task t, jbpm_event e
			where t.name_ = task_name and t.processdefinition_ = def.id_ and e.task_ = t.id_ and e.eventtype_ = event_type;
		Exception
		  When No_Data_Found THEN
			select hibernate_sequence.nextval-1 into v_event_id from dual;
			
			select t.id_ into v_task_id from jbpm_task t where t.name_ = task_name and t.processdefinition_ = def.id_;
			
			insert into jbpm_event(ID_, EVENTTYPE_, TYPE_, GRAPHELEMENT_, TASK_)
			values(v_event_id, event_type, type_, v_task_id, v_task_id);
			
			select hibernate_sequence.nextval-1 into v_delegation_id from dual;
			insert into jbpm_delegation(ID_, CLASSNAME_, PROCESSDEFINITION_)
			values(v_delegation_id, delegation_classname, def.id_);
			
			insert into jbpm_action(ID_, CLASS, ISPROPAGATIONALLOWED_, ISASYNC_, ACTIONDELEGATION_, EVENT_, EVENTINDEX_)
			values(hibernate_sequence.nextval-1, class_, 1, 0, v_delegation_id, v_event_id, 0);
		End;
	End Loop;
END create_jbpm_event_and_action;
/
begin
	create_jbpm_event_and_action('SupplierPartReturn', 'Confirm Part Returns', 'task-create', 'A', 'tavant.twms.jbpm.action.SupplierPartReceivedSchedulerAction', 'A');
end;
/
drop procedure create_jbpm_event_and_action
/