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

import tavant.twms.deployment.tasks.DefaultTask;

public class MockTask extends DefaultTask 
{
	private String property;
	private List<Object> state = new ArrayList<Object>();
	private boolean throwValidationError;
	
	public boolean isThrowValidationError() {
		return throwValidationError;
	}

	public void setThrowValidationError(boolean throwValidationError) {
		this.throwValidationError = throwValidationError;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public List<Object> getState() {
		return state;
	}

	public void perform() {
		state.add(new Object());
	}

	@Override
	public void throwErrorOnInvalidInputs() {
		super.throwErrorOnInvalidInputs();
		if( throwValidationError ) {
			throw new RuntimeException();
		}
	}
	
	
	
	
}