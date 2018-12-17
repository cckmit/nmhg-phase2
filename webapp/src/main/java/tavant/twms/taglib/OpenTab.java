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
import org.apache.log4j.Logger;
import org.apache.struts2.components.ClosingUIBean;
import static tavant.twms.taglib.TaglibUtil.getBoolean;

/**
 * <ul>
 *
 * <li>Creates a Anchor tag, with click resulting in opening of a new tab or focusing of one, is the requested TabLabel is already open</li>
 * <li>autoPickDecendentOf is not a mandatory param, but if set to true, utility.js must be included.</li>
 * <li>Please don't use autopick as a feature, this is only to be used when tag is included in multiple places.</li>
 *
 * </ul>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  &lt;t:openTab&gt;
 *          &lt;div&gt;Anchor for opening new tab.&lt;/div&gt;
 *  &lt;/t:openTab&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author janmejay.singh
 * @t.tag name="openTab" tld-body-content="JSP" description="OpenTab tag" tld-tag-class="tavant.twms.taglib.OpenTabTag"
 */
public class OpenTab extends ClosingUIBean {

    private boolean tagWasUsedBefore;
    private Logger logger;

    private String tabLabel;
    private String forceNewTab;
    private String decendentOf;
    private String catagory;
    private String autoPickDecendentOf;
    private String url;
    private String tagType;
    private String helpCategory;

    final public static String OPEN_TEMPLATE = "openTab";
    final public static String TEMPLATE = "openTab-close";

    public OpenTab(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
        logger = Logger.getLogger(this.getClass());
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
        addParameter("url", findString(url));
        addParameter("tagType", tagType != null ? findString(tagType) : "a");
        if (tabLabel != null) {
            addParameter("tabLabel", findString(tabLabel));
        }
        if (decendentOf != null) {
            addParameter("decendentOf", findString(decendentOf));
        }
        if (forceNewTab != null) {
            addParameter("forceNewTab", findValue(forceNewTab, Boolean.class));
        }
        if(catagory != null) {
            addParameter("catagory", findString(catagory));
        }
        if(helpCategory != null) {
            addParameter("helpCategory", findString(helpCategory));
        }
        addParameter("autoPickDecendentOf",
                this.autoPickDecendentOf != null && getBoolean(findValue(this.autoPickDecendentOf, Boolean.class)));
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
    }

    public String getDecendentOf() {
        return decendentOf;
    }

    public void setDecendentOf(String decendentOf) {
        this.decendentOf = decendentOf;
    }

    public String isForceNewTab() {
        return forceNewTab;
    }

    public void setForceNewTab(String forceNewTab) {
        this.forceNewTab = forceNewTab;
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
    
    public String getHelpCategory() 
	{
		return helpCategory;
	}

	public void setHelpCategory(String helpCatagory) 
	{
		this.helpCategory = helpCatagory;
	}
    
    @Override
    public boolean usesBody() {
        return true;
    }
}