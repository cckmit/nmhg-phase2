package tavant.twms.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.UIBean;

import com.opensymphony.xwork2.util.ValueStack;

public class ConfigParamBean extends UIBean {

	private static final String TEMPLATE = "buconfig.ftl";
	
	private String type;
	
	private String paramId;
	
	private String index;
	
	private String paramName;
	
	private String val;
	
	
	public String getDefaultTemplate(){
		return TEMPLATE;
	}
	
	public ConfigParamBean(ValueStack valuestack,HttpServletRequest request, HttpServletResponse response ){
		super(valuestack, request, response);
	}

	@Override
	protected void evaluateExtraParams() {	
		super.evaluateExtraParams();
		addParameter("type", this.type);
		addParameter("paramName", this.paramName);
		addParameter("val", this.val);			
		addParameter("index",this.index);
		addParameter("paramId",this.paramId);

	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}


	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getParamId() {
		return paramId;
	}

	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}




	
	
	
	
}
