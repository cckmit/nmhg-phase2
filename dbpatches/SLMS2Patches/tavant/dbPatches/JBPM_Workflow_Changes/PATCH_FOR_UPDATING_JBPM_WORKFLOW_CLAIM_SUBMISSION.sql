create or replace 
procedure create_jbpm_transition_con (
  p_process varchar2,
	p_tname varchar2,
	p_fromnode varchar2,
	p_tonode varchar2,
	p_script VARCHAR2,
	p_condition VARCHAR2
) AS
  cursor all_defs is
	select id_ from jbpm_processdefinition where name_=p_process;
	v_id NUMBER;
	v_event NUMBER;
	v_index NUMBER;
	v_node_index NUMBER;
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
	--Code to insert condition
	if p_condition is not null then
		select MAX(dc.index_)+1 into v_node_index
		from JBPM_NODE n, JBPM_DECISIONCONDITIONS dc 
		where dc.decision_ = n.id_ and n.processdefinition_ = def.id_ and n.name_=p_fromnode;
		
		insert into JBPM_DECISIONCONDITIONS (DECISION_, TRANSITIONNAME_, EXPRESSION_, INDEX_)
		select id_ , p_tname, p_condition, v_node_index from JBPM_NODE where PROCESSDEFINITION_=def.id_ and NAME_ = p_fromnode;
	end if;
	--Code to insert condition, ends.
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
END create_jbpm_transition_con;

begin
  create_jbpm_transition_con('ClaimSubmission','goToClaimRules','isFailureReportPending','ClaimAutoAdjudication',null,'#{!claim.failureReportPending and claim.state.state=="Forwarded" and claim.latestAudit.updatedBy.name=="system"}');   
end;
begin
  create_jbpm_transition_con('ClaimSubmission','ForwardDealer','IsProcessorReviewNeeded','ForkForForwarded','claim.setInternalComment("Processing Engine"); claim.setState(tavant.twms.domain.claim.ClaimState.FORWARDED);','#{claimState=="Forwarded"}');
end;