package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;
import static tavant.twms.taglib.TaglibUtil.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: janmejay.singh
 * Date: Aug 8, 2007
 * Time: 4:02:36 PM
 */
public class StylePicker extends UIBean {

    private static final String TEMPLATE = "twms_stylePicker";

    private String fileName;
    private String cssTheme;
    private String common;

    public StylePicker(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        super(stack, req, res);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("fileName", findString(fileName));
        addParameter("cssTheme", cssTheme != null ? cssTheme : getCssTheme(stack));
        addParameter("cssThemeBaseDir", getCssThemeBaseDir());
        addParameter("cssCommonBaseDir", getCssCommonDir());
        if (common == null) {
            common = "false";
        }
        addParameter("common", Boolean.parseBoolean(common));
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
