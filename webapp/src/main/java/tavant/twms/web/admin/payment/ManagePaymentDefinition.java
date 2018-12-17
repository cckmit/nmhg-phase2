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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.category.CategoryRepository;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.PaymentVariableLevel;
import tavant.twms.domain.claim.payment.definition.PolicyCriteria;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifierRepository;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * TODOAPR15: Kannan I have commented out lines that were erroring
 * we will fix this after payment refactoring is done
 * 
 * @author sayedAamir
 * 
 */
@SuppressWarnings("serial")
public class ManagePaymentDefinition extends I18nActionSupport implements Validateable,Preparable{

    private static Logger logger=Logger.getLogger(ManagePaymentDefinition.class);

    private PaymentDefinitionAdminService paymentDefinitionAdminService;
    
    private PaymentModifierRepository paymentModifierRepository;

    private Long id;

    private PaymentDefinition paymentDefinition ;

    private List<Section> allSections = new ArrayList<Section>();
    
    private List<PaymentDefinition> allPaymentDefinitions = new ArrayList<PaymentDefinition>();
    
    private List<PaymentDefinition> paymentDefinitionsForCP = new ArrayList<PaymentDefinition>(2);
    
    private CategoryRepository categoryRepository;
    
    private String partialPolicyCategory;
    
    private String jsonString;
    
    private boolean appPolicySelected;
    
    private LabelService labelService;

    private List<CostCategory> configuredCostCategories = new ArrayList<CostCategory>();

    private ConfigParamService configParamService;

    private ClaimService claimService;
    
    private String paymentPolicyLabel;

    public void setLabelService(LabelService labelService) {
		this.labelService = labelService;
	}

    public List<ClaimType> claimTypes = new ArrayList<ClaimType>();

	@Override
    public void validate() {
		if(this.paymentDefinition !=null){
			for(PaymentSection paymentSection :this.paymentDefinition.getPaymentSections()){
                if (paymentSection != null) {
                    for (PaymentVariableLevel paymentVariableLevel : paymentSection.getPaymentVariableLevels()) {
                        if (paymentVariableLevel.getLevel() != null &&
                                (paymentVariableLevel.getLevel() == 0 || paymentVariableLevel.getLevel() < 0))
                            addActionError("message.paymentDefintion.invalidModifier", paymentVariableLevel.getPaymentVariable().getName());
                    }
                }
            }
			if(hasActionErrors()){
				allSections = paymentDefinitionAdminService.findAllSections();
			}
	}
}

    public String view() {
        paymentDefinition = paymentDefinitionAdminService.findById(id);
        return SUCCESS;
    }

    public String create() throws Exception {

    	// TODO use repeat interceptor for this
        for (Iterator<PaymentSection> iter = paymentDefinition.getPaymentSections().iterator(); iter.hasNext();) {
        	PaymentSection paymentSection = iter.next();
            if (paymentSection==null || paymentSection.getSection() == null) {
                iter.remove();
            }
        }
        for (Iterator<PaymentSection> iter = paymentDefinition.getPaymentSections().iterator(); iter.hasNext();) {
        	PaymentSection paymentSection = iter.next();
        	for(Iterator<PaymentVariableLevel> levelIter=
        		paymentSection.getPaymentVariableLevels().iterator();levelIter.hasNext();){
        		PaymentVariableLevel paymentVariableLevel = levelIter.next();
        		if(paymentVariableLevel.getLevel()==null){
        			levelIter.remove();
        		}
        	}
        }
    	return SUCCESS;
    }
    
