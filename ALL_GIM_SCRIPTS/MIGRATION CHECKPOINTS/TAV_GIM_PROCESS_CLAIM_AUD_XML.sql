CREATE OR REPLACE PACKAGE tav_gim_process_claim_aud_xml
/*
|| Package Name   : TAV_GIM_PROCESS_CLAIM_AUD_XML
|| Purpose        : Package to bundle code for claim audit xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
AS

TYPE t_claim_audit_id_rec IS RECORD (v_id CLAIM_AUDIT.ID%TYPE,
                                     v_xml_data CLAIM_AUDIT.prev_claim_snapshot_string%TYPE,
                                     v_filed_on_date CLAIM.FILED_ON_DATE%TYPE);

TYPE t_claim_audit_id_tab IS TABLE OF t_claim_audit_id_rec;
v_claim_audit_id_tab t_claim_audit_id_tab;

TYPE t_party_array IS TABLE OF NUMBER INDEX BY VARCHAR2(100);
v_party_array t_party_array ;

TYPE t_org_user_array IS TABLE OF NUMBER INDEX BY VARCHAR2(100);
v_org_user_array t_org_user_array ;

TYPE t_cost_category_array IS TABLE OF NUMBER INDEX BY VARCHAR2(100);
v_cost_category_array t_cost_category_array;


TYPE t_claim_audit_job_rec IS RECORD(v_id NUMBER,
                                    v_xml_data CLOB);

TYPE t_claim_audit_job_rec_tab IS TABLE OF t_claim_audit_job_rec;

----->--for bulk string updation---->---
TYPE t_claim_audit_string_rec IS RECORD(v_id NUMBER,
                                    v_claim_no VARCHAR2(50),
                                    v_xml_data CLOB);

TYPE t_claim_audit_string_rec_tab IS TABLE OF t_claim_audit_string_rec;
--<------end---<----


g_cost_category_flag NUMBER := 0;


/*
|| Function Name  : REPLACE_CLAIM_AUDIT_XML_STR
|| Purpose        : Function used to replace claim audit xml string
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_claim_audit_xml_str(p_in_str IN VARCHAR2) RETURN VARCHAR2;


/*
|| Function Name  : REPLACE_CLAIM_AUDIT_XML_ID
|| Purpose        : Function to update xml id based on PREFIX and LOOKUP function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_claim_audit_xml_id( p_in_str IN VARCHAR2)  RETURN VARCHAR2;


/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_XML_ID
|| Purpose        : Procedure to process claim audit xml string for replacing xml id values
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_claim_audit_xml_id(p_out_error_code OUT  NUMBER,
                                     p_out_error_message OUT VARCHAR2
                                     );


/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_XML_BULK
|| Purpose        : Procedure to process claim audit xml string for replacing xml id values [BULK COLLECT/FETCH]
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_claim_audit_xml_bulk(p_out_error_code OUT  NUMBER,
                                       p_out_error_message OUT VARCHAR2
                                     );


/*
|| Procedure Name : process_claim_str_job_master
|| Purpose        : Procedure used to process claim audit xml string for replacing xml tags parallelly
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE process_claim_str_job_master(p_in_jobs IN NUMBER,
                                         P_OUT_ERROR_CODE OUT NUMBER,
                                         p_out_error_message OUT VARCHAR2);
/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_XML_STR
|| Purpose        : Procedure used to process claim audit xml string for replacing xml tags
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
procedure PROCESS_CLAIM_AUDIT_XML_STR(p_in_id_start_range IN NUMBER,
                                        P_IN_ID_END_RANGE in number);
/*
|| Procedure Name : process_claim_audit_job_master
|| Purpose        : Master Procedure to process claim audit xml string for replacing xml id values [DBMS JOBS + BULK COLLECT/FETCH]
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/


PROCEDURE process_claim_audit_job_master(p_in_jobs IN NUMBER,
                                         p_out_error_code OUT NUMBER,
                                         p_out_error_message OUT VARCHAR2);

/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_JOB_CHILD
|| Purpose        : Child Procedure to process claim audit xml string for replacing xml id values [DBMS JOBS + BULK COLLECT/FETCH]
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_claim_audit_job_child(p_in_id_start_range IN NUMBER,
                                        p_in_id_end_range IN NUMBER
                                    );


END TAV_GIM_PROCESS_CLAIM_AUD_XML;
/


CREATE OR REPLACE PACKAGE BODY tav_gim_process_claim_aud_xml
/*
|| Package Name   : TAV_GIM_PROCESS_CLAIM_AUD_XML
|| Purpose        : Package to bundle code for claim audit xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
AS

/*
|| Function Name  : REPLACE_CLAIM_AUDIT_XML_STR
|| Purpose        : Function used to replace claim audit xml string
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_claim_audit_xml_str(p_in_str IN VARCHAR2) RETURN VARCHAR2
AS
    v_ret_string varchar2(4000) := null;
    V_Tab Varchar2(100);
  BEGIN
    CASE
    WHEN (instr(p_in_str, '<inactiveLaborDetails class="org.hibernate.collection.PersistentList"/>') > 0 and v_ret_string is null) then
      NULL;
    WHEN (Instr(p_in_str, '<inactiveLaborDetails class="list"/>') > 0 And V_Ret_String Is Null) Then
      NULL;
    WHEN (Instr(p_in_str, 'org.hibernate.collection.PersistentList') > 0 And V_Ret_String Is Null) Then
       v_ret_string := replace(p_in_str, 'org.hibernate.collection.PersistentList', 'java.util.List');
    WHEN (instr(p_in_str, '<inactiveLaborDetails class="org.hibernate.collection.PersistentList">') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<inactiveLaborDetails class="org.hibernate.collection.PersistentList">', '<!--<inactiveLaborDetails class="org.hibernate.collection.PersistentList">');
    when (instr(p_in_str, '<inactiveLaborDetails class="list">') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<inactiveLaborDetails class="list">', '<!--<inactiveLaborDetails class="list">');
    when (instr(p_in_str, '</inactiveLaborDetails>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '</inactiveLaborDetails>', '</inactiveLaborDetails>-->');
    when (instr(p_in_str, '<payment>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<payment>', '<!--<payment>');
    when (instr(p_in_str, '<payment class') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<payment class', '<!--<payment class');
    when (instr(p_in_str, '</payment>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '</payment>', '</payment>-->');
    when (instr(p_in_str, '<belongsTo class="tavant.twms.domain.orgmodel.Organization">') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<belongsTo class="tavant.twms.domain.orgmodel.Organization">', '<belongsTo class="tavant.twms.domain.orgmodel.Party">');
    when (instr(p_in_str, '<distance></distance>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<distance></distance>', '<distance>0</distance>');
    when (instr(p_in_str, '<hours></hours>') > 0 and v_ret_string is null) then
      v_ret_string := replace(p_in_str, '<hours></hours>', '<hours>0</hours>');
    When (Instr(p_in_str, '<content class="org.hibernate.lob.SerializableBlob"/>') > 0 And V_Ret_String Is Null) Then
     -- v_tab := substr(p_in_str, 0, instr(p_in_str, '<content class="org.hibernate.lob.SerializableBlob"/>')-1);
      v_ret_string := '<content class="dynamic-proxy">' || chr(10)
                      || v_tab || chr(9) || '<interface>java.sql.Blob</interface>' || chr(10)
                      || V_Tab || Chr(9) || '<interface>org.hibernate.engine.jdbc.WrappedBlob</interface>' || Chr(10)
                      || v_tab || chr(9) || '<interface>java.io.Serializable</interface>' || chr(10)
                      || v_tab || chr(9) || '<handler class="org.hibernate.engine.jdbc.SerializableBlobProxy"/>' || chr(10)
                      || v_tab || '</content>';
    when (instr(p_in_str, 'equipmentItemReference') > 0 and v_ret_string is null) then
        v_ret_string := replace(p_in_str, 'equipmentItemReference', 'partItemReference');
    when (instr(p_in_str, '<supplierReturnNeeded/>') > 0 and v_ret_string is null) then
       Null;
    When (Instr(p_in_str, '<supplierReturnNeeded>') > 0 And V_Ret_String Is Null) Then
      null;
    When (Instr(p_in_str, '<siteNumber/>') > 0 And V_Ret_String Is Null) Then
      null;
    When (Instr(p_in_str, '<siteNumber>') > 0 And V_Ret_String Is Null) Then
      null;
    When (Instr(p_in_str, '<location/>') > 0 And V_Ret_String Is Null) Then
      null;
--    when (instr(p_in_str, '<location>') > 0 and v_ret_string is null) then
--        V_Ret_String := Replace(Replace(p_in_str, '<location>', '<!--<location>'), '</location>', '</location>-->');
    When (Instr(p_in_str, '<dueDaysReadOnly>') > 0 And V_Ret_String Is Null) Then
        null;
    When (Instr(p_in_str, '<isDueDateUpdated>') > 0 And V_Ret_String Is Null) Then
       null;
    else
        v_ret_string := p_in_str;
    END CASE;
      return v_ret_string;
  end replace_claim_audit_xml_str;


/*
|| Procedure Name : process_claim_str_job_master
|| Purpose        : Procedure used to process claim audit xml string for replacing xml tags parallelly
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE process_claim_str_job_master(p_in_jobs IN NUMBER,
                                         p_out_error_code OUT NUMBER,
                                         p_out_error_message OUT VARCHAR2)
AS

    v_range_value NUMBER;
    v_range_max_value NUMBER;
    v_records_avail NUMBER;
    v_id_start_range_val NUMBER := 0;
    v_id_end_range_val NUMBER := 0;
    v_record_count NUMBER;
    v_id_start_range NUMBER;
    v_id_end_range NUMBER;
    v_user_job_no NUMBER;
    v_stmt_number NUMBER;

BEGIN

    -- Selecting the total number of records in the claim audit temporary table
    V_STMT_NUMBER := 10;

--    SELECT  COUNT(*) INTO V_RECORD_COUNT
----    FROM CLAIM_AUDIT;
--    FROM claim_audit_temp;
--    WHERE ROWNUM <=1000;


    select count(ca.id)
    into V_RECORD_COUNT
    from claim_audit ca, claim c
    Where C.Id = Ca.For_Claim
    -- and c.claim_number = 'HUS-20000022'
    AND LENGTH(CA.ID) > 13
    and CA.id < 110000000000000
    and c.filed_on_date > to_date('15APR2011')
    and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') = 0
    and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0;


    v_stmt_number := 20;
    SELECT FLOOR(v_record_count/p_in_jobs)
    INTO v_range_value
    FROM DUAL;

    v_stmt_number := 30;
    SELECT v_range_value * p_in_jobs
    INTO v_range_max_value
    FROM DUAL;

    v_stmt_number := 40;
    SELECT v_record_count - v_range_max_value
    INTO v_records_avail
    FROM DUAL;

    v_stmt_number := 80;
    FOR i in 1..p_in_jobs LOOP
      v_id_start_range := v_id_start_range_val + 1;
      IF i = p_in_jobs THEN
        v_id_end_range := (v_range_value * i )  + v_records_avail;
      ELSE
        v_id_end_range := v_range_value * i;
      END IF;
      dbms_output.put_line('Start Range :' || v_id_start_range  ||' ==> ' || 'End Range : ' || v_id_end_range);
      DBMS_JOB.SUBMIT(v_user_job_no,
                             'tav_gim_process_claim_aud_xml.process_claim_audit_xml_str('||v_id_start_range|| ',' ||v_id_end_range||');',
                             SYSDATE,
                             NULL);
      COMMIT;
      DBMS_OUTPUT.PUT_LINE('Job # :' || v_user_job_no);
      v_id_start_range_val := v_id_end_range;
    END LOOP;

EXCEPTION
    WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('Exception Occured' || SUBSTR(SQLERRM,1,255));
END process_claim_str_job_master;



/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_XML_STR
|| Purpose        : Procedure used to process claim audit xml string for replacing xml tags
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
procedure PROCESS_CLAIM_AUDIT_XML_STR(p_in_id_start_range IN NUMBER,
                                        p_in_id_end_range IN NUMBER
                                    )
AS

cursor CLAIM_AUDITS
IS

--              select ca.id,c.claim_number,ca.prev_claim_snapshot_string  from claim_audit ca, claim c
--              Where C.Id = Ca.For_Claim
--              -- and c.claim_number = 'HUS-20000022'
--              and length(CA.id) > 13
--              and CA.id < 110000000000000
--              and c.filed_on_date > to_date('01MAR2011')
--              and c.filed_on_date < to_date('01APR2011')
--              and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED','ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED')
--              AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') = 0
--              and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
--              order by ca.id desc;

select ca.id,c.claim_number,ca.prev_claim_snapshot_string  from CLAIM_AUDIT ca, claim c
Where C.Id = Ca.For_Claim
              -- and c.claim_number = 'HUS-20000022'
              and length(CA.id) > 13
              and CA.id < 110000000000000
              and c.filed_on_date > to_date('01JAN2011')
              AND C.STATE NOT IN ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
              AND INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'XML_UPDATED') = 0
              AND instr(nvl(ca.internal_comments, 'X'), 'XML_UPDATED_61') = 0
              and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
              order by ca.id desc;



  new_audit_xml clob;
  str varchar(4000);
  new_str varchar(4000);
  has_more_data number := 1 ;
  line_number number := 0;
  start_time timestamp;
  error_locator varchar(100);
  V_REC_PROCESSED NUMBER;
  V_REC_REMAINING NUMBER;
  V_REC_NOTAPPL NUMBER;
  V_ERROR_LOG VARCHAR(4000) := '';
  V_COUNT number := 0;
  V_rec_COUNT number := 0;
  v_job_seq_id number := 0;
    v_status		NUMBER := 0;
  v_program_name VARCHAR(4000) := '';
  v_migration_date timestamp;
  v_claim_audit_string_rec_tab t_claim_audit_string_rec_tab := t_claim_audit_string_rec_tab();


begin
  start_time := current_timestamp;
  dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));

              v_program_name := 'CLAIM AUDIT STRING UPDATION';
              v_migration_date := SYSTIMESTAMP;
              v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);

  OPEN CLAIM_AUDITS;
  LOOP


    FETCH CLAIM_AUDITS BULK COLLECT INTO v_claim_audit_string_rec_tab LIMIT 100;
     EXIT WHEN CLAIM_AUDITS%NOTFOUND;

    -- Start of the master loop for processing all the ID selected
        FOR i in v_claim_audit_string_rec_tab.FIRST..v_claim_audit_string_rec_tab.LAST LOOP

		dbms_lob.createtemporary(new_audit_xml,true);
		error_locator := v_claim_audit_string_rec_tab(i).v_claim_no || '|' || v_claim_audit_string_rec_tab(i).v_id;
		has_more_data := 1;
		line_number := 0;
		if (dbms_lob.getlength(v_claim_audit_string_rec_tab(i).v_xml_data) > 0) then
		  While (Has_More_Data > 0) Loop
			--str := claim_audit_update.read_line(v_claim_audit_string_rec_tab(i).v_xml_data, has_more_data);
      str := tav_gim_xml_utilities.read_line(v_claim_audit_string_rec_tab(i).v_xml_data, has_more_data);
			--new_str := claim_audit_update.do_replace(str) || chr(10);
      new_str := tav_gim_process_claim_aud_xml.replace_claim_audit_xml_str(str) || chr(10);

			dbms_lob.writeappend(new_audit_xml, LENGTH(new_str), new_str);
		  END LOOP;
		end if;


  	UPDATE CLAIM_AUDIT CA
      set ca.prev_claim_snapshot_string = new_audit_xml, internal_comments = 'XML_UPDATE_61',d_updated_on = sysdate
		where CA.id = v_claim_audit_string_rec_tab(i).v_id;

   V_rec_COUNT :=  V_rec_COUNT + 1;

   if V_rec_COUNT = 100 then
   commit;
   V_REC_COUNT := 0;
   end if;


    dbms_lob.freetemporary(new_audit_xml);
	  end loop;

   end loop;

    CLOSE CLAIM_AUDITS;

    -- closing log Statistics
         v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );
--           dbms_output.put_line('procedure end time : ' || to_char( sysdate, 'hh24:mi:ss' ));
	Commit;
	  exception when others then
		V_ERROR_LOG := error_locator || ' ~~~ \n' || SUBSTR(SQLERRM,0,3500);
		dbms_lob.freetemporary(new_audit_xml);
		rollback;
END PROCESS_CLAIM_AUDIT_XML_STR;

/*
	select count(*) into V_REC_PROCESSED
	from claim_audit ca, claim c
    where c.id = ca.for_claim
    and c.filed_on_date > to_date('06AUG2009')
    and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') > 0
    AND instr(nvl(ca.d_internal_comments, 'X'), 'IRI-Migration') = 0;

	select count(*) into V_REC_REMAINING
	from claim_audit ca, claim c
    where c.id = ca.for_claim
    And C.Filed_On_Date > To_Date('06AUG2009')
    and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    AND instr(nvl(ca.d_internal_comments, 'X'), 'XML_UPDATED') = 0
    AND instr(nvl(ca.d_internal_comments, 'X'), 'IRI-Migration') = 0;

	select count(*) into V_REC_NOTAPPL
	from claim_audit ca, claim c
    where c.id = ca.for_claim
    and (c.filed_on_date < to_date('06AUG2009')
    OR c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
    OR instr(nvl(ca.d_internal_comments, 'X'), 'IRI-Migration') > 0);


	INSERT INTO update_claim_audit_log VALUES (
	start_time, CURRENT_TIMESTAMP, V_REC_PROCESSED, V_REC_REMAINING, V_REC_NOTAPPL, V_ERROR_LOG);
 */



