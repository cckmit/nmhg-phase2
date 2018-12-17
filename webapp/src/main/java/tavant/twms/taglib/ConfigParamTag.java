package tavant.twms.taglib;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.ComponentTag;

import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigValue;

import com.opensymphony.xwork2.util.ValueStack;

@SuppressWarnings("serial")
public class ConfigParamTag extends ComponentTag{

	private String paramName;
	
	@Override
	public Component getBean(ValueStack stack, HttpServletRequest req,
			HttpServletResponse res) {
		ConfigParam configParam = (ConfigParam) getStack().findValue(paramName);
		ConfigParamBean configParamBean = new ConfigParamBean(stack,req,res);
		configParamBean.setParamName(this.paramName);
		configParamBean.setType(configParam.getParamDisplayType());
		configParamBean.setParamId(String.valueOf(configParam.getId()));
		Integer index =  (Integer)getStack().findValue("#iter.index");
		if(index !=null){
		  configParamBean.setIndex(String.valueOf(index));	
		}		
		List<ConfigValue> values = configParam.getValues();
		if (values != null && values.size() > 0) {
			if (configParam.getParamDisplayType().equals("textbox") ) {
				  configParamBean.setVal(values.get(0).getValue());
			} else if ( configParam.getParamDisplayType().equals("radio")){
				ConfigValue cfgValue = values.get(0);
				if(cfgValue.getConfigParamOption()!=null){
					configParamBean.setVal(String.valueOf(cfgValue.getConfigParamOption().getValue()));
				} 
			}
		}
		
		return configParamBean;
	}

	public String getParamName() {
		return paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}



	
}
