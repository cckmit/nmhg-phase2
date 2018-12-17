create or replace
Procedure Update_Claim_Audit_Xml_Str_Tc As 
CURSOR claim_audits
  Is
    select id, prev_claim_snapshot_string from claim_audit where id in (1100001005107, 1100001005106, 1100001005105);

  new_audit_xml clob;
  str varchar(4000);
  new_str varchar(4000);
  has_more_data number := 1 ;
  
  start_time timestamp;  
  V_Count Number := 0;
  v_error_code  varchar2(4000);

begin
  start_time := current_timestamp;
  BEGIN
	  For Each_Claim_Audit In Claim_Audits Loop
      Begin
        --  dbms_output.put('clm: ' || each_claim_audit.claim_number || ' : ' || current_timestamp);
            Dbms_Lob.Createtemporary(New_Audit_Xml, True);
            Has_More_Data := 1;		
            if (dbms_lob.getlength(EACH_CLAIM_AUDIT.prev_claim_snapshot_string) > 0) then
              While (Has_More_Data > 0) Loop
                Str := Claim_Audit_Update.Read_Line(Each_Claim_Audit.prev_claim_snapshot_string, Has_More_Data);
                new_str := claim_audit_update.do_replace(str);                
                If (New_Str Is Not Null) Then
                  new_str := new_str|| Chr(10);
                  Dbms_Lob.Writeappend(New_Audit_Xml, Length(New_Str), New_Str);
                end if;                
              END LOOP;
            end if;
            
            Update Claim_Audit Ca
            set ca.prev_claim_snapshot_string = new_audit_xml
            Where Ca.Id = Each_Claim_Audit.Id; 
            
            Update Claim_Audit_Backup Cab
            Set Cab.Modified_Xml = New_Audit_Xml
            Where Cab.Id = Each_Claim_Audit.Id; 
            
          --    dbms_output.put_line('~' || current_timestamp);
            Dbms_Lob.Freetemporary(New_Audit_Xml);
      Exception 
      When Others Then    
        V_Error_Code := Substr(Sqlerrm, 1, 4000);
        Update Claim_Audit_Verification4
        Set Exception_Log = v_error_code
        Where Id = Each_Claim_Audit.Id;
        Dbms_Lob.Freetemporary(New_Audit_Xml);      
      end;
	  End Loop;
    Commit;	
    dbms_output.put_line('TOTAL TIME: ' || start_time || '~' || current_timestamp);
	  Exception 
      When Others Then
        dbms_output.put_line('exception');
      rollback;
	END;	
END UPDATE_CLAIM_AUDIT_XML_STR_TC;