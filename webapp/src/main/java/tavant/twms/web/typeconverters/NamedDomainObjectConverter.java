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

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;

import tavant.twms.jbpm.infra.BeanLocator;

public abstract class NamedDomainObjectConverter<S, E> extends StrutsTypeConverter {
    private static final Logger logger = Logger.getLogger(NamedDomainObjectConverter.class);

    private final S service;

    @SuppressWarnings("unchecked")
    public NamedDomainObjectConverter(String serviceBeanName) {

        BeanLocator beanLocator = new BeanLocator();
        this.service = (S) beanLocator.lookupBean(serviceBeanName);
    }

    @Override
    public Object convertFromString(Map ctx, String[] values, Class toClass) {
        // TODO: Try check if E.isAssignableFrom(toClass)
        if (values.length < 1) {
            return null;
        }

        if (values.length == 1) {
            String name = values[0];
            try {
                return fetchByName(name);
            } catch (Exception ex) {
                logger.error("Error fetching entity by name '" + name + "'", ex);
                return null;
            }
        } else {
            try {
                return fetchByNames(values);
            } catch (Exception ex) {
                logger.error("Error fetching entities by values '" + values + "'", ex);
                return null;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String convertToString(Map ctx, Object o) {
        try {
            return getName((E) o);
        } catch (ClassCastException ce) {
            logger.error("Failed to convert object " + o + " using this converter");
            return null;
        } catch (Exception ex) {
            logger.error("Error fetching name of entity '" + o + "'", ex);
            return null;
        }
    }

    public S getService() {
        return this.service;
    }

    public abstract E fetchByName(String name) throws Exception;

    public abstract String getName(E entity) throws Exception;

    public Collection<E> fetchByNames(String[] values) throws Exception {
        return null;
    }
}
