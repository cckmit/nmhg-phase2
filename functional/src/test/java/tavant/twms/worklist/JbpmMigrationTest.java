package tavant.twms.worklist;





import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.management.Query;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.instance.migration.Migrator;
import org.jbpm.scheduler.exe.Timer;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.process.ProcessDefinitionService;
import tavant.twms.process.ProcessService;

public class JbpmMigrationTest extends IntegrationTestCase {
	
	WorkListItemService workListItemService;

    ProcessDefinitionService processDefinitionService;

    ProcessService processService;


//	public void testProcessDefinition() throws Exception {
//		
//		for(int i=0 ; i < 5 ; i ++){
//		ProcessVariables processVariables = new ProcessVariables();
//		processVariables.setVariable("testVar", new Integer(1));
//		ProcessInstance processInstance = processService.startProcess("JbpmMigrationTest", processVariables);
//		getSession().save(processInstance);
//		}
//		getSession().getTransaction().commit();
//		
//	}
	
//	public void testWorkflow() throws Exception{
//		TaskInstance taskInstance = workListItemService.findTask(1119892562320L);
//		taskInstance.end("PreFirstNode2ToFirstNode");
//		getSession().saveOrUpdate(taskInstance.getTaskMgmtInstance().getProcessInstance());
//		getSession().getTransaction().commit();
//		
//		
//	}
    
    public void testMigrationTool(){
    	JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
    	JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
    	Migrator migrator = new Migrator("ClaimSubmission", jbpmContext, "org.jbpm.instance.migration.JbpmMigrationTest");
    	TaskInstance taskInstance = workListItemService.findTask(1119892108300L);
    	ProcessInstance oldProcessInstance = taskInstance.getTaskMgmtInstance().getProcessInstance();
    	
    	ProcessInstance newProcessInstance = migrator.migrate(oldProcessInstance);

    	List<Timer> newTimers = migrator.migrate(getSession(),oldProcessInstance);
    	getSession().saveOrUpdate(newProcessInstance);
    	getSession().saveOrUpdate(oldProcessInstance);
    	
    	System.out.println("No exception");
    	getSession().getTransaction().commit();
    }
    	
    @Override
    public String[] getConfigLocations() {
        return new String[] { "classpath:integration-test-context.xml",
                "classpath*:/app-context.xml" };
    }

	public ProcessDefinitionService getProcessDefinitionService() {
		return processDefinitionService;
	}

	public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
		this.processDefinitionService = processDefinitionService;
	}

	public ProcessService getProcessService() {
		return processService;
	}

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

	public WorkListItemService getWorkListItemService() {
		return workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

}
