DECLARE
  P_OUT_ERROR_CODE NUMBER;
  P_OUT_ERROR_MESSAGE VARCHAR2(200);
  V_TAB_CNT NUMBER :=2;
  v_complete NUMBER :=2;
  v_infinite_loop number := 1000000000-5;
  v_cyc_start number :=0;
BEGIN
  

while v_infinite_loop > 0
LOOP

--Wait time 70 seconds
WHILE V_INFINITE_LOOP < 1000000000
LOOP
V_INFINITE_LOOP :=V_INFINITE_LOOP+1;
end loop;

V_INFINITE_LOOP := 1;

---checking for tables that are not processed and not loaded into the target
SELECT count(table_name)
     INTO v_tab_cnt
  FROM TAV_GIM_VALID_TABLES
  where EXEC_ORDER > 0
--  AND exec_order > 0
  AND LOAD_STATUS NOT LIKE 'NOT%PROCESSED'
  and load_status not like '%-UPLOAD'
--  and table_name = 'CLAIM_AUDIT'
  ORDER BY exec_order;

--if more than 1 table found starting loading the destination
while v_tab_cnt > 0
LOOP

  TAV_GIM_PROCESS_MIGRATION.TAV_GIM_POPULATE_MASTER(
    P_OUT_ERROR_CODE => P_OUT_ERROR_CODE,
    P_OUT_ERROR_MESSAGE => P_OUT_ERROR_MESSAGE
  );
  DBMS_OUTPUT.PUT_LINE('P_OUT_ERROR_CODE = ' || P_OUT_ERROR_CODE);
  DBMS_OUTPUT.PUT_LINE('P_OUT_ERROR_MESSAGE = ' || P_OUT_ERROR_MESSAGE);
  
  if P_OUT_ERROR_CODE like '%ORA-%' or P_OUT_ERROR_CODE like '%ORA-%' then
  goto v_end;
  end if;
  

v_tab_cnt := 0;

end loop;

  select count(1) into v_complete from TAV_GIM_VALID_TABLES where load_status not like '%UPLOAD' and load_status is not null;
  
  if v_complete = 0 THEN
  goto v_end_1;
  end if;

end loop;
<<v_end_1>>
BEGIN
 
  WHILE V_CYC_START < 16
  LOOP
  select COUNT(1) into V_CYC_START from TAV_GIM_MASTER_LOG where JOB_NAME like 'UPDATE TG_%' and CURRENT_STATUS = 'COMPLETE-OK';         
  end LOOP ;
  
   TAV_GIM_PROCESS_MIGRATION.TAV_GIM_UPDATE_CYCLIC_REF_60();
   DBMS_OUTPUT.PUT_LINE('Updation of the cyclic refereneces in the target complete!');    
  
END;
<<v_end>>
begin
null;
end;
END;
/