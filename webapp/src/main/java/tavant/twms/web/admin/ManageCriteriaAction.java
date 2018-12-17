/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.web.admin;

import com.opensymphony.xwork2.Preparable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.web.i18n.I18nActionSupport;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author vineeth.varghese
 * 
 */
public abstract class ManageCriteriaAction extends I18nActionSupport implements Preparable {

    private boolean isDealerGroupSelected;

    private final Map<Boolean, String> yesNo = new HashMap<Boolean, String>();

    protected DealerGroupService dealerGroupService;

    protected CatalogService catalogService;
    
	private ClaimService claimService;

    public List<ClaimType> claimTypes = new ArrayList<ClaimType>();

    public void prepare() throws Exception {
        // TODO Internationalize these labels
//        this.warrantyTypeList.put("ALL", "All");
       /* this.claimTypeList.put("ALL", ClaimType.ALL.getDisplayType());
        this.claimTypeList.put("PARTS", ClaimType.PARTS.getDisplayType());
        this.claimTypeList.put("MACHINE", ClaimType.MACHINE.getDisplayType());
        this.claimTypeList.put("CAMPAIGN", ClaimType.CAMPAIGN.getDisplayType());
        this.claimTypeList.put("ATTACHMENT", ClaimType.ATTACHMENT.getDisplayType());*/
        setClaimTypes();

        this.yesNo.put(true, getText("yes"));
        this.yesNo.put(false, getText("no"));
    }

    public String getCurrentWarrantyType() {
        Criteria criteria = getCriteria();
        if ((criteria == null) || (criteria.getWarrantyType() == null)) {
            return "ALL";
        } else {
            return criteria.getWarrantyType();
        }
    }

    public String getCurrentClaimType() {
        Criteria criteria = getCriteria();
        if ((criteria == null) || (criteria.getClaimType() == null)) {
            return "ALL";
        } else {
            return criteria.getClaimType().name();
        }
    }

    public String getSelectedWarrantyType() {
        Criteria criteria = getCriteria();
        if ((criteria == null) || (criteria.getWarrantyType() == null)) {
            return getText("label.common.allWarrantyTypes");
        } else {
            return criteria.getWarrantyType();
        }
    }

    public String getSelectedClaimType() {
        Criteria criteria = getCriteria();
        if ((criteria == null) || (criteria.getClaimType() == null)) {
            return getText("label.common.allClaimTypes");
        } else {
            return getText(criteria.getClaimType().getDisplayType());
        }
    }

    public String getSelectedProduct() {
        Criteria criteria = getCriteria();
        if ((criteria == null) || (criteria.getProductType() == null)) {
            return getText("label.common.allProductTypes");
        } else {
            return criteria.getProductType().getName();
        }
    }

    public String getSelectedDealerOrGroup() {
        Criteria criteria = getCriteria();
        if ((criteria == null) || (criteria.getDealerCriterion() == null)) {
            return getText("label.common.allDealers");
        } else {
            DealerCriterion dealerCriterion = criteria.getDealerCriterion();
            return dealerCriterion.isGroupCriterion() ? dealerCriterion.getDealerGroup().getName()
                    : dealerCriterion.getDealer().getName();
        }
    }

    protected void validateProductType() {
    	String productType;
    	if(getCriteria().getProductType()!=null){
    		productType= getCriteria().getProductType().getName();
    	}else{
    		productType="";
    	}
    	if (StringUtils.isNotBlank(productType)) {
    		ItemGroup itemGroup = this.catalogService.findProductOrModelWhoseNameIs(productType);
    		if (itemGroup == null) {
    			addFieldError(getCriteriaOgnlExp() + ".productType.name",
    					"error.partReturnConfiguration.invalidProductType",
    					new String[] { productType });
    		}
    		getCriteria().setProductType(itemGroup);
    	} else {
    		getCriteria().setProductType(null);
    	}
    }

    protected void validateDealerCriterion() {
        Criteria criteria = getCriteria();
        String dealerName = criteria.getDealerCriterion().getDealer().getName();
        String dealerGroupName = criteria.getDealerCriterion().getDealerGroup().getName();
        if (this.isDealerGroupSelected && StringUtils.isNotBlank(dealerGroupName)) {
            criteria.getDealerCriterion().setDealer(null);
            DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(dealerGroupName,
                    getPurpose());
            if (dealerGroup == null) {
                // FIXME need to change this message key
                addFieldError(getCriteriaOgnlExp() + ".dealerCriterion.dealerGroup.name",
                        "error.partReturnConfiguration.invalidDealerGroup",
                        new String[] { dealerGroupName });
            } else {
                criteria.getDealerCriterion().setDealerGroup(dealerGroup);
            }
        } else if (!this.isDealerGroupSelected && StringUtils.isNotBlank(dealerName)) {
            criteria.getDealerCriterion().setDealerGroup(null);
            ServiceProvider dealership = this.orgService.findDealerByName(dealerName);
            if (dealership == null) {
                // FIXME need to change this message key
                addFieldError(getCriteriaOgnlExp() + ".dealerCriterion.dealer.name",
                        "error.partReturnConfiguration.invalidDealerName",
                        new String[] { dealerName });
            } else {
                criteria.getDealerCriterion().setDealer(dealership);
            }
        } else {
            criteria.setDealerCriterion(null);
        }
    }

    @Override
    public void validate() {
        validateDealerCriterion();
        validateProductType();
        if(ClaimType.ALL.getType().equals(getCriteria().getClaimType().getType())){
        	getCriteria().setClaimType("");
        }
    }

    public abstract Criteria getCriteria();

    public abstract String getCriteriaOgnlExp();

    public abstract String getPurpose();

    public boolean isDealerGroupSelected() {
        return this.isDealerGroupSelected;
    }

    public void setDealerGroupSelected(boolean isDealerGroupSelected) {
        this.isDealerGroupSelected = isDealerGroupSelected;
    }

	/*public Map<String, String> getClaimTypeList() {
        return this.claimTypeList;
    }
*/
    public Map<Boolean, String> getYesNo() {
        return this.yesNo;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}

    public void setClaimTypes() {
        this.claimTypes = this.claimService.fetchAllClaimTypesForBusinessUnit();
	}
}
