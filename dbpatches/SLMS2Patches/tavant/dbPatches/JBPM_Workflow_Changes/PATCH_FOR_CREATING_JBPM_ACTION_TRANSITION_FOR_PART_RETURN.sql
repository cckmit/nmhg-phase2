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
	create_jbpm_event_and_action('PartsReturn', 'Confirm Dealer Part Returns', 'task-create', 'A', 'tavant.twms.jbpm.action.SupplierPartReceivedSchedulerAction', 'A');
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
  create_jbpm_transition('PartsReturn','Initiate Return To Dealer By Processor','Start','Dealer Requested Part','partReturn.setStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER)');   
end;
/