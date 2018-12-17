/**
 *
 */
package tavant.twms.external;

import org.apache.log4j.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.db.LoggingSession;
import org.jbpm.graph.log.TransitionLog;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.worklist.WorkListItemService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kannan.ekanath
 *
 */
public class WorkflowDebugAction extends TwmsActionSupport {

    private static final Logger logger = Logger.getLogger(WorkflowDebugAction.class);

    private Long id;

    private WorkListItemService workListItemService;

    private LoggingSession loggingSession;

    private JbpmConfiguration jbpmConfiguration;

    private String taskType;

    private List<TaskInstance> tasks;

    @Override
    public String execute() {
        if(logger.isDebugEnabled()) {
            logger.debug("Trying to debug claim [" + this.id + "]");
        }
        Assert.notNull(this.id, "Provide a claim id");
        if("all".equals(this.taskType)) {
        	this.tasks = this.workListItemService.findAllTasksForClaim(this.id);
        } else {
        	this.tasks = this.workListItemService.findAllOpenTasksForClaim(this.id);
        }
        if(logger.isDebugEnabled())
        {
            logger.debug("Found tasks [" + this.tasks + "]");
        }
        return SUCCESS;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public List<TransitionLog> getTransitionLogsForToken(String tokenId) {
        List<TransitionLog> transitionLogs = new ArrayList<TransitionLog>();
        List<ProcessLog> logs = getLoggingSession().findLogsByToken(new Long(tokenId));
        for(ProcessLog log: logs){
            if(log instanceof TransitionLog) {
                transitionLogs.add((TransitionLog) log);
            }
        }
        return transitionLogs;
    }

    public LoggingSession getLoggingSession() {
        if (this.loggingSession == null) {
            this.loggingSession = this.jbpmConfiguration.createJbpmContext().getLoggingSession();
        }
        return this.loggingSession;
    }

    public void setLoggingSession(LoggingSession loggingSession) {
        this.loggingSession = loggingSession;
    }

    public List<TaskInstance> getTasks() {
        return this.tasks;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public void setJbpmConfiguration(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

	public final void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public final String getTaskType() {
		return this.taskType;
	}


}
