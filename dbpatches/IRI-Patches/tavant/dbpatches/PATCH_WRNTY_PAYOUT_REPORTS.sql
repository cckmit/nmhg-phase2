create or replace view warranty_payout_view as
select c.id, c.filed_on_date,liga.accepted_amt,lig.name,liga.accepted_curr, c.repair_date,
NVL((select factor from currency_conversion_factor where parent in 
(select id from currency_exchange_rate where from_currency = (select preferred_currency from dealership where id = c.for_dealer) 
and to_currency = 'USD')
and c.repair_date between from_date and till_date and d_active = 1 and rownum = 1),1) exchange_rate
from claim c, line_item_groups ligs,line_item_group lig, line_item_group_audit liga
where c.state = 'ACCEPTED_AND_CLOSED'
  and c.payment = ligs.for_payment
  and ligs.line_item_groups = lig.id
  and lig.id = liga.for_line_item_grp 
  and liga.list_index = (select max(list_index) from line_item_group_audit where for_line_item_grp = lig.id)
/