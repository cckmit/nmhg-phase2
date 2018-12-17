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
package tavant.twms.infra;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.proxy.HibernateProxy;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;

/**
 * Abstraction which to used to extract variables from and populate variables to
 * the Process's <code>ContextInstance</code>
 * <p>
 * <code>Claim</code> is considered as a first class process variable.
 * 
 * @author vineeth.varghese
 * @date Jul 13, 2006
 */
public class ProcessVariables {
	public static final String CLAIM = "claim";

	public static final String EXCEPTION = "execution-exceptions";

	private final Map<String, Object> processVariables = new HashMap<String, Object>();

	@SuppressWarnings("unchecked")
	public void initializeWithContextVariables(ContextInstance ctx) {
		Assert.notNull(ctx,
				"Null is passed in as ContextInstance, which is not allowed");
		Map<String, Object> vars = ctx.getVariables();
		if (vars != null) {
			this.processVariables.putAll(vars);
		}
	}

	public void extractOutContextVariables(ContextInstance ctx) {
		Iterator<Entry<String, Object>> iterator = this.processVariables
				.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			ctx.setVariable(entry.getKey(), entry.getValue());
		}
	}

	public void setVariable(String id, Object obj) {
		if (obj instanceof HibernateProxy) {
			obj = ((HibernateProxy) obj).getHibernateLazyInitializer()
					.getImplementation();
		}
		this.processVariables.put(id, obj);
	}
	public Object getVariable(String id) {
		return this.processVariables.get(id);
	}

	public static ProcessVariables createProcessVariablesFromTask(
			TaskInstance task) {
		ProcessVariables var = new ProcessVariables();
		var.initializeWithContextVariables(task.getContextInstance());
		return var;
	}
}
