create table claim_audit_backup as
(select id, prev_claim_snapshot_string, previous_state, list_index, for_claim from claim_audit where 1 = 2)
/
alter table claim_audit_backup add (claim_number varchar(50), MODIFIED_XML clob, BUSINESS_UNIT_INFO varchar2(100), EXCEPTION_LOG varchar2(4000))
/
INSERT
INTO claim_audit_backup 
(
	select 	ca.id,
	ca.prev_claim_snapshot_string,
	ca.previous_state,
	ca.list_index,
	ca.for_claim,
	C.Claim_Number,
	Null,
	C.Business_Unit_Info,
	Null
	from claim_audit ca, claim c 
	where c.id = ca.for_claim
)
/
create table update_claim_audit_log (
	start_time TIMESTAMP,
	end_time TIMESTAMP,
	rec_processed number,
	rec_remaining number,
	rec_notappl number,
	error_log varchar(4000)
)
/
commit
/
create or replace
PACKAGE CLAIM_AUDIT_UPDATE
is
  function read_line (p_clob in clob, p_start in out integer)
    return varchar2;
  function do_replace (p_str in varchar2)
    return varchar2;
END CLAIM_AUDIT_UPDATE;
/
create or replace
PROCEDURE UPDATE_CLAIM_AUDIT_XML_STR AS
  Cursor Claim_Audits
  Is
    Select Ca.Id, 
            Ca.Prev_Claim_Snapshot_String, 
            Ca.Previous_State, 
            Ca.For_Claim,
            ca.list_index,
            C.Claim_Number, 
            C.Business_Unit_Info
    From Claim_Audit Ca, Claim C 
    Where C.Id = Ca.For_Claim
    And C.Filed_On_Date > To_Date('06AUG2009')    
    And C.State Not In ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') = 0
    And Instr(Nvl(Ca.D_Internal_Comments, 'X'), 'IRI-Migration') = 0    
    Order By Filed_On_Date Desc;
 
    New_Audit_Xml         Clob;
    Str                   Varchar(4000);
    New_Str               Varchar(4000);
    Has_More_Data         Number := 1 ;  
    Start_Time            Timestamp;
    Error_Locator         Varchar(100);
    V_Rec_Processed       Number;
    V_Rec_Remaining       Number;
    V_Rec_Notappl         Number;  
    V_Error_Log           Varchar(4000) := '';
    V_COUNT               NUMBER := 0; 

