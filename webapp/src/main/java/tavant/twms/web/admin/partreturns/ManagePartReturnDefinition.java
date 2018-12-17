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
package tavant.twms.web.admin.partreturns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.partreturn.LocationRepository;
import tavant.twms.domain.partreturn.PartReturnConfiguration;
import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author aniruddha.chaturvedi
 */
@SuppressWarnings("serial")
public class ManagePartReturnDefinition extends I18nActionSupport implements Preparable,
        Validateable {

    private static final Logger logger = Logger.getLogger(ManagePartReturnDefinition.class);

    private static final String MESSAGE_KEY_OVERLAP = "error.partReturnConfiguration.durationOverlap";

    private static final String MESSAGE_KEY_SUCCESS = "message.partReturnConfiguration.updateSuccess";

    private static final String MESSAGE_KEY_DELETE_SUCCESS = "message.partReturnConfiguration.deleteSuccess";

    private String id;

    private PartReturnDefinition definition;

    private List<PartReturnConfiguration> configurations = new ArrayList<PartReturnConfiguration>();

    private List<PaymentCondition> paymentConditions;

    private LocationRepository locationRepository;

    private PartReturnService partReturnService;

    private DealerGroupService dealerGroupService;

    private CatalogService catalogService;

    private ItemGroupService itemGroupService;

    private Map<Boolean, String> yesNo = new HashMap<Boolean, String>();

    private Map<String, String> warrantyTypeList = new HashMap<String, String>();

    private Map<String, String> claimTypeList = new HashMap<String, String>();

    private String claimType;

    private String dealer;

    private String itemCriterion;

    private String productType;

    private String warrantyType;

    public void prepare() throws Exception {
        this.yesNo.put(true, "Yes");
        this.yesNo.put(false, "No");

        this.warrantyTypeList.put("", "All");
        this.warrantyTypeList.put("STANDARD", "Standard");
        this.warrantyTypeList.put("EXTENDED", "Extended");
        this.warrantyTypeList.put("POLICY", "Policy");

        this.claimTypeList.put("", "All");
        this.claimTypeList.put("Parts", "Parts");
        this.claimTypeList.put("Machine", "Machine");
        this.claimTypeList.put("Campaigns", "Caimpaigns");

        this.paymentConditions = this.partReturnService.findAllPaymentConditions();
        if ((this.id != null) && !"".equals(this.id)) {
            Long definitionId = Long.parseLong(this.id);
            this.definition = this.partReturnService.findPartReturnDefinitionById(definitionId);
        } else {
            this.definition = new PartReturnDefinition();
        }
    }

    @Override
    public void validate() {
        for (Iterator<PartReturnConfiguration> iter = this.configurations.iterator(); iter
                .hasNext();) {
            if (iter.next() == null) {
                iter.remove();
            }
        }
        super.validate();
    }

    public String showDefinition() {
        this.configurations = new ArrayList<PartReturnConfiguration>();
        this.configurations.addAll(this.definition.getConfigurations());
        return SUCCESS;
    }

    public String updateDefinition() {
        // //SortedSet<PartReturnConfiguration> existingValues =
        // this.definition.getConfigurations();
        // setNewClaimType(this.claimType);
        // setNewDealer(this.dealer);
        // setNewItemCriterion(this.itemCriterion);
        // setNewProductType(this.productType);
        // setNewWarrantyType(this.warrantyType);
        //
        // existingValues.clear();
        // // try {
        // for (PartReturnConfiguration config : this.configurations) {
        // this.definition.addPartReturnConfiguration(config);
        // }
        // this.partReturnService.update(this.definition);
        // // } catch (DurationOverlapException e) {
        // // logger.error("Duration Overlap exception", e);
        // // addActionError(MESSAGE_KEY_OVERLAP);
        // // return INPUT;
        // // }
        // addActionMessage(MESSAGE_KEY_SUCCESS);
        return SUCCESS;
    }

    public String deleteDefinition() {
        this.partReturnService.delete(this.definition);
        addActionMessage(MESSAGE_KEY_DELETE_SUCCESS);
        return SUCCESS;
    }

    public String getWarrantyType() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getWarrantyType() == null)) {
            return getText("label.common.allWarrantyTypes");
        }
        return forCriteria.getWarrantyType();
    }

    public String getClaimType() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getClaimType() == null)) {
            return getText("label.common.allClaimTypes");
        }
        return forCriteria.getClaimType().getType();
    }

    public String getDealer() {
        Criteria forCriteria = this.definition.getCriteria();
        if ((forCriteria == null) || (forCriteria.getDealerCriterion() == null)) {
            return getText("label.common.allDealers");
        }
        DealerCriterion dealerCriteria = forCriteria.getDealerCriterion();
        String criterion = null;
        if (dealerCriteria.isGroupCriterion()) {
            criterion = dealerCriteria.getDealerGroup().getName();
        } else {
            criterion = dealerCriteria.getDealer().getName();
        }
        return criterion;
    }

    public String getProductType() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getProductType() == null)) {
            return getText("label.common.allProductTypes");
        }
        return forCriteria.getProductType().getName();
    }

    public String getItemCriterion() {
        ItemCriterion itemCriterion = this.definition.getItemCriterion();
        String criterion = null;
        if (itemCriterion.isGroupCriterion()) {
            criterion = itemCriterion.getItemGroup().getName();
        } else {
            criterion = itemCriterion.getItem().getNumber();
        }
        return criterion;
    }

    // Sets the new claim type
    public void setNewClaimType(String claimType) {
        this.definition.getCriteria().setClaimType(claimType);
    }

    // Sets the dealer value by checking if it is a dealer group or a dealer
    public void setNewDealer(String dealer) {
        if (!StringUtils.isEmpty(dealer)) {
            if (this.orgService.findDealerByName(dealer) != null) {
                this.definition.getCriteria().getDealerCriterion().setDealer(
                        this.orgService.findDealerByName(dealer));
            } else if (this.dealerGroupService.findByNameAndPurpose(dealer,
                    AdminConstants.PART_RETURNS_PURPOSE) != null) {
                this.definition.getCriteria().getDealerCriterion().setDealerGroup(
                        this.dealerGroupService.findByNameAndPurpose(dealer,
                                AdminConstants.PART_RETURNS_PURPOSE));
            } else {
                addFieldError("dealer", "error.partReturnConfiguration.invalidDealerGroup",
                        new String[] { dealer });
            }
        } else {
            addFieldError(dealer, "error.partReturns.mandatoryField");
        }
    }

    // Sets the value of the item criterion into the definion
    public void setNewItemCriterion(String itemCriterion) {
        if (!StringUtils.isEmpty(itemCriterion)) {
            try {
                Item item = this.catalogService.findItemOwnedByManuf(itemCriterion);
                this.definition.getItemCriterion().setItem(item);
            } catch (CatalogException e) {
                try {
                    this.definition.getItemCriterion().setItemGroup(
                            this.catalogService.findItemGroupByName(itemCriterion));
                } catch (Exception ex) {
                    addFieldError("itemCriterion", "error.partReturns.invalidName");
                }
            }
        } else {
            addFieldError("itemCriterion", "error.partReturns.invalidName");
        }
    }

    // Sets the new Product type
    public void setNewProductType(String productType) {
        if (!StringUtils.isEmpty(productType)) {
            if (this.catalogService.findItemGroupByName(productType) != null) {
                this.definition.getForCriteria().getProductType().setName(productType);
            }
        }
    }

    // sets the new warranty type
    public void setNewWarrantyType(String warrantyType) {
        this.definition.getForCriteria().setWarrantyType(warrantyType);
    }

    // Accessors & Mutators
    public List<PartReturnConfiguration> getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(List<PartReturnConfiguration> configurations) {
        this.configurations = configurations;
    }

    public PartReturnDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(PartReturnDefinition definition) {
        this.definition = definition;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String defintionId) {
        this.id = defintionId;
    }

    public List<PaymentCondition> getPaymentConditions() {
        return this.paymentConditions;
    }

    public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
        this.paymentConditions = paymentConditions;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public Map<Boolean, String> getYesNo() {
        return this.yesNo;
    }

    public void setYesNo(Map<Boolean, String> yesNo) {
        this.yesNo = yesNo;
    }

    public Map<String, String> getWarrantyTypeList() {

        return this.warrantyTypeList;
    }

    public void setWarrantyTypeList(Map<String, String> warrantyTypeList) {
        this.warrantyTypeList = warrantyTypeList;
    }

    public Map<String, String> getClaimTypeList() {
        return this.claimTypeList;
    }

    public void setClaimTypeList(Map<String, String> claimTypeList) {
        this.claimTypeList = claimTypeList;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public void setItemCriterion(String itemCriterion) {
        this.itemCriterion = itemCriterion;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void setClaimType(String claimType) {
        this.claimType = claimType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType;
    }

}