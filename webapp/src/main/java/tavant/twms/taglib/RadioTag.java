package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Radio
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class RadioTag extends InputComponentTag {
    
    public String list;
    public String listLabel;
    public String listValue;
    
    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Radio(stack, request, response);
    }
    
    @Override
    public void populateParams() {
        super.populateParams();
        Radio field = (Radio) component;
        field.setList(list);
        field.setListLabel(listLabel);
        field.setListValue(listValue);
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public String getListLabel() {
        return listLabel;
    }

    public void setListLabel(String listLabel) {
        this.listLabel = listLabel;
    }

    public String getListValue() {
        return listValue;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }
}
