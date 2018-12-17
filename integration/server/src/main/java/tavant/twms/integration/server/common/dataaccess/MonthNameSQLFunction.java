package tavant.twms.integration.server.common.dataaccess;

import java.util.List;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

/**
 * 
 * @author roopali.agrawal
 * 
 */
public class MonthNameSQLFunction extends StandardSQLFunction {

	/**
	 * Construct a standard SQL function definition with a variable return type;
	 * the actual return type will depend on the types to which the function is
	 * applied. <p/> Using this form, the return type is considered non-static
	 * and assumed to be the type of the first argument.
	 * 
	 * @param name
	 *            The name of the function.
	 */
	public MonthNameSQLFunction(String name) {
		super(name);
	}

	/**
	 * Construct a standard SQL function definition with a static return type.
	 * 
	 * @param name
	 *            The name of the function.
	 * @param type
	 *            The static return type.
	 */
	public MonthNameSQLFunction(String name, Type type) {
		super(name, type);
	}

	/**
	 * {@inheritDoc}
	 */
	public String render(List args, SessionFactoryImplementor factory) {
		StringBuffer buf = new StringBuffer();
		buf.append("to_char("+args.get(0)+",'month')");		
		return buf.toString();
	}

}

