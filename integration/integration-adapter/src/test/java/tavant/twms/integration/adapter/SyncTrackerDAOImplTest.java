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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SyncTrackerDAOImplTest extends
		IntegrationAdapterRepositoryTestCase {

	private SyncTrackerDAO syncTrackerDAO;

	public void testGetBusinessEntitiesToBeSynced() {
		List<SyncTracker> syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItem");
		assertEquals(10, syncTrackers.size());
		for (SyncTracker syncTracker : syncTrackers) {
			syncTracker.setStatus(SyncStatus.IN_PROGRESS);
			syncTrackerDAO.update(syncTracker);
		}
		flushAndClear();
		syncTrackers = syncTrackerDAO.getBusinessEntitiesToBeSynced("SyncItem");
		assertEquals(5, syncTrackers.size());
	}

	public void testDeleteWithNoDuplicates() {
		List<SyncTracker> syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncCustomer");
		assertEquals(2, syncTrackers.size());
		syncTrackerDAO
				.deleteDuplicatesOfBusinessEntitiesToBeSynced("SyncCustomer");
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncCustomer");
		assertEquals(2, syncTrackers.size());
	}

	public void testDeleteWithDuplicates() {
		List<SyncTracker> syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(2, syncTrackers.size());
		syncTrackerDAO
				.deleteDuplicatesOfBusinessEntitiesToBeSynced("SyncItemGroup");
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(1, syncTrackers.size());
	}

	public void testDeleteForOtherCases() {
		// Testing a few more scnearios for
		// deleteDuplicatesOfBusinessEntitiesToBeSynced()
		// Test data: 2 SyncItemGroup records with the same business id yet to
		// be synced
		List<SyncTracker> syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(2, syncTrackers.size());

		// Update 1 SyncItemGroup record to synced. Verify if there is 1 more
		// SyncItemGroup record to be synced
		SyncTracker syncTracker = syncTrackers.get(0);
		syncTracker.setStatus(SyncStatus.COMPLETED);
		syncTracker.setNoOfAttempts(0);
		syncTrackerDAO.update(syncTracker);
		flushAndClear();
		syncTrackerDAO
				.deleteDuplicatesOfBusinessEntitiesToBeSynced("SyncItemGroup");
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(1, syncTrackers.size());

		// Update 1 SyncItemGroup record to 'sync in progress'. Verify if there
		// is 1 more SyncItemGroup record to be synced
		syncTracker.setStatus(SyncStatus.IN_PROGRESS);
		syncTracker.setNoOfAttempts(0);
		syncTrackerDAO.update(syncTracker);
		flushAndClear();
		syncTrackerDAO
				.deleteDuplicatesOfBusinessEntitiesToBeSynced("SyncItemGroup");
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(1, syncTrackers.size());

		// Update 1 SyncItemGroup record's FAILURE_COUNT to 1. Verify if there
		// still are 2 more SyncItemGroup records to be synced
		syncTracker.setStatus(SyncStatus.FAILED);
		syncTracker.setNoOfAttempts(1);
		syncTrackerDAO.update(syncTracker);
		flushAndClear();
		syncTrackerDAO
				.deleteDuplicatesOfBusinessEntitiesToBeSynced("SyncItemGroup");
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(2, syncTrackers.size());
	}

	public void testGetBusinessEntitiesToBeSyncedWithSyncedRecords() {
		List<SyncTracker> syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(2, syncTrackers.size());
		for (SyncTracker syncTracker : syncTrackers) {
			syncTracker.setStatus(SyncStatus.COMPLETED);
			syncTrackerDAO.update(syncTracker);
		}
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(0, syncTrackers.size());
	}

	public void testGetBusinessEntitiesToBeSyncedWithSyncFailedRecords() {
		List<SyncTracker> syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(2, syncTrackers.size());
		for (SyncTracker syncTracker : syncTrackers) {
			syncTracker.setNoOfAttempts(2);
			syncTrackerDAO.update(syncTracker);
		}
		flushAndClear();
		syncTrackers = syncTrackerDAO
				.getBusinessEntitiesToBeSynced("SyncItemGroup");
		assertEquals(0, syncTrackers.size());
	}

	public void testFindById() {
		SyncTracker syncTrackerFromDB = syncTrackerDAO.findById(1L);
		assertEquals(new Long(1), syncTrackerFromDB.getId());
		assertEquals("SyncItem", syncTrackerFromDB.getSyncType());
		assertEquals("Item001", syncTrackerFromDB.getBusinessId());
	}

	public void testSave() {
		SyncTracker syncTracker = new SyncTracker();
		syncTracker.setBusinessId("1");
		syncTracker.setSyncType("SyncItem");
		syncTrackerDAO.save(syncTracker);
		flushAndClear();
		assertNotNull("Id should have been assigned", syncTracker.getId());
	}

	public void testUpdate() {
		SyncTracker syncTrackerFromDB = syncTrackerDAO.findById(1L);
		Date now = new Date();
		syncTrackerFromDB.setStatus(SyncStatus.IN_PROGRESS);
		syncTrackerFromDB.setStartTime(now);
		syncTrackerFromDB.setUpdateDate(now);
		syncTrackerDAO.update(syncTrackerFromDB);
		flushAndClear();
		syncTrackerFromDB = syncTrackerDAO.findById(1L);
		assertEquals(SyncStatus.IN_PROGRESS.getStatus(), syncTrackerFromDB
				.getStatus().getStatus());
		assertEquals(now, syncTrackerFromDB.getStartTime());
		assertEquals(now, syncTrackerFromDB.getUpdateDate());
	}

	public void testUpdateStatus() {
		SyncTracker syncTrackerFromDB = syncTrackerDAO.findById(1L);
		Date now = new Date();
		syncTrackerFromDB.setStatus(SyncStatus.IN_PROGRESS);
		syncTrackerFromDB.setStartTime(now);
		syncTrackerFromDB.setUpdateDate(now);
		syncTrackerDAO.updateStatus(syncTrackerFromDB);
		flushAndClear();
		syncTrackerFromDB = syncTrackerDAO.findById(1L);
		assertEquals(SyncStatus.IN_PROGRESS.getStatus(), syncTrackerFromDB
				.getStatus().getStatus());
		assertEquals(now, syncTrackerFromDB.getStartTime());
		assertEquals(now, syncTrackerFromDB.getUpdateDate());
	}

	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}

	/**
	 * The methods below are related to summary screens
	 *
	 */

	public void testGetSummary() {

		List<SummaryDTO> summary = syncTrackerDAO.getSummary(
				getDate("02/23/2007"), getDate("02/23/2007"));
		assertEquals(4, summary.size());
		assertEquals("SyncCustomer", summary.get(0).getSyncType());
		assertEquals(2, summary.get(0).getToBeProcessed());

		assertEquals("SyncInventory", summary.get(1).getSyncType());
		assertEquals(1, summary.get(1).getInProgress());
		assertEquals(1, summary.get(1).getToBeProcessed());

		assertEquals("SyncItem", summary.get(2).getSyncType());
		assertEquals(15, summary.get(2).getToBeProcessed());

		assertEquals("SyncItemGroup", summary.get(3).getSyncType());
		assertEquals(2, summary.get(3).getToBeProcessed());

	}

