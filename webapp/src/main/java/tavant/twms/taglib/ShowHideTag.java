package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

/**
 * @see ShowHide
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class ShowHideTag extends AbstractClosingTag {

    private String showOn;
    private String hideOn;
    private String publishOnShow;
    private String publishOnHide;
    private String tagType;
    private String visibleInitially;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new ShowHide(stack, request, response);
    }

    @Override
    public void populateParams() {
        super.populateParams();
        ShowHide element = (ShowHide) component;
        element.setShowOn(showOn);
        element.setHideOn(hideOn);
        element.setPublishOnHide(publishOnHide);
        element.setPublishOnShow(publishOnShow);
        element.setTagType(tagType);
        element.setVisibleInitially(visibleInitially);
    }

    public String getVisibleInitially() {
        return visibleInitially;
    }

    public void setVisibleInitially(String visibleInitially) {
        this.visibleInitially = visibleInitially;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
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
}
