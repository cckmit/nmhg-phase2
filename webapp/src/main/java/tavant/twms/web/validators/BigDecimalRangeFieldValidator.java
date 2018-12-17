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
 * Date: Oct 26, 2007
 */

package tavant.twms.web.validators;

import java.math.BigDecimal;

import com.opensymphony.xwork2.validator.validators.AbstractRangeValidator;

public class BigDecimalRangeFieldValidator extends AbstractRangeValidator {
	   BigDecimal max = null;
	   BigDecimal min = null;

	    public void setMax(BigDecimal max) {
	        this.max = max;
	    }

	    public BigDecimal getMax() {
	        return this.max;
	    }

	    @Override
		public BigDecimal getMaxComparatorValue() {
	        return this.max;
	    }

	    public void setMin(BigDecimal min) {
	        this.min = min;
	    }

	    public BigDecimal getMin() {
	        return this.min;
	    }

	    @Override
		public BigDecimal getMinComparatorValue() {
	        return this.min;
	    }
}
