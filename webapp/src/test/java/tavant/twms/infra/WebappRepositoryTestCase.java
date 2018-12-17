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

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import tavant.twms.security.SecurityHelper;

public abstract class WebappRepositoryTestCase extends
        AbstractTransactionalDataSourceSpringContextTests {

    private final SecurityHelper securityHelper = new SecurityHelper();

    @Override
    public String[] getConfigLocations() {
        return new String[] { "classpath:unittest-env-context.xml", "classpath*:/app-context.xml",
                "classpath:test-context.xml", "classpath:test-seeddataResources-context.xml" };
    }

    protected WebappRepositoryTestCase() {
        setAutowireMode(AUTOWIRE_BY_NAME);
    }

    /**
     * Overrides and delegates the real setup to the {@link #setUpInTxnRollbackOnFailure()} method.
     * Does a rollback of the transaction if the delegate method returns abnormally.
     */
    @Override
    public final void onSetUpInTransaction() throws Exception {
        super.onSetUpInTransaction();
        try {
            setUpInTxnRollbackOnFailure();
        } catch (Throwable t) {
            this.logger.error(this.getClass().getName()
                    + ".setUpInTxnRollbackOnFailure() returned abnormally. "
                    + "Rolling back txn to avoid side-effects.", t);
        }

    }

    /**
     * Load an ApplicationContext from the given config locations.
     * 
     * @param locations
     *                the config locations
     * @return the corresponding ApplicationContext instance (potentially cached)
     */
    @Override
    protected ConfigurableApplicationContext loadContextLocations(String[] locations)
            throws Exception {
        if (this.logger.isInfoEnabled()) {
            this.logger.info("Loading context for: "
                    + StringUtils.arrayToCommaDelimitedString(locations));
        }
        ServletContext servletContext = new MockServletContext();
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setServletContext(servletContext);
        appContext.setConfigLocations(locations);
        appContext.refresh();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
                this.applicationContext);
        return appContext;
    }

    public WebApplicationContext getApplicationContext() {
        return (WebApplicationContext) this.applicationContext;
    }

    /**
     * A safe method for performing setup operations. Test writers are encounraged to use this API
     * instead of the
     * 
     * @throws DatabaseUnitException
     * @throws SQLException
     * @throws IOException
     * @throws DataSetException
     */
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        this.securityHelper.doDefaultAuthentication();
    }
}
