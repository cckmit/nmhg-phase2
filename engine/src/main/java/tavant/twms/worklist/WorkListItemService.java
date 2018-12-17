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
package tavant.twms.worklist;

import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.orgmodel.User;

/**
 * @author vineeth.varghese
 * @date Sep 2, 2006
 */
@Transactional(readOnly = true)
public interface WorkListItemService extends BaseWorkListItemService {

    public TaskInstance findTaskForClaimWithTaskName(Long id, String taskName);

	public List<TaskInstance> findAllOpenTasksForClaim(Long id);

	public List<TaskInstance> findAllTasksForClaim(Long id);

	public TaskInstance findTaskForDuePart(Long partId, String taskName);

	public boolean doesTaskExistForDuePart(Long partId, String taskName);

	public boolean doesTaskExistForSupplierPart(Long partId, String taskName);

    @Transactional(readOnly = false)
    public void cancelAllOpenTasksForClaim(Long claimId);

    public int findCountOfPROpenTasksForClaim(Long id);

	public TaskInstance findTaskForSupplierPart(Long partId, String taskName);
	
	public TaskInstance findTaskForRecClaimWithTaskName(Long id, String taskName);

	public TaskInstance findTaskForRecClaimsWithTaskNames(Long id,
			List taskNames);
	
	public TaskInstance findTaskForClaimForPartReturn(Long id);
	
	public TaskInstance findTaskForClaimWithTaskNames(Long id, List<String> taskNames);
   
	public List<TaskInstance> findAllClaimSubmissionOpenTasksForClaim(Long id);
	
	public List<TaskInstance> getNotShippedPartReturnTaskinstancesForClaim(Long claimId);
	
	public List<TaskInstance> getAllFocClaimsForAutoSubmit(String duePeriodConstraint);
	
	public List<TaskInstance> getAllOpenTasks(TaskCriteria criteria);
	
	public List <TaskInstance> getAllTasksForPendingOverDue(TaskCriteria criteria,int daysForEmailTriggering);
	
	//E-mail Notification merge Start
	/**
     * Fetches all Users associated with the claim
     * @param id
     * @return
     * List<String> userName of all actors
     */
    public List<User> findAllActorForClaim(Long id);
    
    /**
     * Fetches all Users associated with the part return
     * 
     * @param id
     * @return
     */
    public List<User> findAllActorForPartReturn(Long id);
  //E-mail Notification merge End

    public List<User> findAllParticipantsForClaim(Long id);
    
    public List<TaskInstance> getAllOpenTasksInWpraInbox(TaskCriteria criteria);

	public List<TaskInstance> getAllOpenTasksInSupplierPartShippedInbox(
			TaskCriteria criteria);

	public List<TaskInstance> getAllTasksForSupplierResponsePeriod(
			TaskCriteria criteriaForSupplierResponsePeriod,
			int daysForEmailTriggering);

	public List<TaskInstance> getAllTasksForSupplierFinalResponsePeriod(
			TaskCriteria criteriaForSupplierFinalResponse,
			int daysForEmailTriggering);
    
}
