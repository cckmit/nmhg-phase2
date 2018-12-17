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

import org.apache.tools.ant.BuildException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StringUtils;
import tavant.twms.ant.utils.Action;

/**
 * @author radhakrishnan.j
 */
public class ExecuteSQLs implements Action {
    private String driverClassName;
    private String databaseUserName;
    private String jdbcUrl;
    private String sqls;
    private String databasePassword;
    private DriverManagerDataSource dataSource;

    public ExecuteSQLs() {

    }

    public ExecuteSQLs(DriverManagerDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public ExecuteSQLs(String driverClassName, String jdbcUrl,
                       String databaseUserName, String databasePassword) {
        this.driverClassName = driverClassName;
        this.databaseUserName = databaseUserName;
        this.jdbcUrl = jdbcUrl;
        this.databasePassword = databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public void setDatabaseUser(String databaseUserName) {
        this.databaseUserName = databaseUserName;
    }

    public void setSqls(String sqls) {
        this.sqls = sqls;
    }

    public void setDatabaseUserName(String databaseName) {
        this.databaseUserName = databaseName;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setDataSource(DriverManagerDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void perform() {
        if (dataSource == null) {
            dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(driverClassName);
            dataSource.setUrl(jdbcUrl);
            dataSource.setUsername(databaseUserName);
            dataSource.setPassword(databasePassword);
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String[] statements = StringUtils.tokenizeToStringArray(sqls, ";");
        for (String statement : statements) {
            jdbcTemplate.execute(statement);
        }
    }

    public void throwErrorOnInvalidInputs() {
        if (dataSource == null) {
            if (driverClassName == null) {
                throw new BuildException("Missing input 'databaseName'");
            } else if (databaseUserName == null) {
                throw new BuildException("Missing input 'databaseName'");
            } else if (jdbcUrl == null) {
                throw new BuildException("Missing input 'jdbcUrl'");
            } else if (sqls == null) {
                throw new BuildException("Missing input 'sqls'");
            } else if (databasePassword == null) {
                throw new BuildException("Missing input 'databasePassword'");
            }
        }
    }
}