//	public void testGetSyncDetails() {
//		SyncTrackerSearchCriteria syncTrackerSearchCriteria = new SyncTrackerSearchCriteria();
//		syncTrackerSearchCriteria.setSyncType("SyncItem");
//		syncTrackerSearchCriteria.setStartDate(getDate("02/23/2007"));
//		syncTrackerSearchCriteria.setEndDate(getDate("02/23/2007"));
//		syncTrackerSearchCriteria.setStatus(SyncStatus.TO_BE_PROCESSED
//				.getStatus());
//		syncTrackerSearchCriteria.setErrorMessage("test");
//
//		List<SyncTracker> syncTrackerList = syncTrackerDAO
//				.getSyncDetails(syncTrackerSearchCriteria);
//
//		assertEquals(16, syncTrackerList.size());
//	}

//	public void testGetErrorSummary() {
//		SyncTrackerSearchCriteria syncSummaryCriteria = new SyncTrackerSearchCriteria();
//		syncSummaryCriteria.setSyncType("SyncItem");
//		syncSummaryCriteria.setStartDate(getDate("02/23/2007"));
//		syncSummaryCriteria.setEndDate(getDate("02/23/2007"));
//
//		List<ErrorSummaryDTO> syncTrackerList = syncTrackerDAO
//				.getErrorSummary(syncSummaryCriteria);
//		 assertEquals(0, syncTrackerList.size());
//	}

	private Date getDate(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

}
