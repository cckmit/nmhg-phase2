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
package tavant.twms.domain.common;

import javax.persistence.Embeddable;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.style.ToStringCreator;

@Embeddable
public class NumberRange {

    @Override
    public String toString() {
        return new ToStringCreator(this)
                .append("Minimum: ").append(min)
                .append(", Maximum: ").append(max)
                .toString();
    }
    
    public NumberRange() {
        
    }
    
    public NumberRange(String min, String max) {
        this.min = min;
        this.max = max;
    }

    @NotEmpty(message = "{required.serial.number}")
    private String min;

    @NotEmpty(message = "{required.serial.number}")
    private String max;

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}