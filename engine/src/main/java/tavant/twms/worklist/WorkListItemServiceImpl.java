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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.springframework.util.Assert;
import org.springmodules.workflow.jbpm31.JbpmCallback;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SecurityHelper;

/**
 * @author vineeth.varghese
 * @date Sep 2, 2006
 */
public class WorkListItemServiceImpl extends BaseWorkListItemServiceImpl implements WorkListItemService {

	private static final Logger logger = Logger
			.getLogger(WorkListItemServiceImpl.class);

	private static final String TASK_INSTANCE_FOR_CLAIM = "taskInstanceForClaim";

	private static final String TASK_INSTANCE_FOR_REC_CLAIM = "taskInstanceForRecClaim";

	private static final String TASK_INSTANCES_FOR_REC_CLAIMS_IN_STATES = "taskInstanceForRecClaimInMultipleStates";

	private static final String TASK_INSTANCE_FOR_DUE_PART = "taskInstanceForDuePart";

	private static final String TASK_INSTANCE_FOR_SUPPLIER_PART = "taskInstanceForSupplierPart";
	
	private static final String ALL_OPEN_TASKS_FOR_CLAIM = "allOpenTaskInstancesForClaim";

	private static final String ALL_TASKS_FOR_CLAIM = "allTaskInstancesForClaim";

	private static final String OPEN_PRTASKS_QUERY = "openProcessorReviewTasksForClaim";
	
	private static final String TASK_INSTANCES_IN_FOR_CLAIM = "taskInstancesInForClaim";
	
    private static final String ALL_CLAIM_SUBMISSION_OPEN_TASKS_FOR_CLAIM="allClaimSubmissionOpenTaskInstancesForClaim";
	
	private SecurityHelper securityHelper;

  //E-mail Notification merge Start
    private static final String ALL_ACTORS_FOR_CLAIM = "allActorsForClaim";
    
    private static final String ALL_CLAIM_PARTICIPANTS = "allClaimParticipants";
    
    private static final String ALL_CURRENT_ACTORS_FOR_CLAIM = "allCurrentActorsForClaim";
    
    private static final String CURRENT_ACTOR_FOR_CLAIM = "currentActorForClaim";
    
    private static final String ALL_ACTORS_FOR_PART_RETURN = "allActorsForPartReturn";
  //E-mail Notification merge end

    private WorkListDao workListDao;
    
    private ConfigParamService configParamService;

	// TODO: This is hack. We need to figure out why this is happening
	protected void fixProxyVariableInTaskInstance(TaskInstance taskInstance) {
		ProcessVariables var = ProcessVariables
				.createProcessVariablesFromTask(taskInstance);
		Claim claim = getClaimOutOfProxy((Claim) var.getVariable("claim"));
		
		//this is a work around. interceptor not working we need to figure out why
		// Claim would be null incase of a recovery claim flow.
		if(claim != null){
			claim.setLastUpdatedOnDate(new Date());
			claim.setLastUpdatedBy(securityHelper.getLoggedInUser());		
		}
		
		taskInstance.getContextInstance().setVariable(ProcessVariables.CLAIM,
				claim);
	}

	Claim getClaimOutOfProxy(Claim claim) {
		if (claim instanceof HibernateProxy) {
			claim = (Claim) ((HibernateProxy) claim)
					.getHibernateLazyInitializer().getImplementation();
		}
		return claim;
	}

	public void endTaskWithTransition(final TaskInstance taskInstance,
			final String transition) {
		// Hack for ESESA-887
		// Put on Hold is the only action that will leave the claim with the user. System User check to ensure it is not through a task.
        if(taskInstance == null){
            return;
        }
		User loggedInUser = securityHelper.getLoggedInUser();
		String userLogin = loggedInUser.getName();
		if("Hold".equalsIgnoreCase(transition)
				&& !SecurityHelper.SYSTEM_USER_NAME.equalsIgnoreCase(userLogin)){
			if(!userLogin.equalsIgnoreCase(taskInstance.getActorId())){
				endTaskWithReassignment(taskInstance, transition, userLogin);
				return;
			}
		}
		super.endTaskWithTransition(taskInstance, transition);
	}

