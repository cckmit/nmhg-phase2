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
package tavant.twms.web.admin.partreturns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.admin.ManageCriteriaAction;

/**
 * @author vineeth.varghese
 * 
 */
@SuppressWarnings("serial")
public class ManagePartReturnDefinitionAction extends ManageCriteriaAction {

    private static final String CREATED_CONFIG_SUCCESSFULLY = "message.partReturnConfiguration.createSuccess";
    private static final String UPDATED_CONFIG_SUCCESSFULLY = "message.partReturnConfiguration.updateSuccess";
    private static final String DELETED_CONFIG_SUCCESSFULLY = "message.partReturnConfiguration.deleteSuccess";
    private static final String FOUND_DUPLICATE_CONFIG = "error.partReturnConfiguration.duplicateConfig";

    private List<PaymentCondition> paymentConditions;
    private boolean isItemGroupSelected;
    private PartReturnDefinition partReturnDefinition = new PartReturnDefinition();

    private PartReturnService partReturnService;
    private ItemGroupService itemGroupService;
    private String id;
    
    private ServiceProvider chosenDealer;    
    private ItemGroup chosenProduct;
    String dealerGroupName;
    private List<PartReturnDefinitionAudit> actionHistory = new ArrayList<PartReturnDefinitionAudit>();
    private List<ServiceProvider> selectedDealerExclusions = new ArrayList<ServiceProvider>();
    private List<DealerGroup> selectedDealerGroupExclusions = new ArrayList<DealerGroup>();
    private String status;
    private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();
    private WarrantyService warrantyService;

	@Override
    public void prepare() throws Exception {
    	super.prepare();
    	this.paymentConditions = this.partReturnService.findAllPaymentConditions();
    	setWarrantyTypes(this.warrantyService.listWarrantyTypes());
    	if (org.springframework.util.StringUtils.hasLength(this.id)) {
    		Long definitionId = Long.parseLong(this.id);
    		this.partReturnDefinition = this.partReturnService
    		.findPartReturnDefinitionById(definitionId);
    		this.isItemGroupSelected = this.partReturnDefinition.getItemCriterion() != null
    		? this.partReturnDefinition.getItemCriterion().isGroupCriterion() : false;    		   		
    		if(isDealerGroupSelected()){
    			  DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(dealerGroupName,
    	                    getPurpose());
    			  partReturnDefinition.getForCriteria().setDealerCriterion(new DealerCriterion(dealerGroup));
    		}
    		else{
    			partReturnDefinition.getForCriteria().setDealerCriterion(new DealerCriterion(chosenDealer));
    		}
    		partReturnDefinition.getForCriteria().setProductType(chosenProduct);
    	}
    }

    private void validateItemCriterion() {
        String itemGroupName = this.partReturnDefinition.getItemCriterion().getItemGroup()
                .getName();
        String itemName = null;
        if(this.partReturnDefinition.getItemCriterion().getItem()!=null){
        	itemName = this.partReturnDefinition.getItemCriterion().getItem().getNumber();
        }
        if (this.isItemGroupSelected) {
            this.partReturnDefinition.getItemCriterion().setItem(null);
            if (StringUtils.isNotBlank(itemGroupName)) {
                ItemGroup itemGroup = this.itemGroupService.findByNameAndPurpose(itemGroupName,
                    getPurpose());
                if (itemGroup == null) {
                    addFieldError("partReturnDefinition.itemCriterion.itemGroup.name",
                        "error.partReturnConfiguration.noItemGroupExists",
                        new String[] { itemGroupName });
                } else {
                    this.partReturnDefinition.getItemCriterion().setItemGroup(itemGroup);
                }
            } else {
                addFieldError("partReturnDefinition.itemCriterion.itemGroup.name",
                    "error.partReturnConfiguration.invalidValue");
            }
        } else {
            this.partReturnDefinition.getItemCriterion().setItemGroup(null);
            if (StringUtils.isNotBlank(itemName)) {
                try {
                    Item item = this.catalogService.findItemOwnedByManuf(itemName);
                    this.partReturnDefinition.getItemCriterion().setItem(item);
                } catch (CatalogException e) {
                    //logger.error(e);
                    addFieldError("partReturnDefinition.itemCriterion.item.name",
                        "error.partReturnConfiguration.noPartExists", new String[] { itemName });
                }
            } else {
                addFieldError("partReturnDefinition.itemCriterion.item.name",
                    "error.partReturnConfiguration.partNumber/GroupNameNotSpecified");
            }
        }
    }

    private void validateConfigurations() {
    	if(this.partReturnDefinition.getConfigurations().size() == 0)
    		addFieldError("error.partReturnConfiguration.moreConfigRequired",
                    "error.partReturnConfiguration.moreConfigRequired");
        for(PartReturnConfiguration partRetConf : this.partReturnDefinition.getConfigurations()){
        	if(partRetConf.getReturnLocation() == null || (partRetConf.getReturnLocation() !=null && StringUtils.isBlank(partRetConf.getReturnLocation().getCode()))){
        		addFieldError("partReturnConfiguration.returnLocation","error.partReturnConfiguration.returnLocation");
        		}

            if(partRetConf.getRmaNumber().length() > 25){
                addFieldError("partReturnConfiguration.rmaNumber","prc.error.rma.exceedlimit");
            }
        }
    }

