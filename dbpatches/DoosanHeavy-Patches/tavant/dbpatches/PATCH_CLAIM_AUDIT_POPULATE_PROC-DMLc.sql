-- Script for populating the claim audit columns from claim table
-- Author Tanveer Chowdary
-- Date August 13, 2012

declare 
cursor c1 is select * from claim where active_claim_audit is null;
v_claim_audit_id   NUMBER := 0;
begin
for each_rec in c1 loop
  begin
v_claim_audit_id := CLAIM_AUDIT_SEQ.nextval;
  insert into claim_audit values(v_claim_audit_id,null,0,null,
  null,0,0,null, null,null,sysdate, 'Added by Procedure',
  sysdate,null, sysdate, each_rec.decision, 0,
  each_rec.payment,null,each_rec.work_order_number,each_rec.failure_date,each_rec.repair_date,each_rec.installation_date, each_rec.purchase_date,
  each_rec.state,each_rec.service_information,each_rec.probable_cause,each_rec.work_performed,
  each_rec.other_comments,each_rec.condition_found,each_rec.internal_comment,each_rec.external_comment,each_rec.service_manager_accepted,
  each_rec.acceptance_reason,each_rec.acceptance_reason_for_cp,each_rec.rejection_reason,each_rec.accountability_code,each_rec.travel_hrs_config,
  each_rec.travel_trip_config,each_rec.travel_dis_config,each_rec.oem_config,each_rec.non_oem_config,each_rec.misc_parts_config,each_rec.meals_config,
  each_rec.parking_config,each_rec.item_duty_config,each_rec.labor_config,each_rec.cp_reviewed,each_rec.per_DIEM_config,each_rec.rental_charges_config,
  each_rec.additional_travel_hours_config,each_rec.local_purchase_config,each_rec.tolls_config,each_rec.other_freight_duty_config,each_rec.others_config,
  each_rec.invoice_number,each_rec.selling_entity,each_rec.owner_information,each_rec.claim_processed_as,each_rec.assign_to_user);
  
update claim set active_claim_audit=v_claim_audit_id where id=each_rec.id;
exception when others then
  dbms_output.put_line(each_rec.claim_number);
end;
end loop;
commit;
end;
/
UPDATE CLAIM_AUDIT SET SERVICE_MANAGER_ACCEPTED = 0 where SERVICE_MANAGER_ACCEPTED is null
/
alter table claim_AUDIT modify(SERVICE_MANAGER_ACCEPTED not null)
/