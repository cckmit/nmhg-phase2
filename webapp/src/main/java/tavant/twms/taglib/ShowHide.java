package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dojo.components.AbstractRemoteBean;

/**
 * <ul>
 * <li>Creates a tag(div by default), with given content, which shows up and hides on listening to events showOn and hideOn.</li> 
 * <li>It can also publish an event on the show/hide event.</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="showHide" tld-body-content="JSP" description="ShowHide tag" tld-tag-class="tavant.twms.taglib.ShowHideTag"
 */
public class ShowHide extends AbstractRemoteBean {

    public static final String OPEN_TEMPLATE = "twms_showHide",
    			       TEMPLATE = "twms_showHide-close";

    private String showOn;
    private String hideOn;
    private String publishOnShow;
    private String publishOnHide;
    private String tagType;
    private String visibleInitially;

    private boolean tagWasUsedBefore;

    public ShowHide(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
        visibleInitially = Boolean.toString(true);
    }

    @Override
    public void evaluateExtraParams() {
        validate();
        super.evaluateExtraParams();
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        if(showOn != null) {
            addParameter("showOn", showOn);
        }
        if(hideOn != null) {
            addParameter("hideOn", hideOn);
        }
        if(publishOnShow != null) {
            addParameter("publishOnShow", publishOnShow);
        }
        if(publishOnHide != null) {
            addParameter("publishOnHide", publishOnHide);
        }
        if(tagType != null) {
            addParameter("tagType", tagType);
        } else {
            addParameter("tagType", "div");
        }
        if(visibleInitially != null) {
            addParameter("visibleInitially", findValue(visibleInitially, Boolean.class));
        }
    }

    private void validate() {
        if((showOn == null) && (hideOn == null)) {
            throw new IllegalArgumentException("Atleast one of the two attributes(showOn and hideOn) must be defined.");
        }
        if((showOn == null) & (publishOnShow != null)) {
            throw new IllegalArgumentException("publishOnShow can not be used if showOn is not defined.");
        }
        if((hideOn == null) & (publishOnHide != null)) {
            throw new IllegalArgumentException("publishOnHide can not be used if hideOn is not defined.");
        }
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagType() {
        return tagType;
    }

    public String getHideOn() {
        return hideOn;
    }

    public void setHideOn(String hideOn) {
        this.hideOn = hideOn;
    }

    public String getPublishOnHide() {
        return publishOnHide;
    }

    public void setPublishOnHide(String publishOnHide) {
        this.publishOnHide = publishOnHide;
    }

    public String getPublishOnShow() {
        return publishOnShow;
    }

    public void setPublishOnShow(String publishOnShow) {
        this.publishOnShow = publishOnShow;
    }

    public String getShowOn() {
        return showOn;
    }

    public void setShowOn(String showOn) {
        this.showOn = showOn;
    }

    public String getVisibleInitially() {
        return visibleInitially;
    }

    public void setVisibleInitially(String visibleInitially) {
        this.visibleInitially = visibleInitially;
    }
}