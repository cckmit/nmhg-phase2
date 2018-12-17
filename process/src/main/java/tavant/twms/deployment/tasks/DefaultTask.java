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
package tavant.twms.deployment.tasks;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import tavant.twms.ant.utils.Action;

import java.io.IOException;

/**
 * @author radhakrishnan.j
 */
public abstract class DefaultTask implements Action, ApplicationContextAware {
    protected ApplicationContext applicationContext;
    private ExecuteSQLs executeSQLs;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        initExecuteSQLs();
    }

    private void initExecuteSQLs() {
        if (executeSQLs == null) {
            DriverManagerDataSource ds = (DriverManagerDataSource)
                    applicationContext.getBean("dataSource",
                            DriverManagerDataSource.class);

            executeSQLs = new ExecuteSQLs(ds);
        }
    }

    public void throwErrorOnInvalidInputs() {
        if (applicationContext == null) {
            throw new IllegalArgumentException("Missing input 'applicationContext'");
        }
    }

    protected boolean doesTableExist(String tableName)
            throws IOException {

        try {
            executeSQLs.setSqls("select * from " + tableName);
            executeSQLs.perform();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            executeSQLs.setSqls(null); // Clean up.
        }
    }
}