/*
|| Function Name  : REPLACE_CLAIM_AUDIT_XML_ID
|| Purpose        : Function to update xml id based on PREFIX and LOOKUP function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 03/03/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_claim_audit_xml_id(p_in_str IN VARCHAR2) RETURN VARCHAR2
  AS
    v_return_str VARCHAR2(4000);
    v_source_str VARCHAR2(4000);
    v_replace_str VARCHAR2(4000);
    v_start_tag_index NUMBER;
    v_end_tag_index NUMBER := 13; -- Initializing the value to 13, since all ID will be 13-DIGIT character
    v_stmt_num NUMBER;
    V_INPUT_STR VARCHAR2(4000);
    v_input_mod_str VARCHAR2(4000); -- merging string id updationg and audit updation
  BEGIN

  V_STMT_NUM := 10;
  v_input_mod_str := tav_gim_process_claim_aud_xml.replace_claim_audit_xml_str(p_in_str); -- merging string id updationg and audit updation
  v_input_str := v_input_mod_str;

    -- Handle DEALERSHIP ID
    IF instr(v_input_str,'<dealershipId>')> 0 then

      v_stmt_num := 20;
      -- Selecting the Index for the Start Tag
      SELECT INSTR(v_input_str,'<dealershipId>') INTO v_start_tag_index FROM DUAL;

      v_stmt_num := 30;
      -- Selecting the Index for the End Tag
      SELECT INSTR(v_input_str,'</dealershipId>') INTO v_end_tag_index FROM DUAL;

      v_stmt_num := 40;
      -- Select the Source String from the given xml tag
        SELECT SUBSTR(v_input_str, v_start_tag_index + length('<dealershipId>'), (v_end_tag_index - ((v_start_tag_index + length('<dealershipId>')))))
          INTO v_source_str
        FROM DUAL;

      v_stmt_num := 50;

      -- Selecting the replace string from Staging table
      --SELECT id INTO v_replace_str FROM tg_party WHERE old_43_id = v_source_str;
      --SELECT id INTO v_replace_str FROM party_array_table WHERE old_43_id = v_source_str;
        v_replace_str := v_party_array(v_source_str);

      v_stmt_num := 60;
      -- Replacing Source String with Replace String
       SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
      FROM dual;

     -- Handle Cost Category
     ELSIF instr(v_input_str,'<forCategory class="tavant.twms.domain.claim.payment.CostCategory">') > 0 THEN

     v_stmt_num := 70;
     -- Setting the Glbal Variable for Cost Category
     g_cost_category_flag := 1;

     v_return_str := v_input_mod_str;

    -- Handle USER ID
    ELSIF instr(v_input_str,'<userId>')> 0 then

      v_stmt_num := 80;
       -- Selecting the Index for the Start Tag
       SELECT INSTR(v_input_str,'<userId>') INTO v_start_tag_index FROM DUAL;

       v_stmt_num := 90;
       -- Selecting the Index for the End Tag
        SELECT INSTR(v_input_str,'</userId>') INTO v_end_tag_index FROM DUAL;

        v_stmt_num := 100;
        -- Select the Source String from the given xml tag
        SELECT SUBSTR(v_input_str, v_start_tag_index + length('<userId>'), (v_end_tag_index - ((v_start_tag_index + length('<userId>')))))
          INTO v_source_str
        FROM DUAL;

        v_stmt_num := 110;

      -- Selecting the replace string from Staging table
      --SELECT id INTO v_replace_str FROM tg_org_user WHERE old_43_id = v_source_str;
      --SELECT id INTO v_replace_str FROM org_user_array_table WHERE old_43_id = v_source_str;
      v_replace_str := v_org_user_array(v_source_str);

      v_stmt_num := 120;
      -- Replacing Source String with Replace String
       SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
      FROM dual;

    -- Handle ID for PREFIX scenarios
   ELSIF instr(v_input_str,'<id>')>0 THEN
        v_stmt_num := 130;
        -- Selecting the Index for the Start Tag
        SELECT INSTR(v_input_str,'<id>') INTO v_start_tag_index FROM DUAL;

        v_stmt_num := 140;
        -- Selecting the Index for the End Tag
        SELECT INSTR(v_input_str,'</id>') INTO v_end_tag_index FROM DUAL;

        v_stmt_num := 160;
        -- Selecting the Source String from the XML tag
        SELECT SUBSTR(v_input_str, v_start_tag_index + length('<id>'), (v_end_tag_index - ((v_start_tag_index + length('<id>')))))
          INTO v_source_str
        FROM DUAL;

        v_stmt_num := 170;
        -- Check whether ID belongs to Cost Category
        IF g_cost_category_flag = 1 THEN

        v_stmt_num := 180;

        -- Select the replace string from
       --SELECT ID INTO v_replace_str FROM tg_cost_category WHERE OLD_43_ID = v_source_str;
       --SELECT id INTO v_replace_str FROM cost_category_array_table WHERE old_43_id = v_source_str;
        v_replace_str := v_cost_category_array(v_source_str);

        v_stmt_num := 190;
         -- Replacing source string with the replace string
        SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
          INTO v_return_str
        FROM dual;

        v_stmt_num := 200;
        -- Resetting the Global variable back to 0 for COst Category
        g_cost_category_flag := 0;

        ELSE
        -- End of check whether ID belongs to Cost Category
        v_stmt_num := 210;

       -- Prefix the Source String to 15-digit sequence
        SELECT 100000000000000 + TO_NUMBER(v_source_str)
          INTO v_replace_str
        FROM dual;

        v_stmt_num := 220;
      -- Replacing source string with the replace string
      SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
      FROM dual;

      END IF;
      -- End of Check whether ID belongs to Cost Category

     ELSIF instr(v_input_str,'<InventoryId>')>0 THEN
       -- Selecting the Index for the Start Tag
       SELECT INSTR(v_input_str,'<InventoryId>') INTO v_start_tag_index FROM DUAL;
       -- Selecting the Index for the End Tag
       SELECT INSTR(v_input_str,'</InventoryId>') INTO v_end_tag_index FROM DUAL;
       -- Selecting the Source String from the XML tag
       SELECT SUBSTR(v_input_str, v_start_tag_index + length('<InventoryId>'), (v_end_tag_index - ((v_start_tag_index + length('<InventoryId>')))))
          INTO v_source_str
        FROM DUAL;


        -- Prefix the Source String to 15-digit sequence
        SELECT 100000000000000 + TO_NUMBER(v_source_str)
          INTO v_replace_str
        FROM dual;

      -- Replacing source string with the replace string
      SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
      FROM dual;

     ---processing the shipmentid tag now
     ELSIF instr(v_input_str,'<shipmentId>')>0 THEN
       -- Selecting the Index for the Start Tag
       SELECT INSTR(v_input_str,'<shipmentId>') INTO v_start_tag_index FROM DUAL;
       -- Selecting the Index for the End Tag
       SELECT INSTR(v_input_str,'</shipmentId>') INTO v_end_tag_index FROM DUAL;
       -- Selecting the Source String from the XML tag
       SELECT SUBSTR(v_input_str, v_start_tag_index + length('<shipmentId>'), (v_end_tag_index - ((v_start_tag_index + length('<shipmentId>')))))
          INTO v_source_str
        FROM DUAL;


        -- Prefix the Source String to 15-digit sequence
        SELECT 100000000000000 + TO_NUMBER(v_source_str)
          INTO v_replace_str
        FROM dual;

      -- Replacing source string with the replace string
      SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
      FROM DUAL;
      -----end of processing shipmentID


      ELSIF(instr(v_input_str,'<listOfValueId>')>0) then
         -- Selecting the Index for the Start Tag
        SELECT INSTR(v_input_str,'<listOfValueId>') INTO v_start_tag_index FROM DUAL;
         -- Selecting the Index for the End Tag
        SELECT INSTR(v_input_str,'</listOfValueId>') INTO v_end_tag_index FROM DUAL;
        -- Selecting the Source String from the given XML tag
        SELECT SUBSTR(v_input_str, v_start_tag_index + length('<listOfValueId>'), (v_end_tag_index - ((v_start_tag_index + length('<listOfValueId>')))))
          INTO v_source_str
        FROM DUAL;

        -- Prefix the Source String to 15-digit sequence
        SELECT 100000000000000 + TO_NUMBER(v_source_str)
          INTO v_replace_str
        FROM dual;

      -- Replacing source string with the replace string
      SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
      FROM dual;

      END IF;

      IF v_return_str IS NOT NULL THEN
        RETURN v_return_str;
      ELSE
        RETURN v_input_mod_str;
      END IF;

  EXCEPTION
  WHEN OTHERS THEN
    v_return_str := 'EXCEPTION Occured in the Function at statement number :' || v_stmt_num || ' '  || SUBSTR(SQLERRM,1,255) ||'. ' || v_input_mod_str;
    RETURN v_return_str;
  END replace_claim_audit_xml_id;


/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_XML_ID
|| Purpose        : Procedure to process claim audit xml string for replacing xml id values
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/

PROCEDURE process_claim_audit_xml_id(p_out_error_code OUT  NUMBER,
                                     p_out_error_message OUT VARCHAR2)
AS


  CURSOR cur_claim_audit_det IS
  SELECT a.id,a.prev_claim_snapshot_string as xml_data, b.filed_on_date
  	FROM claim_audit a,claim b
	  WHERE b.id = a.for_claim
	  and length(a.id) > 13
    and a.id < 110000000000000
    AND b.filed_on_date > TO_DATE('15APR2011')
	ORDER BY b.filed_on_date DESC;


  v_audit_xml CLOB;
  v_data_count NUMBER := 1;
  v_read_line VARCHAR2(4000);
  v_id_updated_xml VARCHAR2(4000);
  v_row_count NUMBER;
  v_stmt_number NUMBER;
  v_claim_audit_id NUMBER;
  -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;

  -- Exception Variables
  v_readline_exception EXCEPTION;
  v_xml_id_exception EXCEPTION;

BEGIN

  v_stmt_number := 10;

  dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));

  v_stmt_number := 20;

  FOR cur_claim_audit_det_rec IN cur_claim_audit_det LOOP
  -- Start of the master loop for processing all the ID selected
    BEGIN

    v_stmt_number := 30;
     -- Opening Log Statistics
    v_program_name := cur_claim_audit_det_rec.id;
    v_migration_date := SYSTIMESTAMP;
    v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);

    v_stmt_number := 40;
    -- Allocation memory space for LOB storage
    dbms_lob.createtemporary(v_audit_xml, true);
    -- Setting the Data Count Flag to 1
    v_data_count := 1;

    v_stmt_number := 50;
    IF (dbms_lob.getlength(cur_claim_audit_det_rec.xml_data) > 0 ) THEN
      v_stmt_number := 60;
      -- Start of WHILE loop
      WHILE (v_data_count > 0) LOOP
        v_stmt_number := 70;
        -- Invoke function to read XML content line by line
        v_read_line := tav_gim_xml_utilities.read_line(cur_claim_audit_det_rec.xml_data,v_data_count);

        v_stmt_number := 80;

        -- Invoke function to update the ID in the string from XML
        v_id_updated_xml := tav_gim_process_claim_aud_xml.replace_claim_audit_xml_id(v_read_line) || CHR(13);

        IF v_id_updated_xml LIKE 'EXCEPTION%' THEN
          RAISE v_xml_id_exception;
        END IF;

        v_stmt_number := 90;
        -- Append the updated string to LOB
        if v_id_updated_xml is not null then
          dbms_lob.writeappend(v_audit_xml,length(v_id_updated_xml),v_id_updated_xml);
        end if;
        v_stmt_number := 91;
      END LOOP;
      -- End of WHILE loop
    END IF;

    v_stmt_number := 100;

    -- Update the destination table with updated XML content
    UPDATE claim_audit a
      SET a.prev_claim_snapshot_string = v_audit_xml,
          a.d_internal_comments = a.d_internal_comments || ':XML ID UPDATED '
      WHERE a.id =  cur_claim_audit_det_rec.id;

    v_stmt_number := 110;
    -- Free the memory space allocated for LOB
    dbms_lob.freetemporary(v_audit_xml);

    v_stmt_number := 120;
    -- Frequent Commits for 100 claims
    v_row_count := v_row_count + 1;

    IF MOD(v_row_count,100) = 0 THEN
      commit;
    END IF;

   -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );
  EXCEPTION
    WHEN v_xml_id_exception THEN
    tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-100',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' Exception occured in the function to replace id : ' || v_program_name || '. '|| v_id_updated_xml,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in process_claim_audit_xml_id');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );

    WHEN OTHERS THEN
    tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' Exception occured while processing CLAIM AUDIT ID : ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in process_claim_audit_xml_id');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
  END;
  END LOOP;
  -- End of the master loop for processing all the ID selected
  v_stmt_number := 130;
  p_out_error_code := 0;
  p_out_error_message := 'Procedure process_claim_audit_xml_id executed successfully';

  dbms_output.put_line('procedure end time : ' || to_char( sysdate, 'hh24:mi:ss' ));
EXCEPTION
WHEN OTHERS THEN
ROLLBACK;
  --dbms_output.put_line('Exception Occured in procedure at statement number :' || v_stmt_number || SUBSTR(SQLERRM,1,255));
   tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'OTHER Exception occured while processing process_claim_audit_xml_id.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in process_claim_audit_xml_id');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );

END process_claim_audit_xml_id;



PROCEDURE process_claim_audit_xml_bulk(p_out_error_code OUT  NUMBER,
                                       p_out_error_message OUT VARCHAR2)
AS


  CURSOR cur_claim_audit_det IS
  SELECT a.id,a.prev_claim_snapshot_string as xml_src_data, b.filed_on_date
  	FROM claim_audit a,claim b
	  WHERE B.ID = A.FOR_CLAIM
	  and length(a.id) > 13
    AND A.ID < 110000000000000
    AND instr(nvl(a.d_internal_comments, 'X'), 'XML ID UPDATED') = 0
    and b.filed_on_date > to_date('15MAR2011')
    and b.filed_on_date < to_date('01APR2011')
	ORDER BY b.filed_on_date DESC;

--  CURSOR cur_claim_audit_det IS
--  SELECT id, xml_src_data,filed_on_date
--  FROM claim_audit_temp;



  v_audit_xml CLOB;
  v_data_count NUMBER := 1;
  v_read_line VARCHAR2(4000);
  v_id_updated_xml VARCHAR2(4000);
  v_row_count NUMBER;
  v_stmt_number NUMBER;
  v_claim_audit_id NUMBER;
  -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;

  -- Exception Variables
  v_readline_exception EXCEPTION;
  v_xml_id_exception EXCEPTION;
BEGIN

  v_stmt_number := 10;

  dbms_output.put_line('procedure start time : ' || SYSTIMESTAMP);
  /*
  v_stmt_number := 20;
  FOR i IN (SELECT ID,OLD_43_ID FROM party_array_table) LOOP
    v_party_array(i.old_43_id) := i.id;
  END LOOP;

   v_stmt_number := 30;
  FOR i in (SELECT id,old_43_id FROM org_user_array_table) LOOP
    v_org_user_array(i.old_43_id) := i.id;
  END LOOP;

  v_stmt_number := 40;
  FOR i IN (SELECT id,old_43_id FROM cost_category_array_table) LOOP
    v_cost_category_array(i.old_43_id) := i.id;
  END LOOP;

  DBMS_OUTPUT.PUT_LINE('Party Array : ' || v_party_array.COUNT || 'Org User Array : ' || v_org_user_array.COUNT || 'Cost Category Array : '|| v_cost_category_array.COUNT);
  */
  v_stmt_number := 50;
  OPEN cur_claim_audit_det;
  LOOP
    v_stmt_number := 60;
    FETCH cur_claim_audit_det BULK COLLECT INTO v_claim_audit_id_tab LIMIT 100;
    EXIT WHEN cur_claim_audit_det%NOTFOUND;
      FOR i in v_claim_audit_id_tab.FIRST..v_claim_audit_id_tab.LAST LOOP

        v_stmt_number := 70;
        -- Opening Log Statistics
        v_program_name := v_claim_audit_id_tab(i).v_id;
        v_migration_date := SYSTIMESTAMP;
        v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
        BEGIN
          v_stmt_number := 80;
          -- Allocation memory space for LOB storage
          dbms_lob.createtemporary(v_audit_xml, true);
          -- Setting the Data Count Flag to 1
          v_data_count := 1;
          v_stmt_number := 90;
          IF (dbms_lob.getlength(v_claim_audit_id_tab(i).v_xml_data) > 0 ) THEN
              v_stmt_number := 100;
              -- Start of WHILE loop
              WHILE (v_data_count > 0) LOOP
                v_stmt_number := 110;
                -- Invoke function to read XML content line by line
                v_read_line := tav_gim_xml_utilities.read_line(v_claim_audit_id_tab(i).v_xml_data,v_data_count);
                v_stmt_number := 120;
                -- Invoke function to update the ID in the string from XML
                v_id_updated_xml := tav_gim_process_claim_aud_xml.replace_claim_audit_xml_id(v_read_line) || CHR(13);

                IF v_id_updated_xml LIKE 'EXCEPTION%' THEN
                  RAISE v_xml_id_exception;
                END IF;

                v_stmt_number := 130;
                -- Append the updated string to LOB
                IF v_id_updated_xml IS NOT NULL THEN
                    dbms_lob.writeappend(v_audit_xml,length(v_id_updated_xml),v_id_updated_xml);
                END IF;
                v_stmt_number := 140;
              END LOOP;
            -- End of WHILE loop
          END IF;

        v_stmt_number := 150;

    -- Update the destination table with updated XML content

     UPDATE CLAIM_AUDIT A
            SET A.PREV_CLAIM_SNAPSHOT_STRING = V_AUDIT_XML,
                A.INTERNAL_COMMENTS = ' XML ID UPDATED ',
                a.d_updated_on = sysdate
            WHERE a.id =  v_claim_audit_id_tab(i).v_id;


