--Purpose: Patch for removing returnMarkedBy logic
--Author:  Ramalakshmi P 
--Created On: 10 JU 09

delete from jbpm_variableinstance where name_ = 'returnMarkedBy'
/