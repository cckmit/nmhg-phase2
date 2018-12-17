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
package tavant.twms.jbpm.action;

import org.jbpm.graph.def.Action;
import org.jbpm.graph.exe.ExecutionContext;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.jbpm.infra.BeanLocator;

@SuppressWarnings("serial")
public class PartsReceivedSchedulerAction extends Action {

	private final BeanLocator beanLocator = new BeanLocator();

	String transition;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		OEMPartReplaced part = ((SupplierPartReturn) executionContext
				.getVariable("supplierPartReturn")).getRecoverablePart().getOemPart();
		boolean canTakeTransition = part.isPartInWarehouse();
		if (canTakeTransition) {
			executionContext.getTaskInstance().end(getTransition());
		}
	}
	public String getTransition() {
		return transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

}