    protected void handleReassignmentIfMoreThanOneTaskIsOpen(TaskMgmtInstance tmi, String username) {
        /**
         * Logic to handle re-assigning actorId if claim is sent for Advice Request more than 1 time
         * TSESA-64
         */
        Collection taskInstances =  tmi.getTaskInstances();
        if(taskInstances != null)
        {
            Iterator iter = taskInstances.iterator();
            do
            {
                if(!iter.hasNext())
                    break;
                TaskInstance task = (TaskInstance)iter.next();
                if(!task.hasEnded() &&
                        (task.getName().equals("Advice Request") || task.getName().equals("CP Review")))
                {
                    task.setActorId(username);
                    break;
                }
            } while(true);
        }
    }

	/*
	 * This hack is needed here because taskMgmt.getUnfinishedTasks(token)
	 * internally does an obj identity based equality. This hack seems to be
	 * getting popular in the workflow code so there should be some better
	 * solution to this problem - Vineeth
	 */
	private Token getToken(TaskInstance taskInstance) {
		Token token = taskInstance.getToken();
		if (token instanceof HibernateProxy) {
			token = (Token) ((HibernateProxy) token)
					.getHibernateLazyInitializer().getImplementation();
		}
		return token;
	}

	@SuppressWarnings("unchecked")
	public void endTaskWithReassignment(final TaskInstance taskInstance,
			final String transition, final String userName) {
		Assert.notNull(taskInstance, "TaskInstance cannot be null");
		Assert.notNull(transition, "Transition to be taken cannot be null");
		Assert.notNull(userName, "UserName/ActorName cannot be null");
		fixProxyVariableInTaskInstance(taskInstance);
		final Token token = getToken(taskInstance);
		this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {
				taskInstance.end(transition);
				if(logger.isDebugEnabled()){
	                                logger.debug("Ended Task[" + taskInstance.getName()
                                                + "] with transition [" + transition
                                                + "] now going to reassign new Task to user["
                                                + userName + "]");
				}
				TaskMgmtInstance taskMgmt = token.getProcessInstance()
						.getTaskMgmtInstance();
				Collection<TaskInstance> tasks = taskMgmt
						.getUnfinishedTasks(token);
				/*
				 * Reassignment assumes the following :- 1) All TaskNodes have
				 * only one task since only the first task from the collection
				 * is used. 2) When a Task is ended for reassignment the next
				 * task should be a human task(TaskNode). For e.g. if a task 'A'
				 * is ended with a transition 'payment' then the new Node to
				 * which the Token points to SHOULD be a TaskNode. If not we
				 * can't make the reassignment happen. So Task 'A' will be ended
				 * with the given transition and process will take its path from
				 * there.
				 */
				if (!tasks.isEmpty()) {
					if (tasks.size() > 1) {
					        if(logger.isDebugEnabled())
					        {
					            logger.debug("Number of tasks for the token is ["
                                                            + tasks.size() + "]");
					        }
					}
					// Pick the first task...assuming that we will always have
					// one task.
					TaskInstance currentTaskInstance = tasks.iterator().next();
					currentTaskInstance.setActorId(userName);
					if(logger.isDebugEnabled()) {
                                            logger.debug("Task[" + currentTaskInstance.getName()+ "] is reassigned to [" + userName + "]");
                                        }
				} else {
				        if(logger.isDebugEnabled())
				        {
        					logger
        							.debug("After taking the Transition["
        									+ transition
        									+ "] on Task["
        									+ taskInstance.getName()
        									+ "], no active Tasks could be found to reassign to ["
        									+ userName + "]");
				        }
				        /**
				         * Logic to handle re-assigning actorId if claim is sent for Advice Request more than 1 time
				         * TSESA-64
				         */
				        Collection taskInstances =  taskMgmt.getTaskInstances();
				        if(taskInstances != null)
				        {
				            Iterator iter = taskInstances.iterator();
				            do
				            {
				                if(!iter.hasNext())
				                    break;
				                TaskInstance task = (TaskInstance)iter.next();
				                Claim claim = (Claim) task.getVariable("claim");
				                User user = null;
				                if(!task.hasEnded() && 
				                		(task.getName().equals("Advice Request") || task.getName().equals("CP Review") || task.getName().equals(WorkflowConstants.REPLIES)
				                			|| task.getName().equals(WorkflowConstants.FORWARDED_EXTERNALLY) || task.getName().equals(WorkflowConstants.FORWARDED)
				                			|| task.getName().equals(WorkflowConstants.FORWARDED_INTERNALLY)))
				                {
				                	if(task.getName().equals(WorkflowConstants.FORWARDED)){
				                		user = claim.getFiledBy();
				                		task.setActorId(user.getUserType() != null && user.getUserType().equals(AdminConstants.ENTERPRISE_USER) ? 
				                				user.getBelongsToOrganizations().get(0).getId().toString() : user.getName());
				                	}
				                	else if(task.getName().equals(WorkflowConstants.FORWARDED_EXTERNALLY)
				                			||task.getName().equals(WorkflowConstants.ADVICE_REQUEST)){
				                		task.setActorId(userName);
				                	}
				                	else if(task.getName().equals(WorkflowConstants.FORWARDED_INTERNALLY)){
				                		task.setActorId(claim.getLastUpdatedBy().getName());
				                	}
				                	else{
				                		task.setActorId(userName);
				                		break;
				                	}
				                }	
				            } while(true);
				        }
				        
				}
				context.save(taskInstance);
				return null;
			}

		});
	}

    public void cancelAllOpenTasksForClaim(final Long claimId) {

        this.jbpmTemplate.execute(new JbpmCallback() {

			public Object doInJbpm(JbpmContext context) throws JbpmException {
                Set<ProcessInstance> processInstances = new HashSet<ProcessInstance>();
                List<TaskInstance> taskInstances = findAllOpenTasksForClaim(claimId);
                for (TaskInstance ti : taskInstances) {
                    processInstances.add(ti.getToken().getProcessInstance());
                    ti.setSignalling(false);
                    ti.cancel();
                    context.save(ti);
                }
                for (ProcessInstance pi : processInstances) {
                    if (!pi.hasEnded()) {
                        pi.end();
                    }
                }
                return null;
			}

		});
    }

	public TaskInstance findTaskForClaimWithTaskName(Long id, String taskName) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_CLAIM,
						new String[]{"claimId", "taskName"},
						new Object[]{id, taskName});
		Assert.state(taskInstances.size() == 1||taskInstances.isEmpty(),
				"More than one task instance with name [" + taskName
						+ "] for the claim with id [" + id + "]");
		if(taskInstances.isEmpty())
			return null;
		else{
			return (TaskInstance) taskInstances.get(0);
		}
		
	}
	
	
	public TaskInstance findTaskForClaimWithTaskNames(Long id, List<String> taskNames) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCES_IN_FOR_CLAIM,
						new String[]{"claimId", "taskNames"},
						new Object[]{id, taskNames});
		
		Assert.state(!taskInstances.isEmpty(),
				"No Task Instances are associated with the given claim");
		Assert.state(taskInstances.size() == 1,
				"More than one task instance with names [" + taskNames
						+ "] for the claim with id [" + id + "]");
		return (TaskInstance) taskInstances.get(0);
	}
	
	public TaskInstance findTaskForClaimForPartReturn(Long id) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_CLAIM,
						new String[]{"claimId", "taskName"},
						new Object[]{id, WorkflowConstants.ON_HOLD_FOR_PART_RETURN});
		if(!taskInstances.isEmpty()){
		return (TaskInstance) taskInstances.get(0);
		}else{
			return null;
		}
			
	}

	public TaskInstance findTaskForRecClaimWithTaskName(Long id, String taskName) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_REC_CLAIM,
				new String[]{"recClaimId", "taskName"},
				new Object[]{id, taskName});

		if(!taskInstances.isEmpty()){
			Assert.state(taskInstances.size() == 1,
					"More than one task instance with name [" + taskName
					+ "] for the claim with id [" + id + "]");
		}
		return !taskInstances.isEmpty()&&taskInstances.size()>0?(TaskInstance) taskInstances.get(0):null;
	}

	public TaskInstance findTaskForRecClaimsWithTaskNames(Long id,
			List taskNames) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(
						TASK_INSTANCES_FOR_REC_CLAIMS_IN_STATES,
						new String[]{"recClaimId", "taskNames"},
						new Object[]{id, taskNames});
		
		// We dont need to assert as we would only want to auto debit recovery claims
		// that are in that state. This is a mock. Hence no asserting.
