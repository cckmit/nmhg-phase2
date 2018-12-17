select null link,
label  label,total "{n:Amount;t:Line;y:ex}" 
from (
selecT 1, sum(p.total_amount_amt)as total,'0-5 Days' label from claim c,payment p 
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-5) and c.last_updateD_on_date < (sysdate-0)
and c.payment = p.id
group by 1, '0-5 Days'
union
selecT 2, sum(p.total_amount_amt)as total,'5-10 Days' label from claim c,payment p 
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-10) and c.last_updateD_on_date < (sysdate-5)
and c.payment = p.id
group by '5-10 Days'
union
selecT 3,sum(p.total_amount_amt)as total,'10-15 Days' label from claim c,payment p 
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-15) and c.last_updateD_on_date < (sysdate-10)
and c.payment = p.id
group by '10-15 Days'
union
selecT 4,sum(p.total_amount_amt)as total,'15-20 Days' label from claim c,payment p 
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-20) and c.last_updateD_on_date < (sysdate-15)
and c.payment = p.id
group by '15-20 Days'
union
selecT 5,sum(p.total_amount_amt)as total,'More than 20 Days' label from claim c,payment p 
where c.state = 'PROCESSOR_REVIEW' 
and  c.last_updateD_on_date < (sysdate-20)
and c.payment = p.id
group by 'More than 20 Days'
order by 1
);