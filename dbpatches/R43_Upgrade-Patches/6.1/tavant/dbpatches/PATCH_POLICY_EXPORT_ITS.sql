--PURPOSE    : PATCH FOR creating trigger that will generate the XML and insert into sync tracker.
--AUTHOR     : Surendra
--CREATED ON : 23-NOV-10

Insert into sync_type (TYPE,D_CREATED_ON,D_INTERNAL_COMMENTS,D_UPDATED_ON,D_LAST_UPDATED_BY,D_CREATED_TIME,D_UPDATED_TIME,D_ACTIVE) values ('WarrantyPolicyExport',sysdate,null,sysdate,null,sysdate,sysdate,1)
/
create or replace
TRIGGER WARRANTY_POLICY_TRIGGER AFTER
  INSERT OR
  UPDATE OF COMMENTS,
    DESCRIPTION ON POLICY_DEFINITION FOR EACH ROW 
DECLARE 
  v_change_type varchar2(10);
  v_xmldata xmltype;
  v_sync_tracker_id number :=0 ;
  v_update_sync_tracker number :=0;
  BEGIN
    IF :NEW.business_unit_info = 'ITS' THEN
      IF INSERTING THEN
        v_change_type:='INSERT';
      ELSIF UPDATING THEN
        v_change_type:='UPDATE';
      END IF;
      begin
        select id into v_sync_tracker_id from sync_tracker 
        where unique_id_value = :new.code
        and status = 'To be Processed';
        v_update_sync_tracker := 1;
       Exception when NO_DATA_FOUND THEN
        /* Application uses HIBERNATE_SEQUENCE tio generate ids for sync tracker. 
           This was done assuming an increment of 20 is set on HIBERNATE_SEQUENCE
           If the increment is changed to 1 this will fail */
        select (max(id) + 1) into v_sync_tracker_id from sync_tracker;
        v_update_sync_tracker := 0;
      END;
      SELECT XMLElement("SyncWarrantyPlan", 
          XMLElement("ApplicationArea", 
          XMLElement("Sender", 
          XMLElement("Task",'ISSiebel'), 
          XMLElement("LogicalId",'TavantWMS'), 
          XMLElement("ReferenceId",'USINT0268')), 
          XMLElement("BODId",'INT13'), 
          XMLElement("InterfaceNumber"), 
          XMLElement("CreationDateTime",:new.d_created_time)), 
          XMLElement("DataArea", 
          XMLElement("WarrantyPlanHeader", 
          XMLElement("DocumentReference", 
          XMLElement("Description",'Warranty Policy Export From Tavant to Siebel')), 
          XMLElement("WarrantyType",:new.warranty_type), 
          XMLElement("Change",v_change_type), 
          XMLElement("Plan", 
          XMLElement("Code",:new.code), 
          XMLElement("Name",:new.description), 
          XMLElement("Comments",:new.comments), 
          XMLElement("GoodWill", DECODE(:new.warranty_type, 'GOODWILL', 'Y', 'N')), 
          XMLElement("WarrantyCategory"), 
          XMLElement("StartDateTime",:new.active_from)), 
          XMLElement("Service", 
          XMLElement("EffectiveTimePeriod", 
          XMLElement("Duration",:new.months_frm_delivery), 
          XMLElement("HoursCovered",:new.service_hrs_covered)), 
          XMLElement("AccountingCategories", 
          XMLElement("Part"), 
          XMLElement("Labor"), 
          XMLElement("Travel"), 
          XMLElement("Miscellaneous"))))))
          INTO v_xmldata
      FROM dual;
       IF v_update_sync_tracker = 0 THEN
         INSERT INTO SYNC_TRACKER 
         (ID,
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
         v_xmldata.getClobVal(),
         'TavantWMS',
         SYSDATE, 
         null,
         null,
         0,
         v_xmldata.getClobVal(),
         null,
         'WarrantyPolicyExport',
         'Policy Code',
         :new.code,
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
          SET BODXML = v_xmldata.getCLOBVal(), 
          record = v_xmldata.getCLOBVal(), 
          version = version + 1, 
          update_date = sysdate,
          d_updated_on = sysdate,
          d_internal_comments = d_internal_comments || '|Updated by Trigger'
         WHERE id = v_sync_tracker_id;
      END IF;
    END IF;
  END;
/
commit
/