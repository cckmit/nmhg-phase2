/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.integration.layer.quartz.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.domain.upload.controller.DataUploadController;
import tavant.twms.jbpm.infra.BeanLocator;

/**
 * @author jhulfikar.ali
 *
 */
public class UploadDataJob extends QuartzJobBean implements StatefulJob {

	DataUploadController dataUploadController;

	private final BeanLocator beanLocator = new BeanLocator();
	
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			dataUploadController = (DataUploadController) this.beanLocator
			.lookupBean("dataUploadController");
			
			dataUploadController.uploadData();
		} catch (Exception exception) {
			throw new JobExecutionException(exception);
		}
	}

}
