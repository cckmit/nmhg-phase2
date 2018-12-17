package com.iterative.groovy.spring;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.web.context.WebApplicationContext;


public class ApplicationContextWrapper implements ApplicationContextAware, WebApplicationContext {
    private ApplicationContext context;
    
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    public boolean containsBean(String arg0) {
        return context.containsBean(arg0);
    }

    public boolean containsBeanDefinition(String arg0) {
        return context.containsBeanDefinition(arg0);
    }

    public String[] getAliases(String arg0) throws NoSuchBeanDefinitionException {
        return context.getAliases(arg0);
    }

    public Object getBean(String arg0, Class arg1) throws BeansException {
        return context.getBean(arg0, arg1);
    }

    public Object getBean(String arg0) throws BeansException {
        return context.getBean(arg0);
    }

    public int getBeanDefinitionCount() {
        return context.getBeanDefinitionCount();
    }

    public String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }

    public String[] getBeanNamesForType(Class arg0, boolean arg1, boolean arg2) {
        return context.getBeanNamesForType(arg0, arg1, arg2);
    }

    public String[] getBeanNamesForType(Class arg0) {
        return context.getBeanNamesForType(arg0);
    }

    public Map getBeansOfType(Class arg0, boolean arg1, boolean arg2) throws BeansException {
        return context.getBeansOfType(arg0, arg1, arg2);
    }

    public Map getBeansOfType(Class arg0) throws BeansException {
        return context.getBeansOfType(arg0);
    }

    public String getDisplayName() {
        return context.getDisplayName();
    }

    public String getMessage(MessageSourceResolvable arg0, Locale arg1) throws NoSuchMessageException {
        return context.getMessage(arg0, arg1);
    }

    public String getMessage(String arg0, Object[] arg1, Locale arg2) throws NoSuchMessageException {
        return context.getMessage(arg0, arg1, arg2);
    }

    public String getMessage(String arg0, Object[] arg1, String arg2, Locale arg3) {
        return context.getMessage(arg0, arg1, arg2, arg3);
    }

    public ApplicationContext getParent() {
        return context.getParent();
    }

    public BeanFactory getParentBeanFactory() {
        return context.getParentBeanFactory();
    }

    public Resource getResource(String arg0) {
        return context.getResource(arg0);
    }

    public Resource[] getResources(String arg0) throws IOException {
        return context.getResources(arg0);
    }

    public long getStartupDate() {
        return context.getStartupDate();
    }

    public Class getType(String arg0) throws NoSuchBeanDefinitionException {
        return context.getType(arg0);
    }

    public boolean isSingleton(String arg0) throws NoSuchBeanDefinitionException {
        return context.isSingleton(arg0);
    }

    public void publishEvent(ApplicationEvent arg0) {
        context.publishEvent(arg0);
    }

    public ServletContext getServletContext() {
    	if(context instanceof WebApplicationContext) {
    		return ((WebApplicationContext)context).getServletContext();
    	} else {
    		return null;
    	}
    }


	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return context.getAutowireCapableBeanFactory();
	}

	public String getId() {
		return context.getId();
	}

	public Object getBean(String name, Object[] args) {
		return context.getBean(name, args);
	}

	public boolean isPrototype(String name) {
		return context.isPrototype(name);
	}

	public boolean isTypeMatch(String arg0, Class arg1) throws NoSuchBeanDefinitionException {
		return context.isTypeMatch(arg0, arg1);
	}

	public boolean containsLocalBean(String arg0) {
		return context.containsBean(arg0);
	}

	public ClassLoader getClassLoader() {
		return context.getClassLoader();
	}
}
