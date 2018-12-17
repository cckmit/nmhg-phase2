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
package tavant.twms.domain.policy;

import com.domainlanguage.time.CalendarDate;

/**
 * @author vishal.nagota
 *
 */
public class PolicyNotApplicableException extends PolicyException {
    private CalendarDate deliveryDate;
    private CalendarDate warrantyStartDate;
    
    public PolicyNotApplicableException(String message, Throwable e) {
        super(message, e);
        // TODO Auto-generated constructor stub
    }

    public PolicyNotApplicableException(String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }
    
    public PolicyNotApplicableException(CalendarDate deliveryDate,CalendarDate warrantyStartDate) {
        super(" Delivery date "+deliveryDate+" and policy start date is "+warrantyStartDate);        
        this.deliveryDate = deliveryDate;
        this.warrantyStartDate = warrantyStartDate;
    }

    public void setDeliveryDate(CalendarDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public CalendarDate getDeliveryDate() {
        return deliveryDate;
    }

    public CalendarDate getWarrantyStartDate() {
        return warrantyStartDate;
    }
    
    
}
