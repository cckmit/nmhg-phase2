package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.dojo.components.AbstractRemoteBean;

/**
 * <ul>
 * <li>Creates a tag(div by default), with given content, which fetches new markup(and shows) on listening to a given event.</li> 
 * <li>It can also publish an event on the fetch.</li>
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="fetchMarkup" tld-body-content="JSP" description="FetchMarkup tag" tld-tag-class="tavant.twms.taglib.FetchMarkupTag"
 */
public class FetchMarkup extends AbstractRemoteBean {

    public static final String OPEN_TEMPLATE = "twms_fetchMarkup",
    			       TEMPLATE = "twms_fetchMarkup-close";

    private String on;
    private String url;
    private List<String> basedOnIds;
    private String publishOnChange;
    private String tagType;
    private String yftColor;
    private long yftDuration;
    
    private boolean tagWasUsedBefore;

    public FetchMarkup(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        addParameter("on", findString(on));
        addParameter("url", findString(url));
        if(publishOnChange != null) {
            addParameter("publishOnChange", findString(publishOnChange));
        }
        if(basedOnIds != null) {
            addParameter("basedOnIds", basedOnIds);
        }
        if(tagType != null) {
            addParameter("tagType", tagType);
        }
        if(yftColor != null) {
            addParameter("yftColor", yftColor);
            addParameter("yftDuration", yftDuration);
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

    public String getOn() {
        return on;
    }

    public void setOn(String event) {
        this.on = event;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getBasedOnIds() {
        return basedOnIds;
    }

    public void setBasedOnIds(List<String> ids) {
        this.basedOnIds = ids;
    }

    public void setAssociatedFieldIds(String ids) {
        this.basedOnIds = TaglibUtil.splitBasedOnComma(ids);
    }

    public String getPublishOnChange() {
        return publishOnChange;
    }

    public void setPublishOnChange(String publishOnChange) {
        this.publishOnChange = publishOnChange;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getTagType() {
        return tagType;
    }
    
    public String getYftColor() {
        return yftColor;
    }

    public void setYftColor(String color) {
        this.yftColor = color;
    }

    public long getYftDuration() {
        return yftDuration;
    }

    public void setYftDuration(long duration) {
        this.yftDuration = duration;
    }
}
