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
package tavant.twms.web.admin.policy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.PolicyRate;
import tavant.twms.domain.policy.PolicyRates;
import tavant.twms.domain.policy.PolicyRatesAdminService;
import tavant.twms.domain.policy.PolicyRatesCriteria;
import tavant.twms.domain.policy.PolicyRatesRepository;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;

/**
 * @author kiran.sg
 * 
 */
@SuppressWarnings("serial")
public class ManagePolicyPriceModifiers extends I18nActionSupport implements Preparable {

    private final Logger logger = Logger.getLogger(ManagePolicyPriceModifiers.class);

    private static final String MESSAGE_KEY_CREATE = "message.managePolicy.createModifierSuccess";

    private static final String MESSAGE_KEY_DELETE = "message.managePolicy.deleteModifierSuccess";

    private static final String MESSAGE_KEY_UPDATE = "message.managePolicy.updateModifierSuccess";

    private static final String MESSAGE_KEY_DUPLICATE = "error.managePolicy.duplicateCriteria";

    private static final String MESSAGE_KEY_OVERLAP = "error.managePolicy.durationOverlap";

    private String id;

    private PolicyRates definition;

    private List<PolicyRate> rates = new ArrayList<PolicyRate>();

    // Fields for Autocomplete
    private String dealerCriterion;

    private String productType;

    private String dealerGroupName;

    private CatalogService catalogService;

    private LabelService labelService;

    private PolicyRatesRepository policyRatesRepository;

    private PolicyRatesAdminService policyRatesAdminService;

    private DealerGroupService dealerGroupService;

    private boolean dealerGroupSelected;

    private boolean policyNameSelected;

    private List<String> policyDefinitions = new ArrayList<String>();

    private List<String> policyLabels = new ArrayList<String>();

    private String jsonString;

    private PolicyDefinitionRepository policyDefinitionRepository;

    public boolean isDealerGroupSelected() {
        return this.dealerGroupSelected;
    }

    public void setDealerGroupSelected(boolean dealerGroupSelected) {
        this.dealerGroupSelected = dealerGroupSelected;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public boolean isPolicyNameSelected() {
        return this.policyNameSelected;
    }

    public void setPolicyNameSelected(boolean policyNameSelected) {
        this.policyNameSelected = policyNameSelected;
    }

    public void prepare() throws Exception {
        if (StringUtils.isNotBlank(this.id)) {
            Long pk = Long.parseLong(this.id);
            this.definition = this.policyRatesRepository.findById(pk);
            if (this.definition.getForCriteria() != null) {
                DealerCriterion dealerCriterion = this.definition.getForCriteria()
                        .getDealerCriterion();
                if ((dealerCriterion != null) && dealerCriterion.isGroupCriterion()) {
                    this.dealerGroupSelected = true;
                }
            }

            this.policyNameSelected = this.definition.getPolicyLabels().isEmpty();
        } else {
            this.definition = new PolicyRates();
        }
    }

    @Override
    public void validate() {
        for (Iterator iter = this.rates.iterator(); iter.hasNext();) {
            PolicyRate pRate = (PolicyRate) iter.next();
            if ((pRate == null)
                    || ((pRate.getDuration().getFromDate() == null)
                            && (pRate.getDuration().getTillDate() == null) && (pRate.getRate() == null))) {
                iter.remove();
            }
        }
        Criteria forCriteria = this.definition.getForCriteria();
        if (forCriteria == null) {
            this.definition.setForCriteria(new PolicyRatesCriteria());
        }

        if (this.rates.isEmpty()) {
            addActionError("error.managePolicy.emptyRates");
        } else {
            for (int i = 0; i < this.rates.size(); i++) {
                PolicyRate policyRate = this.rates.get(i);
                CalendarDuration duration = policyRate.getDuration();
                if (duration.getFromDate() == null) {
                    addFieldError("rates[" + i + "].duration.fromDate",
                            "error.managePolicy.invalidFromDate");
                }
                if (duration.getTillDate() == null) {
                    addFieldError("rates[" + i + "].duration.tillDate",
                            "error.managePolicy.invalidTillDate");
                }
                if ((policyRate.getRate() == null) || policyRate.getRate().equals(BigDecimal.ZERO)) {
                    addFieldError("rates[" + i + "].rate", "error.managePolicy.invalidRate");
                }
            }
        }

        validateDealerCriterion();
        validateProductName();
        validatePolicyPriceModifierDate();
    }

    public String showPrice() {
        this.rates = new ArrayList<PolicyRate>();
        this.rates.addAll(this.definition.getEntries());
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria != null) && (forCriteria.getDealerCriterion() != null)) {
            this.dealerCriterion = forCriteria.getDealerCriterion().getIdentifier();
            this.dealerGroupName = forCriteria.getDealerCriterion().getIdentifier();
        }
        if ((forCriteria != null) && (forCriteria.getProductType() != null)) {
            this.productType = forCriteria.getProductType().getName();
        }

