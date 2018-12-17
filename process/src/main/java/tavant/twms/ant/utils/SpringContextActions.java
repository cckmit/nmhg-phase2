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
package tavant.twms.ant.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.loader.AntClassLoader2;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

/**
 * Class for initializing the application context
 * 
 * 
 */
public class SpringContextActions extends Task {
    private ApplicationContext applicationContext;
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    private String database;

    private String appContextLocation;

    private List<Action> actions = new ArrayList<Action>();

    public SpringContextActions() {
        setAntClassLoader();
    }

    protected List<Action> getActions() {
        return actions;
    }

    public void addConfiguredAction(ActionDefinition newAction) {
        loadApplicationContextIfNotLoaded();
        String className = newAction.getClassName();
        Action newInstance;
        try {
            Class<?> clazz = Class.forName(className);
            newInstance = (Action) clazz.newInstance();
        } catch (Exception e) {
            throw new BuildException(e);
        }
        injectDependenciesIfAny(newInstance);
        List<ActionInput> inputs = newAction.getInputs();
        for (ActionInput eachInput : inputs) {
            try {
                PropertyUtils.setProperty(newInstance, eachInput.getName(), eachInput.getValue());
            } catch (Exception e) {
                throw new BuildException(e);
            }
        }
        newInstance.throwErrorOnInvalidInputs();
        actions.add(newInstance);
        
        //TODO: The log() calls throw up an NPE. hence sys outs.. Need to find a better way.
        System.out.println("Added action [" + className + "]");
    }

	private void injectDependenciesIfAny(Action newInstance) {
		if( newInstance instanceof ApplicationContextAware ) {
			ApplicationContextAware applicationContextAware = (ApplicationContextAware)newInstance;
			applicationContextAware.setApplicationContext(applicationContext);			
		}
		int autoWireByName = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;
		boolean performDependencyCheck = true;
		if (autowireCapableBeanFactory!=null) {
			autowireCapableBeanFactory.autowireBeanProperties(newInstance,
					autoWireByName, performDependencyCheck);
		}		
	}

	private void loadApplicationContextIfNotLoaded() {
		if (applicationContext == null) {
            initialize();
            try {
                String[] appContextResources = StringUtils
                        .tokenizeToStringArray(getAppContextLocation(), ",");
                applicationContext = constructContext(appContextResources);
                autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
            } catch (Exception e) {
                throw new BuildException("Failure: Application context file [" + getAppContextLocation()
                        + "] could not be loaded", e);
            }
        }
	}

    protected ApplicationContext constructContext(final String[] appContextResources) {
    	return new ClassPathXmlApplicationContext(appContextResources);
    }

    public void execute() {
    	StopWatch stopWatch = new StopWatch();
    	for (Action eachAction : actions) {
        	stopWatch.start(eachAction.getClass().getName());
            eachAction.perform();
            stopWatch.stop();
            //TODO: The log() calls throw up an NPE. hence sys outs.. Need to find a better way.            
            System.out.println(eachAction.getClass().getName() + " done.");
        }
    	System.out.println(stopWatch.prettyPrint());
    }

    private void initialize() {
        if (!StringUtils.hasText(getAppContextLocation()))
            throw new BuildException("Failure: appContextLocation property has value [" + appContextLocation
                    + "], need valid application context file");
        String oldProp = System.getProperty("jdbc.database");
        if ((oldProp == null) && (this.database != null)) {
            System.setProperty("jdbc.database", this.database);
        }
    }

    private void setAntClassLoader() {
        /*
         * sets the AntClassLoader to the thread, to avoid classnotfound issues
         * when ant task is run
         */
        AntClassLoader2 antClassLoader = null;
        Object obj = this.getClass().getClassLoader();
        if (obj instanceof AntClassLoader2) {
            antClassLoader = (AntClassLoader2) obj;
            antClassLoader.setThreadContextLoader();
        }
    }

    public String getAppContextLocation() {
        return appContextLocation;
    }

    public void setAppContextLocation(String appContextLocation) {
        this.appContextLocation = appContextLocation;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getDatabase() {
        return this.database;
    }
}
