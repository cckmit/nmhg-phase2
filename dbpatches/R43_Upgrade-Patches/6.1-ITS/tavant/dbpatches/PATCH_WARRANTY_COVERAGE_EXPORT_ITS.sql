--PURPOSE    : PATCH FOR creating trigger that will generate the XML and insert into sync tracker.
--AUTHOR     : Surendra
--CREATED ON : 10-DEC-10

Insert into sync_type (TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) values ('WarrantyCoverageExport',sysdate,null,sysdate,null,sysdate,sysdate,1)
/
create or replace
TRIGGER WARRANTY_COVERAGE_TRIGGER 
before INSERT OR UPDATE  ON INVENTORY_ITEM 
FOR EACH ROW 
DECLARE 
--cursor c_reg_policies is
--    select type,business_unit_info from inventory_item where serial_number = :new.serial_number;
  
  V_Str1 Varchar2(4000);  
  v_change_type varchar2(10);
  v_xmldata xmltype;
  v_xmldata1 xmltype;
  v_xmldata2 clob;
  v_sync_tracker_id number :=0 ;
  v_update_sync_tracker number :=0;
  v_new_code varchar2(100);
  v_new_bu varchar2(100);
  v_of_type number;
  v_serial_number varchar2(100);
  v_employeeID varchar2(100)  ;
  v_FirstName   varchar2(100);
  v_LastName    varchar2(100);
   v_item_number   varchar2(100);
   v_item_number1 clob;
   v_item_number2 clob;
   
  cursor c_reg_policies(c_serial_number VARCHAR,c_inv_id NUMBER) is 
    select c_serial_number, pd.code code, pa.from_date a_from , pa.till_date a_till,pa.status status
    from
    warranty w,
    policy p,
    policy_audit pa,
    policy_definition pd
    where w.for_item= c_inv_id
    and p.warranty = w.id
    and pa.for_policy = p.id
    and pd.id = p.policy_definition;
 

BEGIN
 
  IF :NEW.business_unit_info = 'ITS' THEN
      IF INSERTING THEN
        v_change_type:='INSERT';
      ELSIF UPDATING THEN
        v_change_type:='UPDATE';
      END IF;
      BEGIN
        select id into v_sync_tracker_id 
        from sync_tracker 
        where unique_id_value =:new.serial_number
        and status = 'To be Processed';
        v_update_sync_tracker := 1;
       Exception when NO_DATA_FOUND THEN
        /* Application uses HIBERNATE_SEQUENCE tio generate ids for sync tracker. 
           This was done assuming an increment of 20 is set on HIBERNATE_SEQUENCE
           If the increment is changed to 1 this will fail */
        select HIBERNATE_SEQUENCE.NEXTVAL into v_sync_tracker_id from dual;
        v_update_sync_tracker := 0;
      END;
    
      begin
          select item_number into v_item_number 
          from item 
          where id = :new.of_type;       
        Exception 
          when NO_DATA_FOUND THEN
            null;
       end;
  

    BEGIN
      SELECT XMLElement("SyncWarrantyCoverage", 
          XMLElement("ApplicationArea", 
          XMLElement("Sender", 
          XMLElement("Task",'ISSiebel'), 
          XMLElement("LogicalId",'TavantWMS'), 
          XMLElement("ReferenceId",'004268884_HQ01')), 
          XMLElement("BODId",'INT243'), 
          XMLElement("InterfaceNumber",'USINT0268'), 
          XMLElement("CreationDateTime",:new.d_created_time)), 
          XMLElement("DataArea", 
          XMLElement("WarrantyCoverage", 
          XMLElement("SerialNumber",:new.serial_number),
           XMLElement("ItemId",v_item_number))))
          
              INTO v_xmldata
      FROM dual;
      V_Str1 := Substr(v_xmldata.getStringVal(),0, Instr(v_xmldata.getStringVal(),'</WarrantyCoverage>')-1);
      v_item_number1 :=V_Str1;
      v_item_number2 :='</WarrantyCoverage></DataArea></SyncWarrantyCoverage>';
      END;        

BEGIN
     
          for v_cur_record in c_reg_policies(:new.serial_number,:new.id) loop
          
      select XMLElement("Coverage", 
             XMLElement("PolicyCode",v_cur_record.code),
             XMLElement("Status",v_cur_record.status),
             XMLElement("TimePeriod", 
              XMLElement("StartDateTime",v_cur_record.a_from),
              XMLElement("EndDateTime",v_cur_record.a_till)))
                      
              INTO v_xmldata1
              FROM dual; 
              
              v_xmldata2 := v_xmldata2 || v_xmldata1.getClobVal();
       
    end loop;
   
      Exception 
          when NO_DATA_FOUND THEN
            null;
     END;
      
       IF v_update_sync_tracker = 0 THEN
       
         INSERT INTO SYNC_TRACKER( ID,
         BODXML,
         BUSINESS_ID,
         CREATE_DATE,
         ERROR_MESSAGE,
         ERROR_TYPE,
         NO_OF_ATTEMPTS,
         RECORD,
         START_TIME,
         SYNC_TYPE,
         UNIQUE_ID_NAME,
         UNIQUE_ID_VALUE,
         UPDATE_DATE,
         VERSION,
         STATUS,
         D_CREATED_ON,
         D_INTERNAL_COMMENTS,
         D_UPDATED_ON,
         D_LAST_UPDATED_BY,
         BUSINESS_UNIT_INFO,
         PROCESSING_STATUS,
         IS_DELETED,
         HIDDEN_BY,
         HIDDEN_ON)
         VALUES 
         (v_sync_tracker_id, 
         v_item_number1 || v_xmldata2 || v_item_number2 ,
         'ITS',
         SYSDATE, 
         null,
         null,
         0,
         v_item_number1 || v_xmldata2 || v_item_number2,
         null,
         'WarrantyCoverageExport',
         'Policy Code',
         :new.serial_number,
         SYSDATE,
         0,
         'To be Processed',
         sysdate,
         'Created by Trigger',
         sysdate,
         56,
         :NEW.business_unit_info,
         null,
         'N',
         null,
         null);
        ELSE
         UPDATE SYNC_TRACKER 
          SET BODXML = v_item_number1 || v_xmldata2 || v_item_number2, 
          record = v_item_number1 || v_xmldata2 || v_item_number2, 
          version = version + 1, 
          update_date = sysdate,
          d_updated_on = sysdate,
          d_internal_comments = d_internal_comments
           WHERE id = v_sync_tracker_id;
      END IF;
    END IF;
END;
/
commit
/