    public String continueCreate() throws Exception{
    	
    	if(StringUtils.hasText(paymentPolicyLabel))    	
    	    paymentDefinition.getCriteria().setLabel(labelService.findById(paymentPolicyLabel));
    	else{
    		paymentDefinition.getCriteria().setLabel(null);
    	}
    	
    	if(paymentDefinition.getCriteria().getPolicyDefinition() != null && paymentDefinition.getCriteria().getPolicyDefinition().getId() == null)
    		paymentDefinition.getCriteria().setPolicyDefinition(null);
    	
		for (Iterator<PaymentSection> iter = paymentDefinition.getPaymentSections().iterator(); iter.hasNext();) {
        	PaymentSection paymentSection = iter.next();
            if (paymentSection==null || paymentSection.getSection() == null) {
                iter.remove();
            }
        }
        for (Iterator<PaymentSection> iter = paymentDefinition.getPaymentSections().iterator(); iter.hasNext();) {
        	PaymentSection paymentSection = iter.next();
        	for(Iterator<PaymentVariableLevel> levelIter=
        		paymentSection.getPaymentVariableLevels().iterator();levelIter.hasNext();){
        		PaymentVariableLevel paymentVariableLevel = levelIter.next();
        		if(paymentVariableLevel.getLevel()==null){
        			levelIter.remove();
        		}
        	}
        }
        
        if(paymentDefinition.getCriteria().getLabel()==null && paymentDefinition.getCriteria().getPolicyDefinition() == null){
            paymentDefinition.getCriteria().setIdentifier(ALL_POLICY_TYPES);
        }else if(paymentDefinition.getCriteria().getLabel()!=null){
            paymentDefinition.getCriteria().setIdentifier(paymentDefinition.getCriteria().getLabel().getName());
        }else{
        	  paymentDefinition.getCriteria().setIdentifier(paymentDefinition.getCriteria().getPolicyDefinition().getCode());
        }
        
        if(paymentDefinition.getCriteria().getClaimType()==null){
            paymentDefinition.getCriteria().setClmTypeName(ALL_CLAIM_TYPES);
        }else if (ClaimType.CAMPAIGN.getType().equals(paymentDefinition.getCriteria().getClaimType().getType())) {
            paymentDefinition.getCriteria().setClmTypeName(ClaimType.FIELD_MODIFICATION.getType());
        } else {
            paymentDefinition.getCriteria().setClmTypeName(paymentDefinition.getCriteria().getClaimType().getType());
        }
        paymentDefinitionAdminService.saveOrUpdate(paymentDefinition);
        addActionMessage("message.managePayment.definitionCreateSuccess");
    	return SUCCESS;
    }
    
    public String newPaymentDefinition(){
    	paymentDefinition = new PaymentDefinition();
        allSections = paymentDefinitionAdminService.findAllSections();
        return SUCCESS;
    }

	public String listallPaymentDefinitions() {
		setAllPaymentDefinitions(paymentDefinitionAdminService.findAll());
		return SUCCESS;
	}
    
    public String update() throws Exception {
    	PolicyCriteria crit = paymentDefinition.getCriteria();
		if(crit.getLabel() != null
    			&& crit.getLabel().getName() != null) {
    		crit.setLabel(labelService.findById(crit.getLabel().getName()));
    	}
        paymentDefinitionAdminService.update(paymentDefinition);
        return SUCCESS;
    }
    
	public String updatePaymentDefinitionForCP() {
		if (!paymentDefinitionsForCP.isEmpty()) {
			addActionMessage("message.managePayment.definitionUpdateSuccess");
			if(paymentDefinitionsForCP.get(0)!=null)
			{
			paymentDefinitionsForCP.get(0).getCriteria().setApplForCommPolicyClaims(false); // This holds the value which was selected previously
			paymentDefinitionsForCP.get(1).getCriteria().setApplForCommPolicyClaims(true); // This holds the current updated value
			paymentDefinitionAdminService.updateAll(paymentDefinitionsForCP);
			}
			else
			{
			paymentDefinitionsForCP.get(1).getCriteria().setApplForCommPolicyClaims(true);
			paymentDefinitionAdminService.update(paymentDefinitionsForCP.get(1));	
			}
			
		}
		return listallPaymentDefinitions();
	}
    
    public String detail(){
        paymentDefinition = paymentDefinitionAdminService.findById(id);
        checkSelectedPolicyType();
        allSections = paymentDefinitionAdminService.findAllSections();
        return SUCCESS;
    }
    
	private void checkSelectedPolicyType() {
		if(paymentDefinition.getCriteria()!=null){
			if (paymentDefinition.getCriteria().getPolicyDefinition() != null
					&& paymentDefinition.getCriteria().getPolicyDefinition().getId() != null) {
				appPolicySelected = true;
			} else if (paymentDefinition.getCriteria().getLabel() != null && paymentDefinition.getCriteria().getLabel().getName() != null) {
				appPolicySelected = false;
			}
		}
	}

