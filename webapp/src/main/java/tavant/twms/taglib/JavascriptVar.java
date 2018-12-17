package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import static tavant.twms.taglib.TaglibUtil.BLANK_FTL_NAME;
import static tavant.twms.taglib.TaglibUtil.seperateMarkupAndScript;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;

/**
 * <ul>
 *
 * <li>Creates a tag that reads the innerHTML and saves it as a javascript variable.</li>
 *
 * </ul>
 *
 * @author janmejay.singh
 */
public class JavascriptVar extends ClosingUIBean {

    private String varName;
    private String ajaxMode;

    public static final String MARKUP = "markup",
                               SCRIPT = "script";

    final public static String TEMPLATE = "javascriptVar";

    public JavascriptVar(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    public String getDefaultOpenTemplate() {
        return BLANK_FTL_NAME;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        boolean ajaxMode = Boolean.parseBoolean(this.ajaxMode);
        validate(ajaxMode);
        if(varName != null) addParameter("varName", findString(varName));
        addParameter("ajaxMode", ajaxMode);
    }

    private void validate(boolean ajaxMode) {
        if(varName == null && !ajaxMode) {
            throw new IllegalStateException("Either varName should be set, or ajaxMode should be set to true.");
        } else if(varName != null && ajaxMode) {
            throw new IllegalStateException("When ajaxMode is set to true, varName cannot be specified... " +
                    "it doesn't make any sence.");
        }
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    public void setAjaxMode(String ajaxMode) {
        this.ajaxMode = ajaxMode;
    }

    @Override
    public boolean end(Writer writer, String body) {
        String[] markupAndScript = seperateMarkupAndScript(body, false);
        addParameter(MARKUP, markupAndScript[0]);
        addParameter(SCRIPT, markupAndScript[1]);
        return super.end(writer, "");
    }

    @Override
    public boolean usesBody() {
        return true;
    }
}