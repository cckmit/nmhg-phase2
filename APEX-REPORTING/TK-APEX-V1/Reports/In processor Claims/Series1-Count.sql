select null link,
label  label,count value
from (
selecT 1, count(c.id) as count,'0-5 Days' label from claim c
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-5) and c.last_updateD_on_date < (sysdate-0)
group by 1, '0-5 Days'
union
selecT 2, count(c.id) as count,'5-10 Days' label from claim c
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-10) and c.last_updateD_on_date < (sysdate-5)
group by '5-10 Days'
union
selecT 3,count(c.id) as count,'10-15 Days' label from claim c
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-15) and c.last_updateD_on_date < (sysdate-10)
group by '10-15 Days'
union
selecT 4,count(c.id) as count,'15-20 Days' label from claim c
where c.state = 'PROCESSOR_REVIEW' 
and c.last_updateD_on_date  > (sysdate-20) and c.last_updateD_on_date < (sysdate-15)
group by '15-20 Days'
union
selecT 5, count(c.id) as count,'More than 20 Days' label from claim c
where c.state = 'PROCESSOR_REVIEW' 
and  c.last_updateD_on_date < (sysdate-20)
group by 'More than 20 Days'
order by 1
);