    private void validationDealerDealerGroupsExclusions(){
    	tavant.twms.common.CollectionUtils.removeNullsFromList(selectedDealerExclusions);
    	tavant.twms.common.CollectionUtils.removeNullsFromList(selectedDealerGroupExclusions);
    	if(!CollectionUtils.isEmpty(selectedDealerExclusions)){
    		Map<Long, ServiceProvider> dealerMap = new HashMap<Long, ServiceProvider>();
    		for(ServiceProvider dealer : selectedDealerExclusions){
    			if(StringUtils.isBlank(dealer.getName())){
    				addActionError("error.partReturnConfiguration.specifyDealer");
    				break;
    			}
    			if(dealerMap.containsKey(dealer.getId())){
    				addActionError("error.partReturnConfiguration.duplicateDealer");
    				break;
    			} else {
    				dealerMap.put(dealer.getId(), dealer);
    			}
    			if(chosenDealer != null && dealer.equals(chosenDealer)){
    				addActionError("error.partReturnConfiguration.dealerCannotBeExcluded", dealer.getName());
    			}
    			if(!CollectionUtils.isEmpty(selectedDealerGroupExclusions)){
    				for(DealerGroup dealerGroup : selectedDealerGroupExclusions){
    					if(dealerGroup.isDealerInGroup(dealer)){
    						addActionError("error.partReturnConfiguration.dealerExistsInDealerGroup", new String[]{dealer.getName(), dealerGroup.getName()});
    						break;
    					}
    				}
    			}
    		}
    	}
    	if(!CollectionUtils.isEmpty(selectedDealerGroupExclusions)){
    		Map<Long, DealerGroup> dealerGroupMap = new HashMap<Long, DealerGroup>();
    		for(DealerGroup dealerGroup : selectedDealerGroupExclusions){
    			if(StringUtils.isBlank(dealerGroup.getName())){
    				addActionError("error.partReturnConfiguration.specifyDealerDroup");
    				break;
    			}
    			if(dealerGroupMap.containsKey(dealerGroup.getId())){
    				addActionError("error.partReturnConfiguration.duplicateDealerGroup");
    				break;
    			} else {
    				dealerGroupMap.put(dealerGroup.getId(), dealerGroup);
    			}
    			if(this.partReturnDefinition.getForCriteria().getDealerCriterion() != null && this.partReturnDefinition.getForCriteria().getDealerCriterion().getDealerGroup() != null &&
    					dealerGroup.equals(this.partReturnDefinition.getForCriteria().getDealerCriterion().getDealerGroup())){
    				addActionError("error.partReturnConfiguration.dealerGroupCannotBeExcluded", dealerGroup.getName());
    			}
    		}
    	}
    	partReturnDefinition.getExcludedDealers().clear();
		partReturnDefinition.getExcludedDealers().addAll(selectedDealerExclusions);
		partReturnDefinition.getExcludedDealerGroups().clear();
		partReturnDefinition.getExcludedDealerGroups().addAll(selectedDealerGroupExclusions);
    }

    @Override
    public void validate() {        
        validateItemCriterion();
        validateConfigurations();
        validationDealerDealerGroupsExclusions();
        //Disabled AutoFlush at the DAO level coz we don't want any commit at this point.
//        if(partReturnDefinition.getForCriteria().getDealerCriterion() == null || partReturnDefinition.getForCriteria().getDealerCriterion().getDealer().getId() == null){
//    		ServiceProvider dealer = null;
//    		partReturnDefinition.getForCriteria().setDealerCriterion(new DealerCriterion(dealer));
//    	}
        if (!this.hasErrors() && !(this.partReturnService.isUnique(this.partReturnDefinition))) {
            addActionError(FOUND_DUPLICATE_CONFIG);
        }
    }
    
    public String listServiceProviders(){
    	try{
            List<ServiceProvider> serviceProviders = orgService.findDealersWhoseNameStartsWith(getSearchPrefix(), 0, 10);
            return generateAndWriteComboboxJson(serviceProviders,"id","name");
    	} catch(Exception ex){
            throw new RuntimeException("Error while generating JSON", ex);
    	}
    }
    
