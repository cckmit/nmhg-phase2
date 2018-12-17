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
package tavant.twms.web.xforms;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly=true)
public interface TaskViewService {

	public TaskView getTaskView(Long taskInstanceId);
        
    @Deprecated
    public List<TaskView> getTaskViews(List taskInstanceIds);

    public List<TaskInstance> getTaskInstances(List taskInstanceIds);

    @Transactional(readOnly=false)
	public void submitTaskView(TaskView taskView);
        
    @Transactional(readOnly=false)
    public void submitAllTaskInstances(List<TaskInstance> taskInstances, String transition);

    @Transactional(readOnly=false)
    public void submitAllTaskInstances(List<TaskInstance> taskInstances);

}
