-- Purpose : Patch to create jbpm workflow Supplier to NMHG
-- Created On : 12-June_2014
-- Created By : ParthaSarathy R

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
CREATE PROCEDURE create_jbpm_form_nodes(
	def_name varchar2,
	node_name varchar2,
	form_value varchar2,
	form_type varchar2
) AS
	CURSOR all_defs IS
	select id_ from jbpm_processdefinition where name_=def_name;
	
	v_nid number;
	v_node_id number;
BEGIN
	For def in all_defs
	LOOP
		BEGIN
			select form_nodes.FORM_TASK_NODE_FORM_ID into v_nid
			from jbpm_processdefinition defn, jbpm_node node, jbpm_form_nodes form_nodes
			where defn.id_ = def.id_
			and node.name_ = node_name and node.processdefinition_ = defn.id_
			and form_nodes.form_task_node_form_id = node.id_;
		
		Exception When No_Data_Found Then
			select n.id_ into v_node_id from jbpm_node n
			where n.processdefinition_ = def.id_ and n.name_ = node_name;
			
			insert into JBPM_FORM_NODES(FORM_TASK_NODE_FORM_ID, FORM_VALUE, FORM_TYPE)
			values (v_node_id, form_value, form_type);
			commit;
		
		END;
	END LOOP;
END create_jbpm_form_nodes;
/
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
begin
  create_jbpm_node('SupplierPartReturn','Parts for Return To NMHG','A',0,4,1,0); 
end;
/
begin
	create_jbpm_form_nodes('SupplierPartReturn', 'Parts for Return To NMHG', 'duePartsForSupplier', 'actionUrl');
end;
/
begin
	create_jbpm_task('Parts for Return To NMHG', 'SupplierPartReturn', 'supplier', 'Parts for Return To NMHG');
end;
/
begin
  create_jbpm_node('SupplierPartReturn','Shipment Generated To NMHG','A',0,4,1,0); 
end;
/
begin
	create_jbpm_form_nodes('SupplierPartReturn', 'Shipment Generated To NMHG', 'shipmentGeneratedToNMHG', 'actionUrl');
end;
/
begin
	create_jbpm_task('Shipment Generated To NMHG', 'SupplierPartReturn', 'supplier', 'Shipment Generated To NMHG');
end;
/
begin
  create_jbpm_node('SupplierPartReturn','PartsShippedBySupplier','V',0,null,null,null); 
end;
/
begin
  create_jbpm_node('SupplierPartReturn','Parts Shipped to NMHG','A',0,4,1,0); 
end;
/
begin
	create_jbpm_form_nodes('SupplierPartReturn', 'Parts Shipped to NMHG', 'partShippedToNMHG', 'actionUrl');
end;
/
begin
	create_jbpm_task('Parts Shipped to NMHG', 'SupplierPartReturn', 'supplier', 'Parts Shipped to NMHG');
end;
/
begin
  create_jbpm_node('SupplierPartReturn','Supplier Parts Receipt','A',0,4,1,0); 
end;
/
begin
	create_jbpm_form_nodes('SupplierPartReturn', 'Supplier Parts Receipt', 'supplierPartsReceipt', 'actionUrl');
end;
/
begin
	create_jbpm_task('Supplier Parts Receipt', 'SupplierPartReturn', 'receiver', 'Supplier Parts Receipt');
end;
/
begin
  create_jbpm_node('SupplierPartReturn','Supplier Parts Inspection','A',0,4,1,0); 
end;
/
begin
  create_jbpm_node('SupplierPartReturn','JoinAfterPartReceiverResponse','G',0,null,null,null); 
end;
/
begin
  create_jbpm_node('SupplierPartReturn','Check Receiver Action','D',0,null,null,null); 
end;
/
begin
	create_jbpm_form_nodes('SupplierPartReturn', 'Supplier Parts Inspection', 'supplierPartsInspection', 'actionUrl');
end;
/
begin
	create_jbpm_task('Supplier Parts Inspection', 'SupplierPartReturn', 'inspector', 'Supplier Parts Inspection');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','NMHG Request Parts Back From Supplier','Start','Parts for Return To NMHG',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Generate Shipment','Parts for Return To NMHG','Shipment Generated To NMHG','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.SHIPMENT_GENERATED_BY_SUPPLIER)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Can not ship','Parts for Return To NMHG','End','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PARTS_MARKED_AS_CAN_NOT_SHIPPED_BY_SUPPLIER)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Parts for Return To NMHG','End',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Parts Shipped fork','Shipment Generated To NMHG','PartsShippedBySupplier','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PARTS_SHIPPED_BY_SUPPLIER_TO_NMHG)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Shipment Generated To NMHG','End',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Parts Shipped','PartsShippedBySupplier','Parts Shipped to NMHG',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Supplier Parts receipt','PartsShippedBySupplier','Supplier Parts Receipt',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','To SingleTokenJoin','Parts Shipped to NMHG','JoinAfterPartReceiverResponse',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Parts Shipped to NMHG','End',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Join After Receive','Supplier Parts Receipt','JoinAfterPartReceiverResponse',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Supplier Parts Receipt','End',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','checkReceiverAction','JoinAfterPartReceiverResponse','Check Receiver Action',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Mark for Scrap','Supplier Parts Inspection','End','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Send To Supplier','Supplier Parts Inspection','PartShipper Generate Shipment','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Supplier Parts Inspection','End',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Send for Inspection','Check Receiver Action','Supplier Parts Inspection','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PARTS_RECEIVED_AND_MARKED_FOR_INSPECTION)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Part Not Received','Check Receiver Action','Parts for Return To NMHG','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Inspected','Check Receiver Action','End',null);
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Scrapped','Check Receiver Action','End','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PARTS_FROM_SUPPLIER_MARKED_AS_SCRAPPED)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','Send To Supplier','Check Receiver Action','PartShipper Generate Shipment','supplierPartReturn.setBasePartReturnStatus(tavant.twms.domain.partreturn.PartReturnStatus.PART_TO_BE_SHIPPED)');
end;
/
begin
  create_jbpm_transition('SupplierPartReturn','toEnd','Check Receiver Action','End',null);
end;
/
begin
	create_jbpm_decision('SupplierPartReturn', 'Check Receiver Action', 'Send for Inspection', '#{transition=="Send for Inspection"}', 0);
end;
/
begin
	create_jbpm_decision('SupplierPartReturn', 'Check Receiver Action', 'Part Not Received', '#{transition=="Part Not Received"}', 1);
end;
/
begin
	create_jbpm_decision('SupplierPartReturn', 'Check Receiver Action', 'Inspected', '#{transition=="Inspected"}', 2);
end;
/
begin
	create_jbpm_decision('SupplierPartReturn', 'Check Receiver Action', 'Scrapped', '#{transition=="Scrapped"}', 3);
end;
/
begin
	create_jbpm_decision('SupplierPartReturn', 'Check Receiver Action', 'Send To Supplier', '#{transition=="Send To Supplier"}', 4);
end;
/
commit
/
drop procedure create_jbpm_node
/
drop procedure create_jbpm_form_nodes
/
drop procedure create_jbpm_task
/
drop procedure create_jbpm_transition
/
drop procedure create_jbpm_decision
/