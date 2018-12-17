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
import org.apache.struts2.dojo.components.AbstractRemoteBean;

/**
 * <ul>
 * 
 * <li>Creates an Anchor tag.</li>
 * <li>It can optionally publish an event on click.</li>
 *
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="a" tld-body-content="JSP" description="AnchorTag tag" tld-tag-class="tavant.twms.taglib.AnchorTag"
 */
public class Anchor extends AbstractRemoteBean {
    
    private boolean tagWasUsedBefore;

    private String url;
    private String tagType;
    private String publishOnClick;
    
    final public static String OPEN_TEMPLATE = "twms_anchor";
    final public static String TEMPLATE = "twms_anchor-close";
    
    public Anchor(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
    }

    @Override
    public String getDefaultOpenTemplate() {
        return OPEN_TEMPLATE;
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if (url != null) {
            addParameter("url", findString(url));
        }
        if(tagType != null) {
            addParameter("tagType", tagType);
        } else {
            addParameter("tagType", "a");
        }
        if(publishOnClick != null) {
            addParameter("publishOnClick", findString(publishOnClick));
        }
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
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
