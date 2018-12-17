CREATE TABLE jbpm_processinstance_bak AS
SELECT ID_,
  VERSION_,
  START_,
  END_,
  ISSUSPENDED_,
  PROCESSDEFINITION_,
  ROOTTOKEN_,
  SUPERPROCESSTOKEN_
FROM jbpm_processinstance
WHERE 1 <> 1
/

CREATE TABLE jbpm_moduleinstance_bak AS
SELECT ID_,
  CLASS_,
  PROCESSINSTANCE_,
  TASKMGMTDEFINITION_,
  NAME_
FROM jbpm_moduleinstance
WHERE 1 <> 1
/

CREATE TABLE jbpm_token_bak AS
SELECT ID_,
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
FROM jbpm_token
WHERE 1 <> 1
/

CREATE TABLE jbpm_variableinstance_bak AS
SELECT ID_,
  CLASS_,
  NAME_,
  CONVERTER_,
  TOKEN_,
  TOKENVARIABLEMAP_,
  PROCESSINSTANCE_,
  BYTEARRAYVALUE_,
  LONGVALUE_,
  STRINGVALUE_,
  DOUBLEVALUE_,
  STRINGIDCLASS_,
  LONGIDCLASS_,
  DATEVALUE_,
  TASKINSTANCE_
FROM jbpm_variableinstance
WHERE 1 <> 1
/

CREATE TABLE jbpm_timer_bak AS
SELECT ID_,
  NAME_,
  DUEDATE_,
  REPEAT_,
  TRANSITIONNAME_,
  EXCEPTION_,
  ISSUSPENDED_,
  ACTION_,
  TOKEN_,
  PROCESSINSTANCE_,
  TASKINSTANCE_,
  GRAPHELEMENTTYPE_,
  GRAPHELEMENT_
FROM jbpm_timer
WHERE 1 <> 1
/

CREATE TABLE jbpm_tokenvariablemap_bak AS
SELECT ID_, TOKEN_, CONTEXTINSTANCE_ FROM jbpm_tokenvariablemap WHERE 1 <> 1
/

CREATE TABLE jbpm_taskinstance_bak AS
SELECT ID_,
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
FROM jbpm_taskinstance
WHERE 1 <> 1
/

CREATE TABLE jbpm_swimlaneinstance_bak AS
SELECT ID_,
  NAME_,
  ACTORID_,
  SWIMLANE_,
  TASKMGMTINSTANCE_
FROM jbpm_swimlaneinstance
WHERE 1 <> 1
/

--Indexes for PK on backup tables
create index jbpm_proc_id_idx on jbpm_processinstance_bak(id_)
/

create index jbpm_mod_ins_id_idx on jbpm_moduleinstance_bak(id_)
/

create index jbpm_tok_id_idx on jbpm_token_bak(id_)
/

create index jbpm_var_id_idx on jbpm_variableinstance_bak(id_)
/

create index jbpm_timer_id_idx on jbpm_timer_bak(id_)
/

create index jbpm_tvm_id_idx on jbpm_tokenvariablemap_bak(id_)
/

create index jbpm_ti_id_idx on jbpm_taskinstance_bak(id_)
/

create index jbpm_si_id_idx on jbpm_swimlaneinstance_bak(id_)
/

--indexes needed for faster inserations based on processinstance_
create index jbpm_token_pi_idx on jbpm_token(processinstance_)
/

create index jbpm_timer_pi_idx on jbpm_timer(processinstance_)
/