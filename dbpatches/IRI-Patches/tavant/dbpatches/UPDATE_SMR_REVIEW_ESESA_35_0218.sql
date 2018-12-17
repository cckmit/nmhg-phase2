ALTER TABLE CLAIM_AUDIT MODIFY D_INTERNAL_COMMENTS VARCHAR2(4000)
/
declare
cursor all_rec is
select distinct for_claim 
from claim_audit a,
claim b 
where a.decision is null
and a.previous_state = 'SERVICE_MANAGER_RESPONSE'
and a.for_claim = b.id
and b.state not in ('SERVICE_MANAGER_RESPONSE','DELETED')
group by for_claim, previous_state
having count(*) <= 1;

v_claim_audit_id  number;
v_list_index number;
v_next_clm_state varchar2(255);
begin
 for each_rec in all_rec loop
 
 	 select id,list_index
	 into v_claim_audit_id,v_list_index
	 from claim_audit
	 where for_claim = each_rec.for_claim
	 and decision is null
	 and previous_state = 'SERVICE_MANAGER_RESPONSE';
	 
	 begin
	 select previous_state
	 into v_next_clm_state
	 from claim_audit
	 where for_claim = each_rec.for_claim
	 and list_index = v_list_index+1;
	 exception when others then
	 	dbms_output.put_line('exception while getting next state for claim'||each_rec.for_claim||substr(sqlerrm,0,3500));
	 end;

	 if v_next_clm_state = 'SUBMITTED'  then
	   update claim_audit
	   set decision = 'Recommended to Approve,',
	   d_updated_on = sysdate,
	   UPDATED_TIME = sysdate,
	   d_internal_comments = d_internal_comments||'- Updated For Issue ESESA-899 And ESESA-35'
	   where id = v_claim_audit_id;
	   
	 else
	 
	   update claim_audit
	   set decision = 'Recommended to Reject,',
	   d_updated_on = sysdate,
	   UPDATED_TIME = sysdate,
	   d_internal_comments = d_internal_comments||'- Updated For Issue ESESA-899 And ESESA-35'
	   where id = v_claim_audit_id;
	 end if;
 end loop;	 
end;  
/
commit
/
DECLARE
CURSOR all_rec IS
SELECT distinct for_claim
FROM claim_audit a,
  claim b
WHERE a.decision IS NULL
 AND a.previous_state = 'SERVICE_MANAGER_RESPONSE'
 AND a.for_claim = b.id
 AND b.state NOT IN('SERVICE_MANAGER_RESPONSE',   'DELETED')
 --AND a.for_claim = 1119895419320
GROUP BY for_claim,
  previous_state HAVING COUNT(*) > 1;

CURSOR all_excluded_audits(p_claim_audit_id number, p_claim_id NUMBER) IS
SELECT id
FROM claim_audit
WHERE id <> p_claim_audit_id
AND for_claim = p_claim_id
 AND previous_state = 'SERVICE_MANAGER_RESPONSE';

v_claim_audit_id NUMBER;
v_list_index NUMBER;
v_next_clm_state VARCHAR2(255);
BEGIN
  FOR each_rec IN all_rec
  LOOP

    SELECT id,
      list_index
    INTO v_claim_audit_id,
      v_list_index
    FROM claim_audit a
    WHERE a.for_claim = each_rec.for_claim
     AND a.decision IS NULL
     AND a.previous_state = 'SERVICE_MANAGER_RESPONSE'
     AND a.list_index =
      (SELECT MAX(b.list_index)
       FROM claim_audit b
       WHERE b.for_claim = a.for_claim
       AND b.previous_state = 'SERVICE_MANAGER_RESPONSE')
    ;

    BEGIN
      SELECT previous_state
      INTO v_next_clm_state
      FROM claim_audit
      WHERE for_claim = each_rec.for_claim
       AND list_index = v_list_index + 1;

    EXCEPTION
    WHEN others THEN
      DBMS_OUTPUT.PUT_LINE('exception while getting next state for 

claim' || each_rec.for_claim || SUBSTR(sqlerrm,   0,   3500));
    END;

    IF v_next_clm_state = 'SUBMITTED' THEN

      UPDATE claim_audit
      SET decision = 'Recommended to Approve,',
        d_updated_on = sysdate,
        updated_time = sysdate,
        d_internal_comments = d_internal_comments || '- Updated For Issue ESESA-899 And ESESA-35'
      WHERE id = v_claim_audit_id;

    ELSE

      UPDATE claim_audit
      SET decision = 'Recommended to Reject,',
        d_updated_on = sysdate,
        updated_time = sysdate,
        d_internal_comments = d_internal_comments || '- Updated For Issue ESESA-899 And ESESA-35'
      WHERE id = v_claim_audit_id;
    END IF;

    FOR each_excluded_audit IN all_excluded_audits(v_claim_audit_id,each_rec.for_claim)
    LOOP

      UPDATE claim_audit
      SET decision = 'Recommended to Reject,',
        d_updated_on = sysdate,
        updated_time = sysdate,
        d_internal_comments = SUBSTR(d_internal_comments,   0,   3500) || '- Updated For Issue ESESA-899 And ESESA-35'
      WHERE id = each_excluded_audit.id;
    END LOOP;
  END LOOP;

  COMMIT;
END;
/
declare
CURSOR all_rec IS
SELECT distinct for_claim
FROM claim_audit a,
  claim b
WHERE a.decision IS NULL
 AND a.previous_state = 'SERVICE_MANAGER_RESPONSE'
 AND a.for_claim = b.id
 AND b.state IN('SERVICE_MANAGER_RESPONSE');

CURSOR all_excluded_audits(p_claim_audit_id number, p_claim_id NUMBER) IS
SELECT id
FROM claim_audit
WHERE id <> p_claim_audit_id
AND for_claim = p_claim_id
 AND previous_state = 'SERVICE_MANAGER_RESPONSE';

v_claim_audit_id NUMBER;
v_list_index NUMBER;
v_next_clm_state VARCHAR2(255);
TWMS_EXCEPTION EXCEPTION;
BEGIN
  FOR each_rec IN all_rec
  LOOP
  BEGIN

    begin	
    SELECT id,
      list_index
    INTO v_claim_audit_id,
      v_list_index
    FROM claim_audit a
    WHERE a.for_claim = each_rec.for_claim
     AND a.decision IS NULL
     AND a.previous_state = 'SERVICE_MANAGER_RESPONSE'
     AND a.list_index =
      (SELECT MAX(b.list_index)
       FROM claim_audit b
       WHERE b.for_claim = a.for_claim
       AND b.previous_state = 'SERVICE_MANAGER_RESPONSE')
    ;
    exception when others then 
	dbms_output.put_line(substr(sqlerrm,0,3500)||'-'||each_rec.for_claim);
 	RAISE TWMS_EXCEPTION;
    end;

    FOR each_excluded_audit IN all_excluded_audits(v_claim_audit_id,each_rec.for_claim)
    LOOP

      UPDATE claim_audit
      SET decision = 'Recommended to Reject,',
        d_updated_on = sysdate,
        updated_time = sysdate,
        d_internal_comments = SUBSTR(d_internal_comments,   0,   3500) || '- Updated For Issue ESESA-899 And ESESA-35'
      WHERE id = each_excluded_audit.id;
    END LOOP;
  EXCEPTION WHEN TWMS_EXCEPTION THEN
   Null;
  END;
  END LOOP;

  COMMIT;
END;
/
