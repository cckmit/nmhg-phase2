package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see EventAdaptor
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class EventAdaptorTag extends AbstractUITag {

    private String listen;
    private String publish;
    private String resetOn;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new EventAdaptor(stack, request, response);
    }

    @Override
    public void populateParams() {
        super.populateParams();
        EventAdaptor adaptor = (EventAdaptor) component;
        adaptor.setListen(listen);
        adaptor.setPublish(publish);
        if(resetOn != null) {
            adaptor.setResetOn(resetOn);
        }
    }

	public String getListen() {
		return listen;
	}

	public void setListen(String listen) {
		this.listen = listen;
	}

	public String getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = publish;
	}

    public String getResetOn() {
        return resetOn;
    }

    public void setResetOn(String resetOn) {
        this.resetOn = resetOn;
    }
}
