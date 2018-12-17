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
import tavant.oagis.BODDTO;
import tavant.oagis.BODDocumentDTO;

import java.util.ArrayList;
import java.util.List;

public class SyncResponseProcessorTest extends MockObjectTestCase {

    SyncResponseProcessor fixture = new SyncResponseProcessor();

    Mock syncTrackerDAOMock;

    Mock eventContextMock;

    protected void setUp() throws Exception {
        super.setUp();
        syncTrackerDAOMock = mock(SyncTrackerDAO.class);
        fixture.setSyncTrackerDAO((SyncTrackerDAO) syncTrackerDAOMock.proxy());
        eventContextMock = mock(EventContext.class);
        fixture.setEventContext((EventContext) eventContextMock.proxy());
    }

    public void testProcessResponseForSuccessfulSync() {
        String syncType = "SyncItem";
        Long id = 1L;
        String businessId = "Item001";
        boolean successful = true;
        ArrayList<BODDTO> boddtos = getBODDTOs(businessId, successful, null);
        SyncTracker syncTracker = getSyncTracker(id, businessId, syncType);
        List<SyncTracker> syncTrackers = new ArrayList<SyncTracker>();
        syncTrackers.add(syncTracker);

        eventContextMock
                .expects(once()).method("getRecordsBeingSynced")
                .will(returnValue(syncTrackers));
        syncTrackerDAOMock
                .expects(once()).method("findById")
                .with(eq(id))
                .will(returnValue(syncTracker));
        syncTrackerDAOMock
                .expects(once()).method("update");

        fixture.processResponse(boddtos);
        assertEquals(new Integer(1), syncTracker.getNoOfAttempts());
        assertEquals(SyncStatus.COMPLETED, syncTracker.getStatus());
        assertEquals(null, syncTracker.getErrorMessage());
        assertNotNull(syncTracker.getUpdateDate());
    }

    public void testProcessResponseForFailedSync() {
        String syncType = "SyncItem";
        Long id = 1L;
        String businessId = "Item001";
        boolean successful = false;
        String errorMessage = "error message";

        ArrayList<BODDTO> boddtos = getBODDTOs(businessId, successful, errorMessage);
        SyncTracker syncTracker = getSyncTracker(id, businessId, syncType);
        List<SyncTracker> syncTrackers = new ArrayList<SyncTracker>();
        syncTrackers.add(syncTracker);

        eventContextMock
                .expects(once()).method("getRecordsBeingSynced")
                .will(returnValue(syncTrackers));
        syncTrackerDAOMock
                .expects(once()).method("findById")
                .with(eq(id))
                .will(returnValue(syncTracker));
        syncTrackerDAOMock
                .expects(once()).method("update");

        fixture.processResponse(boddtos);
        assertEquals(new Integer(1), syncTracker.getNoOfAttempts());
        assertEquals(SyncStatus.FAILED, syncTracker.getStatus());
        assertEquals(errorMessage, syncTracker.getErrorMessage());
        assertNotNull(syncTracker.getUpdateDate());
    }

    private ArrayList<BODDTO> getBODDTOs(String businessId, boolean successful, String errorMessage) {
        BODDocumentDTO bodDocumentDTO = BODDocumentDTO.Factory.newInstance();
        BODDTO boddto = bodDocumentDTO.addNewBOD();
        boddto.setBusinessId(businessId);
        boddto.setSuccessful(successful);
        boddto.setException(errorMessage);
        ArrayList<BODDTO> boddtos = new ArrayList<BODDTO>();
        boddtos.add(boddto);
        return boddtos;
    }

    private SyncTracker getSyncTracker(Long id, String businessId, String syncType) {
        SyncTracker syncTracker = new SyncTracker();
        syncTracker.setId(id);
        syncTracker.setBusinessId(businessId);
        syncTracker.setSyncType(syncType);
        syncTracker.setNoOfAttempts(0);
        syncTracker.setUpdateDate(null);
        return syncTracker;
    }

}
