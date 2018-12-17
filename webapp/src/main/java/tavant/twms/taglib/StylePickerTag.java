package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: janmejay.singh
 * Date: Aug 8, 2007
 * Time: 4:00:19 PM
 */
public class StylePickerTag extends AbstractUITag {

    private String fileName;
    private String cssTheme;
    private String common;

    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new StylePicker(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();
        StylePicker stylePicker = (StylePicker) component;
        stylePicker.setFileName(fileName);
        stylePicker.setCssTheme(cssTheme);
        stylePicker.setCommon(common);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCssTheme(String cssTheme) {
        this.cssTheme = cssTheme;
    }

    public void setCommon(String common) {
        this.common = common;
    }
}
