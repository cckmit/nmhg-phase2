CREATE OR REPLACE PACKAGE              tav_gim_process_dom_rule_xml 
/*
|| Package Name   : tav_gim_process_dom_rule_xml
|| Purpose        : Package to bundle code for domain rule audit xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 18/04/2011
|| Modification History (when, who, what)
||
||
*/
AS 

/*
|| Function Name  : REPLACE_DOMAIN_RULE_XML_ID
|| Purpose        : Function used to replace domain rule audit xml id
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 18/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_dom_rule_xml_id(p_in_str IN VARCHAR2) RETURN VARCHAR2;  

PROCEDURE process_dom_rule_xml_id(p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2);

END tav_gim_process_dom_rule_xml;
/


CREATE OR REPLACE PACKAGE body tav_gim_process_dom_rule_xml 
/*
|| Package Name   : tav_gim_process_dom_rule_xml
|| Purpose        : Package to bundle code for domain rule audit xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 18/04/2011
|| Modification History (when, who, what)
||
||
*/
AS 

/*
|| Function Name  : REPLACE_DOMAIN_RULE_XML_ID
|| Purpose        : Function used to replace domain rule audit xml id
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 18/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_dom_rule_xml_id(p_in_str IN VARCHAR2) RETURN VARCHAR2
AS
    v_return_str VARCHAR2(4000);
    v_source_str VARCHAR2(4000);
    v_replace_str VARCHAR2(4000);
    v_start_tag_index NUMBER;
    v_end_tag_index NUMBER := 13; -- Initializing the value to 13, since all ID will be 13-DIGIT character
    v_stmt_num NUMBER;
    v_input_str VARCHAR2(4000);
    
BEGIN
      v_stmt_num := 10;
      v_input_str := p_in_str;
      
      IF instr(v_input_str,'<id>')>0 THEN
        v_stmt_num := 20;
        -- Selecting the Index for the Start Tag 
        SELECT INSTR(v_input_str,'<id>') INTO v_start_tag_index FROM DUAL;  
       
        v_stmt_num := 30;
        -- Selecting the Index for the End Tag       
        SELECT INSTR(v_input_str,'</id>') INTO v_end_tag_index FROM DUAL;
        
        v_stmt_num := 40;
        -- Selecting the Source String from the XML tag        
        SELECT SUBSTR(v_input_str, v_start_tag_index + length('<id>'), (v_end_tag_index - ((v_start_tag_index + length('<id>')))))
          INTO v_source_str
        FROM DUAL;
        
        v_stmt_num := 50;
         -- Prefix the Source String to 15-digit sequence      
        SELECT 100000000000000 + TO_NUMBER(v_source_str)
          INTO v_replace_str
        FROM dual;
        
       v_stmt_num := 60; 
       -- Replacing source string with the replace string            
       SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
        INTO v_return_str
       FROM dual;
      
      END IF;
      
      v_stmt_num := 70;
      IF v_return_str IS NOT NULL THEN
        RETURN v_return_str;
      ELSE
        RETURN p_in_str;
      END IF;  

EXCEPTION
   WHEN OTHERS THEN
    v_return_str := 'Exception Occured in the Function at statement number :' || v_stmt_num || ' '  || SUBSTR(SQLERRM,1,255) ||'. ' || p_in_str; 
    RETURN v_return_str;
END replace_dom_rule_xml_id;  

PROCEDURE process_dom_rule_xml_id(p_out_error_code OUT NUMBER,
                                  p_out_error_message OUT VARCHAR2)
AS
CURSOR cur_dom_rule_aud_det IS
select id, rule_snapshot_string from domain_rule_audit where rule_snapshot_string is not null
and length(id) > 13;

  v_dom_rule_aud_xml CLOB;
  v_data_count NUMBER := 1;
  v_read_line VARCHAR2(4000);
  v_id_updated_xml VARCHAR2(4000);
  v_row_count NUMBER;
  v_stmt_number NUMBER;
  v_dom_rule_aud_id NUMBER;
  -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 	TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;
    

BEGIN

  v_stmt_number := 10;  
  dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));   
  v_stmt_number := 20;
  
  FOR cur_dom_rule_aud_det_rec in cur_dom_rule_aud_det LOOP
      BEGIN
          v_stmt_number := 30;
          -- Opening Log Statistics    
          v_program_name := cur_dom_rule_aud_det_rec.id;    
          v_migration_date := SYSTIMESTAMP;
          v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
          
          v_stmt_number := 40;    
          -- Allocation memory space for LOB storage    
          dbms_lob.createtemporary(v_dom_rule_aud_xml, true);    
          -- Setting the Data Count Flag to 1
          v_data_count := 1;
          
          v_stmt_number := 50;    
          IF (dbms_lob.getlength(cur_dom_rule_aud_det_rec.rule_snapshot_string) > 0 ) THEN
              v_stmt_number := 60;
              -- Start of WHILE loop
              WHILE (v_data_count > 0) LOOP
                      v_stmt_number := 70;
                      -- Invoke function to read XML content line by line        
                      v_read_line := tav_gim_xml_utilities.read_line(cur_dom_rule_aud_det_rec.rule_snapshot_string,v_data_count);        
                      
                      v_stmt_number := 80;    
                      -- Invoke function to update the ID in the string from XML        
                     v_id_updated_xml := tav_gim_process_dom_rule_xml.replace_dom_rule_xml_id(v_read_line) || CHR(13);  
                    
                      v_stmt_number := 90;        
                      -- Append the updated string to LOB
                      IF v_id_updated_xml IS NOT NULL THEN
                        dbms_lob.writeappend(v_dom_rule_aud_xml,length(v_id_updated_xml),v_id_updated_xml);
                      END IF;                                        
                 END LOOP;
                -- End of WHILE loop
          END IF;
        
         v_stmt_number := 100;    
        -- Update the destination table with updated XML content
        UPDATE domain_rule_audit a 
          SET a.rule_snapshot_string = v_dom_rule_aud_xml,
            a.d_internal_comments = a.d_internal_comments || ':XML ID UPDATED '
          WHERE a.id =  cur_dom_rule_aud_det_rec.id;   
      
        v_stmt_number := 110;
        -- Free the memory space allocated for LOB
        dbms_lob.freetemporary(v_dom_rule_aud_xml);      
    
        v_stmt_number := 120;
        -- Frequent Commits for 100 claims
        v_row_count := v_row_count + 1;               
        IF MOD(v_row_count,100) = 0 THEN
          COMMIT;
        END IF;     
      -- closing log Statistics
      v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );  
     
      EXCEPTION
      WHEN OTHERS THEN
        tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' Exception occured while processing DOMAIN RULE AUDIT ID : ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in PROCESS_DOM_RULE_XML_ID');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
     END ;
  END LOOP;
  
  -- End of the master loop for processing all the ID selected 
  v_stmt_number := 130;
  p_out_error_code := 0;
  p_out_error_message := 'Procedure PROCESS_DOM_RULE_XML_ID executed successfully';

  dbms_output.put_line('procedure end time : ' || to_char( sysdate, 'hh24:mi:ss' ));
  
EXCEPTION
WHEN OTHERS THEN
 tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' OTHERS Exception occured while processing DOMAIN RULE AUDIT ID : ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in PROCESS_DOM_RULE_XML_ID');
    v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
END process_dom_rule_xml_id;

END tav_gim_process_dom_rule_xml;
/
