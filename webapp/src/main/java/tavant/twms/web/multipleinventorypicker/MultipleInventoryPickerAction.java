package tavant.twms.web.multipleinventorypicker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletResponseAware;
import org.springframework.util.StringUtils;

import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.*;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.infra.PageSpecification;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 19 Nov, 2007
 * Time: 9:53:43 AM
 */
public class MultipleInventoryPickerAction extends I18nActionSupport implements ServletResponseAware {

    private List<InventoryItem> inventoryItems = new ArrayList<InventoryItem>(5);
    private InventorySearchCriteria inventorySearchCriteria;
    private Integer pageNo = new Integer(0);
    private InventoryService inventoryService;
    private HttpServletResponse response;
    private boolean sendInventoryId;
    private int size;
    private List<Integer> pageNoList = new ArrayList<Integer>();
    private String selectedItemsIds;
    private String coverageAction ;
    private ConfigParamService configParamService;
    private InvTransationType transationType;
    private String actionType;
    
   
    public List<InventoryItem> getInventoryItems() {
        return this.inventoryItems;
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public InventorySearchCriteria getInventorySearchCriteria() {
        return this.inventorySearchCriteria;
    }

    public void setInventorySearchCriteria(InventorySearchCriteria inventorySearchCriteria) {
        this.inventorySearchCriteria = inventorySearchCriteria;
    }

    public InventoryService getInventoryService() {
        return this.inventoryService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public String setupSearch() {
        return SUCCESS;
    }

    public String searchInventories() throws IOException {
    	
        addSearchConstraints();

        if(!isValidSearchRequest()) {
            return sendValidationResponse();
        }
        Pagination pagination = new Pagination();
        pagination.setPageNumber(this.pageNo);
		pagination.setPageSize(50);
		this.inventoryItems = inventoryService.findAllItemsMatchingCriteria(this.inventorySearchCriteria, pagination, new PageSpecification());
        this.setSize(this.inventoryItems.size());
        for (int i=0;i<pagination.getNoOfPages();i++){
        	this.pageNoList.add(new Integer(i+1));
        }
        return SUCCESS;
    }

    protected void addSearchConstraints() {
             
    }

    public String handleInventorySelection() throws IOException {
    	List<InventoryItem> selectedInvItems = getSelectedInvItems();
    	setInventoryItems(selectedInvItems);
        if(!isValidSelection()) {
            return sendValidationResponse();
        }
        this.setSize(this.inventoryItems.size());
        return SUCCESS;
    }
    
    public List<InventoryItem> getSelectedInvItems(){
		 this.selectedItemsIds=this.selectedItemsIds.replaceAll(" ","");
//       Removal of duplicate id's which were coming is fixed in multipleInventorySelection.js
//       Just making sure of it over here as well
		 String[] stringArray = StringUtils.removeDuplicateStrings(this.selectedItemsIds.split(","));
		 List<Long> idList = new ArrayList<Long>();
		 for (int i = 0; i < stringArray.length; i++) {
			 Long idValue = Long.parseLong(stringArray[i]);
			 idList.add(idValue);
		 }
		 List<InventoryItem> selectedItems=this.inventoryService.findInventoryItemsForIds(idList);
		 return selectedItems;
	 }

    protected boolean isValidSelection() {
        return true;
    }

    protected String sendValidationResponse() throws IOException {
        this.response.setHeader("Pragma", "no-cache");
        this.response.addHeader("Cache-Control", "must-revalidate");
        this.response.addHeader("Cache-Control", "no-cache");
        this.response.addHeader("Cache-Control", "no-store");
        this.response.setDateHeader("Expires", 0);
        this.response.setContentType("text/html");
        this.response.getWriter().write("<true>");
        this.response.flushBuffer();
        return null;
    }

    protected boolean isValidSearchRequest() {
        if (this.inventorySearchCriteria != null) {
            if (StringUtils.hasText(this.inventorySearchCriteria.getItemModel())  ||
                  (inventorySearchCriteria.getSelectedBusinessUnits() != null &&
                   !inventorySearchCriteria.getSelectedBusinessUnits().isEmpty() && 
                   getLoggedInUser().getBusinessUnits().size()>1) ||
                    StringUtils.hasText(this.inventorySearchCriteria.getSerialNumber()) ||
                    StringUtils.hasText(this.inventorySearchCriteria.getFactoryOrderNumber()) ||
                    StringUtils.hasText(this.inventorySearchCriteria.getDealerName()) ||
                    StringUtils.hasText(this.inventorySearchCriteria.getDealerNumber()) ||
                    StringUtils.hasText(this.inventorySearchCriteria.getModelNumber()) ||
                    (this.inventorySearchCriteria.getCustomer() != null &&
                            (StringUtils.hasText(this.inventorySearchCriteria.getCustomer().getCompanyName()) ||
                                    StringUtils.hasText(this.inventorySearchCriteria.getCustomer().getCorporateName())) )) {
                return true;
            }
        }

        return false;
    }
    
    public boolean isFactoryOrderNumberRequired() {
		return this.configParamService
				.getBooleanValue(ConfigName.ENABLE_FACTORY_ORDER_NUMBER.getName());
	}
    
    public ConfigParamService getConfigParamService() {
		return this.configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public boolean isSendInventoryId() {
        return this.sendInventoryId;
    }

    public void setSendInventoryId(boolean sendInventoryId) {
        this.sendInventoryId = sendInventoryId;
    }

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Integer getPageNo() {
		return pageNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}

	public List<Integer> getPageNoList() {
		return pageNoList;
	}

	public void setPageNoList(List<Integer> pageNoList) {
		this.pageNoList = pageNoList;
	}

	public String getSelectedItemsIds() {
		return selectedItemsIds;
	}

	public void setSelectedItemsIds(String selectedItemsIds) {
		this.selectedItemsIds = selectedItemsIds;
	}

	public String getCoverageAction() {
		return coverageAction;
	}

	public void setCoverageAction(String coverageAction) {
		this.coverageAction = coverageAction;
	}

    public String getSelectedBusinessUnit() {
        return SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
    }

    public HttpServletResponse getServletResponse() {
        return this.response;
    }

    public InvTransationType getTransationType() {
        return transationType;
    }

    public void setTransationType(InvTransationType transationType) {
        this.transationType = transationType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public List<BusinessUnit> getRestrictedBu(){
        if("RMT".equals(actionType)){
            return configParamService.configApplicableForBuWithValue(ConfigName.CAN_DEALER_PERFORM_RMT.getName(),TRUE);
        }
        return new ArrayList<BusinessUnit>(); // returnung empty Set so that code doesn't break beacuse of null value
    }
}
