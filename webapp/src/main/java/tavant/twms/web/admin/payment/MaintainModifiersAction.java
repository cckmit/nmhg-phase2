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
package tavant.twms.web.admin.payment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.claim.payment.definition.modifiers.CriteriaBasedValue;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifier;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifierAdminService;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author Kiran.Kollipara
 * 
 */
/**
 * @author prashanth.konda
 *
 */
@SuppressWarnings("serial")
public class MaintainModifiersAction extends I18nActionSupport implements Validateable, Preparable ,BUSpecificSectionNames {

    private final Logger logger = Logger.getLogger(MaintainModifiersAction.class);

    private static final String MESSAGE_KEY_OVERLAP = "error.managePayment.durationOverlap";

    private static final String MESSAGE_KEY_DUPLICATE = "error.managePayment.duplicateCriteria";

    private static final String MESSAGE_KEY_UPDATE = "message.managePayment.updateModifierSuccess";

    private static final String MESSAGE_KEY_CREATE = "message.managePayment.createModifierSuccess";

    private static final String MESSAGE_KEY_DELETE = "message.managePayment.deleteModifierSuccess";

    private String id;

    private String paymentVariableId;
    
    private PaymentModifier modifier;

    private List<CriteriaBasedValue> entries = new ArrayList<CriteriaBasedValue>();

    public List<ClaimType> claimTypes = new ArrayList<ClaimType>();

    // Fields for Autocomplete
    private String dealerCriterion;

    private String productType;

    private String dealerGroupName;

    private CatalogService catalogService;

    private PaymentModifierAdminService paymentModifierAdminService;

    private DealerGroupService dealerGroupService;

    private boolean dealerGroupSelected;
            
    private Map<Object,Object> customerTypes = new HashMap<Object,Object>();
    
    private ConfigParamService configParamService;

    private ClaimService claimService;

    private OrgService orgService;
    
    private Boolean landedCost;
    
    private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();

    private WarrantyService warrantyService;

	public Boolean getLandedCost() {
		return landedCost;
	}

	public void setLandedCost(Boolean landedCost) {
		this.landedCost = landedCost;
	}

	public List<WarrantyType> getWarrantyTypes() {
		return warrantyTypes;
	}

	public void setWarrantyTypes(List<WarrantyType> warrantyTypes) {
		this.warrantyTypes = warrantyTypes;
	}

