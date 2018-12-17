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
package tavant.twms.domain.partreturn;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

/**
 * @author amritha.k
 * 
 */
public enum InspectionStatus {

    ACCEPT("Accept"),
    
    REJECT("Reject");

    private String status;
    
    public static InspectionStatus getInspectionStatus(String status)
    {
        if(!StringUtils.hasText(status)){
            return null;
        }
        else if (ACCEPT.status.equalsIgnoreCase(status))
    		return ACCEPT;
    	else if (REJECT.status.equalsIgnoreCase(status))
    		return     REJECT;
    	 else
            throw new IllegalArgumentException("Cannot understand the Inspection Status");       
    }

    private InspectionStatus(String status) {
        this.status = status;
    }

    /**
     * only for displaying purposes
     * 
     * @return
     */
    public String getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("status", this.status).toString();
    }


}
