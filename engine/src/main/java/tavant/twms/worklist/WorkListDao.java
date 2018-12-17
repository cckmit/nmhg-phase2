package tavant.twms.worklist;

import java.util.List;
import java.util.Map;

import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.orgmodel.User;

public interface WorkListDao extends BaseWorkListDao {

    /**
	 * Method to retrieve the List of TaskInstances and the Total count of the
	 * task instances for a particular user.
	 *
	 * @param criteria
	 * @return WorkList - The worklist object with the list of task instances
	 *         and the count.
	 */
	public WorkList getWorkList(WorkListCriteria criteria);

	/**
	 * Returns the tasks and the count for that particular task for a given
	 * actor.
	 * 
	 * @param criteria
	 * @return Map<Task, Long> - The task along with count.
	 */
	public Map<Task, Long> getAllTasks(final WorkListCriteria criteria,User loggedInUser);

	public List<TaskInstance> getNotShippedPartReturnTaskinstancesForClaim(
			final Long claimId);

	public List<TaskInstance> getAllFocClaimTaskinstancesToBeSubmitted(String duePeriodConstraint);

	public User getCurrentAssigneeForRecClaim(final Long recClaimId);
	
	public List<TaskInstance> getAllOpenTasksForClaim(final Claim claim);
	
	public List<TaskInstance> getAllOpenTasks(TaskCriteria criteria);
	
	public List<TaskInstance> getAllTasksForEmailTriggerring(TaskCriteria criteria,int daysForEmailTriggering);

    public List<String> getPartReturnInboxOrders();

	public List<TaskInstance> getAllOpenTasksInWpraInbox(TaskCriteria criteria);

	public List<TaskInstance> getAllOpenTasksInSupplierPartShippedInbox(
			TaskCriteria criteria);

	public List<TaskInstance> getAllRecoveryClaimTasksForEmailTriggerring(
			TaskCriteria criteria, int daysForEmailTriggering);

	public List<TaskInstance> getAllFinalResponseRecoveryClaimTasksForEmailTriggerring(
			TaskCriteria criteria, int daysForEmailTriggering);
	
	public TaskInstance getOpenOnHoldTaskForRecoveryClaim(final RecoveryClaim recoveryClaim);
}