        this.policyDefinitions = new ArrayList<String>();
        for (PolicyDefinition policyDefinition : this.definition.getPolicyDefinitions()) {
            this.policyDefinitions.add(policyDefinition.getCode());
        }

        this.policyLabels = new ArrayList<String>();
        for (Label label : this.definition.getPolicyLabels()) {
            this.policyLabels.add(label.getName());
        }

        return SUCCESS;
    }

    public String createPrice() {
        preparePrice();
        
        List<PolicyDefinition> definitions = null;
        List<Label> labels = null;
        
        if(isPolicyNameSelected())
        {
        	definitions = preparePolicyDefinitions();
        }
        else
        {
        	labels = preparePolicyLabels();
        }
        if (hasErrors()) {
            return INPUT;
        }

        try {
            this.definition.setPolicyDefinitions(definitions);
            this.definition.setPolicyLabels(labels);
            prepareCustomerState();
            this.policyRatesRepository.savePolicyRates(this.definition);
        } catch (Exception e) {
            this.logger.error("Exception in Creating PolicyRate Configuration", e);
            return INPUT;
        }
        addActionMessage(MESSAGE_KEY_CREATE);

        return SUCCESS;
    }

    private void prepareCustomerState() {
        PolicyRatesCriteria forCriteria = this.definition.getForCriteria();
        String customerState = forCriteria.getCustomerState();
        forCriteria.setCustomerState(StringUtils.trimToNull(customerState));
    }

    public String updatePrice() {
        preparePrice();

        List<PolicyDefinition> definitions = preparePolicyDefinitions();
        List<Label> labels = preparePolicyLabels();

        if (hasErrors()) {
            return INPUT;
        }

        try {
            this.definition.setPolicyDefinitions(definitions);
            this.definition.setPolicyLabels(labels);
            prepareCustomerState();
            this.policyRatesRepository.updatePolicyRates(this.definition);
        } catch (Exception e) {
            this.logger.error("Exception in Updating PolicyRate Configuration", e);
            return INPUT;
        }
        addActionMessage(MESSAGE_KEY_UPDATE);

        return SUCCESS;
    }

    public String deletePrice() throws Exception {
        this.policyRatesRepository.deletePolicyRates(this.definition);
        addActionMessage(MESSAGE_KEY_DELETE);
        return SUCCESS;
    }

    public String getWarrantyTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getWarrantyType() == null)) {
            return getText("label.common.allWarrantyTypes");
        }
        return forCriteria.getWarrantyType();
    }

    public String getClaimTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getClaimType() == null)) {
            return getText("label.common.allClaimTypes");
        }
        return forCriteria.getClaimType().getType();
    }

    public String getDealerString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getDealerCriterion() == null)) {
            return getText("label.common.allDealers");
        }
        return forCriteria.getDealerCriterion().getIdentifier();
    }

    public String getProductTypeString() {
        Criteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getProductType() == null)) {
            return getText("label.common.allProductTypes");
        }
        return forCriteria.getProductType().getName();
    }

    public String getCustomerStateString() {
        PolicyRatesCriteria forCriteria = this.definition.getForCriteria();
        if ((forCriteria == null) || (forCriteria.getCustomerState() == null)) {
            return getText("label.common.allCustomerStates");
        }
        return forCriteria.getCustomerState();
    }

    public String getPolicyDefinitionCodes() {
        try {
            if (this.policyDefinitions.isEmpty()) {
                return generateAndWriteEmptyComboboxJson();

            }

            Assert.isTrue(this.policyDefinitions.size() == 1);
            String policyDefinition = this.policyDefinitions.get(0);
            if (!StringUtils.isBlank(policyDefinition)) {
                    List<String> names = this.policyDefinitionRepository
                            .findPolicyDefinitionCodesStartingWith(getSearchPrefix(), 0, 10);
                    return generateAndWriteComboboxJson(names);

            }
            else
            {
                return generateAndWriteEmptyComboboxJson();
            }
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }

    public String getPolicyLabelNames() {
        try {
            if (this.policyLabels.isEmpty()) {
                return generateAndWriteEmptyComboboxJson();
            }

            Assert.isTrue(this.policyLabels.size() == 1);
            String policyLabel = this.policyLabels.get(0);

            if (!StringUtils.isBlank(policyLabel)) {
                    List<String> names = this.labelService.findLabelsWithNameAndTypeLike(getSearchPrefix(), Label.POLICY, 0, 10);
                    return generateAndWriteComboboxJson(names);
            }
            else
            {
                return generateAndWriteEmptyComboboxJson();
            }
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
   }

    // **************** Private Methods ******************//
    private void preparePrice() {
        Set<Long> idsFromUI = new HashSet<Long>();
        for (PolicyRate rate : this.rates) {
            if (rate.getId() != null) {
                idsFromUI.add(rate.getId());
            }
        }

        for (Iterator<PolicyRate> it = this.definition.getEntries().iterator(); it.hasNext();) {
            PolicyRate rate = it.next();
            if ((rate.getId() != null) && !idsFromUI.contains(rate.getId())) {
                it.remove();
            }
        }

        if (!this.policyRatesAdminService.isUnique(this.definition)) {
            addActionError(MESSAGE_KEY_DUPLICATE);
            return;
        }
        try {
            for (PolicyRate item : this.rates) {
                this.definition.set(item.getValue(), item.getDuration());
            }
        } catch (DurationOverlapException e) {
            this.logger.error("Duration Overlap exception", e);
            addActionError(MESSAGE_KEY_OVERLAP);
        }
    }

    // ****** Private Methods *************//
    private void validateDealerCriterion() {
        Criteria forCriteria = this.definition.getForCriteria();
        if (!this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerCriterion)) {
        	ServiceProvider dealership = this.orgService.findDealerByName(this.dealerCriterion);
            if (dealership == null) {
                addFieldError("dealerCriterion", "error.managePolicy.invalidDealer",
                        new String[] { this.dealerCriterion });
            } else {
                DealerCriterion criterion = new DealerCriterion();
                criterion.setDealer(dealership);
                forCriteria.setDealerCriterion(criterion);
            }
        } else if (this.dealerGroupSelected && StringUtils.isNotBlank(this.dealerGroupName)) {
            DealerGroup dealerGroup = this.dealerGroupService.findByNameAndPurpose(
                    this.dealerGroupName, AdminConstants.DEALER_RATES_PURPOSE);
            if (dealerGroup == null) {
                addFieldError("dealerGroupName", "error.managePolicy.invalidDealerGroup",
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
        Criteria forCriteria = this.definition.getForCriteria();
        if (StringUtils.isNotBlank(this.productType)) {
            ItemGroup itemGroup = this.catalogService.findItemGroupByName(this.productType);
            if (itemGroup == null) {
                addFieldError("productType", "error.managePolicy.invalidProductType",
                        new String[] { this.productType });
            }
            forCriteria.setProductType(itemGroup);
        } else {
            forCriteria.setProductType(null);
        }
    }

    private void validatePolicyPriceModifierDate() {
        int numRates = this.rates.size();
        
        if(numRates == 0){
        	addActionError("error.manageRates.moreConfigRequired");
        	return;
        }

        if (numRates < 2) {
        	CalendarDate startDate = this.rates.get(0).getDuration().getFromDate();
        	CalendarDate endDate = this.rates.get(0).getDuration().getTillDate();
        	if(startDate.isAfter(endDate))
        		addActionError("error.manageRates.endDateBeforeStartDate", new String[] {
                        endDate.toString(), startDate.toString() });
          	return;
        }

        for (int i = 1; i < numRates; i++) {
            CalendarDuration thisPPMDuration = this.rates.get(i).getDuration();
            CalendarDuration prevPPMDuration = this.rates.get(i - 1).getDuration();
        
            CalendarDate preStartDate = prevPPMDuration.getFromDate();
            CalendarDate prevEndDate = prevPPMDuration.getTillDate();
            CalendarDate currentStartDate = thisPPMDuration.getFromDate();
            CalendarDate currentEndDate = thisPPMDuration.getTillDate();
            
            if(preStartDate.isAfter(prevEndDate) ||  currentStartDate.isAfter(currentEndDate) )
            	 addActionError("error.manageRates.endDateBeforeStartDate", new String[] {
                         prevEndDate.toString(), currentStartDate.toString() });
            

            if (((prevEndDate != null) && (currentStartDate != null))
                    && !(prevEndDate.plusDays(1).equals(currentStartDate))) {

                addActionError("error.manageRates.noGapsInConsecutiveDateRange", new String[] {
                        prevEndDate.toString(), currentStartDate.toString() });
            }
        }
    }

    private List<PolicyDefinition> preparePolicyDefinitions() {
        List<PolicyDefinition> definitions = new ArrayList<PolicyDefinition>();
        for (int i = 0; i < this.policyDefinitions.size(); i++) {
            PolicyDefinition policyDefinition = this.policyDefinitionRepository
                    .findPolicyDefinitionByCode(this.policyDefinitions.get(i));
            if (policyDefinition == null) {
                addFieldError("policyDefinitions[" + i + "]",
                        "error.managePolicy.invalidPolicyDefinition",
                        new String[] { this.policyDefinitions.get(i) });
            } else {
                definitions.add(policyDefinition);
            }
        }
        return definitions;
    }

    private List<Label> preparePolicyLabels() {
        List<Label> labels = new ArrayList<Label>();
        for (int i = 0; i < this.policyLabels.size(); i++) {
            Label label = this.labelService.findById(this.policyLabels.get(i));
            if (label == null) {
                addFieldError("policyLabels[" + i + "]", "error.managePolicy.invalidPolicyLabel",
                        new String[] { this.policyLabels.get(i) });
            } else {
                labels.add(label);
            }
        }
        return labels;
    }

    public Map<String, String> getWarrantyTypes() {
        Map<String, String> warrantyTypes = new TreeMap<String, String>();
        warrantyTypes.put("", getText("dropdown.common.all"));
        warrantyTypes.put(WarrantyType.STANDARD.getType(), getText("dropdown.common.standard"));
        warrantyTypes.put(WarrantyType.EXTENDED.getType(), getText("dropdown.common.extended"));
        warrantyTypes.put(WarrantyType.POLICY.getType(), getText("dropdown.common.goodwill"));
        return warrantyTypes;
    }

    public Map<String, String> getRegistrationTypes() {
        Map<String, String> registrationTypes = new TreeMap<String, String>();
        registrationTypes.put("ALL", getText("dropdown.common.all"));
        registrationTypes
                .put("REGISTRATION", getText("dropdown.managePolicy.warrantyRegistration"));
        registrationTypes.put("TRANSFER", getText("dropdown.managePolicy.equipmentTransfer"));
        return registrationTypes;
    }

    public CatalogService getCatalogService() {
        return this.catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public String getDealerCriterion() {
        return this.dealerCriterion;
    }

    public void setDealerCriterion(String dealerCriterion) {
        this.dealerCriterion = dealerCriterion;
    }

    public String getDealerGroupName() {
        return this.dealerGroupName;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public PolicyRates getDefinition() {
        return this.definition;
    }

    public void setDefinition(PolicyRates definition) {
        this.definition = definition;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PolicyRatesAdminService getPolicyRatesAdminService() {
        return this.policyRatesAdminService;
    }

    public void setPolicyRatesAdminService(PolicyRatesAdminService policyRatesAdminService) {
        this.policyRatesAdminService = policyRatesAdminService;
    }

    public PolicyRatesRepository getPolicyRatesRepository() {
        return this.policyRatesRepository;
    }

    public void setPolicyRatesRepository(PolicyRatesRepository policyRatesRepository) {
        this.policyRatesRepository = policyRatesRepository;
    }

    public String getProductType() {
        return this.productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public List<PolicyRate> getRates() {
        return this.rates;
    }

    public void setRates(List<PolicyRate> rates) {
        this.rates = rates;
    }

    public DealerGroupService getDealerGroupService() {
        return this.dealerGroupService;
    }

    public String getJsonString() {
        return this.jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public PolicyDefinitionRepository getPolicyDefinitionRepository() {
        return this.policyDefinitionRepository;
    }

    public void setPolicyDefinitionRepository(PolicyDefinitionRepository policyDefinitionRepository) {
        this.policyDefinitionRepository = policyDefinitionRepository;
    }

    public List<String> getPolicyDefinitions() {
        return this.policyDefinitions;
    }

    public void setPolicyDefinitions(List<String> policyDefinitions) {
        this.policyDefinitions = policyDefinitions;
    }

    public List<String> getPolicyLabels() {
        return this.policyLabels;
    }

    public void setPolicyLabels(List<String> policyLabels) {
        this.policyLabels = policyLabels;
    }

    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }
}