--    UPDATE claim_audit_temp a
--      SET a.xml_data = v_audit_xml,
--          a.comments = 'XML ID UPDATED '
--      WHERE a.id =  v_claim_audit_id_tab(i).v_id;

    v_stmt_number := 160;
    -- Free the memory space allocated for LOB
    dbms_lob.freetemporary(v_audit_xml);

    v_stmt_number := 170;
    -- Frequent Commits for 100 claims
    v_row_count := v_row_count + 1;

    IF MOD(v_row_count,100) = 0 THEN
      COMMIT;
    END IF;

    -- closing log Statistics
   v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );

    EXCEPTION
      WHEN v_xml_id_exception THEN
      tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-100',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' Exception occured in the function to replace id : ' || v_program_name || '. '|| v_id_updated_xml,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in process_claim_audit_xml_id');
      v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );

      WHEN OTHERS THEN
        tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' Exception occured while processing CLAIM AUDIT ID : ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in process_claim_audit_xml_bulk');
      v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
    END;
    END LOOP;

  END LOOP;
  CLOSE cur_claim_audit_det;

  dbms_output.put_line('procedure end time : ' || SYSTIMESTAMP);
EXCEPTION
WHEN OTHERS THEN
ROLLBACK;
 -- dbms_output.put_line('Exception Occured in procedure at statement number :' || v_stmt_number || SUBSTR(SQLERRM,1,255));

  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'OTHER Exception occured while processing process_claim_audit_xml_bulk.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in process_claim_audit_xml_bulk');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );

