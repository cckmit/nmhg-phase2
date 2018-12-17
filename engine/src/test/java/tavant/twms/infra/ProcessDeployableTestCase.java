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
package tavant.twms.infra;

import java.io.InputStream;

import org.jbpm.graph.def.ProcessDefinition;

import tavant.twms.process.ProcessDefinitionService;

/**
 * @author vineeth.varghese
 * @date Sep 2, 2006
 */

/**
 * We can rename this class later but deploying a process seems to be a very
 * common need in test cases of the engine. This utility test case will have 
 * method for deployment.
 * <b>NO OTHER SERVICES OR METHODS SHOULD BE ADDED HERE</b>
 */
public class ProcessDeployableTestCase extends EngineRepositoryTestCase {
    
    ProcessDefinitionService processDefinitionService;

    /**
     * @param processDefinitionService the processDefinitionService to set
     */
    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }
    
    public ProcessDefinition deployProcess(String processFile) {
        InputStream ip = this.getClass().getResourceAsStream(processFile);
        assertNotNull("Couldnot load resource [" + processFile + "]", ip);
        ProcessDefinition def = ProcessDefinition.parseXmlInputStream(ip);
        processDefinitionService.deploy(def);
        return def;
    }
    
    

}
