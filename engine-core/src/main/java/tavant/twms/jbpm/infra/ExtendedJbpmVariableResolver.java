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
package tavant.twms.jbpm.infra;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.ELException;
import org.jbpm.jpdl.el.VariableResolver;
import org.jbpm.jpdl.el.impl.JbpmVariableResolver;

public class ExtendedJbpmVariableResolver extends JbpmVariableResolver
		implements VariableResolver {

	@Override
	public Object resolveVariable(String name) throws ELException {
		Object value = super.resolveVariable(name);
		if (value == null) {
			ExecutionContext executionContext = ExecutionContext.currentExecutionContext();
			value = executionContext.getContextInstance().getTransientVariable(name);
		}
		return value;
	}
	
	

}
