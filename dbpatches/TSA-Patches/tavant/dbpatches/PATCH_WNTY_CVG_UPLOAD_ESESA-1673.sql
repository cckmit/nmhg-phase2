--Purpose : ESESA-1673, Item number entered on the template can also be an alternate item number.
--Author : raghuram.d
--Date : 12/May/2011

create or replace PROCEDURE WNTY_CVG_UPLOAD
AS
  CURSOR ALL_REC
  IS
    SELECT *
    FROM STG_WARRANTY_COVERAGES
    WHERE NVL(ERROR_STATUS, 'N') = 'Y'
    AND ERROR_CODE              IS NULL
    AND NVL(UPLOAD_STATUS, 'N')  = 'N'
    ORDER BY ID ASC;
	
  --ALL GLOBAL VARIABLES DECLARED FOR A PROCEDURE
  v_upload_error          VARCHAR2(4000) := NULL;
  v_warranty_id           NUMBER := NULL;
  v_inventory_id          NUMBER;
  v_policy_id             NUMBER  := NULL;
  v_policy_audit_id       NUMBER  := NULL;
  v_policy_def_id         NUMBER  := NULL;
  v_policy_status         varchar2(100)  := NULL;
  v_policy_audit_comments VARCHAR2(4000) :=NULL;
  v_bu_name                   VARCHAR2(255);
   v_uploaded_by               NUMBER  := NULL;
  v_start_dt			DATE;
  v_end_dt			DATE;
