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

package tavant.twms.web.typeconverters;

import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Map;

/**
 * @author kiran.sg
 *
 */
public class LocaleConverter extends StrutsTypeConverter {

	private static final Logger logger = Logger
			.getLogger(LocaleConverter.class);

	@Override
	public Object convertFromString(Map ctx, String[] value, Class toType) {
		if (toType != Locale.class) {
		        if(logger.isDebugEnabled())
		        {
		            logger.debug("Attempting to convert [" + value + "] to " + toType);
		        }
			throw new IllegalArgumentException("Attempting to convert ["
					+ value + "] to " + toType);
		}

		Assert.isTrue(value.length == 1);
		return StringUtils.parseLocaleString(value[0]);
	}

	@Override
	public String convertToString(Map ctx, Object value) {
		Assert.isTrue(value instanceof Locale);
		return value.toString();
	}
}
