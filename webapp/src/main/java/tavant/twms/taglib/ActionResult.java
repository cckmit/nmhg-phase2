package tavant.twms.taglib;

import static tavant.twms.taglib.TaglibUtil.getBoolean;
import static tavant.twms.taglib.TaglibUtil.getInt;
import static tavant.twms.taglib.TaglibUtil.isUsedBefore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.UIBean;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * @author janmejay.singh
 * Date: Apr 17, 2007
 * Time: 2:33:40 PM
 */
public class ActionResult extends UIBean {

    public static final String TEMPLATE = "actionResult";

    private String wipeMessages;
    private String wipeOutTime;

    private String errorsMessageKey;
    private String warningsMessageKey;
    private String messagesMessageKey;

    public ActionResult(ValueStack valueStack, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        super(valueStack, httpServletRequest, httpServletResponse);
    }

    @SuppressWarnings("unchecked")
	@Override
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();
        boolean wipeMessages = true;//will wipe by default
        if(this.wipeMessages != null) {
            wipeMessages = getBoolean(findValue(this.wipeMessages, Boolean.class));
        }
        addParameter("wipeMessages", wipeMessages);
        int wipeOutTime = 20000;//will wipe after 3 seconds by default
        if(this.wipeOutTime != null) {
            wipeOutTime = getInt(findValue(this.wipeOutTime, Integer.class));
        }
        addParameter("wipeOutTime", wipeOutTime);
        Collection errors = (Collection) findValue("actionErrors", Collection.class);
        appendFieldErrors(errors);
        addParameter("errors", errors);
        Collection messages = (Collection) findValue("actionMessages", Collection.class);
        addParameter("messages", messages);
        
        Collection warnings = (Collection) findValue("actionWarnings", Collection.class);
        addParameter("warnings", warnings);
        
        addParameter("tagWasUsedBefore", isUsedBefore(this.request, this.getClass()));
        
        boolean errorsOrWarningsExist = (errors.size() > 0 || (warnings!=null && warnings.size() > 0));
        addParameter("showComponentBody", (errorsOrWarningsExist || messages.size() > 0));
        addParameter("errorsOrWarningsExist", errorsOrWarningsExist);

        addParameter("messagesMessageKey",
                this.messagesMessageKey != null ? this.messagesMessageKey : "label.common.messages");
        addParameter("warningsMessageKey",
                this.warningsMessageKey != null ? this.warningsMessageKey : "label.common.warnings");
        addParameter("errorsMessageKey",
                this.errorsMessageKey != null ? this.errorsMessageKey : "label.common.errors");
    }


	// TODO: Needs a cleanup. Implemented a crude way for now.
	// The field errors have a List<String> as a value.
	@SuppressWarnings("unchecked")
	private void appendFieldErrors(Collection errors) {
		Collection<List<String>> values = (Collection) findValue(
				"fieldErrors.values", Collection.class);
		Collection<String> fieldErrorValues = new HashSet<String>();
		for (Iterator<List<String>> it = values.iterator(); it.hasNext();) {
			List<String> fieldErrorValue = it.next();
			fieldErrorValues.add(fieldErrorValue.get(0));
		}
		errors.addAll(fieldErrorValues);
	}

	@Override
	protected String getDefaultTemplate() {
        return TEMPLATE;
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
