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
package tavant.twms.domain.claim.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Holds one or more validation messages.
 * 
 * @author <a href="radhakrishnan.j@tavant.com">radhakrishnan.j</a>
 * @date Sep 1, 2006
 */
// TODO : Need a better name!  Check if we need the map.
public class ValidationResults {
	List<String> errors = new ArrayList<String>();
	List<String> warnings = new ArrayList<String>();
	List<String> defaultErrorMsgInUS =  new ArrayList<String>();
	Map<String, Set> assignStateToUserGroupMap = new HashMap<String, Set>();
        
	public void addErrorMessage(String errorMessage) {
       errors.add(  errorMessage ); 
    }
    
    public void addWarningMessage(String warningMessage) {
       warnings.add( warningMessage);
    }
    
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }    
    
    public boolean hasNoErrors() {
        return errors.isEmpty();
    }

	public List<String> getErrors() {
    	return errors;
    }

	public List<String> getWarnings() {
    	return warnings;
    }

	public Map<String, Set> getAssignStateToUserGroupMap() {
		return assignStateToUserGroupMap;
	}

	public void addDefalutErrorMsgInUS(String errorMessage){
		defaultErrorMsgInUS.add(errorMessage);
	}

	public List<String> getDefaultErrorMsgInUS() {
		return defaultErrorMsgInUS;
	}

	public void setDefaultErrorMsgInUS(List<String> defaultErrorMsgInUS) {
		this.defaultErrorMsgInUS = defaultErrorMsgInUS;
	}
	    
	
}
