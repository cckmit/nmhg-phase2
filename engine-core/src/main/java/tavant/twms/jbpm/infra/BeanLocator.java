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
package tavant.twms.jbpm.infra;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springmodules.workflow.jbpm31.JbpmFactoryLocator;

/**
 * @author vineeth.varghese
 * @date Aug 22, 2006
 */
public class BeanLocator {
    
    public Object lookupBean(String beanName) {
        return retrieveBeanFactory().getBean(beanName);
    }

    BeanFactory retrieveBeanFactory() {
        BeanFactoryLocator factoryLocator = new JbpmFactoryLocator();
        BeanFactoryReference factory = factoryLocator.useBeanFactory(null);
        if (factory == null)
            throw new IllegalArgumentException("no beanFactory found");

        return factory.getFactory();
    }    
}
