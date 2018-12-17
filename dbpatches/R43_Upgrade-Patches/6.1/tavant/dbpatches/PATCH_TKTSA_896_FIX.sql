--Purpose    : Updating the to task node of OfflineNoResponseAutoDebited transition to point closed task node. Fix for TKTSA-896.
--Created On : 21-July-2011
--Created By : Kuldeep Patil
--Impact     : None

UPDATE jbpm_transition
SET to_ =
  (SELECT id_ FROM jbpm_node WHERE name_ = 'Closed Fork'
  )
WHERE processdefinition_ =
  (SELECT id_ FROM jbpm_processdefinition WHERE name_ = 'SupplierRecovery'
  )
AND name_ = 'OfflineNoResponseAutoDebited'
/
commit
/
declare

	cursor recovery_claims
	is
	SELECT ti.id_,
	  ti.name_,
	  isopen_,
	  ti.taskmgmtinstance_,
	  ti.token_,
	  MI.PROCESSINSTANCE_,
	  clm.recovery_claim_number
	FROM jbpm_taskinstance ti,
	  jbpm_variableinstance vi,
	  recovery_claim clm,
	  jbpm_moduleinstance mi,
	  jbpm_task task
	WHERE TI.TASKMGMTINSTANCE_   = MI.ID_
	AND TI.TASK_                 = TASK.ID_
	AND VI.PROCESSINSTANCE_      = MI.PROCESSINSTANCE_
	AND VI.LONGVALUE_            = CLM.ID
	AND vi.class_                = 'H'
	AND clm.recovery_claim_state = 'NO_RESPONSE_AND_AUTO_DEBITTED_CLOSED'
	AND TI.NAME_                 = 'Ready For Debit'
	AND TI.ISOPEN_               = 0
	AND NOT EXISTS
	  (SELECT id_
	  FROM jbpm_taskinstance ti1
	  WHERE ti1.taskmgmtinstance_ = TI.TASKMGMTINSTANCE_
	  AND TI1.isopen_               = 1
	  );
	
	v_closed_token number(19,0) := 0;
	v_debited_fork_token number(19,0) := 0;
	v_debited_token number(19,0) := 0;
	v_debit_schedlr_token number(19,0) := 0;

