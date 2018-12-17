/**
 *
 */
package tavant.twms.external;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.actions.TwmsActionSupport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

/**
 * @author kiran.sg
 *
 */
@SuppressWarnings("serial")
public class GroovyScriptAction extends TwmsActionSupport {

	private static final Logger logger = Logger
			.getLogger(GroovyScriptAction.class);

	private Object groovyShellService;

	private String script;

	private String output;

	@Override
    public String execute() {
		if (this.groovyShellService == null) {
			initGroovyShellService();
			if (this.groovyShellService == null) {
				addActionError("Please run the Webapp in Debug Mode");
				if(logger.isInfoEnabled())
				{
				    logger.info("Webapp not built in debug mode");
				}
				return ERROR;
			}
		}
		if (this.script != null && this.script.trim().length() != 0) {
			Class params[] = { String.class };
			try {
				Method method = this.groovyShellService.getClass()
						.getDeclaredMethod("execute", params);
				this.output = (String) method.invoke(this.groovyShellService,
						new Object[] { this.script });
			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				this.output = sw.toString();
			}
		}
		return SUCCESS;
	}

	private void initGroovyShellService() {
		BeanLocator beanLocator = new BeanLocator();
		try {
			this.groovyShellService = beanLocator.lookupBean("groovyShellService");
		} catch (NoSuchBeanDefinitionException e) {
			logger.error("Not able to lookup groovyShellService");
		}
	}

	public String getScript() {
		return this.script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getOutput() {
		return this.output;
	}
}