//		Assert.state(!taskInstances.isEmpty(),
//				"No Task Instances are associated with the given claim");
//		Assert.state(taskInstances.size() == 1,
//				"More than one task instance with name [" + taskNames
//						+ "] for the claim with id [" + id + "]");
		if(taskInstances == null || taskInstances.isEmpty()){
			return null;
		}
		return (TaskInstance) taskInstances.get(0);
	}

	public TaskInstance findTaskForDuePart(Long partId, String taskName) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_DUE_PART,
						new String[]{"partId", "taskName"},
						new Object[]{partId, taskName});
		Assert.state(!taskInstances.isEmpty(),
				"No Task Instances are associated with the given claim");
		Assert.state(taskInstances.size() == 1,
				"More than one task instance with name [" + taskName
						+ "] for the part with id [" + partId + "]");
		return (TaskInstance) taskInstances.get(0);
	}

	public TaskInstance findTaskForSupplierPart(Long partId, String taskName) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_SUPPLIER_PART,
						new String[]{"partId", "taskName"},
						new Object[]{partId, taskName});
		Assert.state(!taskInstances.isEmpty(),
				"No Task Instances are associated with the given claim");
		Assert.state(taskInstances.size() == 1,
				"More than one task instance with name [" + taskName
						+ "] for the part with id [" + partId + "]");
		return (TaskInstance) taskInstances.get(0);
	}
	
	public boolean doesTaskExistForDuePart(Long partId, String taskName) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_DUE_PART,
						new String[]{"partId", "taskName"},
						new Object[]{partId, taskName});
		return !taskInstances.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllOpenTasksForClaim(Long id) {
		return this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(ALL_OPEN_TASKS_FOR_CLAIM,
						new String[]{"claimId"}, new Object[]{id});
	}

    @SuppressWarnings("unchecked")
	public List<TaskInstance> findAllClaimSubmissionOpenTasksForClaim(Long id) {
		return this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(ALL_CLAIM_SUBMISSION_OPEN_TASKS_FOR_CLAIM,
						new String[]{"claimId"}, new Object[]{id});
	}

    @SuppressWarnings("unchecked")
	public List<TaskInstance> findAllTasksForClaim(Long id) {
		return this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(ALL_TASKS_FOR_CLAIM,
						new String[]{"claimId"}, new Object[]{id});
	}
	
	@SuppressWarnings("unchecked")
	public int findCountOfPROpenTasksForClaim(Long id) {
		return this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(OPEN_PRTASKS_QUERY,
						new String[]{"claimId"}, new Object[]{id}).size();
	}

    public boolean doesTaskExistForSupplierPart(Long partId, String taskName) {
		List taskInstances = this.jbpmTemplate.getHibernateTemplate()
				.findByNamedQueryAndNamedParam(TASK_INSTANCE_FOR_SUPPLIER_PART,
						new String[]{"partId", "taskName"},
						new Object[]{partId, taskName});
		return !taskInstances.isEmpty();
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
	//E-mail Notification merge Start
	@SuppressWarnings("unchecked")
    public List<User> findAllActorForClaim(Long id) {
        return jbpmTemplate.getHibernateTemplate().findByNamedQueryAndNamedParam(
        		ALL_CURRENT_ACTORS_FOR_CLAIM, new String[] { "claimId"},
                new Object[] { id});
    }
	
	
	@SuppressWarnings("unchecked")
    public List<User> findAllActorForPartReturn(Long id) {
        return jbpmTemplate.getHibernateTemplate().findByNamedQueryAndNamedParam(
        		ALL_ACTORS_FOR_PART_RETURN, new String[] { "partReturnId"},
                new Object[] { id});
    }
	//E-mail Notification merge end
	
	@SuppressWarnings("unchecked")
    public List<User> findAllParticipantsForClaim(Long id) {
		List<User> actors = jbpmTemplate.getHibernateTemplate().findByNamedQueryAndNamedParam(
        		ALL_ACTORS_FOR_CLAIM, new String[] { "claimId"},
                new Object[] { id});
		List<User> participants = jbpmTemplate.getHibernateTemplate().findByNamedQueryAndNamedParam(
        		ALL_CLAIM_PARTICIPANTS, new String[] { "claimId"},
                new Object[] { id});
		for(Iterator<User> actorIterator = actors.iterator(); actorIterator.hasNext() ;) {
			User actor = actorIterator.next();
			for(User participant : participants) {
				if(participant.getId().longValue() == actor.getId().longValue()) {
					actorIterator.remove();
					break;
				}
			}
		}
		participants.addAll(actors);
		return participants;
    }
	
	public List<TaskInstance> getNotShippedPartReturnTaskinstancesForClaim(Long claimId) {
		return this.workListDao.getNotShippedPartReturnTaskinstancesForClaim(claimId);
	}

	public void setWorkListDao(WorkListDao workListDao) {
		this.workListDao = workListDao;
	}

	public List<TaskInstance> getAllFocClaimsForAutoSubmit(String duePeriodConstraint){
		return this.workListDao.getAllFocClaimTaskinstancesToBeSubmitted(duePeriodConstraint);
	}
	
	public List<TaskInstance> getAllOpenTasks(TaskCriteria criteria){
		criteria.setDaysBUMapConsideredForDenying(configParamService.getValuesForAllBUs(criteria.getConfigParam()));
		return this.workListDao.getAllOpenTasks(criteria);
	}
	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public List<TaskInstance> getAllTasksForPendingOverDue(TaskCriteria criteria,int daysForEmailTriggering) {
		criteria.setPendingOverDueDaysBUMapForEmail((configParamService.getValuesForAllBUs(criteria.getConfigParam())));
		return this.workListDao.getAllTasksForEmailTriggerring(criteria,daysForEmailTriggering);
	}
	
	public List<TaskInstance> getAllOpenTasksInWpraInbox(TaskCriteria criteria){
		criteria.setDaysBUMapConsideredForDenying(configParamService.getValuesForAllBUs(criteria.getConfigParam()));
		return this.workListDao.getAllOpenTasksInWpraInbox(criteria);
	}
	
	public List<TaskInstance> getAllOpenTasksInSupplierPartShippedInbox(TaskCriteria criteria){
		criteria.setDaysBUMapConsideredForDenying(configParamService.getValuesForAllBUs(criteria.getConfigParam()));
		return this.workListDao.getAllOpenTasksInSupplierPartShippedInbox(criteria);
	}
	
	public List<TaskInstance> getAllTasksForSupplierResponsePeriod(TaskCriteria criteria,int daysForEmailTriggering) {
		criteria.setPendingOverDueDaysBUMapForEmail((configParamService.getValuesForAllBUs(criteria.getConfigParam())));
		return this.workListDao.getAllRecoveryClaimTasksForEmailTriggerring(criteria,daysForEmailTriggering);
	}
	
	public List<TaskInstance> getAllTasksForSupplierFinalResponsePeriod(
			TaskCriteria criteria,
			int daysForEmailTriggering){
		criteria.setPendingOverDueDaysBUMapForEmail((configParamService.getValuesForAllBUs(criteria.getConfigParam())));
		return this.workListDao.getAllFinalResponseRecoveryClaimTasksForEmailTriggerring(criteria,daysForEmailTriggering);
	}
}
