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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.shipment.ContractShipmentService;
import tavant.twms.worklist.WorkListItemService;

/**
 * @author kannan.ekanath
 *
 */
public class LogicalShipmentServiceImpl extends HibernateDaoSupport
		implements
			LogicalShipmentService {

	private static final String WAIT_FOR_TIMER = "Wait for Timer";

	private static final String DEALER_WAIT_FOR_TIMER = "Dealer Wait for Timer";

	private static final Logger logger = Logger
			.getLogger(LogicalShipmentServiceImpl.class);

	private ContractShipmentService contractShipmentService;

	private WorkListItemService workListItemService;

	/**
	 * Get all suppliers with SPRs awaiting logical shipment generate one
	 * shipment for each supplier and send them
	 *
	 */
	public List<Shipment> generateAllLogicalShipments() {
		return generateForTaskName(WAIT_FOR_TIMER);
	}

	public List<Shipment> generateAllDealerLogicalShipments() {
		return generateForTaskName(DEALER_WAIT_FOR_TIMER);
	}

	private List<Shipment> generateForTaskName(String taskName) {
		List<Shipment> shipments = new ArrayList<Shipment>();
		List<TaskInstance> tasks = getTaskInstancesAtTaskName(taskName);
		if(logger.isDebugEnabled())
		{
		    logger.debug("Following tasks are waiting at [" + taskName + "]");
		}
		Map<Supplier, List<TaskInstance>> supplierBasedTasksGrouping = getSupplierBasedTaskGrouping(tasks);
		if(logger.isDebugEnabled())
		{
		    logger.debug("Obtained the grouping of suppliers as ["
                            + supplierBasedTasksGrouping + "]");
		}
		for (Supplier s : supplierBasedTasksGrouping.keySet()) {
		        if(logger.isDebugEnabled()) {
                    logger.debug("Logical shipment for supplier [" + s + "]");
                }
			List<TaskInstance> supplierTask = supplierBasedTasksGrouping.get(s);
			if(logger.isDebugEnabled()) {
                            logger.debug("Found tasks [" + supplierTask + "]");
                        }
			List<OEMPartReplaced> parts = getPartsFromTasks(supplierTask);
			if(logger.isDebugEnabled()) {
                            logger.debug("Found parts [" + parts + "] for supplier [" + s
                				+ "]");
                        }
//			Shipment shipment = generateLogicalShipmentForParts(parts);
//			endTaskInstances(supplierTask);
//			if(logger.isDebugEnabled()) {
//                            logger.debug("Generated shipment [" + shipment + "]");
//                        }
//			shipments.add(shipment);
		}
		return shipments;
	}

	private void endTaskInstances(List<TaskInstance> tasks) {
		for (TaskInstance t : tasks) {
		    if(logger.isDebugEnabled()) {
                        logger.debug("Ending task [" + t + "]");
                    }
			t.end();
		}
	}

//	private Shipment generateLogicalShipmentForParts(List<OEMPartReplaced> parts) {
//		Shipment shipment = this.contractShipmentService
//				.generateContractShipments(parts);
//		shipment.setTrackingId("Logical Shipment (No Physical Transfer)");
//		if(logger.isDebugEnabled()) {
//                    logger.debug("Genereated Shipment [" + shipment + "] for these parts");
//                }
//		return shipment;
//	}

	private List<OEMPartReplaced> getPartsFromTasks(List<TaskInstance> tasks) {
		List<OEMPartReplaced> parts = new ArrayList<OEMPartReplaced>();
		for (TaskInstance task : tasks) {
			parts.add((OEMPartReplaced) task.getVariable("part"));
		}
		return parts;
	}

	private Map<Supplier, List<TaskInstance>> getSupplierBasedTaskGrouping(
			List<TaskInstance> tasks) {
		Map<Supplier, List<TaskInstance>> supplierMap = new LinkedHashMap<Supplier, List<TaskInstance>>();
		for (TaskInstance task : tasks) {
			RecoveryClaim recClaim = (RecoveryClaim) task
					.getVariable("recoveryClaim");
			Supplier supplier = recClaim.getSupplier();
			if (!supplierMap.containsKey(supplier)) {
				supplierMap.put(supplier, new ArrayList<TaskInstance>());
			}
			supplierMap.get(supplier).add(task);
		}
		return supplierMap;
	}
	protected List<TaskInstance> getTaskInstancesAtTaskName(String taskName) {
		return this.workListItemService.getTaskInstancesAtTaskName(taskName);
	}

	@Required
	public void setContractShipmentService(
			ContractShipmentService contractShipmentService) {
		this.contractShipmentService = contractShipmentService;
	}

	@Required
	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

}
