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

package tavant.twms.integration.adapter;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;

import java.util.List;
import java.util.ArrayList;

public class SyncInitiatorTest extends MockObjectTestCase {

    SyncInitiator fixture = new SyncInitiator();

    Mock syncTrackerDAOMock;

    Mock eventContextMock;

    protected void setUp() throws Exception {
        super.setUp();
        syncTrackerDAOMock = mock(SyncTrackerDAO.class);
        fixture.setSyncTrackerDAO((SyncTrackerDAO) syncTrackerDAOMock.proxy());

        eventContextMock = mock(EventContext.class);
        fixture.setEventContext((EventContext) eventContextMock.proxy());
    }

    public void testInitiateSyncWithEmptySyncTrackerList() {
        String syncType = "SyncItem";
        syncTrackerDAOMock
                .expects(once()).method("deleteDuplicatesOfBusinessEntitiesToBeSynced")
                .with(eq(syncType));
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(0)));

        fixture.initiateSync(syncType);
    }

    public void testInitiateSyncWithSyncTrackerListSizeLessThanMaxResults() {
        String syncType = "SyncItem";
        syncTrackerDAOMock
                .expects(once()).method("deleteDuplicatesOfBusinessEntitiesToBeSynced")
                .with(eq(syncType));
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(0)));
        List<SyncTracker> syncTrackers = getSyncTrackerList(5);
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(syncTrackers));
        syncTrackerDAOMock
                .expects(exactly(5)).method("updateStatus");
        eventContextMock
                .expects(once()).method("sendEvent");
        fixture.initiateSync(syncType);
        for (SyncTracker syncTracker : syncTrackers) {
            assertEquals(SyncStatus.IN_PROGRESS, syncTracker.getStatus());
            assertNotNull(syncTracker.getStartTime());
            assertNotNull(syncTracker.getUpdateDate());
        }
    }

    public void testInitiateSyncWithSyncTrackerListSizeEqualToMaxResults() {
        String syncType = "SyncItem";
        syncTrackerDAOMock
                .expects(once()).method("deleteDuplicatesOfBusinessEntitiesToBeSynced")
                .with(eq(syncType));
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(0)));
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(10)));
        syncTrackerDAOMock
                .expects(exactly(10)).method("updateStatus");
        eventContextMock
                .expects(once()).method("sendEvent");
        fixture.initiateSync(syncType);
    }

    public void testInitiateSyncWithSyncTrackerListSizeGreaterThanMaxResults() {
        String syncType = "SyncItem";
        syncTrackerDAOMock
                .expects(once()).method("deleteDuplicatesOfBusinessEntitiesToBeSynced")
                .with(eq(syncType));
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(0)));
        syncTrackerDAOMock
                .expects(once()).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(5)));
        syncTrackerDAOMock
                .expects(exactly(2)).method("getBusinessEntitiesToBeSynced")
                .with(eq(syncType))
                .will(returnValue(getSyncTrackerList(10)));
        syncTrackerDAOMock
                .expects(exactly(25)).method("updateStatus");
        eventContextMock
                .expects(exactly(3)).method("sendEvent");
        fixture.initiateSync(syncType);
    }

    private List<SyncTracker> getSyncTrackerList(int listSize) {
        List<SyncTracker> syncTrackers = new ArrayList<SyncTracker>();
        for (int i=0; i<listSize; i++) {
            SyncTracker syncTracker = new SyncTracker();
            syncTracker.setStatus(SyncStatus.TO_BE_PROCESSED);
            syncTracker.setStartTime(null);
            syncTracker.setUpdateDate(null);
            syncTrackers.add(syncTracker);
        }
        return syncTrackers;
    }
}