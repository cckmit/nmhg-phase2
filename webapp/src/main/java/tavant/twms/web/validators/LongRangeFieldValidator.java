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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jan 17, 2007
 * Time: 8:33:24 PM
 */

package tavant.twms.web.validators;

import com.opensymphony.xwork2.validator.validators.AbstractRangeValidator;

public class LongRangeFieldValidator extends AbstractRangeValidator  {

    Long max = null;
    Long min = null;


    public void setMax(Long max) {
        this.max = max;
    }

    public Long getMax() {
        return max;
    }

    public Comparable getMaxComparatorValue() {
        return max;
    }

    public void setMin(Long min) {
        this.min = min;
    }

    public Long getMin() {
        return min;
    }

    public Comparable getMinComparatorValue() {
        return min;
    }
    
}
