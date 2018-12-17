package tavant.twms.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.components.Anchor;

import com.opensymphony.xwork2.util.ValueStack;

public class MultipleInventoryPicker extends Anchor{
	 private boolean tagWasUsedBefore;
	 private final Logger logger;
	 
	private String searchTitle;
    private String searchInputUrl;
    private String searchLinkLabel;
    private String searchLinkClass;
    private String searchHandlerUrl;
    private String selectionHandlerUrl;
    private String selectedItemsContentPane;
    private String useInnerHTML;
    private String searchHandlerParams;
    private String cssClass;
    private String searchInvType;
    private String searchActionType;
    private String isMultiLineUser;
    private String isRestrictedBuListDisplayed;
    
    final public static String TEMPLATE = "multipleinventorypicker";
    

    public MultipleInventoryPicker(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
        this.logger = Logger.getLogger(this.getClass());
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if(this.searchTitle!=null){
            addParameter("searchTitle", findString(this.searchTitle));
        }
        if(this.searchTitle!=null) {
			addParameter("searchInputUrl", findString(this.searchInputUrl));
		}
        if(this.searchLinkLabel!=null) {
			addParameter("searchLinkLabel", findString(this.searchLinkLabel));
		}
        if(this.searchLinkClass!=null) {
			addParameter("searchLinkClass", findString(this.searchLinkClass));
		}
        if(this.searchHandlerUrl!=null) {
			addParameter("searchHandlerUrl", findString(this.searchHandlerUrl));
		}
        if(this.selectionHandlerUrl!=null) {
			addParameter("selectionHandlerUrl", findString(this.selectionHandlerUrl));
		}
        if(this.selectedItemsContentPane!=null) {
			addParameter("selectedItemsContentPane", findString(this.selectedItemsContentPane));
		}
        if(this.useInnerHTML!=null) {
			addParameter("useInnerHTML", findString(this.useInnerHTML));
		}
        if(this.searchHandlerParams !=null) {
			addParameter("searchHandlerParams", findString(this.searchHandlerParams));
		}
        if(this.cssClass !=null) {
			addParameter("cssClass", findString(this.cssClass));
		}
        if(this.searchInvType!=null) {
			addParameter("searchInvType", findString(this.searchInvType));
		}
        if(this.searchActionType!=null) {
			addParameter("searchActionType", findString(this.searchActionType));
		}
        if(this.isMultiLineUser!=null) {
			addParameter("isMultiLineUser", findString(this.isMultiLineUser));
		}
        if(this.isRestrictedBuListDisplayed!=null) {
			addParameter("isRestrictedBuListDisplayed", findString(this.isMultiLineUser));
		}
        addParameter("tagWasUsedBefore", this.tagWasUsedBefore);
    }

	public boolean isTagWasUsedBefore() {
		return this.tagWasUsedBefore;
	}

	public void setTagWasUsedBefore(boolean tagWasUsedBefore) {
		this.tagWasUsedBefore = tagWasUsedBefore;
	}

	public String getSearchTitle() {
		return this.searchTitle;
	}

	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	public String getSearchInputUrl() {
		return this.searchInputUrl;
	}

	public void setSearchInputUrl(String searchInputUrl) {
		this.searchInputUrl = searchInputUrl;
	}

	public String getSearchLinkLabel() {
		return this.searchLinkLabel;
	}

	public void setSearchLinkLabel(String searchLinkLabel) {
		this.searchLinkLabel = searchLinkLabel;
	}

	public String getSearchLinkClass() {
		return this.searchLinkClass;
	}

	public void setSearchLinkClass(String searchLinkClass) {
		this.searchLinkClass = searchLinkClass;
	}

	public String getSearchHandlerUrl() {
		return this.searchHandlerUrl;
	}

	public void setSearchHandlerUrl(String searchHandlerUrl) {
		this.searchHandlerUrl = searchHandlerUrl;
	}

	public String getSelectionHandlerUrl() {
		return this.selectionHandlerUrl;
	}

	public void setSelectionHandlerUrl(String selectionHandlerUrl) {
		this.selectionHandlerUrl = selectionHandlerUrl;
	}

	public String getSelectedItemsContentPane() {
		return this.selectedItemsContentPane;
	}

	public void setSelectedItemsContentPane(String selectedItemsContentPane) {
		this.selectedItemsContentPane = selectedItemsContentPane;
	}

	public String getUseInnerHTML() {
		return this.useInnerHTML;
	}

	public void setUseInnerHTML(String useInnerHTML) {
		this.useInnerHTML = useInnerHTML;
	}

	public String getSearchHandlerParams() {
		return this.searchHandlerParams;
	}

	public void setSearchHandlerParams(String searchHandlerParams) {
		this.searchHandlerParams = searchHandlerParams;
	}

	public String getSearchInvType() {
		return searchInvType;
	}

	public void setSearchInvType(String searchInvType) {
		this.searchInvType = searchInvType;
	}

	public String getSearchActionType() {
		return searchActionType;
	}
	public void setSearchActionType(String searchActionType) {
		this.searchActionType = searchActionType;
	}

    public String getMultiLineUser() {
        return isMultiLineUser;
    }

    public void setMultiLineUser(String multiLineUser) {
        isMultiLineUser = multiLineUser;
    }

    public String getRestrictedBuListDisplayed() {
        return isRestrictedBuListDisplayed;
    }

    public void setRestrictedBuListDisplayed(String restrictedBuListDisplayed) {
        isRestrictedBuListDisplayed = restrictedBuListDisplayed;
    }
}
