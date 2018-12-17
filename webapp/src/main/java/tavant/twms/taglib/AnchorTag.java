/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractClosingTag;

/**
 * @see Anchor
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class AnchorTag extends AbstractClosingTag {
    
    private String url;
    private String tagType;
    private String publishOnClick;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Anchor(stack, request, response);
    }
    
    @Override
    protected void populateParams() {
        super.populateParams();
        Anchor link = (Anchor) component;
        
        link.setUrl(url);
        link.setTagType(tagType);
        link.setPublishOnClick(publishOnClick);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTagType() {
        return tagType;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }

    public String getPublishOnClick() {
        return publishOnClick;
    }

    public void setPublishOnClick(String publishOnClick) {
        this.publishOnClick = publishOnClick;
    }
}
