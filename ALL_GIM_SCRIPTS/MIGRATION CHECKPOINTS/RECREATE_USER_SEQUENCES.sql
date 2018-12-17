DECLARE
	
	CURSOR cur_user_sequences IS
		SELECT *
			from USER_SEQUENCES
			where UPPER(SEQUENCE_NAME) not in ('TAV_GIM_JOB_SEQ','SHIPMENT_SEQ')
			and upper(sequence_name) not in (select distinct upper(sequence_name) from claim_number_pattern);
      
  v_sequence_name VARCHAR2(100);
	v_increment_by NUMBER;
	v_cache_size NUMBER;
	v_cache_str VARCHAR2(100);
  v_stmt_number NUMBER := 0;
  v_seq_exists_cnt NUMBER;
   -- Open and Close Log variables
  v_status		NUMBER := 0;
  v_job_seq_id		NUMBER := 0;
  v_migration_date	TIMESTAMP;
  v_program_name 		TAV_GIM_EXCEPTION_LOG.TABLE_NAME%TYPE ;
  
  
	
BEGIN
  
  v_stmt_number := 10;
	FOR cur_user_sequences_rec IN cur_user_sequences LOOP	
		BEGIN
      v_sequence_name := cur_user_sequences_rec.sequence_name;
			v_increment_by 	:= cur_user_sequences_rec.increment_by;
			v_cache_size 	:= cur_user_sequences_rec.cache_size;
      v_program_name := v_sequence_name;    
      v_migration_date := SYSTIMESTAMP;
      v_job_seq_id     := tav_gim_initial_setup.open_log (tav_gim_initial_setup.g_inprog_ok,v_program_name,v_migration_date);	
    	
			IF v_cache_size = 0 THEN
				v_cache_str := ' NOCACHE';
			ELSE
				v_cache_str := ' CACHE ' || v_cache_size;
			END IF;
      
      v_stmt_number := 11;
      SELECT count(1) 
      INTO v_seq_exists_cnt
      FROM user_sequences 
      WHERE sequence_name = v_sequence_name;
      
      IF v_seq_exists_cnt > 0 THEN
        v_stmt_number := 20;
        EXECUTE IMMEDIATE 'DROP SEQUENCE ' || v_sequence_name;
      END IF;  
			
      v_stmt_number := 30;
			IF v_sequence_name IN ('PART_RETURN_ACTION_SEQ','SERVICE_DETAIL_SEQ','SERVICE_INFO_SEQ') THEN		        
        v_stmt_number := 40;
				EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || v_sequence_name ||' MINVALUE 1 MAXVALUE 999999999999999999999999999 START WITH 110000000000000 INCREMENT BY '|| v_increment_by || v_cache_str || ' ORDER ' || ' NOCYCLE ';			
			ELSE			
        v_stmt_number := 50;
				EXECUTE IMMEDIATE 'CREATE SEQUENCE ' || v_sequence_name ||' MINVALUE 1 MAXVALUE 999999999999999999999999999 START WITH 110000000000000 INCREMENT BY '|| v_increment_by || v_cache_str || ' NOORDER ' || ' NOCYCLE ';			
			END IF;
		
     v_stmt_number := 60; 
     v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_complete_ok );   	
		EXCEPTION
			WHEN OTHERS THEN
				tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-100',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => ' Exception occured while recreating sequence: ' || v_program_name || '. Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in recreating sequence');
      v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );	
		END;		
	END LOOP;

EXCEPTION
	WHEN OTHERS THEN
		tav_gim_initial_setup.proc_insert_error_record (gjob_seq_id => v_job_seq_id,
                                              g_issue_id => '-100',
                                              g_table_name => v_program_name,
                                              g_issue_col_name => NULL,
                                              g_issue_col_value => NULL,
                                              g_issue_type => 'OTHER Exception occured while processing UPDATE_CLAIM_AUDIT_XML_ID.Statement Number : ' || v_stmt_number,
                                              g_ora_error_message => SUBSTR(SQLERRM,1,255),
                                              g_run_date => SYSTIMESTAMP,
                                              g_block_name => 'Issue in recreating Sequences');
		v_status := tav_gim_initial_setup.close_log (v_job_seq_id,tav_gim_initial_setup.g_comp_proc_error );
END;
/