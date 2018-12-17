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
/**
 * 
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
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.Duration;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.LocationRepository;
import tavant.twms.domain.partreturn.PartReturnConfiguration;
import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@SuppressWarnings("serial")
public class CreatePartReturnDefinition extends I18nActionSupport implements Preparable,
        Validateable {

    private static final String MESSAGE_KEY_OVERLAP = "error.partReturnConfiguration.durationOverlap";
    private static final String MESSAGE_KEY_SUCCESS = "message.partReturnConfiguration.createSuccess";

    private static final Logger logger = Logger.getLogger(CreatePartReturnDefinition.class);

    private List<PartReturnConfiguration> configurations = new ArrayList<PartReturnConfiguration>();
    private PartReturnDefinition definition = new PartReturnDefinition();

    private List<PaymentCondition> paymentConditions;
    private Map<Boolean, String> yesNo = new HashMap<Boolean, String>();

    private LocationRepository locationRepository;
    private CatalogService catalogService;
    private PartReturnService partReturnService;
    private DealerGroupService dealerGroupService;
    private ItemGroupService itemGroupService;
    private boolean dealerGroupSelected;
    private boolean itemGroupSelected;
    private Map<String, String> warrantyTypeList = new HashMap<String, String>();
    private Map<String, String> claimTypeList = new HashMap<String, String>();
    

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

        this.warrantyTypeList.put("", "All");
        this.warrantyTypeList.put("STANDARD", "Standard");
        this.warrantyTypeList.put("EXTENDED", "Extended");
        this.warrantyTypeList.put("POLICY", "Policy");

        this.claimTypeList.put("", "All");
        this.claimTypeList.put("Parts", "Parts");
        this.claimTypeList.put("Machine", "Machine");
        this.claimTypeList.put("Campaigns", "Caimpaigns");

        this.yesNo.put(true, getText("yes"));
        this.yesNo.put(false, getText("no"));

        this.paymentConditions = this.partReturnService.findAllPaymentConditions();
    }

    @Override
    public void validate() {
        for (Iterator<PartReturnConfiguration> iter = this.configurations.iterator(); iter
                .hasNext();) {
            PartReturnConfiguration prConfig = iter.next();
            if ((prConfig == null)
                    || ((prConfig.getDuration().getFromDate() == null) || (prConfig.getDuration()
                            .getTillDate() == null))) {
                iter.remove();
            }
        }
        super.validate();
        validateProductName();
        validateDealerCriterion();
        validatePartCriterion();
        validatePartReturnDates();
    }

    // Public methods
    public String createDefinition() {
        return SUCCESS;
    }

    public String saveDefinition() {
        boolean isUnique = this.partReturnService.isUnique(this.definition);
        if (!isUnique) {
            addActionError("error.partReturnConfiguration.duplicateConfig");
            return INPUT;
        }

        for (PartReturnConfiguration configuration : this.configurations) {
            this.definition.addPartReturnConfiguration(configuration);
        }
        this.partReturnService.createPartReturnDefinitionAudit(this.definition);
        this.partReturnService.save(this.definition);
        addActionMessage(MESSAGE_KEY_SUCCESS);
        return SUCCESS;
    }

    // Accessors & Mutators
    public PartReturnDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(PartReturnDefinition definition) {
        this.definition = definition;
    }

    public List<PaymentCondition> getPaymentConditions() {
        return this.paymentConditions;
    }

    public void setPaymentConditions(List<PaymentCondition> paymentConditions) {
        this.paymentConditions = paymentConditions;
    }

    public Map<Boolean, String> getYesNo() {
        return this.yesNo;
    }

    public void setYesNo(Map<Boolean, String> yesNo) {
        this.yesNo = yesNo;
    }

    public List<PartReturnConfiguration> getConfigurations() {
        return this.configurations;
    }

    public void setConfigurations(List<PartReturnConfiguration> configurations) {
        this.configurations = configurations;
    }

    // Service Dependency
    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    // Fields to hold values for autoselect.
    private String productType;
    private String dealerCriterion;
    private String partCriterion;
    private String dealerGroupName;
    private String itemGroupName;

    public String getItemGroupName() {
        return this.itemGroupName;
    }

    public void setItemGroupName(String itemGroupName) {
        this.itemGroupName = itemGroupName;
    }

    public String getDealerGroupName() {
        return this.dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public String getDealerCriterion() {
        return this.dealerCriterion;
    }

    public void setDealerCriterion(String dealerCriterion) {
        this.dealerCriterion = dealerCriterion;
    }

    public String getPartCriterion() {
        return this.partCriterion;
    }

    public void setPartCriterion(String partCriterion) {
        this.partCriterion = partCriterion;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    //****** Private Methods *************//
    private void validatePartCriterion() {
        if (!this.itemGroupSelected && StringUtils.isNotBlank(this.partCriterion)) {
            try {
                Item item = this.catalogService.findItemOwnedByManuf(this.partCriterion);
                ItemCriterion criterion = new ItemCriterion();
                criterion.setItem(item);
                this.definition.setItemCriterion(criterion);

            } catch (CatalogException e) {
                logger.error(e);
                addFieldError("partCriterion", "error.partReturnConfiguration.noPartExists",
                        new String[] { this.partCriterion });
            }
        } else if (this.itemGroupSelected && StringUtils.isNotBlank(this.itemGroupName)) {
            ItemGroup itemGroup = this.itemGroupService.findByNameAndPurpose(this.itemGroupName,
                    AdminConstants.PART_RETURNS_PURPOSE);
            if (itemGroup == null) {
                addFieldError("itemGroupName", "error.partReturnConfiguration.noItemGroupExists",
                        new String[] { this.itemGroupName });
            } else {
                ItemCriterion criterion = new ItemCriterion();
                criterion.setItemGroup(itemGroup);
                this.definition.setItemCriterion(criterion);
            }
        } else {
            addFieldError("partCriterion", "error.partReturnConfiguration.invalidValue");
        }
    }

    private void validateDealerCriterion() {
        if (!this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerCriterion)) {
        	ServiceProvider dealership = this.orgService.findDealerByName(this.dealerCriterion);
            if (dealership == null) {
                addFieldError("dealerCriterion", "error.partReturnConfiguration.invalidDealerName",
                        new String[] { this.dealerCriterion });
            } else {
                Criteria forCriteria = this.definition.getForCriteria();
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealer(dealership);
                forCriteria.setDealerCriterion(criterion);
            }
        } else if (this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerGroupName)) {
            DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(
                    this.dealerGroupName, AdminConstants.PART_RETURNS_PURPOSE);
            if (dealerGroup == null) {
                addFieldError("dealerGroupName",
                        "error.partReturnConfiguration.invalidDealerGroup",
                        new String[] { this.dealerGroupName });
            } else {
                Criteria forCriteria = this.definition.getForCriteria();
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealerGroup(dealerGroup);
                forCriteria.setDealerCriterion(criterion);
            }
        } else {
            Criteria forCriteria = this.definition.getForCriteria();
            forCriteria.setDealerCriterion(null);
        }
    }

    private void validateProductName() {
        if (StringUtils.isNotBlank(this.productType)) {
            ItemGroup itemGroup = this.catalogService.findItemGroupByName(this.productType);
            if (itemGroup == null) {
                addFieldError("productType", "error.partReturnConfiguration.invalidProductType",
                        new String[] { this.productType });
            }
            Criteria forCriteria = this.definition.getForCriteria();
            forCriteria.setProductType(itemGroup);
        } else {
            Criteria forCriteria = this.definition.getForCriteria();
            forCriteria.setProductType(null);
        }
    }

    private void validatePartReturnDates() {
        int numRates = this.configurations.size();
        if (numRates < 2) {
            return;
        }
        for (int i = 1; i < numRates; i++) {
            Duration thisPRDuration = this.configurations.get(i).getDuration();
            Duration prevPRDuration = this.configurations.get(i - 1).getDuration();
            CalendarDate prevEndDate = prevPRDuration.getTillDate();
            CalendarDate currentStartDate = thisPRDuration.getFromDate();
            if (!(prevEndDate.nextDay().equals(currentStartDate))) {
                addActionError("error.partReturnConfiguration.noGapsInConsecutiveDateRange",
                        new String[] { prevEndDate.toString(), currentStartDate.toString() });
            }
        }
    }
    
    public boolean isItemGroupSelected() {
        return this.itemGroupSelected;
    }

    public void setItemGroupSelected(boolean itemGroupSelected) {
        this.itemGroupSelected = itemGroupSelected;
    }

    public void setItemGroupService(ItemGroupService itemGroupService) {
        this.itemGroupService = itemGroupService;
    }

    public Map<String, String> getWarrantyTypeList() {
        return this.warrantyTypeList;
    }

    public Map<String, String> getClaimTypeList() {
        return this.claimTypeList;
    }

    public void setWarrantyTypeList(Map<String, String> warrantyTypeList) {
        this.warrantyTypeList = warrantyTypeList;
    }

    public void setClaimTypeList(Map<String, String> claimTypeList) {
        this.claimTypeList = claimTypeList;
    }
}