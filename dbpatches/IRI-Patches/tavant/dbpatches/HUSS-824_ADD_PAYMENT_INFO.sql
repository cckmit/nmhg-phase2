--Purpose : HUSS-824 , Create Additional Pyament Info for CP on migrated Hussmann claims
--Author : raghuram.d
--Date : 09/Jul/2011

declare

  cursor lineitems is
  select lig.id,lig.accepted_curr
  from claim c,line_item_groups ligs,line_item_group lig
  where c.business_unit_info in ('Hussmann')
    and c.state not in ('DRAFT','DRAFT_DELETED','DELETED','DEACTIVATED')
    and c.payment=ligs.for_payment and ligs.line_item_groups=lig.id
    and lig.name='Claim Amount' and lig.accepted_curr is not null
    and (select count(*) from add_payment_info api,additional_payment_info pi 
      where api.line_item_group=lig.id and api.additional_payment_info=pi.id
      and pi.type='ACCEPTED_FOR_CP')=0;

  pi_id number;
  idx number;
  
begin

idx := 19;
for lineitem in lineitems loop
    if idx = 19 then
      select ADDITIONAL_PAYMENT_INFO_SEQ.nextval into pi_id from dual;
      idx := 0;
    end if;

    insert into additional_payment_info (id,type,additional_amt,additional_curr,percentage_acceptance)
    values(pi_id+idx,'ACCEPTED_FOR_CP',0,lineitem.accepted_curr,0);

    insert into add_payment_info (additional_payment_info,line_item_group)
    values(pi_id,lineitem.id);

    commit;
    
    idx := idx +1;
end loop;
end;
/
