package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.ClosingUIBean;
import static tavant.twms.config.UiConfReader.isPropertySet;
import static tavant.twms.taglib.TaglibUtil.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static java.lang.Boolean.*;

/**
 * @author : janmejay.singh
 * Date : May 11, 2007
 * Time : 12:42:28 AM
 */
public class Body extends ClosingUIBean {

    public static final String OPEN_TEMPLATE = "body", TEMPLATE = "body-close";

    private String smudgeAlert;
    private String loadingMessageKey;

    protected Body(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("cssThemeBaseDir", getCssThemeBaseDir());
        addParameter("cssTheme", getCssTheme(stack));
        addParameter("showLid", isPropertySet("loadingLid.enable"));
        addParameter("loadingMessageKey", loadingMessageKey == null ?
                "javascript.var.loading_message" : loadingMessageKey);
        addParameter("smudgeAlert",
                smudgeAlert == null ? isPropertySet("win32.alert.smudge") : parseBoolean(smudgeAlert));
    }

    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setSmudgeAlert(String smudgeAlert) {
        this.smudgeAlert = smudgeAlert;
    }

    public void setLoadingMessageKey(String loadingMessageKey) {
        this.loadingMessageKey = loadingMessageKey;
    }
}
