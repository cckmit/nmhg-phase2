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
package tavant.twms.integration.layer.quartz.jobs;

import java.util.Calendar;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.integration.layer.component.global.CommonSyncProcessor;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SecurityHelper;

public class ItemSyncRetryTask extends QuartzJobBean implements StatefulJob {

    private static final Logger logger = Logger.getLogger(ItemSyncRetryTask.class);
    private final BeanLocator beanLocator = new BeanLocator();
    private CommonSyncProcessor commonSyncProcessor;
    private Integer maxNoOfRetries;
    private SyncTrackerService syncTrackerService;

    public Integer getMaxNoOfRetries() {
        return maxNoOfRetries;
    }

    public void setMaxNoOfRetries(Integer maxNoOfRetries) {
        this.maxNoOfRetries = maxNoOfRetries;
    }

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {
        try {
            syncTrackerService = (SyncTrackerService) this.beanLocator.lookupBean("syncTrackerService");
            commonSyncProcessor = (CommonSyncProcessor) this.beanLocator.lookupBean("commonSyncProcessor");
            populateDummyAuthentication();
            logger.info("Inside ItemSyncRetryTask: Starting item sync process by thread "
                    + Thread.currentThread().getId());            
            Calendar calStartTime = Calendar.getInstance();
            long startTimeOfItemSyncRetryTask = calStartTime.getTimeInMillis();
            logger.info("Start Time of ItemSyncRetryTask " + startTimeOfItemSyncRetryTask);
            List<Long> syncTrackerIdsList = syncTrackerService.getIdsForRetryProcessing(IntegrationConstants.ITEM, maxNoOfRetries);
            commonSyncProcessor.syncProcessor(IntegrationConstants.ITEM, syncTrackerIdsList, false);
            Calendar calEndTime = Calendar.getInstance();
            long endTimeOfstartTimeOfItemSyncRetryTask = calEndTime.getTimeInMillis();
            logger.info("ItemSyncRetryTask was executed in "
                    + (endTimeOfstartTimeOfItemSyncRetryTask - startTimeOfItemSyncRetryTask)
                    + "m sec");
        } catch (Exception e) {
            logger.error("Error while running Item Sync Retry Task !!", e);
        }
    }

    private void populateDummyAuthentication() {
        final SecurityHelper securityHelper = new SecurityHelper();
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            securityHelper.populateSystemUser();
        }
    }
}
