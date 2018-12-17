package tavant.twms.jbpm.nodes;

import java.util.Collection;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import tavant.twms.infra.ProcessDeployableTestCase;

public class EndTasksJoinTest extends ProcessDeployableTestCase {

    ProcessInstance processInstance;

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        ProcessDefinition def = deployProcess("end-tasks-join-process.xml");
        processInstance = new ProcessInstance(def);
    }

    @SuppressWarnings("unchecked")
    public void testCancelJoin() {
        processInstance.signal();
        assertFalse(processInstance.hasEnded());
        Token rootToken = processInstance.getRootToken();
        assertEquals("SingleTokenFork", rootToken.getNode().getName());
        Token makeSaleToken = rootToken.findToken("makesale");
        Token monitorSaleToken = rootToken.findToken("monitorsale");
        assertNotNull(makeSaleToken);
        assertNotNull(monitorSaleToken);
        TaskMgmtInstance taskMgmtInstance = processInstance.getTaskMgmtInstance();

        Collection<TaskInstance> tasks = taskMgmtInstance.getUnfinishedTasks(makeSaleToken);
        assertEquals(1, tasks.size());
        TaskInstance makeSaleTask = tasks.iterator().next();
        assertEquals("buch", makeSaleTask.getActorId());

        tasks = taskMgmtInstance.getUnfinishedTasks(monitorSaleToken);
        assertEquals(1, tasks.size());
        TaskInstance monitorSaleTask = tasks.iterator().next();
        assertEquals("mark", monitorSaleTask.getActorId());

        makeSaleTask.setVariable("sale", "Done");
        makeSaleTask.end();

        assertTrue(processInstance.hasEnded());
        assertEquals("Done", processInstance.getContextInstance().getVariable("sale"));
    }

}
