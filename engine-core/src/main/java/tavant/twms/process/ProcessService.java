/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.process;

import java.util.Date;
import java.util.List;

import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.scheduler.exe.Timer;
import org.springframework.transaction.annotation.Transactional;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.infra.ProcessVariables;

/**
 * @author vineeth.varghese
 * @date Jul 13, 2006
 */
@Transactional(readOnly=true)
public interface ProcessService {

    @Transactional(readOnly=false, noRollbackFor=Exception.class)
    public ProcessInstance startProcess(String processName, ProcessVariables processVariables);
    
    @Transactional(readOnly=false, noRollbackFor=Exception.class)
    public ProcessInstance startProcessWithTransition(String processName, 
            ProcessVariables processVariables, String transitionName);
   
    public List<ProcessInstance> findAllProcessesByName(String processName);

    public ProcessInstance findProcess(Long processId);
    
    public void updateDueDateForPartReturn(Timer timer, CalendarDate updatedDueDate);

}