begin
  start_time := current_timestamp;
  BEGIN
	  FOR EACH_CLAIM_AUDIT IN claim_audits
	  Loop
      Begin       
        dbms_lob.createtemporary(new_audit_xml, true);
        error_locator := each_claim_audit.claim_number || '|' || each_claim_audit.id;
        has_more_data := 1;      
        If (Dbms_Lob.Getlength(Each_Claim_Audit.Prev_Claim_Snapshot_String) > 0) Then
          while (has_more_data > 0) loop
            Str := Claim_Audit_Update.Read_Line(Each_Claim_Audit.Prev_Claim_Snapshot_String, Has_More_Data);            
            new_str := claim_audit_update.do_replace(str);
            If (New_Str Is Not Null) Then
              new_str := new_str|| Chr(10);
              Dbms_Lob.Writeappend(New_Audit_Xml, Length(New_Str), New_Str);
            end if;            
          END LOOP;
        End If;       
        Insert Into Claim_Audit_Backup 
        Values
        (
          each_claim_audit.id,
          EACH_CLAIM_AUDIT.prev_claim_snapshot_string,
          EACH_CLAIM_AUDIT.previous_state,
          each_claim_audit.list_index,
          Each_Claim_Audit.For_Claim,
          Each_Claim_Audit.Claim_Number,
          New_Audit_Xml,
          Each_Claim_Audit.Business_Unit_Info,
          null
        );
        
        Update Claim_Audit Ca
        set ca.prev_claim_snapshot_string = new_audit_xml, d_internal_comments = d_internal_comments || ' | XML_UPDATED'
        where ca.id = EACH_CLAIM_AUDIT.id;
        commit;	
        Dbms_Lob.Freetemporary(New_Audit_Xml);        
        Exception
          When Others Then
            Begin
              -- Dbms_Output.Put_Line('Ex: '||Each_Claim_Audit.Id);
              v_error_log := Substr(Sqlerrm, 0 , 2000);
              Insert Into Claim_Audit_Backup
              VALUES
              (
                each_claim_audit.id,
                EACH_CLAIM_AUDIT.prev_claim_snapshot_string,
                EACH_CLAIM_AUDIT.previous_state,
                each_claim_audit.list_index,
                Each_Claim_Audit.For_Claim,
                Each_Claim_Audit.Claim_Number,
                New_Audit_Xml,
                Each_Claim_Audit.Business_Unit_Info,
                v_error_log
              );               
              Commit;
              Dbms_Lob.Freetemporary(New_Audit_Xml);  
              v_error_log := null;
            end;
      end;	
	  end loop;	
	  Exception When Others Then
    	V_Error_Log := Error_Locator || ' ~~~ \n' || Substr(Sqlerrm, 0, 3500);
      --Dbms_Output.Put_Line('Exception in main block: '|| V_Error_Log);
      dbms_lob.freetemporary(new_audit_xml);
      rollback;
	END;
	
	select count(*) into V_REC_PROCESSED
	from claim_audit ca, claim c
    where c.id = ca.for_claim
    And C.Filed_On_Date > To_Date('06AUG2009')
    And C.State Not In ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') > 0
    AND instr(nvl(ca.d_internal_comments, 'X'), 'IRI-Migration') = 0;

	select count(*) into V_REC_REMAINING
	from claim_audit ca, claim c
    where c.id = ca.for_claim
    And C.Filed_On_Date > To_Date('06AUG2009')
    And C.State Not In ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') = 0
    AND instr(nvl(ca.d_internal_comments, 'X'), 'IRI-Migration') = 0;

	select count(*) into V_REC_NOTAPPL
	from claim_audit ca, claim c
    where c.id = ca.for_claim
    And (C.Filed_On_Date < To_Date('06AUG2009')
    OR c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    OR instr(nvl(ca.d_internal_comments, 'X'), 'IRI-Migration') > 0);
	
	Insert Into Update_Claim_Audit_Log VALUES 
	(
		Start_Time, 
		Current_Timestamp, 
		V_Rec_Processed, 
		V_Rec_Remaining, 
		V_Rec_Notappl, 
		V_ERROR_LOG
	);

	Commit;	
   --dbms_output.put(start_time|| ' : ' || current_timestamp);
