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
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.TransitionTaken;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.worklist.WorkListItemService;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for dealing with Task View object.
 * 
 */
public class TaskViewServiceImpl implements TaskViewService {

    WorkListItemService workListItemService;

    ClaimService claimService;
    
    private SecurityHelper securityHelper;
    
    private ClaimProcessService claimProcessService;
    
    public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}
    
    public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}


    /**
     * Given the task instance id, it creates the Task View object. Task view
     * object holds the claim object and, transition names of the task
     */
    public TaskView getTaskView(Long taskInstanceId) {
        TaskInstance taskInstance = workListItemService.findTask(taskInstanceId);
        TaskView taskView = new TaskView(taskInstance);
        return taskView;
    }
    
    @Deprecated
    public List<TaskView> getTaskViews(List taskInstanceIds) {
        List<TaskInstance> taskInstances = workListItemService.findTasks(taskInstanceIds);
        List<TaskView> taskViews = new ArrayList<TaskView>();
        for (TaskInstance taskInstance : taskInstances) {
            taskViews.add(new TaskView(taskInstance));
        }
        return taskViews;
    }
    
    public List<TaskInstance> getTaskInstances(List taskInstanceIds) {
        return workListItemService.findTasks(taskInstanceIds);        
    }
    
    /**
     * Ends the task instance for the given transition. Also handles the merging
     * of the claim object from the view with the claim in the database.
     */    
	public void submitTaskView(TaskView taskView) {
		if (taskView.getTakenTransition() != null && "Deny".equalsIgnoreCase(taskView.getTakenTransition())
				&& taskView.getClaim().getItemReference().isSerialized()) {
			Claim claimToBeReopened = this.claimService.reopenClaimForLaborRndUpOnClaimDenial(taskView.getClaim());		
			if (claimToBeReopened != null) {
				try {
					this.claimService.updatePaymentInformation(claimToBeReopened);
				} catch (PaymentCalculationException e) {
					e.printStackTrace();
				}
                claimToBeReopened.setInternalComment("Claim re-opened to adjust minimum labor.");
                claimToBeReopened.setExternalComment("Claim re-opened to adjust minimum labor.");
				this.claimProcessService.startClaimProcessingForReopenClaims(claimToBeReopened,WorkflowConstants.REOPEN);
				TaskInstance taskInstance = this.workListItemService.findTaskForClaimWithTaskName(claimToBeReopened.getId(), "Processor Review");
				this.workListItemService.endTaskWithReassignment(taskInstance, "System Accept", securityHelper
						.getLoggedInUser().getName());
				
			}
		}
		TaskInstance taskInstance = taskView.getTask();
		if (StringUtils.hasText(taskView.getAssignTo())) {
            //Sometime we are getting value for transfer to , putting a hack to reassign it to logged user only , if block should not execute in ideal case
            if(TransitionTaken.HOLD.getTransitionTaken().equalsIgnoreCase(taskView.getTakenTransition())){
                workListItemService.endTaskWithReassignment(taskInstance, taskView.getTakenTransition(), getSecurityHelper().getLoggedInUser().getName());
            } else{
			workListItemService.endTaskWithReassignment(taskInstance, taskView.getTakenTransition(), taskView
					.getAssignTo());
            }
		} else {
			workListItemService.endTaskWithTransition(taskInstance, taskView.getTakenTransition());
		}
	}
    
    public void submitAllTaskInstances(List<TaskInstance> taskInstances, String transition) {                
        workListItemService.endAllTasksWithTransition(taskInstances, transition);
    }
    
    public void submitAllTaskInstances(List<TaskInstance> taskInstances) {
        workListItemService.endAllTasksWithTransition(taskInstances);
    }    
    
    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    /**
     * @param workListItemService the workListItemService to set
     */
    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

	public ClaimProcessService getClaimProcessService() {
		return claimProcessService;
	}

	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}
    
    
    
    

}
