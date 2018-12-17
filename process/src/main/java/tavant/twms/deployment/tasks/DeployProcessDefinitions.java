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
package tavant.twms.deployment.tasks;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tools.ant.BuildException;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import tavant.twms.process.ProcessDefinitionService;


public class DeployProcessDefinitions extends DefaultTask {
	private static String PROCESS_DEFINITION_ADMIN = "processDefinitionService";

	private String process;

	public void throwErrorOnInvalidInputs() {
		super.throwErrorOnInvalidInputs();
		if(!StringUtils.hasText(process))
			throw new BuildException("Failure: Need to have either property 'process' ");
	}

	public void perform() {
		try {
			String[] processFiles = StringUtils.tokenizeToStringArray(process, ",");
			for (String process : processFiles) {
				Resource resource = new ClassPathResource(process);
				deploy(resource.getInputStream());
			}
		} catch (Exception e) {
			throw new BuildException( "Failure: Could not deploy process archives : "
							+ e.getMessage(), e);
		}
	}

	void deploy(InputStream processStream) throws IOException, FileNotFoundException {		
		try {
			ProcessDefinition processDefinition = 
				ProcessDefinition.parseXmlInputStream(processStream);
			ProcessDefinitionService processDefService = getProcessDefintionService();
			processDefService.deploy(processDefinition);

		} finally {
			processStream.close();
		}
	}

	private ProcessDefinitionService getProcessDefintionService() {
		return (ProcessDefinitionService) applicationContext.getBean(PROCESS_DEFINITION_ADMIN);
	}
	
	public void setProcess(String process) {
		this.process = process;
	}
}