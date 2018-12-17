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
package tavant.twms.web.actions;

import org.jbpm.taskmgmt.exe.TaskInstance;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;

/**
 * @author kamal.govindraj
 *
 */
public class TaskWrapper{
    
    protected TaskInstance task;
    
    public TaskWrapper(TaskInstance task){
        this.task = task;
    }
    public Claim getClaim(){
        return (Claim) task.getVariable("claim");
    }
    public OEMPartReplaced getPart(){
        return (OEMPartReplaced) task.getVariable("part");
    }
    public long getId(){
        return task.getId();
    }
    
    public TaskInstance getTask() {
        return this.task;
    }
    
    public void setTask(TaskInstance task) {
        this.task = task;
    }
}
