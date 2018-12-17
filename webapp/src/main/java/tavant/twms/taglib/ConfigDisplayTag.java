package tavant.twms.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import com.opensymphony.xwork2.util.ValueStack;

public class ConfigDisplayTag extends ComponentTagSupport{
	
	private static final long serialVersionUID = 1L;

	String param;
	
	String cssClass;

	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,HttpServletResponse res) {
		return new ConfigDisplayComponent(stack);
	}
	
	@Override
	protected void populateParams() {
		super.populateParams();
		ConfigDisplayComponent tag = (ConfigDisplayComponent)component;
		tag.setParam(param);
		tag.setCssClass(cssClass);
	}
	
	@Override
	public int doEndTag() throws JspException {	
		return SKIP_BODY;
	}
	
	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

}
