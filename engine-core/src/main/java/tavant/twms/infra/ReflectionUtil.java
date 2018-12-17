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
package tavant.twms.infra;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.util.Assert;

import tavant.twms.jbpm.EngineException;

public abstract class ReflectionUtil {

    private static final Logger logger = Logger.getLogger(ReflectionUtil.class);

    /**
     * This is different from the method in java.lang.Class. If there is a
     * method load with some parameters and you are not sure of the parameter
     * types then you can use this method to just get the method with the name
     *
     * @param clazz
     * @param methodName
     * @return
     */
    public static Method getMethod(Object bean, String methodName) {
        try {
            if(logger.isDebugEnabled())
            {
                logger.debug("Getting method [" + methodName + "] on class ["
                        + AopUtils.getTargetClass(bean) + "]");
            }
            /**
             * TODO: problem here the parameter types are not known a) one
             * option is you have actual values calling getClass might give you
             * the class. But if the value is null the code will fail This was
             * the earlier code b) for now get all methods and check for the/
             * method we need by simple name comparison. The downfall is the
             * method is overloaded then we have a problem
             */
            Class beanClass = bean.getClass();
            Method[] methods = beanClass.getMethods();
            if(logger.isDebugEnabled())
            {
                logger.debug("Methods in class are " + Arrays.asList(methods) + "]");
            }
            List<Method> matchingMethods = new ArrayList<Method>();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    matchingMethods.add(method);
                }
            }
            Assert.state(matchingMethods.size() != 0, "Class [" + beanClass + "] has no methods with name ["
                    + methodName + "]");
            Assert.state(matchingMethods.size() == 1, "Class [" + beanClass
                    + "] has more than one method with name [" + methodName
                    + "]. For now overloading is not supported");
            return matchingMethods.get(0);
        } catch (SecurityException e) {
            logger.error("error", e);
            throw new EngineException("Error executing service", e);
        }
    }

    public static Object executeMethod(Object bean, Method method, Object[] paramValues) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(getErrorMessage(bean, method, paramValues));
            }
            return method.invoke(bean, paramValues);
        } catch (Exception e) {
            String errorMessage = getErrorMessage(bean, method, paramValues);
            logger.error(errorMessage, e);
            throw new EngineException(errorMessage, e);
        }
    }

    private static String getErrorMessage(Object bean, Method method, Object[] paramValues) {
        return "Invoking method [" + method + "] of " + "Bean [" + bean + "] "+ "With values [" +
                Arrays.asList(paramValues) + "]";
    }

    public static Object executeMethod(Object bean, String methodName, Object[] paramValues) {
        Method method = getMethod(bean, methodName);
        return executeMethod(bean, method, paramValues);
    }

    public static Object createNewInstance(String className) {
        try {
            Class clazz = Class.forName(className);
            return clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to locate class[" + className + "]", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to instantiate class[" + className + "]", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Couldnt access class[" + className + "]", e);
        }
    }
}
