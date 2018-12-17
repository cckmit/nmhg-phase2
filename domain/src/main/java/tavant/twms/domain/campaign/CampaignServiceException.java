/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.campaign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.style.ToStringCreator;

/**
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class CampaignServiceException extends Exception {

    private Map<String, String> fieldErrors = new HashMap<String, String>();

    private List<String> actionErrors = new ArrayList<String>();

    public CampaignServiceException(Map<String, String> fieldErrors2, List<String> actionErrors2) {
        this.fieldErrors = fieldErrors2;
        this.actionErrors = actionErrors2;
    }

    public CampaignServiceException() {
    }

    public List<String> actionErrors() {
        return actionErrors;
    }

    public Map<String, String> fieldErrors() {
        return fieldErrors;
    }
    
    @Override
    public String toString() {
        ToStringCreator toStringCreator = new ToStringCreator(this);
        toStringCreator.append("Field Errors:\n");
        for (Map.Entry<String, String> entry : fieldErrors.entrySet()) {
            toStringCreator.append("\tProperty Path: ", entry.getKey());
            toStringCreator.append("\tMessage: ", entry.getValue());
            toStringCreator.append("\n");
        }
        
        toStringCreator.append("Action Errors:\n");
        for (String anActionError : actionErrors) {
            toStringCreator.append("\tMessage: ", anActionError);
            toStringCreator.append("\n");
        }
        return toStringCreator.toString();
    }
}