END UPDATE_CLAIM_AUDIT_XML_STR;
/
create or replace
PACKAGE BODY CLAIM_AUDIT_UPDATE
IS
  FUNCTION read_line(p_clob IN CLOB, p_start IN OUT INTEGER)
    RETURN VARCHAR2
  IS
    r_record     VARCHAR2 (4000);
    end_pos      INTEGER;
    file_length  BINARY_INTEGER;
  BEGIN
    file_length  := sys.DBMS_LOB.getlength (p_clob); 
    end_pos      :=
      DBMS_LOB.INSTR (lob_loc => p_clob, pattern => CHR (10), offset => p_start);
    IF end_pos > 0 THEN
      r_record  :=
        RTRIM (
          DBMS_LOB.SUBSTR (lob_loc   => p_clob,
                           amount    => end_pos - p_start,
                           offset    => p_start
                          ),
          CHR (13) || CHR (10)
        );
      p_start  := end_pos + 1;
    ELSE
      r_record  := DBMS_LOB.SUBSTR (lob_loc   => p_clob,
                         amount    => file_length - p_start + 1,
                         offset    => p_start
                        );
      p_start   := 0; -- End of XML indication
    END IF;
    RETURN r_record;
  end read_line;

  Function do_replace(P_Str In Varchar2)
    Return Varchar2
  Is
    v_ret_string varchar2(4000) := null;
    V_Tab Varchar2(100);
  Begin 
    case 
    when (instr(p_str, '<inactiveLaborDetails class="org.hibernate.collection.PersistentList"/>') > 0 and v_ret_string is null) then
      null;
    When (Instr(P_Str, '<inactiveLaborDetails class="list"/>') > 0 And V_Ret_String Is Null) Then
      Null;
    When (Instr(P_Str, 'org.hibernate.collection.PersistentList') > 0 And V_Ret_String Is Null) Then
       v_ret_string := replace(p_str, 'org.hibernate.collection.PersistentList', 'java.util.List');
    when (instr(p_str, '<inactiveLaborDetails class="org.hibernate.collection.PersistentList">') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<inactiveLaborDetails class="org.hibernate.collection.PersistentList">', '<!--<inactiveLaborDetails class="org.hibernate.collection.PersistentList">'); 
    when (instr(p_str, '<inactiveLaborDetails class="list">') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<inactiveLaborDetails class="list">', '<!--<inactiveLaborDetails class="list">'); 
    when (instr(p_str, '</inactiveLaborDetails>') > 0 and v_ret_string is null) then  
      v_ret_string := replace(p_str, '</inactiveLaborDetails>', '</inactiveLaborDetails>-->');
    when (instr(p_str, '<payment>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<payment>', '<!--<payment>');
    when (instr(p_str, '<payment class') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<payment class', '<!--<payment class');
    when (instr(p_str, '</payment>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '</payment>', '</payment>-->');
    when (instr(p_str, '<belongsTo class="tavant.twms.domain.orgmodel.Organization">') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<belongsTo class="tavant.twms.domain.orgmodel.Organization">', '<belongsTo class="tavant.twms.domain.orgmodel.Party">');
    when (instr(p_str, '<distance></distance>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<distance></distance>', '<distance>0</distance>');
    when (instr(p_str, '<hours></hours>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_str, '<hours></hours>', '<hours>0</hours>');
    When (Instr(P_Str, '<content class') > 0 And V_Ret_String Is Null) Then
      v_tab := substr(p_str, 0, instr(p_str, '<content class="org.hibernate.lob.SerializableBlob"/>')-1);
      v_ret_string := v_tab || '<content class="dynamic-proxy">' || chr(10)
                      || v_tab || chr(9) || '<interface>java.sql.Blob</interface>' || chr(10)
                      || V_Tab || Chr(9) || '<interface>org.hibernate.engine.jdbc.WrappedBlob</interface>' || Chr(10)
                      || v_tab || chr(9) || '<interface>java.io.Serializable</interface>' || chr(10)
                      || v_tab || chr(9) || '<handler class="org.hibernate.engine.jdbc.SerializableBlobProxy"/>' || chr(10)
                      || v_tab || '</content>';
    when (instr(p_str, 'equipmentItemReference') > 0 and v_ret_string is null) then
        v_ret_string := replace(p_str, 'equipmentItemReference', 'partItemReference');
    when (instr(p_str, '<supplierReturnNeeded/>') > 0 and v_ret_string is null) then
       Null;
    When (Instr(P_Str, '<supplierReturnNeeded>') > 0 And V_Ret_String Is Null) Then    
      null;
    When (Instr(P_Str, '<siteNumber/>') > 0 And V_Ret_String Is Null) Then
      null;
    When (Instr(P_Str, '<siteNumber>') > 0 And V_Ret_String Is Null) Then    
      null;
    When (Instr(P_Str, '<location/>') > 0 And V_Ret_String Is Null) Then
      null;
--    when (instr(p_str, '<location>') > 0 and v_ret_string is null) then    
--        V_Ret_String := Replace(Replace(P_Str, '<location>', '<!--<location>'), '</location>', '</location>-->');
    When (Instr(P_Str, '<dueDaysReadOnly>') > 0 And V_Ret_String Is Null) Then    
        null;
    When (Instr(P_Str, '<isDueDateUpdated>') > 0 And V_Ret_String Is Null) Then    
       null;
    else
        v_ret_string := p_str;
    END CASE;
      return v_ret_string;  
  end do_replace;  
END CLAIM_AUDIT_UPDATE;