BEGIN
  FOR EACH_REC IN ALL_REC
  LOOP
    BEGIN
      --RESET THE VALUE TO ZERO
      v_warranty_id     := 0;
      v_policy_id       := NULL;
      v_policy_audit_id := 0;
      v_policy_def_id   :=0;
      v_start_dt			:=NULL;
      v_end_dt			  :=NULL; 
      v_policy_status :='Active';
      
      
      BEGIN
          SELECT u.id, f.business_unit_info INTO v_uploaded_by, v_bu_name
          FROM org_user u,file_upload_mgt f
          WHERE u.id = f.uploaded_by  AND f.id in
              (SELECT file_upload_mgt_id FROM stg_warranty_coverages WHERE rownum = 1);
       END;
     
      --GET THE SEQUENCE FOR POLICY
     
      BEGIN
        SELECT A.ID,
          B.ID
        INTO v_warranty_id,
          v_inventory_id
        FROM WARRANTY A,
          INVENTORY_ITEM B,
          ITEM C
        WHERE A.FOR_ITEM           = B.ID
        AND upper(B.SERIAL_NUMBER) = upper(EACH_REC.SERIAL_NUMBER)
        AND (upper(C.item_number) = upper(EACH_REC.ITEM_NUMBER) or
			upper(C.alternate_item_number) = upper(EACH_REC.ITEM_NUMBER) )
        AND A.list_index           =
          (SELECT MAX(list_index) FROM warranty WHERE for_item=B.id AND d_active=1
          )
        AND b.of_type =C.ID
        AND B.d_active=1;
               
      END;
     
    --GET THE POLICY DEFINITION
      BEGIN
        SELECT ID
        INTO v_policy_def_id
        FROM POLICY_DEFINITION
        WHERE upper(CODE) = upper(EACH_REC.plan_code)
        and upper(business_unit_info)= upper(v_bu_name);
     
       END;
       
      BEGIN
        SELECT ID
        INTO v_policy_id
        FROM POLICY
        WHERE warranty = v_warranty_id and
        policy_definition=v_policy_def_id;
        EXCEPTION
            WHEN OTHERS THEN
                v_policy_id := null;
       END;
      
       --GET THE SEQUENCE FOR POLICY AUDIT
      SELECT policy_audit_seq.NEXTVAL
      INTO v_policy_audit_id
      FROM DUAL;
      
    IF TO_DATE(EACH_REC.WARRANTY_START_DATE,'YYYYMMDD') = TO_DATE(EACH_REC.WARRANTY_END_DATE,'YYYYMMDD') THEN
      
      v_policy_status :='InActive';
    
    END IF;
        
    IF v_policy_id IS NULL THEN
     
      SELECT policy_seq.NEXTVAL
      INTO v_policy_id
      FROM DUAL;
     
     --INSERT THE RECORD INTO WARRANTY POLICY

      INSERT INTO POLICY
        (
          ID ,
          AMOUNT ,
          CURRENCY ,
          POLICY_DEFINITION,
          WARRANTY,
          purchase_date,
          purchase_order_number,           
          d_created_time,
		      d_created_on,
          d_updated_time,
		      d_updated_on,
          d_internal_comments,
          d_active
        )
        VALUES
        (
          v_policy_id ,
         0,
         'USD',
          v_policy_def_id,
          v_warranty_id,
          TO_DATE(EACH_REC.order_date,'YYYYMMDD'), 
          EACH_REC.order_number,
          sysdate,
          sysdate,
          sysdate,
          sysdate,
          null,
          1
        );
			END IF;
      --INSERT THE RECORD INTO WARRANTY POLICY AUDIT
      INSERT
      INTO POLICY_AUDIT
        (
          ID ,
          COMMENTS ,
          CREATED_ON,
          STATUS ,
          FROM_DATE ,
          TILL_DATE ,
          CREATED_BY,
          FOR_POLICY,
          d_created_on,
          SERVICE_HOURS_COVERED,
          d_created_time,
          d_updated_time,
          d_internal_comments,
          d_active
        )
        VALUES
        (
          v_policy_audit_id ,
          EACH_REC.comments,
          to_number(sysdate - to_date('01-JAN-1970','DD-MON-YYYY')) * (24 * 60 * 60 * 1000)  ,
          v_policy_status  ,
          TO_DATE(EACH_REC.WARRANTY_START_DATE,'YYYYMMDD'),
          TO_DATE(EACH_REC.WARRANTY_END_DATE,'YYYYMMDD') ,
          v_uploaded_by ,
          v_policy_id ,
          sysdate,
          EACH_REC.HOURS_COVERED,
          sysdate,
          sysdate,
          null,
          1
        );
      
      COMMIT;
       BEGIN
               
        select max(pa.till_date) into v_end_dt
        from policy p, policy_audit pa
        where p.id=pa.for_policy
        and p.warranty=v_warranty_id
        and pa.status in ('Active','Terminated') 
        and pa.id in ( select max(id ) from (
        select for_policy,id from policy_audit where for_policy in (select id from policy where warranty = v_warranty_id))
        group by for_policy);
        
       IF v_end_dt is null then
         UPDATE inventory_item SET wnty_end_date =null
         WHERE ID = v_inventory_id;
        ELSE
        UPDATE inventory_item SET wnty_end_date =v_end_dt
         WHERE ID = v_inventory_id;
        END IF;
       
       END;
       
       BEGIN
        select min(pa.from_date)  into v_start_dt
        from policy p, policy_audit pa
        where p.id=pa.for_policy
        and p.warranty=v_warranty_id
        and pa.status in ('Active','Terminated')  
        and pa.id in ( select max(id ) from (
        select for_policy,id from policy_audit where for_policy in (select id from policy where warranty = v_warranty_id))
        group by for_policy);
        
        IF v_start_dt is null then
          UPDATE inventory_item SET wnty_start_date = null
         WHERE ID = v_inventory_id;
        ELSE
        UPDATE inventory_item SET wnty_start_date = v_start_dt
         WHERE ID = v_inventory_id;
        END IF;
              
           
        END;
      
      --UPDATE RECORD WITH ERROR MESSAGE
      UPDATE STG_WARRANTY_COVERAGES
      SET UPLOAD_STATUS = 'Y' ,
        UPLOAD_ERROR    = NULL
      WHERE ID      = EACH_REC.ID;
      COMMIT;
    EXCEPTION
    WHEN OTHERS THEN
      --FIRST ROLLBACK
      ROLLBACK;
      --GET THE ERROR MESSAGE
      v_upload_error := SUBSTR(SQLERRM,0,3500);
      dbms_output.put_line(SQLERRM);
      --UPDATE RECORD WITH ERROR MESSAGE
      UPDATE STG_WARRANTY_COVERAGES
      SET UPLOAD_STATUS = 'N' ,
        UPLOAD_ERROR    = v_upload_error
      WHERE ID      = EACH_REC.ID;
      --COMMIT UPDATE STATEMENT
      COMMIT;
    END;
  END LOOP;
END WNTY_CVG_UPLOAD;
/