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
	create_jbpm_swimlane('SupplierPartReturn', 'receiver', 'tavant.twms.jbpm.assignment.ScriptAssignmentHandler', '<beanName>warehouseService</beanName>
		<methodName>getReceiverAtLocation</methodName>
		<parameters><parameter>partReturn.returnLocation</parameter></parameters>');
end;
/
begin
	create_jbpm_swimlane('SupplierPartReturn', 'inspector', 'tavant.twms.jbpm.assignment.ScriptAssignmentHandler', '<beanName>warehouseService</beanName>
		<methodName>getInspectorAtLocation</methodName>
		<parameters><parameter>partReturn.returnLocation</parameter></parameters>');
end;
/
commit
/
drop procedure create_jbpm_swimlane
/