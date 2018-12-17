-- Patch for Updating Delegation for GenerateEmailNotifications
-- Created On : 20th June, 2014
-- Created By : ParthaSarathy R

create or replace procedure update_jbpm_delegation(
	p_process_name varchar2,
	p_node_name varchar2,
	p_configuration varchar2
) AS
	cursor all_defs IS
	select id_ from jbpm_processdefinition where name_ = p_process_name;
	v_node_id number;
	v_delegation_id number;
begin
	for def in all_defs
	loop
		begin
			select n.decisiondelegation into v_delegation_id from jbpm_node n
			where n.processdefinition_ = def.id_ and name_ = p_node_name;
			
			if v_delegation_id is not null then
				update jbpm_delegation set configuration_ = p_configuration where id_ = v_delegation_id;
			end if;
			
			commit;
		end;
	end loop;
end update_jbpm_delegation;
/
begin
	update_jbpm_delegation('PartsReturn', 'GenerateEmailNotifications', '
    <transition name="goToOverdueParts" to="Overdue Parts for Shipment"/>
    <beanName>sendEmailForOverdue</beanName>
    <methodName>createEmailEventForOverdue</methodName>
    <parameters><variable>claim</variable><variable>partReturn</variable></parameters>
  ');
end;
/
commit
/
drop procedure update_jbpm_delegation
/