-- Patch for update_jbpm_transition_to_node
-- Created By : ravikumar.Y
-- Created On : 26-08-2014


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
begin
	update_jbpm_transition_to_node('ClaimSubmission', 'IsProcessorReviewNeeded', 'Forwarded', 'ForwardDealer');
end;
/
commit
/