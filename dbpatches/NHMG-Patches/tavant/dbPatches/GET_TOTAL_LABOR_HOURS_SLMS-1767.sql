--Purpose : vendor recovery extract modified for SLMS-1767 fixed
--Author  : ajitkumar.singh
--Date    : 25/June/13
create or replace
FUNCTION                "GET_TOTAL_LABOR_HOURS" (p_claimId NUMBER) RETURN NUMBER IS
 CURSOR ALL_STD_HOURS IS
  	select SUM(SUGGESTED_LABOUR_HOURS) from service_procedure where id in ( 
	select service_procedure from labor_detail where id in(
	select labor_performed from service_labor_performed where service 
	in (select service_detail from service_information where id in 
	(select service_information from claim_audit claimAudit,claim c where c.active_claim_audit=claimAudit.id and c.id = p_claimId))));

 CURSOR ALL_ADD_HOURS IS
	select SUM(ADDITIONAL_LABOR_HOURS) from labor_detail where id in(
	select labor_performed from service_labor_performed where service 
	in (select service_detail from service_information where id in 
	(select service_information from claim_audit claimAudit,claim c where c.active_claim_audit=claimAudit.id and c.id = p_claimId)));

v_total_labor_hrs	NUMBER(19,2);
v_std_hours NUMBER(19,2);
v_add_hours NUMBER(19,2);

BEGIN

OPEN ALL_STD_HOURS;
OPEN ALL_ADD_HOURS;

fetch ALL_STD_HOURS into v_std_hours;
fetch ALL_ADD_HOURS into v_add_hours;

v_total_labor_hrs := NVL(v_std_hours,0) + NVL(v_add_hours,0);

CLOSE ALL_STD_HOURS;
CLOSE ALL_ADD_HOURS;

RETURN v_total_labor_hrs;

END;
/