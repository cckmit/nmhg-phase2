package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author janmejay.singh
 * Date: Apr 17, 2007
 * Time: 2:36:32 PM
 */
public class ActionResultTag extends AbstractUITag {

    private String wipeMessages;
    private String wipeOutTime;

    private String errorsMessageKey;
    private String warningsMessageKey;
    private String messagesMessageKey;

    @Override
	public Component getBean(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return new ActionResult(valueStack, httpServletRequest, httpServletResponse);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        ActionResult results = (ActionResult) this.component;
        results.setWipeMessages(this.wipeMessages);
        results.setWipeOutTime(this.wipeOutTime);
        results.setErrorsMessageKey(this.errorsMessageKey);
        results.setWarningsMessageKey(this.warningsMessageKey);
        results.setMessagesMessageKey(this.messagesMessageKey);
    }

    public void setWipeMessages(String wipeMessages) {
        this.wipeMessages = wipeMessages;
    }

    public void setWipeOutTime(String wipeOutTime) {
        this.wipeOutTime = wipeOutTime;
    }

    public void setErrorsMessageKey(String errorsMessageKey) {
        this.errorsMessageKey = errorsMessageKey;
    }

    public void setWarningsMessageKey(String warningsMessageKey) {
        this.warningsMessageKey = warningsMessageKey;
    }

    public void setMessagesMessageKey(String messagesMessageKey) {
        this.messagesMessageKey = messagesMessageKey;
    }
}
