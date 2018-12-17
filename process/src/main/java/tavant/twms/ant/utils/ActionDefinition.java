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
package tavant.twms.ant.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.types.DataType;

/**
 * @author radhakrishnan.j
 *
 */
public class ActionDefinition extends DataType {
	private String className;
	private List<ActionInput> inputs = new ArrayList<ActionInput>();

	public List<ActionInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<ActionInput> inputs) {
		this.inputs = inputs;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public void addConfiguredActionInput(ActionInput input) {
		inputs.add(input);
	}
}
