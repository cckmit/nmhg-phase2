-- Purpose : Patch to create jbpm transitions for On Hold For Part Return
-- Created On : 09-June_2014
-- Created By : ParthaSarathy R

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
  create_jbpm_transition('SupplierRecovery','toAutoDispute','Supplier Contract','SuppplierRejectEndFork','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.AUTO_DISPUTED_INITIAL_RESPONSE)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','On Hold For Part Return','Supplier Contract','On Hold For Part Return','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.ON_HOLD_FOR_PART_RETURN)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','toEnd','On Hold For Part Return','UpdatePayment','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.NO_RESPONSE_AND_AUTO_DEBITTED)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','On Hold For Part Return','On Hold For Part Return','On Hold For Part Return','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.ON_HOLD_FOR_PART_RETURN)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','Accept','On Hold For Part Return','SuppplierAcceptEndFork','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.READY_FOR_DEBIT)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','Reject','On Hold For Part Return','SuppplierRejectEndFork','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.REJECTED)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','Wnty Claim Reopened','On Hold For Part Return','SupplierEndFork','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.WNTY_CLAIM_REOPENED)');   
end;
/
begin
  create_jbpm_transition('SupplierRecovery','toAutoDispute','On Hold For Part Return','SuppplierRejectEndFork','recoveryClaim.setRecoveryClaimState(tavant.twms.domain.claim.RecoveryClaimState.AUTO_DISPUTED_FINAL_RESPONSE)');   
end;
/
commit
/
drop procedure create_jbpm_transition
/