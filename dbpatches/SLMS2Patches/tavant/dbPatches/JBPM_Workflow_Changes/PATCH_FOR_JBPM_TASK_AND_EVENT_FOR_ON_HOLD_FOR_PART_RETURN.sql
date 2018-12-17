-- Purpose : Patch to create jbpm task for On Hold For Part Return
-- Created On : 09-June_2014
-- Created By : ParthaSarathy R

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
	create_jbpm_task('On Hold For Part Return', 'SupplierRecovery', 'supplier', 'On Hold For Part Return');
end;
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
begin
	create_jbpm_event('SupplierRecovery', 'task-create', 'A', 'On Hold For Part Return');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'timer-create', 'A', 'On Hold For Part Return');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-end', 'A', 'On Hold For Part Return');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-create', 'A', 'New');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'timer-create', 'A', 'New');
end;
/
begin
	create_jbpm_event('SupplierRecovery', 'task-end', 'A', 'New');
end;
/
commit
/
drop procedure create_jbpm_task
/
drop procedure create_jbpm_event
/