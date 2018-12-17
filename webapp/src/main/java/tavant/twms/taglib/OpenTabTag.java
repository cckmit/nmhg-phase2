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
 * @see OpenTab
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class OpenTabTag extends AbstractClosingTag {

    private String tabLabel;
    private String forceNewTab;
    private String decendentOf;
    private String catagory;
    private String autoPickDecendentOf;
    private String url;
    private String tagType;
    private String helpCategory;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new OpenTab(stack, request, response);
    }

    @Override
    protected void populateParams() {
        super.populateParams();

        OpenTab link = (OpenTab) component;

        link.setTabLabel(tabLabel);
        link.setForceNewTab(forceNewTab);
        link.setDecendentOf(decendentOf);
        link.setCatagory(catagory);
        link.setHelpCategory(helpCategory);
        link.setAutoPickDecendentOf(autoPickDecendentOf);
        link.setTagType(tagType);
        link.setUrl(url);
    }

    public String getDecendentOf() {
        return decendentOf;
    }

    public void setDecendentOf(String decendentOf) {
        this.decendentOf = decendentOf;
    }

    public String getForceNewTab() {
        return forceNewTab;
    }

    public void setForceNewTab(String forceNewTab) {
        if(forceNewTab.equalsIgnoreCase("true") || forceNewTab.equalsIgnoreCase("false")) {
            this.forceNewTab = forceNewTab;
        } else {
            throw new IllegalArgumentException("The attribute forceNewTab has illegal value. Legal values are only 'true' or 'false'.");
        }
    }

    public String getTabLabel() {
        return tabLabel;
    }

    public void setTabLabel(String tabLabel) {
        this.tabLabel = tabLabel;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
    }

    public void setAutoPickDecendentOf(String autoPickDecendentOf) {
        this.autoPickDecendentOf = autoPickDecendentOf;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTagType(String tagType) {
        this.tagType = tagType;
    }
    
    public String getHelpCategory() {
		return helpCategory;
	}

	public void setHelpCategory(String helpCategory) {
		this.helpCategory = helpCategory;
	}
}