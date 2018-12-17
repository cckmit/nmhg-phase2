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
 *
 */
package tavant.twms.integration.server.sync.inbound;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;

public class InboundSyncInterceptor implements MethodInterceptor {

    private SyncTrackerDAO syncTrackerDao;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        SyncTracker syncTracker = syncTrackerDao.save(
                invocation.getMethod().getName().substring(4), // remove sync  from integration service method name to get sync type
                (String) invocation.getArguments()[0], SyncStatus.TO_BE_PROCESSED // first argument is the bod XML
        );
        String[] responses = (String[]) invocation.proceed();
        syncTrackerDao.update(syncTracker, responses[1], responses[0]);
        return responses[0];
    }

    public void setSyncTrackerDao(SyncTrackerDAO syncTrackerDao) {
        this.syncTrackerDao = syncTrackerDao;
    }
}
