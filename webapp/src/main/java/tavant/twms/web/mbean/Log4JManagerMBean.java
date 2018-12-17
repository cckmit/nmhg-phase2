package tavant.twms.web.mbean;

import java.util.Enumeration;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com">Vikas Sasidharan</a>
 * Date: 22 Sep, 2010
 * Time: 2:43:04 PM
 */

@ManagedResource(
        objectName = "bean:name=log4jManager", description = "View and change logging levels"
)
public class Log4JManagerMBean {
    private static final String LOGGER_NOT_FOUND_MESSAGE_PREFIX = "No logger found for the category: ";

    @ManagedOperation(description = "View the logging level for a specified category")
    @ManagedOperationParameters(
            @ManagedOperationParameter(name = "categoryName",
                    description = "FQN of the java package or class (e.g. org.hibernate)")
    )
    public String getLoggingLevel(String categoryName) {
        final Logger logger = LogManager.exists(categoryName);
        if (logger == null) {
            return LOGGER_NOT_FOUND_MESSAGE_PREFIX + categoryName;
        } else {
            final Level level = logger.getLevel();
            return level == null ? "NOT SET" : level.toString();
        }
    }

    @ManagedOperation(description = "Set the logging level for a specified category")
    @ManagedOperationParameters({
            @ManagedOperationParameter(name = "categoryName",
                    description = "FQN of the java package or class (e.g. org.hibernate)"),
            @ManagedOperationParameter(name = "loggingLevel",
                    description = "The desired logging level (DEBUG/ERROR etc.)")
    })
    public void setLoggingLevel(String categoryName, String loggingLevel) {
        final Logger logger = LogManager.exists(categoryName);
        if (logger == null) {
            throw new RuntimeException(LOGGER_NOT_FOUND_MESSAGE_PREFIX + categoryName);
        } else {
            logger.setLevel(Level.toLevel(loggingLevel));
        }
    }

    @ManagedOperation(description = "Check if logging of SQL queries is enabled.")
    public String isQueryLoggingEnabled() {
        final Logger queryLogger = LogManager.exists("org.hibernate.SQL");

        boolean isEnabled = (queryLogger != null) && Level.DEBUG.equals(queryLogger.getLevel());

        return String.valueOf(isEnabled);
    }

    @ManagedOperation(description = "Enable SQL Logging")
    public void enableQueryLogging() {
        setQueryLoggingEnabled(true);
    }

    @ManagedOperation(description = "Disable SQL Logging")
    public void disableQueryLogging() {
        setQueryLoggingEnabled(false);
    }
    

    private void setQueryLoggingEnabled(boolean isEnabled) {
        final Logger queryLogger = LogManager.exists("org.hibernate.SQL");

        if (queryLogger == null) {
            throw new RuntimeException("Loggers are not setup for hibernate query logging!");
        } else {
            Level logLevel = isEnabled ? Level.DEBUG : Level.ERROR;

            queryLogger.setLevel(logLevel);

            final Logger paramsLogger = LogManager.exists("org.hibernate.type");
            if(paramsLogger != null) {
                paramsLogger.setLevel(logLevel);
            }
        }
    }
}
