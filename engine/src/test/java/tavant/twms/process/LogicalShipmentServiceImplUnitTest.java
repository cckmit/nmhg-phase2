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

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.shipment.ContractShipmentServiceImpl;

import com.domainlanguage.timeutil.Clock;

/**
 * @author kannan.ekanath
 * 
 */
public class LogicalShipmentServiceImplUnitTest extends TestCase {

	TaskInstance t1 = new TaskInstance(), t2 = new TaskInstance(),
			t3 = new TaskInstance();

	Supplier s1 = new Supplier(), s2 = new Supplier();

	OEMPartReplaced p1 = new OEMPartReplaced(), p2 = new OEMPartReplaced(),
			p3 = new OEMPartReplaced();

	Claim claim1 = new MachineClaim();
	Claim claim2 = new MachineClaim();
	Claim claim3 = new MachineClaim();

	RecoveryClaim recoveryClaim1 = new RecoveryClaim();
	RecoveryClaim recoveryClaim2 = new RecoveryClaim();
	RecoveryClaim recoveryClaim3 = new RecoveryClaim();

	SupplierPartReturn spr1 = new SupplierPartReturn(),
			spr2 = new SupplierPartReturn(), spr3 = new SupplierPartReturn();

	public void testGenerateShipment() {
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		Clock.timeSource();
		t1.setVariable("part", p1);
		t2.setVariable("part", p2);
		t3.setVariable("part", p3);
		t1.setVariable("recoveryClaim", recoveryClaim1);
		t2.setVariable("recoveryClaim", recoveryClaim2);
		t3.setVariable("recoveryClaim", recoveryClaim3);

		p1.setSupplierPartReturn(spr1);
		p2.setSupplierPartReturn(spr2);
		p3.setSupplierPartReturn(spr3);

		claim1.addRecoveryClaim(recoveryClaim1);
		recoveryClaim1.setSupplier(s1);
		claim2.addRecoveryClaim(recoveryClaim2);
		recoveryClaim2.setSupplier(s1);
		claim3.addRecoveryClaim(recoveryClaim3);
		recoveryClaim3.setSupplier(s2);

		spr1.setReturnLocation(new Location());
		spr2.setReturnLocation(new Location());
		spr3.setReturnLocation(new Location());

		final List<TaskInstance> tasks = Arrays.asList(new TaskInstance[]{t1,
				t2, t3});
		LogicalShipmentServiceImpl logicalShipmentService = new LogicalShipmentServiceImpl() {

			@Override
			protected List<TaskInstance> getTaskInstancesAtTaskName(
					String taskName) {
				return tasks;
			}

		};
		logicalShipmentService
				.setContractShipmentService(new ContractShipmentServiceImpl());
		List<Shipment> shipments = logicalShipmentService
				.generateAllLogicalShipments();

		// there must be two shipments for two suppliers, first with two parts
		assertEquals(2, shipments.size());

		assertEquals(2, shipments.get(0).getSupplierParts().size());
		assertTrue(shipments.get(0).getSupplierParts().contains(p1));
		assertTrue(shipments.get(0).getSupplierParts().contains(p2));

		assertEquals(1, shipments.get(1).getSupplierParts().size());
		assertTrue(shipments.get(1).getSupplierParts().contains(p3));

		assertTrue(t1.hasEnded());
		assertTrue(t2.hasEnded());
		assertTrue(t3.hasEnded());

	}
}
