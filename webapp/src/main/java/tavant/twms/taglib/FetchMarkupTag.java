package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

/**
 * @see FetchMarkup
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class FetchMarkupTag extends AbstractClosingTag {

    private String on;
    private String url;
    private String basedOnIds;
    private String publishOnChange;
    private String tagType;
    private String yftColor;
    private long yftDuration = 1000;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new FetchMarkup(stack, request, response);
    }

    @Override
    public void populateParams() {
        super.populateParams();
        FetchMarkup element = (FetchMarkup) component;
        element.setOn(on);
        element.setUrl(url);
        element.setPublishOnChange(publishOnChange);
        if(basedOnIds != null) {
            element.setAssociatedFieldIds(basedOnIds);
        }
        element.setTagType(tagType);
        element.setYftColor(yftColor);
        element.setYftDuration(yftDuration);
    }

    public String getOn() {
        return on;
    }

    public void setOn(String event) {
        this.on = event;
    }

    public String getBasedOnIds() {
        return basedOnIds;
    }

    public void setBasedOnIds(String ids) {
        this.basedOnIds = ids;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishOnChange() {
        return publishOnChange;
    }

    public void setPublishOnChange(String publishOnChange) {
        this.publishOnChange = publishOnChange;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
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
