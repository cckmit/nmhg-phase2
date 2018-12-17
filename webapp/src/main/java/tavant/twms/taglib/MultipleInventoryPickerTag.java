package tavant.twms.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.views.jsp.ui.AnchorTag;
import org.apache.struts2.components.Component;

import com.opensymphony.xwork2.util.ValueStack;

public class MultipleInventoryPickerTag extends AnchorTag {
	
    private String searchTitle;
    private String searchInputUrl;
    private String searchLinkLabel;
    private String searchLinkClass;
    private String searchHandlerUrl;
    private String selectionHandlerUrl;
    private String selectedItemsContentPane;
    private String useInnerHTML;
    private String searchHandlerParams;
    private String searchInvType;
    private String searchActionType;
    private String isMultiLineUser;
    private String isRestrictedBuListDisplayed;
    

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new MultipleInventoryPicker(stack, request, response);
    }
    @Override
    protected void populateParams() {
        super.populateParams();

        MultipleInventoryPicker inventoryPicker = (MultipleInventoryPicker) this.component;

        inventoryPicker.setSearchTitle(this.searchTitle);
        inventoryPicker.setSearchInputUrl(this.searchInputUrl);
        inventoryPicker.setSearchLinkLabel(this.searchLinkLabel);
        inventoryPicker.setSearchLinkClass(this.searchLinkClass);
        inventoryPicker.setSearchHandlerUrl(this.searchHandlerUrl);
        inventoryPicker.setSelectionHandlerUrl(this.selectionHandlerUrl);
        inventoryPicker.setSelectedItemsContentPane(this.selectedItemsContentPane);
        inventoryPicker.setUseInnerHTML(this.useInnerHTML);
        inventoryPicker.setSearchHandlerParams(this.searchHandlerParams);
        inventoryPicker.setCssClass(this.cssClass);
        inventoryPicker.setSearchInvType(this.searchInvType);
        inventoryPicker.setSearchActionType(this.searchActionType);
        inventoryPicker.setMultiLineUser(this.isMultiLineUser);
        inventoryPicker.setRestrictedBuListDisplayed(this.isRestrictedBuListDisplayed);
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
	public String getCssClass() {
		return this.cssClass;
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
