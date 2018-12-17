CREATE OR REPLACE PACKAGE              TAV_GIM_PROCESS_FOC_ORDER_XML 
/*
|| Package Name   : TAV_GIM_PROCESS_FOC_ORDER_DET
|| Purpose        : Package to bundle code for FOC order details xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/04/2011
|| Modification History (when, who, what)
||
||
*/
AS 

-- Global variable declaration

g_fault_found_flag NUMBER := 0;
g_caused_by_flag NUMBER := 0;

/*
|| Function Name  : REPLACE_FOC_ORDER_DET_XML_ID
|| Purpose        : Function to update xml id based on PREFIX and LOOKUP function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_foc_order_det_xml_id( p_in_str IN VARCHAR2)  RETURN VARCHAR2;


/*
|| Procedure Name : PROCESS_FOC_ORDER_DET_XML_ID
|| Purpose        : Procedure to process FOC ORDER DETAILS xml string for replacing xml id values
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_foc_order_det_xml_id(p_out_error_code OUT  NUMBER, 
                                       p_out_error_message OUT VARCHAR2
                                     );

END TAV_GIM_PROCESS_FOC_ORDER_XML;
/


CREATE OR REPLACE PACKAGE BODY TAV_GIM_PROCESS_FOC_ORDER_xml 
/*
|| Package Name   : TAV_GIM_PROCESS_FOC_ORDER_DET
|| Purpose        : Package to bundle code for FOC order details xml processing
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/04/2011
|| Modification History (when, who, what)
||
||
*/
AS 


/*
|| Function Name  : REPLACE_FOC_ORDER_DET_XML_ID
|| Purpose        : Function to update xml id based on PREFIX and LOOKUP function
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/04/2011
|| Modification History (when, who, what)
||
||
*/
FUNCTION replace_foc_order_det_xml_id( p_in_str IN VARCHAR2)  RETURN VARCHAR2
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
    
    IF INSTR(v_input_str,' <faultFound>') > 0 THEN
       v_stmt_num := 20;
      -- Setting the global variable for Fault Found 
      g_fault_found_flag := 1;
      v_return_str := p_in_str;
    ELSIF INSTR(v_input_str,'<causedBy>')>0 THEN
      v_stmt_num := 30;
      --Setting the global variable for Caused By
      g_caused_by_flag := 1;
      v_return_str := p_in_str;
    ELSIF instr(v_input_str,'<id>')>0 THEN
        v_stmt_num := 40;    
        -- Selecting the Index for the Start Tag 
        SELECT INSTR(v_input_str,'<id>') INTO v_start_tag_index FROM DUAL;  
       
        v_stmt_num := 50;    
        -- Selecting the Index for the End Tag       
        SELECT INSTR(v_input_str,'</id>') INTO v_end_tag_index FROM DUAL;
        
        v_stmt_num := 60;    
        -- Selecting the Source String from the XML tag        
        SELECT SUBSTR(v_input_str, v_start_tag_index + length('<id>'), (v_end_tag_index - ((v_start_tag_index + length('<id>')))))
          INTO v_source_str
        FROM DUAL;
        
        -- Check whether the ID belongs to Fault Found 
        IF g_fault_found_flag = 1 THEN
          v_stmt_num := 70;
            --SELECT id INTO v_replace_str FROM tg_failure_type_definition WHERE old_43_id = v_source_str;
          v_stmt_num := 80;    
         -- Replacing source string with the replace string            
        SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
          INTO v_return_str
        FROM dual;
        
          v_stmt_num := 90;    
          -- Resetting the Global variable back to 0 for fault found
          g_fault_found_flag := 0;
       END IF;   
       -- Check whether the ID belongs to Caused By
        IF g_caused_by_flag = 1 THEN
         v_stmt_num := 100;
            --SELECT id INTO v_replace_str FROM tg_failure_type_definition WHERE old_43_id = v_source_str;
          v_stmt_num := 110;    
         -- Replacing source string with the replace string            
        SELECT REPLACE(v_input_str,v_source_str,v_replace_str)
          INTO v_return_str
        FROM dual;
        
          v_stmt_num := 120;    
          -- Resetting the Global variable back to 0 for caused by
          g_caused_by_flag := 0;
        END IF;
      END IF;    
      
      IF v_return_str IS NOT NULL THEN
        RETURN v_return_str;
      ELSE
        RETURN p_in_str;
      END IF;  
      
EXCEPTION 
WHEN OTHERS THEN
  v_return_str := 'Exception Occured in the Function at statement number :' || v_stmt_num || ' '  || SUBSTR(SQLERRM,1,255) ||'. ' || p_in_str; 
  RETURN v_return_str;
END replace_foc_order_det_xml_id;

/*
|| Procedure Name : PROCESS_FOC_ORDER_DET_XML_ID
|| Purpose        : Procedure to process FOC ORDER DETAILS xml string for replacing xml id values
|| Author         : Prabhu Ramasamy
|| Version        : Initial Write-Up
|| Creation Date  : 19/04/2011
|| Modification History (when, who, what)
||
||
*/
PROCEDURE process_foc_order_det_xml_id(p_out_error_code OUT  NUMBER, 
                                       p_out_error_message OUT VARCHAR2
                                     )