begin
	
	for each_rec_claim in recovery_claims
	loop
		begin
			select hibernate_sequence.nextval
			into v_closed_token
			from dual;

			select hibernate_sequence.nextval
			into v_debited_fork_token
			from dual;

			select hibernate_sequence.nextval
			into v_debited_token
			from dual;

			select hibernate_sequence.nextval
			into v_debit_schedlr_token
			from dual;

			INSERT
			INTO JBPM_TOKEN
			  (
				ID_,
				VERSION_,
				NAME_,
				START_,
				END_,
				NODEENTER_,
				NEXTLOGINDEX_,
				ISABLETOREACTIVATEPARENT_,
				ISTERMINATIONIMPLICIT_,
				ISSUSPENDED_,
				NODE_,
				PROCESSINSTANCE_,
				PARENT_,
				SUBPROCESSINSTANCE_
			  )
			  VALUES
			  (
				v_closed_token,
				1,
				'closeClaim',
				systimestamp,
				NULL,
				systimestamp,
				2,1,0,0,
				(select id_ from jbpm_node where name_ = 'Closed Claims'),
				each_rec_claim.PROCESSINSTANCE_,
				each_rec_claim.token_,
				NULL
			  );
			
			INSERT
			INTO JBPM_TOKEN
			  (
				ID_,
				VERSION_,
				NAME_,
				START_,
				END_,
				NODEENTER_,
				NEXTLOGINDEX_,
				ISABLETOREACTIVATEPARENT_,
				ISTERMINATIONIMPLICIT_,
				ISSUSPENDED_,
				NODE_,
				PROCESSINSTANCE_,
				PARENT_,
				SUBPROCESSINSTANCE_
			  )
			  VALUES
			  (
				v_debited_fork_token,
				1,
				'showDebited',
				systimestamp,
				NULL,
				systimestamp,
				3,1,0,0,
				(select id_ from jbpm_node where name_ = 'DebitedFork'),
				each_rec_claim.PROCESSINSTANCE_,
				each_rec_claim.token_,
				NULL
			  );

			INSERT
			INTO JBPM_TOKEN
			  (
				ID_,
				VERSION_,
				NAME_,
				START_,
				END_,
				NODEENTER_,
				NEXTLOGINDEX_,
				ISABLETOREACTIVATEPARENT_,
				ISTERMINATIONIMPLICIT_,
				ISSUSPENDED_,
				NODE_,
				PROCESSINSTANCE_,
				PARENT_,
				SUBPROCESSINSTANCE_
			  )
			  VALUES
			  (
				v_debited_token,
				1,
				'showDebited',
				systimestamp,
				NULL,
				systimestamp,
				3,1,0,0,
				(select id_ from jbpm_node where name_ = 'Debited'),
				each_rec_claim.PROCESSINSTANCE_,
				v_debited_fork_token,
				NULL
			  );

			INSERT
			INTO JBPM_TOKEN
			  (
				ID_,
				VERSION_,
				NAME_,
				START_,
				END_,
				NODEENTER_,
				NEXTLOGINDEX_,
				ISABLETOREACTIVATEPARENT_,
				ISTERMINATIONIMPLICIT_,
				ISSUSPENDED_,
				NODE_,
				PROCESSINSTANCE_,
				PARENT_,
				SUBPROCESSINSTANCE_
			  )
			  VALUES
			  (
				v_debit_schedlr_token,
				1,
				'showDebitScheduler',
				systimestamp,
				NULL,
				systimestamp,
				4,1,0,0,
				(select id_ from jbpm_node where name_ = 'DebitScheduler'),
				each_rec_claim.PROCESSINSTANCE_,
				v_debited_fork_token,
				NULL
			  );
			  
			INSERT
			INTO jbpm_taskinstance
			  (
				ID_,
				CLASS_,
				NAME_,
				DESCRIPTION_,
				ACTORID_,
				CREATE_,
				START_,
				END_,
				DUEDATE_,
				PRIORITY_,
				ISCANCELLED_,
				ISSUSPENDED_,
				ISOPEN_,
				ISSIGNALLING_,
				ISBLOCKING_,
				TASK_,
				TOKEN_,
				SWIMLANINSTANCE_,
				TASKMGMTINSTANCE_
			  )
			  VALUES
			  (
				hibernate_sequence.nextval,
				'T',
				'Closed',
				NULL,
				NULL,
				systimestamp,
				NULL,
				NULL,
				NULL,
				3,0,0,1,1,0,
				(SELECT ID_
				FROM jbpm_task
				WHERE NAME_            = 'Closed'
				AND PROCESSDEFINITION_ =
				  (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery'
				  )
				),
				v_closed_token,
				NULL,
				each_rec_claim.taskmgmtinstance_
			  );

			INSERT
			INTO jbpm_taskinstance
			  (
				ID_,
				CLASS_,
				NAME_,
				DESCRIPTION_,
				ACTORID_,
				CREATE_,
				START_,
				END_,
				DUEDATE_,
				PRIORITY_,
				ISCANCELLED_,
				ISSUSPENDED_,
				ISOPEN_,
				ISSIGNALLING_,
				ISBLOCKING_,
				TASK_,
				TOKEN_,
				SWIMLANINSTANCE_,
				TASKMGMTINSTANCE_
			  )
			  VALUES
			  (
				hibernate_sequence.nextval,
				'T',
				'Debited',
				NULL,
				NULL,
				systimestamp,
				NULL,
				NULL,
				NULL,
				3,0,0,1,1,0,
				(SELECT ID_
				FROM jbpm_task
				WHERE NAME_            = 'Debited'
				AND PROCESSDEFINITION_ =
				  (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery'
				  )
				),
				v_debited_token,
				NULL,
				each_rec_claim.taskmgmtinstance_
			  );

     		INSERT
			INTO jbpm_taskinstance
			  (
				ID_,
				CLASS_,
				NAME_,
				DESCRIPTION_,
				ACTORID_,
				CREATE_,
				START_,
				END_,
				DUEDATE_,
				PRIORITY_,
				ISCANCELLED_,
				ISSUSPENDED_,
				ISOPEN_,
				ISSIGNALLING_,
				ISBLOCKING_,
				TASK_,
				TOKEN_,
				SWIMLANINSTANCE_,
				TASKMGMTINSTANCE_
			  )
			  VALUES
			  (
				hibernate_sequence.nextval,
				'T',
				'DebitScheduler',
				NULL,
				NULL,
				systimestamp,
				NULL,
				NULL,
				NULL,
				3,0,0,1,1,0,
				(SELECT ID_
				FROM jbpm_task
				WHERE NAME_            = 'DebitScheduler'
				AND PROCESSDEFINITION_ =
				  (SELECT ID_ FROM JBPM_PROCESSDEFINITION WHERE NAME_ = 'SupplierRecovery'
				  )
				),
				v_debit_schedlr_token,
				NULL,
				each_rec_claim.taskmgmtinstance_
			  );

		end;
	end loop;
	commit;

	exception when others then
	rollback;
	raise;
end;
/