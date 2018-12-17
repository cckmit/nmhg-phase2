--Purpose : Get job code descriptions for vendor recovery extract
--Author  : raghuram.d
--Date    : 11/Jan/2010

create or replace FUNCTION GET_JOB_CODE_DESC (p_claimId NUMBER) RETURN VARCHAR2 IS
 CURSOR ALL_JOB_CODES IS
  select id,code,action_definition from service_procedure_definition 
    where id in (
	select definition from service_procedure where id in ( 
	select service_procedure from labor_detail where id in(
	select labor_performed from service_labor_performed where service 
	in (select service_detail from service_information where id in 
	(select service_information from claim where id = p_claimId)))));

CURSOR JC_COMPS(sp_id NUMBER) IS
    SELECT a.name
    FROM service_proc_def_comps c,assembly_definition a
    WHERE c.service_procedure_definition=sp_id
        AND c.components = a.id
    ORDER BY c.list_index;

v_return_str	VARCHAR2(4000) := null;
v_job_code VARCHAR2(255);
v_action VARCHAR(255);

BEGIN

for rec in ALL_JOB_CODES loop

    v_job_code := NULL;
    FOR comp IN JC_COMPS(rec.id) LOOP
        IF v_job_code IS NULL THEN
            v_job_code := comp.name;
        ELSE
            v_job_code := v_job_code || '-' || comp.name;
        END IF;
        SELECT name INTO v_action FROM action_definition WHERE id=rec.action_definition;
        v_job_code := v_job_code || '-' || v_action;
    END LOOP;

    IF v_return_str IS NULL THEN
        v_return_str := v_job_code; 
    ELSE 
        v_return_str := v_return_str || ', ' || v_job_code;
    END IF;

END LOOP; 

RETURN v_return_str;

END;
/