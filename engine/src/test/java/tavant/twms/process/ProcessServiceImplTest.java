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
package tavant.twms.process;

import java.util.List;

import org.jbpm.graph.exe.ProcessInstance;

import tavant.twms.infra.ProcessDeployableTestCase;
import tavant.twms.infra.ProcessVariables;

public class ProcessServiceImplTest extends ProcessDeployableTestCase {

    private static final String SAMPLE_PROCESS 
        = "SampleProcess.xml";

    private ProcessService processService;

    protected void setUpInTxnRollbackOnFailure() throws Exception {
        deployProcess(SAMPLE_PROCESS);
    }

    public void testStartProcess() {
        ProcessVariables var = new ProcessVariables();
        var.setVariable("key1", "ProServ1");
        var.setVariable("key2", "ProServ2");
        var.setVariable("key3", "ProServ3");
        List<ProcessInstance> processes = processService.findAllProcessesByName("SampleProcess");
        int initialCount = processes.size();
        ProcessInstance instance = processService.startProcess("SampleProcess", var);
        instance = processService.findProcess(instance.getId());
        assertEquals("SampleProcess", instance.getProcessDefinition().getName());
        processes = processService.findAllProcessesByName("SampleProcess");
        int finalCount = processes.size();
        assertTrue((finalCount - initialCount) == 1);
        assertEquals("ProServ1", instance.getContextInstance().getVariable("key1"));
        assertEquals("ProServ2", instance.getContextInstance().getVariable("key2"));
        assertEquals("ProServ3", instance.getContextInstance().getVariable("key3"));
    }

    public void testStartProcessWithNull() {
        try {
            processService.startProcess("SampleProcess", null);
            fail("Expected to get IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            processService.startProcess(null, new ProcessVariables());
            fail("Expected to get IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }    

    public void testfindAllProcesses() {
        List<ProcessInstance> processes = processService.findAllProcessesByName("SampleProcess");
        assertTrue(processes.size() == 0);
        processService.startProcess("SampleProcess", new ProcessVariables());
        processService.startProcess("SampleProcess", new ProcessVariables());
        processService.startProcess("SampleProcess", new ProcessVariables());
        processService.startProcess("SampleProcess", new ProcessVariables());
        processes = processService.findAllProcessesByName("SampleProcess");
        assertTrue(processes.size() == 4);
    }

    public void testfindAllProcessesWithNull() {
        try {
            processService.findAllProcessesByName(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
}