    public String delete() {
        return SUCCESS;
    }
    
	public List<PaymentVariable> getPaymentVaribles(String sectionName){
        return paymentModifierRepository.findPaymentVariablesBySection(sectionName);
	}

	public String listPolicyCategories() {
        try {
            List<String> policyCategories = labelService.findLabelsWithNameAndTypeLike(getSearchPrefix(), Label.POLICY, 0, 10);
            return generateAndWriteComboboxJson(policyCategories);
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }
    }
	
	public String listPolicies(){
        try {
            List<PolicyDefinition> policies =  categoryRepository.findPolicyDefinitionsByName(partialPolicyCategory);
            return generateAndWriteComboboxJson(policies,"id","code");
        } catch (Exception e) {
            logger.error("Error while generating JSON", e);
            throw new RuntimeException("Error while generating JSON", e);
        }


    }

    public PaymentDefinition getPaymentDefinition() {
        return paymentDefinition;
    }

    public void setPaymentDefinition(PaymentDefinition paymentDefinition) {
        this.paymentDefinition = paymentDefinition;
    }

    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public List<Section> getAllSections() {
		return allSections;
	}


	public void setPaymentModifierRepository(
			PaymentModifierRepository paymentModifierRepository) {
		this.paymentModifierRepository = paymentModifierRepository;
	}
	
	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}
	
	public String getPartialPolicyCategory() {
		return partialPolicyCategory;
	}

	public void setPartialPolicyCategory(String partialPolicyCategory) {
		this.partialPolicyCategory = partialPolicyCategory;
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}


	
	public void prepare() throws Exception {
		String actionName = ActionContext.getContext().getName();
        setClaimTypes();
		if(paymentDefinition!=null &&  ("create_payment_definition".equals(actionName) || "continue_create_payment_definition".equals(actionName))){
			paymentDefinition.getPaymentSections().clear();			
		}
        List<Object> costCategories = configParamService.getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES.getName());
        for (Object costCategory : costCategories) {
            configuredCostCategories.add((CostCategory)costCategory);
        }
        // TWMS4.3U-102 - Specifying Invalid values for Modifiers in payment definition throws exception
    	//	Prior to this fix, I have added the code to show all the sections that is showing below as well as i have added ManagePaymentDefinition.properties file.
       
        //for showing all the sections
        allSections = paymentDefinitionAdminService.findAllSections();
    }
	
	public Long getPaymentSectionId(String section){
		Long paymentSectionId = null;
		for(PaymentSection paymentSection:paymentDefinition.getPaymentSections()){
			if(section.equals(paymentSection.getSection().getName())){
				paymentSectionId=paymentSection.getId();
		}}
		return paymentSectionId;
		
	}

	public boolean isAppPolicySelected() {
		return appPolicySelected;
	}

	public void setAppPolicySelected(boolean appPolicySelected) {
		this.appPolicySelected = appPolicySelected;
	}

    public boolean isSectionConfigured(String sectionName){
        boolean toReturn = false;
        for (CostCategory costCategory : configuredCostCategories) {
           if(costCategory.getName().equals(sectionName)){
               return true;
           }
        }
        return toReturn;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public String getPaymentPolicyLabel() {
		return paymentPolicyLabel;
	}

	public void setPaymentPolicyLabel(String paymentPolicyLabel) {
		this.paymentPolicyLabel = paymentPolicyLabel;
	}

	public void setAllPaymentDefinitions(List<PaymentDefinition> allPaymentDefinitions) {
		this.allPaymentDefinitions = allPaymentDefinitions;
	}

	public List<PaymentDefinition> getAllPaymentDefinitions() {
		return allPaymentDefinitions;
	}

	public List<PaymentDefinition> getPaymentDefinitionsForCP() {
		return paymentDefinitionsForCP;
	}

	public void setPaymentDefinitionsForCP(List<PaymentDefinition> paymentDefinitionsForCP) {
		this.paymentDefinitionsForCP = paymentDefinitionsForCP;
	}


    public List<ClaimType> getClaimTypes() {
		return claimTypes;
	}

    public void setClaimTypes() {
		 this.claimTypes = this.claimService.fetchAllClaimTypesForBusinessUnit();
	}

}