END process_claim_audit_xml_bulk;


/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_JOB_MASTER
|| Purpose        : Procedure to process claim audit xml string for replacing xml id values [DBMS JOBS + BULK COLLECT/FETCH]
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_claim_audit_job_master(p_in_jobs IN NUMBER,
                                         p_out_error_code OUT NUMBER,
                                         p_out_error_message OUT VARCHAR2)
AS

    v_range_value NUMBER;
    v_range_max_value NUMBER;
    v_records_avail NUMBER;
    v_id_start_range_val NUMBER := 0;
    v_id_end_range_val NUMBER := 0;
    v_record_count NUMBER;
    v_id_start_range NUMBER;
    v_id_end_range NUMBER;
    v_user_job_no NUMBER;
    v_stmt_number NUMBER;

BEGIN

    -- Selecting the total number of records in the claim audit temporary table
    V_STMT_NUMBER := 10;

--    SELECT  COUNT(*) INTO V_RECORD_COUNT
----    FROM CLAIM_AUDIT;
--    FROM claim_audit_temp;
--    WHERE ROWNUM <=1000;


    SELECT count(1) INTO V_RECORD_COUNT
  	FROM claim_audit a,claim b
	  WHERE B.ID = A.FOR_CLAIM
	  AND LENGTH(A.ID) > 13
    AND A.ID < 110000000000000
    AND B.FILED_ON_DATE > TO_DATE('15APR2011');


    v_stmt_number := 20;
    SELECT FLOOR(v_record_count/p_in_jobs)
    INTO v_range_value
    FROM DUAL;

    v_stmt_number := 30;
    SELECT v_range_value * p_in_jobs
    INTO v_range_max_value
    FROM DUAL;

    v_stmt_number := 40;
    SELECT v_record_count - v_range_max_value
    INTO v_records_avail
    FROM DUAL;

    v_stmt_number := 80;
    FOR i in 1..p_in_jobs LOOP
      v_id_start_range := v_id_start_range_val + 1;
      IF i = p_in_jobs THEN
        v_id_end_range := (v_range_value * i )  + v_records_avail;
      ELSE
        v_id_end_range := v_range_value * i;
      END IF;
      dbms_output.put_line('Start Range :' || v_id_start_range  ||' ==> ' || 'End Range : ' || v_id_end_range);
      DBMS_JOB.SUBMIT(v_user_job_no,
                             'tav_gim_process_claim_aud_xml.process_claim_audit_job_child('||v_id_start_range|| ',' ||v_id_end_range||');',
                             SYSDATE,
                             NULL);
      COMMIT;
      DBMS_OUTPUT.PUT_LINE('Job # :' || v_user_job_no);
      v_id_start_range_val := v_id_end_range;
    END LOOP;
