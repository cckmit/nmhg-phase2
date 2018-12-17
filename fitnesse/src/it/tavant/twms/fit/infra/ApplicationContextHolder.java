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
package tavant.twms.fit.infra;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.AbstractTransactionalSpringContextTests;


/**
 * @author vineeth.varghese
 * @date Oct 25, 2006
 */
public class ApplicationContextHolder extends AbstractTransactionalSpringContextTests {
	
	/** The only instance. This class implements the Singleton pattern. */
    private static ApplicationContextHolder applicationContextHolder;
    
    private static ConfigurableApplicationContext configurableApplicationContext;

    /** Location of the configuration files. */
    private String[] configLocations;
    
    public static ApplicationContextHolder getApplicationContextHolder() {
        if (applicationContextHolder == null) {
            applicationContextHolder = new ApplicationContextHolder();
        }
        return applicationContextHolder;
    }
    
    public void initializeContext(String[] configLocation) {
        this.setConfigLocation(configLocation);
    	try {
    		configurableApplicationContext = applicationContextHolder.getContext(configLocations);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		autowireBeanProperties(this);
    }
    
    public void autowireBeanProperties(Object obj) {
    	checkForAvailablityOfApplicationContext();
    	configurableApplicationContext.getBeanFactory().autowireBeanProperties(obj, 
        		AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
    }

	@Override
	protected String[] getConfigLocations() {
		return configLocations;
	}
	
	 
    public void setConfigLocation(String[] configLocation) {
    	if (configLocation == null || configLocation.length == 0) {
    		throw new IllegalArgumentException("Cannot create spring cntext without configurations.");
    	}
        this.configLocations = configLocation;
    }

    @Override    
    public void startNewTransaction() {
    	checkForAvailablityOfApplicationContext();
    	super.startNewTransaction();
    }
    
    @Override
    public void endTransaction() {
    	checkForAvailablityOfApplicationContext();
    	super.endTransaction();
    }
    
    private void checkForAvailablityOfApplicationContext() {
    	if (configurableApplicationContext == null) {
    		throw new IllegalStateException("Application context not initialized." 
    				+ " Please call initializeContext with appropriate Spring configurations");
    	}    	
    }

}
