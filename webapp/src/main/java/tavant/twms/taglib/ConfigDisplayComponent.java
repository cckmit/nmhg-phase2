package tavant.twms.taglib;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Button;
import org.apache.ecs.html.Html;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.P;
import org.apache.ecs.html.Script;
import org.apache.ecs.html.Span;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;

import org.apache.struts2.components.Component;

import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.security.model.OrgAwareUserDetails;

import com.opensymphony.xwork2.util.ValueStack;

public class ConfigDisplayComponent extends Component {

	public static final String STRING_INPUT = "string";
	public static final String NUMBER_INPUT = "number";
	public static final String BOOLEAN_INPUT = "boolean";
	public static final String LIST_INPUT = "list";
	public static final int MAX_TEXT_SIZE = 4000;

	protected String param;
	protected String cssClass;

	public ConfigDisplayComponent(ValueStack stack) {
		super(stack);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean start(Writer writer) {
		try {
			OrgAwareUserDetails userDetail = (OrgAwareUserDetails) SecurityContextHolder
			.getContext().getAuthentication().getPrincipal();
			List<ConfigParam> configParams = (List<ConfigParam>) getStack().findValue(param);
			Table table = new Table();
			table.setWidth("100%");
			for (ConfigParam param : configParams) {
				TR row = new TR();
				row.setClass(cssClass);
				TD column1 = new TD();
				column1.setTagText(param.getDisplayName());
				TD column2 = new TD();
				TD column3 = new TD();
				Span error = new Span();
				error.setClass("error");
				column3.addElement(error);
				/*
				 * With the assumption that when a input type is either a string
				 * or number there would be only a single ConfigValue for the
				 * ConfigParam
				 */
				if (STRING_INPUT.equalsIgnoreCase(param.getType())
						|| NUMBER_INPUT.equalsIgnoreCase(param.getType())) {
					for (ConfigValue cfgValue : param.getValues()) {
						if (cfgValue != null
								&& userDetail.getDefaultBusinessUnit()
										.getName().equals(
												cfgValue.getBusinessUnitInfo()))  {
							P p = new P();
							p.setID("" + cfgValue.getId());
							p.setCase(P.MIXEDCASE);
							p.addAttribute("dojoType", "inlineEditBox");
							p.addAttribute("mode", "text");
							p.addAttribute("name", "" + cfgValue.getId());
							p.setTagText(cfgValue.getValue());
							column2.addElement(p);
							Script javaScript = new Script();
							javaScript.setType("text/javascript");
							javaScript.setTagText(" dojo.addOnLoad("
									+ "function(){	"
									+ "var editable = dojo.widget.byId( \""
									+ cfgValue.getId()
									+ "\");"
									+ "dojo.event.connect(editable, 'onSave', saveHandler);"
									+ "dojo.event.connect(editable,'onChange',cancelCall);"
									+ "dojo.event.connect(editable,'onCancel',changeCall);"
									+ "}" + ");");
							error.setID("error_" + cfgValue.getId());
							row.addElement(javaScript);
						}
					}
				} else if (BOOLEAN_INPUT.equalsIgnoreCase(param.getType())) {
					for (ConfigValue cfgValue : param.getValues()) {
						if (cfgValue != null
								&& userDetail.getDefaultBusinessUnit()
										.getName().equals(
												cfgValue.getBusinessUnitInfo())) {
							Input radioElement1 = new Input();
							radioElement1.setType(Input.RADIO);
							radioElement1.setName("configValueId"
									+ cfgValue.getId());
							radioElement1.setValue("true");
							radioElement1.setTagText("Yes");
							radioElement1.setID("radio" + cfgValue.getId()
									+ "0");
							radioElement1.addAttribute("onChange",
									"handleRadioChange(" + cfgValue.getId()
											+ ")");
							Input radioElement2 = new Input();
							radioElement2.setType(Input.RADIO);
							radioElement2.setName("configValueId"
									+ cfgValue.getId());
							radioElement2.setValue("false");
							radioElement2.setTagText("No");
							radioElement2.setID("radio" + cfgValue.getId()
									+ "1");
							radioElement2.addAttribute("onChange",
									"handleRadioChange(" + cfgValue.getId()
											+ ")");
							if (Boolean.parseBoolean(cfgValue.getValue())) {
								radioElement1.setChecked(true);
							} else {
								radioElement2.setChecked(true);
							}
							column2.addElement(radioElement1);
							column2.addElement(radioElement2);
						}
					}
				} else if (LIST_INPUT.equalsIgnoreCase(param.getType())) {
					for (ConfigValue configValue : param.getValues()) {
						if (configValue!= null && userDetail.getDefaultBusinessUnit().getName()
								.equals(configValue.getBusinessUnitInfo())) {
							Input chkBoxElement = new Input();
							column2.addElement(chkBoxElement);
							column2.addElement(new BR());
							chkBoxElement.setType("checkbox");
							chkBoxElement.setCase(Input.MIXEDCASE);
							chkBoxElement.addAttribute("dojoType", "Checkbox");
							chkBoxElement.addAttribute("name", "" + configValue.getId());
							chkBoxElement.setID(""+configValue.getId());
							chkBoxElement.setTagText(configValue.getValue());
							if(configValue.getActive()){
								chkBoxElement.addAttribute("checked", "checked");
							}
							Script javaScript = new Script();
							javaScript.setType("text/javascript");
							javaScript.setTagText(" dojo.addOnLoad("
									+ "function(){	"
									+"dojo.widget.byId( '"+ configValue.getId()+ "').onClick =handleCheckboxChange"
									+ "}" + ");");
							row.addElement(javaScript);
						}
					}
				}
				row.addElement(column1);
				row.addElement(column2);
				row.addElement(column3);
				table.addElement(row);
			}
			writer.write(table.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
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
