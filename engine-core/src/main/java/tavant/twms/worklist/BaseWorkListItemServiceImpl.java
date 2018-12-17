package tavant.twms.worklist;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.springframework.util.Assert;
import org.springmodules.workflow.jbpm31.JbpmCallback;
import org.springmodules.workflow.jbpm31.JbpmTemplate;

public class BaseWorkListItemServiceImpl implements BaseWorkListItemService {

    private static final Logger logger = Logger.getLogger(BaseWorkListItemService.class);
    private static final String ALL_OPEN_TASKS_WITH_NAME = "allOpenTasksForTaskName";

    protected JbpmTemplate jbpmTemplate;

    public TaskInstance findTask(final Long id) {
        Assert.notNull(id, "Cannot load TaskInstane with null identifier");
        return (TaskInstance) this.jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) throws JbpmException {
                return context.getTaskMgmtSession().loadTaskInstance(id);
            }

        });
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findTasks(final List ids) {
        Assert.notNull(ids, "Cannot load TaskInstance with null identifier");
        return (List<TaskInstance>) this.jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) throws JbpmException {
                return context.getTaskMgmtSession().findTaskInstancesByIds(ids);
            }

        });
    }

    public void endTaskWithTransition(final TaskInstance taskInstance, final String transition) {
        Assert.notNull(taskInstance, "TaskInstance cannot be null");
        Assert.notNull(transition, "Transition to be taken cannot be null");
        fixProxyVariableInTaskInstance(taskInstance);
        this.jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) throws JbpmException {
                taskInstance.end(transition);
                context.save(taskInstance);
                return null;
            }

        });
        if (logger.isDebugEnabled()) {
            logger.debug("Ended Task[" + taskInstance.getName()
                    + "] with transition [" + transition + "]");
        }
    }

    public void endAllTasksWithTransition(final List<TaskInstance> taskInstances, final String transition) {
        this.jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) throws JbpmException {
                for (TaskInstance taskInstance : taskInstances) {
                    fixProxyVariableInTaskInstance(taskInstance);
                    taskInstance.end(transition);
                    context.save(taskInstance);
                }
                return null;
            }

        });
    }

    public void endAllTasksWithTransition(final List<TaskInstance> taskInstances) {
        this.jbpmTemplate.execute(new JbpmCallback() {

            public Object doInJbpm(JbpmContext context) throws JbpmException {
                for (TaskInstance taskInstance : taskInstances) {
                    fixProxyVariableInTaskInstance(taskInstance);
                    taskInstance.end(taskInstance.getVariable("transition")
                            .toString());
                    context.save(taskInstance);
                }
                return null;
            }

        });
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
                if (logger.isDebugEnabled()) {
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
                        if (logger.isDebugEnabled()) {
                            logger.debug("Number of tasks for the token is ["
                                    + tasks.size() + "]");
                        }
                    }
                    // Pick the first task...assuming that we will always have
                    // one task.
                    TaskInstance currentTaskInstance = tasks.iterator().next();
                    currentTaskInstance.setActorId(userName);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Task[" + currentTaskInstance.getName() + "] is reassigned to [" + userName + "]");
                    }
                } else {
                    if (logger.isDebugEnabled()) {
                        logger
                                .debug("After taking the Transition["
                                        + transition
                                        + "] on Task["
                                        + taskInstance.getName()
                                        + "], no active Tasks could be found to reassign to ["
                                        + userName + "]");
                    }
                    handleReassignmentIfMoreThanOneTaskIsOpen(taskMgmt, userName);

                }
                context.save(taskInstance);
                return null;
            }

        });
    }

    protected void handleReassignmentIfMoreThanOneTaskIsOpen(TaskMgmtInstance tmi, String username) {
    }

    @SuppressWarnings("unchecked")
    public List<TaskInstance> getTaskInstancesAtTaskName(String taskName) {
        return this.jbpmTemplate.getHibernateTemplate()
                .findByNamedQueryAndNamedParam(ALL_OPEN_TASKS_WITH_NAME,
                        new String[]{"taskName"}, new Object[]{taskName});
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

    protected void fixProxyVariableInTaskInstance(TaskInstance taskInstance) {
    }

    public JbpmTemplate getJbpmTemplate() {
        return this.jbpmTemplate;
    }

    public void setJbpmTemplate(JbpmTemplate jbpmTemplate) {
        this.jbpmTemplate = jbpmTemplate;
    }
}
