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
package tavant.twms.web.typeconverters;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * @author vineeth.varghese
 *
 */
public abstract class ValidatableDomainObjectConverter<S, E> extends
        NamedDomainObjectConverter<S, E> {
    private static final Logger logger = Logger.getLogger(NamedDomainObjectConverter.class);

    public ValidatableDomainObjectConverter(String serviceBeanName) {
        super(serviceBeanName);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Object convertFromString(Map ctx, String[] values, Class toClass) {
        if (values.length < 1) {
            return null;
        }
        String name = values[0];
        if(logger.isDebugEnabled())
        {
            logger.debug("Fetching Entity by name [" + name + "]");
        }
        Object result = null;
        try {
            result = fetchByName(name);
        } catch (Exception e) {
            logger.error("Error fetching entity by name [" + name + "]", e);
            throw new TypeConversionException("Error fetching entity by name [" + name + "]");
        }
        if (result == null) {
            throw new TypeConversionException("Unable to get an Entity by name [" + name + "]");
        }
        return result;
    }
}