	@Required
	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}


	private List<OrganizationAddress> servicingLocations;

	public List<OrganizationAddress> getServicingLocations() {
		if(this.servicingLocations != null){
			Collections.sort(this.servicingLocations, new Comparator(){
				public int compare(Object obj0, Object obj1){
					OrganizationAddress address0 = (OrganizationAddress)obj0;
					OrganizationAddress address1 = (OrganizationAddress)obj1;
					return address0.getShipToCodeAppended().compareTo(address1.getShipToCodeAppended());
				}
			});
		}
		return servicingLocations;
	}

	public void setServicingLocations(List<OrganizationAddress> servicingLocations) {
		this.servicingLocations = servicingLocations;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public boolean isDealerGroupSelected() {
        return this.dealerGroupSelected;
    }

    public void setDealerGroupSelected(boolean dealerGroupSelected) {
        this.dealerGroupSelected = dealerGroupSelected;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void prepare() throws Exception {
        if (StringUtils.isNotBlank(this.id)) {
            Long pk = Long.parseLong(this.id);
            this.modifier = this.paymentModifierAdminService.findById(pk);
            if (this.modifier.getCriteria() != null) {
                DealerCriterion dealerCriterion = this.modifier.getCriteria().getDealerCriterion();
                if (dealerCriterion != null && dealerCriterion.isGroupCriterion()) {
                    this.dealerGroupSelected = true;
                }
            }
        } else {
            Assert.hasText(this.paymentVariableId, "Payment Variable cannot be null or empty");

            this.modifier = new PaymentModifier();            
            this.modifier.setForPaymentVariable(this.paymentModifierAdminService.findPaymentVariableById(Long.parseLong(this.paymentVariableId)));
        }
        setClaimTypes();
        populateCustomerTypes();
        setWarrantyTypes(this.warrantyService.listWarrantyTypes());
    }

    @Override
    public void validate() {
        Criteria forCriteria = this.modifier.getForCriteria();
        if (forCriteria == null) {
            this.modifier.setForCriteria(new Criteria());
        }

        super.validate();
        validateDealerCriterion();
        validateProductName();    
        validateRateConfigurations();
    }

    

	

	public String showModifier() {
        this.entries = new ArrayList<CriteriaBasedValue>();
        this.entries.addAll(this.modifier.getEntries());
        Criteria forCriteria = this.modifier.getCriteria();
        if (forCriteria != null && forCriteria.getDealerCriterion() != null) {
            this.dealerCriterion = forCriteria.getDealerCriterion().getIdentifier();
            this.dealerGroupName = forCriteria.getDealerCriterion().getIdentifier();
        }
        if (forCriteria != null && forCriteria.getProductType() != null) {
            this.productType = forCriteria.getProductType().getName();
        }
        return SUCCESS;
    }

    public String createModifier() throws Exception {
        String action = prepareModifier();
        if (SUCCESS.equals(action)) {
            this.paymentModifierAdminService.save(this.modifier);
            addActionMessage(MESSAGE_KEY_CREATE);
        }
        return action;
    }

    public String updateModifier() throws Exception {
        String action = prepareModifier();
        if (SUCCESS.equals(action)) {
            this.paymentModifierAdminService.update(this.modifier);
            addActionMessage(MESSAGE_KEY_UPDATE);
        }
        return action;
    }

    public String deleteModifier() throws Exception {
    	if (this.modifier.getD() != null) {
			this.modifier.getD().setActive(Boolean.FALSE);
		}
    	this.paymentModifierAdminService.update(this.modifier);
    	clearErrorsAndMessages();
        addActionMessage(MESSAGE_KEY_DELETE);
        return SUCCESS;
    }

    public String getWarrantyTypeString() {
        Criteria forCriteria = this.modifier.getForCriteria();
        if (forCriteria == null || forCriteria.getWarrantyType() == null) {
            return getText("label.common.allWarrantyTypes");
        }
        return forCriteria.getWarrantyType();
    }

    public String getClaimTypeString() {
        Criteria forCriteria = this.modifier.getForCriteria();
        if (forCriteria == null || forCriteria.getClaimType() == null) {
            return getText("label.common.allClaimTypes");
        }
        return getText(forCriteria.getClaimType().getDisplayType());
    }

    public String getDealerString() {
        Criteria forCriteria = this.modifier.getCriteria();
        if (forCriteria == null || forCriteria.getDealerCriterion() == null) {
            return getText("label.common.allDealers");
        }
        return forCriteria.getDealerCriterion().getIdentifier();
    }

    public String getProductTypeString() {
        Criteria forCriteria = this.modifier.getForCriteria();
        if (forCriteria == null || forCriteria.getProductType() == null) {
            return getText("label.common.allProductTypes");
        }
        return forCriteria.getProductType().getName();
    }

    // ********************* Accessors & Mutators ****************//

    public String getDealerCriterion() {
        return this.dealerCriterion;
    }

    public void setDealerCriterion(String dealerCriterion) {
        this.dealerCriterion = dealerCriterion;
    }

    public List<CriteriaBasedValue> getEntries() {
        return this.entries;
    }

    public void setEntries(List<CriteriaBasedValue> entries) {
        this.entries = entries;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPaymentVariableId(String paymentVariableId) {
        this.paymentVariableId = paymentVariableId;
    }

    public PaymentModifier getModifier() {
        return this.modifier;
    }

    public void setModifier(PaymentModifier modifier) {
        this.modifier = modifier;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setPaymentModifierAdminService(
            PaymentModifierAdminService paymentModifierAdminService) {
        this.paymentModifierAdminService = paymentModifierAdminService;
    }

    // **************** Private Methods ******************//
    private String prepareModifier() {    	
        Long paymentVariableId = this.modifier.getForPaymentVariable().getId();
        PaymentVariable forPaymentVariable = null;
        forPaymentVariable = this.paymentModifierAdminService
                .findPaymentVariableById(paymentVariableId);
        this.modifier.setForPaymentVariable(forPaymentVariable);
        if(landedCost!=null){
            this.modifier.setLandedCost(landedCost);
        }
        Set<Long> idsFromUI = new HashSet<Long>();
        for (CriteriaBasedValue modifierValue : this.entries) {
            if (modifierValue.getId() != null) {
                idsFromUI.add(modifierValue.getId());
            }
        } 

        for (Iterator<CriteriaBasedValue> it = this.modifier.getEntries().iterator(); it.hasNext();) {
            CriteriaBasedValue rate = it.next();
            if (rate.getId() != null && !idsFromUI.contains(rate.getId())) {
                it.remove();
            }
        }

        if (!this.paymentModifierAdminService.isUnique(this.modifier)) {
            addActionError(MESSAGE_KEY_DUPLICATE);
            return INPUT;
        }       
        try {
            for (CriteriaBasedValue modifierValue : this.entries) {
                this.modifier.setModifier(modifierValue.getValue(), modifierValue.getDuration(),modifierValue.getIsFlatRate());
            }
        } catch (DurationOverlapException e) {
            this.logger.error("Duration Overlap exception", e);
            addActionError(MESSAGE_KEY_OVERLAP);
            return INPUT;
        }
        
        if(dealerGroupSelected){
        	modifier.setServicingLocation(null);
        }
        return SUCCESS;
    }

    // ****** Private Methods *************//
    private void validateDealerCriterion() {
    	
    	
        Criteria forCriteria = this.modifier.getForCriteria();
        
        if (!this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerCriterion)) {
        	ServiceProvider dealership = this.orgService.findDealerByName(this.dealerCriterion);
            if (dealership == null) {
                addFieldError("dealerCriterion", "error.managePayment.invalidDealer",
                        new String[] { this.dealerCriterion });
            } else {
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealer(dealership);
                forCriteria.setDealerCriterion(criterion);
            }
        } else if (this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerGroupName)) {
            DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(
                    this.dealerGroupName, AdminConstants.MODIFIERS_PURPOSE);
            if (dealerGroup == null) {
                addFieldError("dealerGroupName", "error.managePayment.invalidDealerGroup",
                        new String[] { this.dealerGroupName });
            } else {
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealerGroup(dealerGroup);
                forCriteria.setDealerCriterion(criterion);
            }
        } else {
            forCriteria.setDealerCriterion(null);
        }
    }

    private void validateProductName() {
    	
    	Criteria forCriteria = this.modifier.getForCriteria();
        if (StringUtils.isNotBlank(this.productType)) {
            ItemGroup itemGroup = this.catalogService.findItemGroupByName(this.productType);
            if (itemGroup == null) {
                addFieldError("productType", "error.managePayment.invalidProductType",
                        new String[] { this.productType });
            }
            forCriteria.setProductType(itemGroup);
        } else {
            forCriteria.setProductType(null);
        }
    }
    
    private void validateRateConfigurations() {
    	int numRates = this.entries.size();
        
    	if(this.entries.size()==0){
        	//addActionError("error.manageRates.moreConfigRequired");
        	return;
        }
        
        if (numRates < 2) {
        	CalendarDate startDate = this.entries.get(0).getDuration().getFromDate();
        	CalendarDate endDate = this.entries.get(0).getDuration().getTillDate();
        	if(startDate != null && endDate != null){
        	if(startDate.isAfter(endDate))
        		addActionError("error.manageRates.endDateBeforeStartDate", new String[] {
                        endDate.toString(), startDate.toString() });
        	}
        	return;
        }
        	

        for (int i = 1; i < numRates; i++) {

            CalendarDuration thisLRDuration = this.entries.get(i).getDuration();
            CalendarDuration prevLRDuration = this.entries.get(i - 1).getDuration();

            CalendarDate preStartDate = prevLRDuration.getFromDate();
            CalendarDate prevEndDate = prevLRDuration.getTillDate();
            CalendarDate currentStartDate = thisLRDuration.getFromDate();
            CalendarDate currentEndDate = thisLRDuration.getTillDate();
            
            if((preStartDate != null && prevEndDate != null) || (currentStartDate != null && currentEndDate != null)){
	            if(preStartDate.isAfter(prevEndDate) ||  currentStartDate.isAfter(currentEndDate) )
	            	 addActionError("error.manageRates.endDateBeforeStartDate", new String[] {
	                         prevEndDate.toString(), currentStartDate.toString() });
            }
            
            if ((prevEndDate != null && currentStartDate != null)
                    && !(prevEndDate.plusDays(1).equals(currentStartDate))) {
                addActionError("error.manageRates.noGapsInConsecutiveDateRange", new String[] {
                        prevEndDate.toString(), currentStartDate.toString() });
            }
            
            if (this.entries.get(i - 1).getPercentage() != null) {
				if (this.entries.get(i - 1).getPercentage().isNaN()) {
					addActionError("error.manageRates.invalidPercentage");
				}
			}
        }
		
	}
   
    protected void populateCustomerTypes() {    	
/*    	customerTypes.put("DEALER", "Dealer");
    	customerTypes.put("END CUSTOMER", "End Customer");
    	customerTypes.put("NATIONAL ACCOUNT", "National Account");
*/

    	Map<Object, Object> keyValueOfCustomerTypes = this.configParamService.getKeyValuePairOfObjects(ConfigName.
    			CUSTOMER_TYPES_FOR_MODIFIERS.getName());
    	
    	if(keyValueOfCustomerTypes != null && !keyValueOfCustomerTypes.isEmpty()){
	    			customerTypes.putAll(keyValueOfCustomerTypes);
	 	}
	
    
    }
    

    public String getDealerGroupName() {
        return this.dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

	public Map<Object, Object> getCustomerTypes() {
		return customerTypes;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public String getMessageKey(String sectionName){
        return NAMES_AND_KEY.get(sectionName);
    }

    public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}

    public void setClaimTypes() {
		  this.claimTypes = this.claimService.fetchAllClaimTypesForBusinessUnit();
	}
    
    public String listServicingLocationForDealer(){
    	ServiceProvider dealer = orgService.findServiceProviderByName(dealerCriterion);
    	servicingLocations = 
    		orgService.getAddressesForOrganization(dealer);
    	return SUCCESS;
    }
    
}