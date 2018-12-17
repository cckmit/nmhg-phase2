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

import java.io.InputStream;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;

import tavant.twms.infra.EngineRepositoryTestCase;

/**
 * @author kamal.govindraj
 * 
 */
public class ProcessDefinitionServiceTest extends EngineRepositoryTestCase {

    private static final String SAMPLE_PROCESS = "SampleProcess.xml";

    private static final String PAYMENT_POLICY_PROCESS = "ClaimPaymentServiceSubProcess.xml";
    
    private static final String PART_RETURN_FLOW = "PartReturnFlowTest.xml";

    ProcessDefinitionService processDefinitionService;

    public void testDeploy() {
        deploy(PAYMENT_POLICY_PROCESS);

        ProcessDefinition sampleProcess = processDefinitionService.find("PolicyAndPaymentComputationProcess");
        assertNotNull(sampleProcess);
        assertEquals(1, sampleProcess.getVersion());
        assertTrue(sampleProcess.hasNode("ComputePolicy"));
        assertTrue(sampleProcess.hasNode("Start"));
        assertTrue(sampleProcess.hasNode("End"));
    }

    public void testDeployPassingNull() {
        try {
            processDefinitionService.deploy(null);
            fail("Should allow deploying null process");
        } catch (IllegalArgumentException e) {

        }
    }

    private void deploy(String processXmlFileName) {

        InputStream prodessDefinitionStream = this.getClass().getResourceAsStream(processXmlFileName);

        final ProcessDefinition processDefnition = ProcessDefinition
                .parseXmlInputStream(prodessDefinitionStream);

        processDefinitionService.deploy(processDefnition);
    }

    public void testDeploySameProcessTwice() {
        deploy(PAYMENT_POLICY_PROCESS);
        deploy(PAYMENT_POLICY_PROCESS);

        ProcessDefinition sampleProcess = processDefinitionService.find("PolicyAndPaymentComputationProcess");

        assertNotNull(sampleProcess);
        assertEquals(2, sampleProcess.getVersion());
        assertTrue(sampleProcess.hasNode("ComputePolicy"));
        assertTrue(sampleProcess.hasNode("Start"));
        assertTrue(sampleProcess.hasNode("End"));
    }

    public void testUndeployNonExistentProcess() {
        try {
            processDefinitionService.undeploy("NON EXISTANT PROCESS");
            fail("Undeploying non existant process shouldn't succeed");
        } catch (RuntimeException e) {

        }
    }

    public void testFindNonExistantProcess() {
        assertNull(processDefinitionService.find("NON EXISTANT PROCESS"));
    }

    public void testFindAll() {
        deploy(PAYMENT_POLICY_PROCESS);
        deploy(SAMPLE_PROCESS);
        List<ProcessDefinition> allProcesses = processDefinitionService.findAll();
        assertEquals(2, allProcesses.size());
        assertTrue(containsProcess(allProcesses, "PolicyAndPaymentComputationProcess"));
        assertTrue(containsProcess(allProcesses, "SampleProcess"));
    }

    public void testFindAllMultipleVersionsOfSameProcess() {
        deploy(PAYMENT_POLICY_PROCESS);
        deploy(PAYMENT_POLICY_PROCESS);
        List<ProcessDefinition> allProcesses = processDefinitionService.findAll();
        assertEquals(1, allProcesses.size());
        assertTrue(containsProcess(allProcesses, "PolicyAndPaymentComputationProcess"));
    }

    public void testFindAllBeforeDeployingAnyProcess() {
        List<ProcessDefinition> allProcesses = processDefinitionService.findAll();
        assertEquals(0, allProcesses.size());
    }

    private boolean containsProcess(List<ProcessDefinition> allProcesses, String processName) {
        for (ProcessDefinition definition : allProcesses) {
            if (definition.getName().equals(processName)) {
                return true;
            }
        }
        return false;
    }

    public void testUndeployProcessDefinition() {

        deploy(PAYMENT_POLICY_PROCESS);
        ProcessDefinition sampleProcess = processDefinitionService.find("PolicyAndPaymentComputationProcess");
        assertNotNull(sampleProcess);

        processDefinitionService.undeploy("PolicyAndPaymentComputationProcess");
        assertNull(processDefinitionService.find("PolicyAndPaymentComputationProcess"));

    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }
    
    public void testPartReturnDeployment() {
        deploy(PART_RETURN_FLOW);
        List<ProcessDefinition> allProcesses = processDefinitionService.findAll();
        assertEquals(1, allProcesses.size());
        assertTrue(containsProcess(allProcesses, "PartsReturn"));
        
        processDefinitionService.undeploy("PartsReturn");
        assertNull(processDefinitionService.find("PartsReturn"));
    }

}
