--Purpose    : Patch to populate the historical jbpm process instances need to be migrated
--Created On : 11-Jun-2011
--Created By : Kuldeep Patil
--Impact     : None

CREATE TABLE HISTORICAL_PROCESS_INSTANCES(
	PROCESS_INSTANCE NUMBER(19, 0),
	PROCESS_DEFINITION_NAME Varchar2(255),
	IS_MIGRATED varchar2(1)
)
/
insert into HISTORICAL_PROCESS_INSTANCES(PROCESS_INSTANCE, PROCESS_DEFINITION_NAME, IS_MIGRATED)
SELECT PROCESS_INSTANCE, PROCESS_DEFINITION_NAME, 'N' FROM (
	--claim submission 
	select distinct pi.id_ PROCESS_INSTANCE, pd.name_ PROCESS_DEFINITION_NAME 
	from jbpm_ProcessInstance pi, jbpm_TaskInstance ti, jbpm_Task t, jbpm_ProcessDefinition pd, jbpm_ModuleInstance mi
	where ti.isOpen_ = 1 and ti.name_ not in ('ClosedClaim', 'Closed') and ti.taskMgmtInstance_ = mi.id_ and t.id_ = ti.task_
	and PD.ID_ = T.PROCESSDEFINITION_ and PD.NAME_ = 'ClaimSubmission' and PD.ID_ = PI.PROCESSDEFINITION_ and PI.ID_ = MI.PROCESSINSTANCE_
	AND PD.VERSION_ <= (SELECT MAX(VERSION_) FROM JBPM_PROCESSDEFINITION PD WHERE PD.NAME_ ='ClaimSubmission')
	UNION
	--Parts return
	SELECT DISTINCT PI.ID_ PROCESS_INSTANCE, PD.name_ PROCESS_DEFINITION_NAME 
	FROM JBPM_PROCESSINSTANCE PI, JBPM_TASKINSTANCE TI, JBPM_TASK T, JBPM_PROCESSDEFINITION PD, JBPM_MODULEINSTANCE MI  
	WHERE TI.ISOPEN_ = 1 AND TI.TASKMGMTINSTANCE_ = MI.ID_ AND T.ID_ = TI.TASK_
	AND PD.ID_ = T.PROCESSDEFINITION_ AND PD.NAME_ = 'PartsReturn' AND PD.ID_ = PI.PROCESSDEFINITION_ AND PI.ID_ = MI.PROCESSINSTANCE_
	AND PD.VERSION_ <= (SELECT MAX(VERSION_) FROM JBPM_PROCESSDEFINITION PD WHERE PD.NAME_ ='PartsReturn')
	union
	--Supplier Recovery  
	select distinct pi.id_ PROCESS_INSTANCE, pd.name_ PROCESS_DEFINITION_NAME
	from jbpm_ProcessInstance pi, jbpm_TaskInstance ti, jbpm_Task t, jbpm_ProcessDefinition pd, jbpm_ModuleInstance mi
	where ti.isOpen_ = 1 and ti.name_ not in ('ClosedClaim', 'Closed') and ti.taskMgmtInstance_ = mi.id_ and t.id_ = ti.task_
	and PD.ID_ = T.PROCESSDEFINITION_ and PD.NAME_ = 'SupplierRecovery' and PD.ID_ = PI.PROCESSDEFINITION_ and PI.ID_ = MI.PROCESSINSTANCE_
	AND PD.VERSION_ <= (SELECT MAX(VERSION_) FROM JBPM_PROCESSDEFINITION PD WHERE PD.NAME_ ='SupplierRecovery')
)
ORDER BY PROCESS_DEFINITION_NAME, PROCESS_INSTANCE
/
commit
/