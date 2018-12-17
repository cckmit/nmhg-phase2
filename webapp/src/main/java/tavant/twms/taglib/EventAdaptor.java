package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <ul>
 * <li>Generates javascript, which listens to multiple events(given in listen attribute), and on reciving them all, publishes multiple events(given in publish attribute).</li> 
 * <li>It can be reset by using resetOn parametrer, in which multiple events can be configured to reset the adapter, once reset, it'll require all the events published atleast once again to publish the events given in 'publish' attribute.</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="evtAdpt" tld-body-content="empty" description="EventAdapter tag" tld-tag-class="tavant.twms.taglib.EventAdapterTag"
 */
public class EventAdaptor extends UIBean {
    
    public static final String TEMPLATE = "twms_eventAdaptor";
    
    private boolean tagWasUsedBefore;
    
    private List<String> listen;
    private List<String> publish;
    private List<String> resetOn;

    public EventAdaptor(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
    }
    
    @Override
    public void evaluateExtraParams() {
    	super.evaluateExtraParams();
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        addParameter("listen", listen);
        addParameter("publish", publish);
        if(resetOn != null) {
            addParameter("resetOn", resetOn);
        }
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

	public List<String> getListen() {
		return listen;
	}

	public void setListen(String listen) {
		this.listen = TaglibUtil.splitBasedOnComma(listen);
	}

	public List<String> getPublish() {
		return publish;
	}

	public void setPublish(String publish) {
		this.publish = TaglibUtil.splitBasedOnComma(publish);
	}

	public void setListen(List<String> listen) {
		this.listen = listen;
	}

	public void setPublish(List<String> publish) {
		this.publish = publish;
	}

    public List<String> getResetOn() {
        return resetOn;
    }

    public void setResetOn(List<String> resetOn) {
        this.resetOn = resetOn;
    }
    
    public void setResetOn(String resetOn) {
        this.resetOn = TaglibUtil.splitBasedOnComma(resetOn);
    }
}