EXCEPTION
    WHEN OTHERS THEN
    DBMS_OUTPUT.PUT_LINE('Exception Occured' || SUBSTR(SQLERRM,1,255));
END process_claim_audit_job_master;


/*
|| Procedure Name : PROCESS_CLAIM_AUDIT_JOB_CHILD
|| Purpose        : Child Procedure to process claim audit xml string for replacing xml id values [DBMS JOBS + BULK COLLECT/FETCH]
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 12/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_claim_audit_job_child(p_in_id_start_range IN NUMBER,
                                        p_in_id_end_range IN NUMBER
                                    )
AS

  CURSOR CUR_CLAIM_AUDIT_DET IS
/*    SELECT ca.id  id,XMLSERIALIZE(Document XMLTYPE (prev_claim_snapshot_string) as CLOB INDENT SIZE = 2) xml_src_data
      FROM claim_audit ca, claim c
      WHERE C.Id = Ca.For_Claim
      --AND C.FILED_ON_DATE <= add_months(sysdate,-6)
      AND C.FILED_ON_DATE <= add_months(sysdate,-18)
      AND prev_claim_snapshot_string is not null
      AND c.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED', 'DRAFT_DELETED', 'DEACTIVATED') 
      AND INSTR(NVL(CA.INTERNAL_COMMENTS, 'X'), 'XML ID UPDATED') = 0
      AND instr(nvl(ca.internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0
      AND INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
      and length(ca.id) > 13;*/
    WITH claimauditidview as (
      SELECT ca.id  id
      FROM claim_audit ca, claim c
      WHERE C.Id = Ca.For_Claim
      AND C.FILED_ON_DATE >=add_months(sysdate,-6)
      AND prev_claim_snapshot_string is not null
      AND c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
      AND INSTR(NVL(CA.INTERNAL_COMMENTS, 'X'), 'XML ID UPDATED') = 0
      AND instr(nvl(ca.D_INTERNAL_COMMENTS, 'X'), 'XML_ID_UPDATED_61') = 0
      AND INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
      and length(ca.id) > 13
      UNION
      SELECT ca.id  id
      FROM claim_audit ca, claim c
      WHERE C.Id = Ca.For_Claim
      AND prev_claim_snapshot_string is not null
      AND C.FILED_ON_DATE < add_months(sysdate,-6)
      AND c.state not in ('ACCEPTED_AND_CLOSED','DENIED_AND_CLOSED','DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
      AND INSTR(NVL(CA.INTERNAL_COMMENTS, 'X'), 'XML ID UPDATED') = 0
      AND instr(nvl(ca.D_INTERNAL_COMMENTS, 'X'), 'XML_ID_UPDATED_61') = 0
      AND INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
      and length(ca.id) > 13
      )
    SELECT ca.id,XMLSERIALIZE(Document XMLTYPE (prev_claim_snapshot_string) as CLOB INDENT SIZE = 2) xml_src_data
    FROM claim_audit ca, claimauditidview cv
    WHERE cv.id = ca.id;

--  SELECT ROW_NUM, ID, XML_SRC_DATA
--  FROM
--      (SELECT ROWNUM ROW_NUM, ID, XML_SRC_DATA
--       FROM
--            (
--              SELECT ROWNUM ROW_NUM, a.id as ID,a.prev_claim_snapshot_string as XML_SRC_DATA
--              FROM claim_audit a,claim b
--              WHERE b.id = a.for_claim
--              and length(a.id) > 13
--              AND A.ID < 110000000000000
--              AND instr(nvl(a.d_internal_comments, 'X'), 'XML ID UPDATED') = 0
--              and B.FILED_ON_DATE > TO_DATE('15APR2011')
--              ORDER BY a.id desc;


--              select ROWNUM ROW_NUM, ca.id as id,ca.prev_claim_snapshot_string as XML_SRC_DATA
--              from claim_audit ca, claim c
--              Where C.Id = Ca.For_Claim
--              -- and c.claim_number = 'HUS-20000022'
--              and length(CA.id) > 13
--              and CA.id < 110000000000000
--              AND C.FILED_ON_DATE < TO_DATE('01JAN2011')
--              and c.filed_on_date > to_date('29DEC2010')
--              and c.state not in ('DELETED', 'DRAFT_DELETED', 'DEACTIVATED')
--              AND INSTR(NVL(CA.INTERNAL_COMMENTS, 'X'), 'XML ID UPDATED') = 0
--              AND instr(nvl(ca.internal_comments, 'X'), 'XML_ID_UPDATED_61') = 0
--              and INSTR(NVL(CA.D_INTERNAL_COMMENTS, 'X'), 'IRI-Migration') = 0
--              order by ca.id desc;
/*
 SELECT ROW_NUM, ID, XML_SRC_DATA
  FROM
      (SELECT ROWNUM ROW_NUM, ID, XML_SRC_DATA
       FROM
            (SELECT ID, XML_SRC_DATA
             FROM CLAIM_AUDIT_TEMP
             ORDER BY ID ASC
             )
       ) T ;


*/


--  SELECT ROW_NUM, ID, XML_SRC_DATA
--  FROM
--      (SELECT ROWNUM ROW_NUM, ID, XML_SRC_DATA
--       FROM
--            (SELECT ID, XML_SRC_DATA
--             FROM CLAIM_AUDIT_TEMP
--             ORDER BY ID ASC
--             )
--       ) T
--  WHERE T.ROW_NUM >= P_IN_ID_START_RANGE
--  AND t.row_num   <= p_in_id_end_range;

  v_audit_xml CLOB;
  v_data_count NUMBER := 1;
  v_read_line VARCHAR2(4000);
  v_id_updated_xml VARCHAR2(4000);
  v_row_count NUMBER;
  v_stmt_number NUMBER;
  v_claim_audit_id NUMBER;
  v_collection_count NUMBER;
  v_claim_audit_job_rec_tab t_claim_audit_job_rec_tab := t_claim_audit_job_rec_tab();
  -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;

  -- Exception Variables
  v_readline_exception EXCEPTION;
  v_update_xml_id_exception EXCEPTION;

BEGIN
  dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));
  v_stmt_number := 10;


  DBMS_OUTPUT.PUT_LINE('Party Array : ' || v_party_array.COUNT || ' Org User Array : ' || v_org_user_array.COUNT || ' Cost Category Array : '|| v_cost_category_array.COUNT);
  IF v_party_array.COUNT = 0 THEN
  /* Code changes need to be included for ARRAY manipulation */
    v_stmt_number := 50;
      FOR i IN (SELECT ID,OLD_43_ID FROM party_array_table) LOOP
        v_party_array(i.old_43_id) := i.id;
      END LOOP;

     v_stmt_number := 60;
      FOR i in (SELECT id,old_43_id FROM org_user_array_table) LOOP
        v_org_user_array(i.old_43_id) := i.id;
      END LOOP;

     v_stmt_number := 70;
      FOR i IN (SELECT id,old_43_id FROM cost_category_array_table) LOOP
        v_cost_category_array(i.old_43_id) := i.id;
      END LOOP;

    DBMS_OUTPUT.PUT_LINE('Party Array : ' || v_party_array.COUNT || ' Org User Array : ' || v_org_user_array.COUNT || ' Cost Category Array : '|| v_cost_category_array.COUNT);
    /* End of Code changes need to be included for ARRAY manipulation */
  END IF;

  OPEN cur_claim_audit_det;
  LOOP
    v_stmt_number := 60;

    FETCH cur_claim_audit_det BULK COLLECT INTO v_claim_audit_job_rec_tab LIMIT 500;
     EXIT WHEN cur_claim_audit_det%NOTFOUND;

    -- Start of the master loop for processing all the ID selected
        FOR i in v_claim_audit_job_rec_tab.FIRST..v_claim_audit_job_rec_tab.LAST LOOP

              v_collection_count := v_claim_audit_job_rec_tab.COUNT;
              v_stmt_number := 70;
              -- Opening Log Statistics
              v_program_name := v_claim_audit_job_rec_tab(i).v_id;
              v_migration_date := SYSTIMESTAMP;
              v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
              BEGIN
                v_stmt_number := 80;
                -- Allocation memory space for LOB storage
                dbms_lob.createtemporary(v_audit_xml, true);
                -- Setting the Data Count Flag to 1
                v_data_count := 1;
                v_stmt_number := 90;
                IF (dbms_lob.getlength(v_claim_audit_job_rec_tab(i).v_xml_data) > 0 ) THEN
                    v_stmt_number := 100;
                    -- Start of WHILE loop
                    WHILE (v_data_count > 0) LOOP
                      v_stmt_number := 110;
                      -- Invoke function to read XML content line by line
                      v_read_line := tav_gim_xml_utilities.read_line(v_claim_audit_job_rec_tab(i).v_xml_data,v_data_count);
                      v_stmt_number := 120;

                      -- Invoke function to update the ID in the string from XML
                      v_id_updated_xml := tav_gim_process_claim_aud_xml.replace_claim_audit_xml_id(v_read_line) || CHR(13);

                      IF v_id_updated_xml LIKE 'EXCEPTION%' THEN
                        RAISE v_update_xml_id_exception;
                      END IF;

                      v_stmt_number := 130;
                      -- Append the updated string to LOB
                      IF v_id_updated_xml IS NOT NULL THEN
                          dbms_lob.writeappend(v_audit_xml,length(v_id_updated_xml),v_id_updated_xml);
                      END IF;
                      v_stmt_number := 140;
                    END LOOP;
                  -- End of WHILE loop
                END IF;
              v_stmt_number := 150;
          -- Update the destination table with updated XML content
--           UPDATE CLAIM_AUDIT A
--            SET A.PREV_CLAIM_SNAPSHOT_STRING = V_AUDIT_XML,
--                A.INTERNAL_COMMENTS = 'XML_ID_UPDATED_61',
--                A.d_updated_on = sysdate
--            WHERE a.id =  v_claim_audit_job_rec_tab(i).v_id;

         ---using temp tables to test
            UPDATE claim_audit a
            SET A.PREV_CLAIM_SNAPSHOT_STRING = V_AUDIT_XML,
                A.d_internal_COMMENTS = d_internal_COMMENTS || 'XML_ID_UPDATED_61' , D_UPDATED_ON = sysdate
            WHERE a.id =  v_claim_audit_job_rec_tab(i).v_id;


--          UPDATE claim_audit_temp a
--            SET a.xml_data = v_audit_xml,
--                a.comments = 'XML ID UPDATED '
--            WHERE a.id =  v_claim_audit_job_rec_tab(i).v_id;

          v_stmt_number := 160;
          -- Free the memory space allocated for LOB
          dbms_lob.freetemporary(v_audit_xml);

          v_stmt_number := 170;
          -- Frequent Commits for 100 claims
          v_row_count := v_row_count + 1;

          IF MOD(v_row_count,100) = 0 THEN
            COMMIT;
          END IF;
          -- closing log Statistics
         v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );
          EXCEPTION
            WHEN v_update_xml_id_exception THEN
            tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                                    g_issue_id => '-100',
                                                    g_table_name => v_program_name,
                                                    g_issue_col_name => NULL,
                                                    g_issue_col_value => NULL,
                                                    g_issue_type =>' Exception occured : ' ||v_program_name ||' ' || v_id_updated_xml,
                                                    g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                                    g_run_date => SYSTIMESTAMP,
                                                    g_block_name => 'Issue in PROCESS_CLAIM_AUDIT_JOB_CHILD');
            v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );

            WHEN OTHERS THEN
              tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                                    g_issue_id => '-99',
                                                    g_table_name => v_program_name,
                                                    g_issue_col_name => NULL,
                                                    g_issue_col_value => NULL,
                                                    g_issue_type =>' Exception occured while processing CLAIM AUDIT ID : ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                                    g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                                    g_run_date => SYSTIMESTAMP,
                                                    g_block_name => 'Issue in PROCESS_CLAIM_AUDIT_JOB_CHILD');
            v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
          END;
          END LOOP;
          -- End of the master loop for processing all the ID selected
  END LOOP;
  CLOSE cur_claim_audit_det;

  dbms_output.put_line('procedure end time : ' || SYSTIMESTAMP);
EXCEPTION
WHEN OTHERS THEN
ROLLBACK;
 -- dbms_output.put_line('Exception Occured in procedure at statement number :' || v_stmt_number || SUBSTR(SQLERRM,1,255));
  tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'OTHER Exception occured while processing PROCESS_CLAIM_AUDIT_JOB_CHILD.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in PROCESS_CLAIM_AUDIT_JOB_CHILD');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );

END process_claim_audit_job_child;

END TAV_GIM_PROCESS_CLAIM_AUD_XML;
/
