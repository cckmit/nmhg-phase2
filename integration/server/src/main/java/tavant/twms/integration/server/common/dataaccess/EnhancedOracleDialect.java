package tavant.twms.integration.server.common.dataaccess;

import org.hibernate.Hibernate;
import org.hibernate.dialect.Oracle9Dialect;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class EnhancedOracleDialect extends Oracle9Dialect {
	public EnhancedOracleDialect() {
		super();
		registerFunction("monthname", new MonthNameSQLFunction("monthname",
				Hibernate.STRING));
	}

}