AS
  CURSOR cur_foc_order_det IS
    SELECT id,order_info,claim_info
      FROM foc_order_details
    WHERE length(id)>13;
    
   v_order_info_xml CLOB;
   v_claim_info_xml CLOB;
   v_order_info_data_count NUMBER := 1;
   v_claim_info_data_count NUMBER := 1;
   v_read_line VARCHAR2(4000);
   v_order_info_id_updated_xml VARCHAR2(4000);
   v_claim_info_id_updated_xml VARCHAR2(4000);
   v_row_count NUMBER;
   v_stmt_number NUMBER;
   
   -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;

BEGIN
    v_stmt_number := 10;
    dbms_output.put_line('procedure start time : ' || to_char( sysdate, 'hh24:mi:ss' ));
    
    v_stmt_number := 20;
    FOR cur_foc_order_det_rec IN cur_foc_order_det LOOP
      BEGIN
        v_stmt_number := 30;
         -- Opening Log Statistics    
        v_program_name := cur_foc_order_det_rec.id;    
        v_migration_date := SYSTIMESTAMP;
        v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);
        
        v_stmt_number := 40;
        -- Allocation memory space for LOB storage    
        dbms_lob.createtemporary(v_order_info_xml, true);    
        dbms_lob.createtemporary(v_claim_info_xml, true);  
        
        v_stmt_number := 50;
        v_order_info_data_count := 1;
        v_claim_info_data_count := 1;
        
        v_stmt_number := 60;
        IF(dbms_lob.getlength(cur_foc_order_det_rec.order_info) > 0) THEN
          v_stmt_number := 70;
          -- Start of WHILE LOOP for order info processing
          WHILE(v_order_info_data_count > 0) LOOP
            v_stmt_number := 80;
            -- Invoke the function to read the XML CLOB content as a string
            v_read_line := tav_gim_xml_utilities.read_line(cur_foc_order_det_rec.order_info,v_order_info_data_count);
            
            v_stmt_number := 90;
            -- Invoke the function to update the ID in the string from XML content
            v_order_info_id_updated_xml := tav_gim_process_foc_order_det.replace_foc_order_det_xml_id(v_read_line) || CHR(13);
            
            v_stmt_number := 100;
            -- Append the Updated string to LOB
            IF v_order_info_id_updated_xml IS NOT NULL THEN
              dbms_lob.writeappend(v_order_info_xml,length(v_order_info_id_updated_xml),v_order_info_id_updated_xml);              
            END IF;
          END LOOP;
          -- End of WHILE LOOP for Order Info Processing
        END IF;
        
        
        
        v_stmt_number := 110;
        IF(dbms_lob.getlength(cur_foc_order_det_rec.claim_info) > 0) THEN
          v_stmt_number := 120;
          -- Start of WHILE LOOP for order info processing
          WHILE(v_claim_info_data_count > 0) LOOP
            v_stmt_number := 130;
            -- Invoke the function to read the XML CLOB content as a string
            v_read_line := tav_gim_xml_utilities.read_line(cur_foc_order_det_rec.claim_info,v_claim_info_data_count);
            
            v_stmt_number := 140;
            -- Invoke the function to update the ID in the string from XML content
            v_claim_info_id_updated_xml := tav_gim_process_foc_order_det.replace_foc_order_det_xml_id(v_read_line) || CHR(13);
            
            v_stmt_number := 150;
            -- Append the Updated string to LOB
            IF v_claim_info_id_updated_xml IS NOT NULL THEN
              dbms_lob.writeappend(v_claim_info_xml,length(v_claim_info_id_updated_xml),v_claim_info_id_updated_xml);              
            END IF;
          END LOOP;
          -- End of WHILE LOOP for Order Info Processing
        END IF;
      
      v_stmt_number := 160;
      UPDATE foc_order_details a
        SET a.order_info = v_order_info_xml,
            a.claim_info = v_claim_info_xml,
            a.d_internal_comments = a.d_internal_comments || ':XML ID UPDATED'
        WHERE a.id = cur_foc_order_det_rec.id;
        
      v_stmt_number := 170;
      -- Free the memory space allocated for LOBs
      dbms_lob.freetemporary(v_order_info_xml);
      dbms_lob.freetemporary(v_claim_info_xml);
      
      v_stmt_number := 180;
      -- Frequent Commit. Commit Frequency for every 100 rows
      v_row_count := v_row_count + 1;
      IF MOD(v_row_count,100) = 0 THEN
        COMMIT;
      END IF;  
      
      v_stmt_number := 190;
      -- Closing Log Statistics
      v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );
      
      EXCEPTION
        WHEN OTHERS THEN
        tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-99',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type =>' Exception occured while processing FOC ORDER DETAILS ID : ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in PROCESS_FOC_ORDER_DET_XML_ID');
       v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
      END;  
    END LOOP;
    -- End of the master loop for processing all the ID selected 
    v_stmt_number := 200;
    p_out_error_code := 0;
    p_out_error_message := 'Procedure UPDATE_CLAIM_AUDIT_XML_ID executed successfully';
     
    dbms_output.put_line('procedure end time : ' || to_char( sysdate, 'hh24:mi:ss' ));
EXCEPTION 
  WHEN OTHERS THEN
  NULL;
END process_foc_order_det_xml_id;

END tav_gim_process_foc_order_xml;
/
