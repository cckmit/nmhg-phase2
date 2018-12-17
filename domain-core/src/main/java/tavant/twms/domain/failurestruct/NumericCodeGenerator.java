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
package tavant.twms.domain.failurestruct;

import org.apache.commons.lang.StringUtils;

import tavant.twms.common.TWMSException;

/**
 * @author kamal.govindraj
 *
 */
public class NumericCodeGenerator implements CodeGenerator {

	/* (non-Javadoc)
	 * @see tavant.twms.domain.failurestruct.CodeGenerator#nextCode(java.lang.String)
	 */
	public String nextCode(String currentCode) {
		if (StringUtils.isEmpty(currentCode)) {
			return "001";
		}
		
		if (StringUtils.isNumeric(currentCode)) {
			int currentValue = Integer.parseInt(currentCode);
			if (currentValue == 999) {
				throw new TWMSException("Code range exhausted - a new code can't be generated");
			}
			return String.format("%1$03d",currentValue+1);
		} else {
			throw new TWMSException("The current code is not numeric");
		}
	}

}
