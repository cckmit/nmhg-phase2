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
package tavant.twms.web.search;

import com.opensymphony.xwork2.Preparable;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.web.common.LabelsPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 */
@SuppressWarnings("serial")
public class InventorySearchAction extends SummaryTableAction implements ServletRequestAware, Preparable {

    private Logger logger = Logger.getLogger(InventorySearchAction.class);

    private InventoryService inventoryService;
    private InventoryItem inventoryItem;
    private Collection<Claim> previousClaimsForItem;
    private ClaimService claimService;
    private boolean isDifferentDealerAndOwner;
    private PolicyService policyService;
    private Collection<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
    private CatalogService catalogService;
    private String domainPredicateId;
    private String savedQueryId;
    private HttpServletRequest servletRequest;
    private ConfigParamService configParamService;


    public static final String SHOW_SEARCH_PARAM = "show_search_param";

    @Override
	protected PageResult<?> getBody() {

        PageResult<InventoryItem> inventoryItems =null;
        if(domainPredicateId!=null &&!("".equals(domainPredicateId.trim()))){
        inventoryItems = inventoryService.findAllItemsMatchingQuery(
        			Long.parseLong(domainPredicateId),getCriteria(), getLoggedInUser().getBelongsToOrganization());
        }
        return inventoryItems;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		tableHeadData = new ArrayList<SummaryTableColumn>();
		tableHeadData.add(new SummaryTableColumn("columnTitle.inventorySearchAction.hidden", "id", 0, "String", "id", false, true, true, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.inventorySearchAction.serial_no", "serialNumber", 11, "String", "serialNumber", true, false, false, false));
        if(isVisibleAcrossAnyBu(ConfigName.ENABLE_FACTORY_ORDER_NUMBER.toString()))
        {
        	tableHeadData.add(new SummaryTableColumn("label.common.factoryOrderNumber", "factoryOrderNumber", 14, "String"));
        }	
		tableHeadData.add(new SummaryTableColumn("columnTitle.common.product",
				"ofType.product.groupCode", 26, "String"));
		tableHeadData.add(new SummaryTableColumn("columnTitle.inventorySearchAction.item_model", "ofType.model.description", 14, "String"));       	
       /* tableHeadData.add(new SummaryTableColumn("columnTitle.inventorySearchAction.item_number", "ofType.number", 14, "String"));*/
        /*tableHeadData.add(new SummaryTableColumn("label.common.description", "ofType.description", 10, "String",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));*/  
		tableHeadData.add(new SummaryTableColumn("label.common.seriesDescription", "ofType.product.description", 10,"string", SummaryTableColumnOptions.NO_SORT));
        tableHeadData.add(new SummaryTableColumn("label.common.machineAge", "machineAge", 10, "String",SummaryTableColumnOptions.NO_FILTER | SummaryTableColumnOptions.NO_SORT));
        tableHeadData.add(new SummaryTableColumn("", "imageCol", 2, IMAGE, "labelsImg", false, false, false, false));        
        return tableHeadData;
	}
	
	protected boolean isVisibleAcrossAnyBu(String configName){
        boolean isVisible = false;
        Map<String, List<Object>> buValues = configParamService.getValuesForAllBUs(configName);
        for (String buName : buValues.keySet()) {
              Boolean booleanValue = new Boolean (buValues.get(buName).get(0).toString());
              if(booleanValue){
                 isVisible=true;
                 break;
              }
        }
        return isVisible;
    }

	@Override
	protected BeanProvider getBeanProvider() {
		return new LabelsPropertyResolver();
	}

	public CatalogService getCatalogService() {
        return catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public Collection<RegisteredPolicy> getPolicies() {
        return policies;
    }

    public void setPolicies(Collection<RegisteredPolicy> policies) {
        this.policies = policies;
    }

    public PolicyService getPolicyService() {
        return policyService;
    }

    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }

    public boolean getIsDifferentDealerAndOwner() {
        return isDifferentDealerAndOwner;
    }

    public void setIsDifferentDealerAndOwner(boolean isDifferentDealerAndOwner) {
        this.isDifferentDealerAndOwner = isDifferentDealerAndOwner;
    }

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public Collection<Claim> getPreviousClaimsForItem() {
        return previousClaimsForItem;
    }

    public void setPreviousClaimsForItem(Collection<Claim> previousClaimsForItem) {
        this.previousClaimsForItem = previousClaimsForItem;
    }

    public InventoryItem getInventoryItem() {
        return inventoryItem;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {
        this.inventoryItem = inventoryItem;
    }

    public InventoryService getInventoryService() {
        return inventoryService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // Need to set this value. Commented out for now.
    public void setUnderWarrantyPolicies(String[] underWarrantyPolicies) {
        //inventorySearchCriteria.setUnderWarrantyPolicies(underWarrantyPolicies);
    }

	public String getDomainPredicateId() {
		return domainPredicateId;
	}

	public void setDomainPredicateId(String domainPredicateId) {
		this.domainPredicateId = domainPredicateId;
	}

	public String getSavedQueryId() {
		return savedQueryId;
	}

	public void setSavedQueryId(String savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}

	public void setServletRequest(HttpServletRequest servletRequest) {
		this.servletRequest = servletRequest;
	}

	public void prepare() throws Exception {
		if(getServletRequest().getAttribute("savedQueryId")!=null) {
			savedQueryId=getServletRequest().getAttribute("savedQueryId").toString();
		}
		if(getServletRequest().getAttribute("domainPredicateId")!=null) {
			domainPredicateId=getServletRequest().getAttribute("domainPredicateId").toString();
		}
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public boolean isEligibleForExtendedWarrantyPurchase() {
		boolean isEligible = false;
		Map<String, List<Object>> buValues = configParamService
				.getValuesForAllBUs(ConfigName.CAN_EXTERNAL_USER_PURCHASE_EXTENDED_WARRANTY
						.getName());
		for (String buName : buValues.keySet()) {
			Boolean booleanValue = new Boolean(buValues.get(buName).get(0)
					.toString());
			if (booleanValue) {
				isEligible = true;
				break;
			}
		}
		return isEligible;
	}

    public boolean isDealerEligibleToPerformRMT() {
        boolean isEligible = false;
        if (isLoggedInUserADealer()) {
            Map<String, List<Object>> buValues = configParamService.
                    getValuesForAllBUs(ConfigName.CAN_DEALER_PERFORM_RMT.getName());
            for (String buName : buValues.keySet()) {
                Boolean booleanValue = new Boolean(buValues.get(buName).get(0).toString());
                if (booleanValue) {
                    isEligible = true;
                    break;
                }
            }
        }
        return isEligible;
    }
	
    public boolean isStockClaimAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.STOCK_CLAIM_ALLOWED
                                .getName());
    }
    
    public boolean isD2DAllowed(){
    	return this.configParamService
                .getBooleanValue(ConfigName.D2D_ALLOWED
                                .getName());
    }
}