    public String listDealerGroups() {
        try {
            List<DealerGroup> dealerGroups = this.dealerGroupService.findDealerGroupsWithNameLike(
                    getSearchPrefix(), new PageSpecification(0, 10), AdminConstants.PART_RETURNS_PURPOSE);
            return generateAndWriteComboboxJson(dealerGroups,"id","name");
        } catch (Exception e) {
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

    public String createDefinition() throws Exception {
        return SUCCESS;
    }

    public String viewDefinition() throws Exception {
    	this.paymentConditions = this.partReturnService.findAllPaymentConditions();
    	super.prepare();
    	setWarrantyTypes(this.warrantyService.listWarrantyTypes());
    	if (org.springframework.util.StringUtils.hasLength(this.id)) {
            Long definitionId = Long.parseLong(this.id);
            this.partReturnDefinition = this.partReturnService
                    .findPartReturnDefinitionById(definitionId);
            this.isItemGroupSelected = this.partReturnDefinition.getItemCriterion() != null
                ? this.partReturnDefinition.getItemCriterion().isGroupCriterion() : false;
            Criteria criteria = this.partReturnDefinition.getCriteria();
            if (criteria != null) {
                this.setDealerGroupSelected(criteria.getDealerCriterion() != null ? criteria
                        .getDealerCriterion().isGroupCriterion() : false);
            }
            this.setActionHistory(partReturnDefinition.getPartReturnDefinitionAudits());
        }
        return SUCCESS;
    }

    public String saveDefinition() throws Exception {
    	
    	if(isDealerGroupSelected()){
			  DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(dealerGroupName,
	                    getPurpose());
			  partReturnDefinition.getForCriteria().setDealerCriterion(new DealerCriterion(dealerGroup));
		} 
    	else{
		partReturnDefinition.getForCriteria().setDealerCriterion(new DealerCriterion(chosenDealer));
		}
		partReturnDefinition.getForCriteria().setProductType(chosenProduct);
		partReturnDefinition.setStatus(STATUS_ACTIVE);
		this.partReturnService.createPartReturnDefinitionAudit(this.partReturnDefinition);
        this.partReturnService.save(this.partReturnDefinition);
        addActionMessage(CREATED_CONFIG_SUCCESSFULLY);
        return SUCCESS;
    }

    public String updateDefinition() throws Exception {
    	this.partReturnService.createPartReturnDefinitionAudit(this.partReturnDefinition);
        this.partReturnService.update(this.partReturnDefinition);
        addActionMessage(UPDATED_CONFIG_SUCCESSFULLY);
        return SUCCESS;
    }

    public String updateDefinitionStatus() throws Exception {
    	this.partReturnDefinition.setStatus(status);
    	this.partReturnService.createPartReturnDefinitionAudit(this.partReturnDefinition);
        this.partReturnService.update(this.partReturnDefinition);
        addActionMessage(UPDATED_CONFIG_SUCCESSFULLY);
        return SUCCESS;
    }
    
    public String deleteDefinition() throws Exception {
        this.partReturnDefinition.getD().setActive(false);
        this.partReturnService.createPartReturnDefinitionAudit(this.partReturnDefinition);
        this.partReturnService.update(this.partReturnDefinition);
        addActionMessage(DELETED_CONFIG_SUCCESSFULLY);
        return SUCCESS;
    }
    public PartReturnDefinition getPartReturnDefinition() {
        return this.partReturnDefinition;
    }

    public void setPartReturnDefinition(PartReturnDefinition partReturnDefinition) {
        this.partReturnDefinition = partReturnDefinition;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    @Override
    public Criteria getCriteria() {
        return this.partReturnDefinition.getForCriteria();
    }

    @Override
    public String getCriteriaOgnlExp() {
        return "partReturnDefinition.forCriteria";
    }

    @Override
    public String getPurpose() {
        return AdminConstants.PART_RETURNS_PURPOSE;
    }

    public List<PaymentCondition> getPaymentConditions() {
        return this.paymentConditions;
    }

    public boolean isItemGroupSelected() {
        return this.isItemGroupSelected;
    }

    public void setItemGroupSelected(boolean isItemGroupSelected) {
        this.isItemGroupSelected = isItemGroupSelected;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

	public ServiceProvider getChosenDealer() {
		return chosenDealer;
	}

	public void setChosenDealer(ServiceProvider chosenDealer) {
		this.chosenDealer = chosenDealer;
	}

	public ItemGroup getChosenProduct() {
		return chosenProduct;
	}

	public void setChosenProduct(ItemGroup chosenProduct) {
		this.chosenProduct = chosenProduct;
	}

	public String getDealerGroupName() {
		return dealerGroupName;
	}

	public void setDealerGroupName(String dealerGroupName) {
		this.dealerGroupName = dealerGroupName;
	}

	public List<PartReturnDefinitionAudit> getActionHistory() {
		Collections.reverse(actionHistory);
		return actionHistory;
	}

	public void setActionHistory(List<PartReturnDefinitionAudit> actionHistory) {
		this.actionHistory = actionHistory;
	}

	public List<ServiceProvider> getSelectedDealerExclusions() {
		return selectedDealerExclusions;
	}

	public void setSelectedDealerExclusions(
			List<ServiceProvider> selectedDealerExclusions) {
		this.selectedDealerExclusions = selectedDealerExclusions;
	}

	public List<DealerGroup> getSelectedDealerGroupExclusions() {
		return selectedDealerGroupExclusions;
	}

	public void setSelectedDealerGroupExclusions(
			List<DealerGroup> selectedDealerGroupExclusions) {
		this.selectedDealerGroupExclusions = selectedDealerGroupExclusions;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

}
